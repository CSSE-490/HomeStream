package networking;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import com.sun.jna.platform.win32.Guid;
import networking.handlers.HandlerFactory;
import networking.handlers.IMessageEventHandler;
import networking.protocol.*;
import util.GUIDDeserializer;
import util.GUIDSerializer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MessageHandler extends Thread {

    private final Socket socket;
    private final BufferedWriter bufferedWriter;
    public boolean run;
    private List<IMessageEventHandler> observers;
    private Gson gson;
    public Host host;
    private ScheduledFuture<?> networkUpdater;

    public MessageHandler(Host host) throws IOException {
        this(new Socket(host.hostname, host.port), host);
    }

    public MessageHandler(Socket socket, Host host) throws IOException {
        this.socket = socket;
        this.host = host;
        this.run = true;

        final RuntimeTypeAdapterFactory<IMessage> typeFactory = RuntimeTypeAdapterFactory.of(IMessage.class, "type")
                .registerSubtype(PartialFileRequest.class)
                .registerSubtype(SearchCommandRequest.class)
                .registerSubtype(SearchCommandResponse.class)
                .registerSubtype(NetworkMapRequest.class)
                .registerSubtype(NetworkMapResponse.class)
                .registerSubtype(IdentificationReceive.class)
                .registerSubtype(IdentificationSend.class);
        this.gson = new GsonBuilder()
                .registerTypeAdapterFactory(typeFactory)
                .registerTypeAdapter(Guid.GUID.class, new GUIDSerializer())
                .registerTypeAdapter(Guid.GUID.class, new GUIDDeserializer())
                .create();
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        this.observers = HandlerFactory.getdefaultHandlers();
    }

    @Override
    public void run() {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        networkUpdater = Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> sendMessage(new NetworkMapRequest()), 1,1, TimeUnit.MINUTES);


        System.out.println("Ready to accept messages from " + socket.getInetAddress().toString());
        while (run) {
            try {
                String line = reader.readLine();
                IMessage message = gson.fromJson(line, IMessage.class);
                for (IMessageEventHandler eventHandler : observers) {
                    eventHandler.handleMessageEvent(message, this);
                }

            } catch (IOException e) {
                e.printStackTrace();
                NetworkMap.NETWORK_MAP.hostMap.remove(this.host);
                System.out.println("Node " + host.toString() + " disconnected. Closing connection.");
                networkUpdater.cancel(true);
                try {
                    socket.close();
                } catch (IOException e1) {}
                return;
            }
        }
    }



    public void registerForMessageEvent(IMessageEventHandler e) {
        this.observers.add(e);
    }

    public void sendMessage(IMessage message) {
        String jsonMessage = this.gson.toJson(message, IMessage.class);
        try {
            bufferedWriter.write(jsonMessage);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void identify() {
        start();
        sendMessage(new IdentificationSend(Server.host));
    }
}

