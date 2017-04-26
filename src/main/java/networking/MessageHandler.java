package networking;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import networking.handlers.IMessageEventHandler;
import networking.protocol.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static networking.NetworkMap.NETWORK_MAP;

public class MessageHandler extends Thread {

    private final Socket socket;
    private final BufferedWriter bufferedWriter;
    public boolean run;
    private List<IMessageEventHandler> observers;
    private Gson gson;

    public MessageHandler(String hostname, int port) throws IOException {
        this(new Socket(hostname, port));
    }

    public MessageHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.observers = new ArrayList<>();
        this.run = true;

        final RuntimeTypeAdapterFactory<IMessage> typeFactory = RuntimeTypeAdapterFactory.of(IMessage.class, "type")
                .registerSubtype(PartialFileRequest.class)
                .registerSubtype(SearchCommand.class)
                .registerSubtype(NetworkMapRequest.class)
                .registerSubtype(NetworkMapResponse.class);
        this.gson = new GsonBuilder().registerTypeAdapterFactory(typeFactory).create();

        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        // TODO figure out how to determine who
        //NETWORK_MAP.addHostToMap(new Host( , ), this);
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
        while (run) {
            try {
                String line = reader.readLine();
                IMessage message = gson.fromJson(line, IMessage.class);
                for (IMessageEventHandler eventHandler : observers) {
                    eventHandler.handleMessageEvent(message);
                }

            } catch (IOException e) {
                e.printStackTrace();
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
}

