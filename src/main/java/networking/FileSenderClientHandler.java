package networking;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import com.sun.jna.platform.win32.Guid;
import networking.protocol.*;
import util.GUIDDeserializer;
import util.GUIDSerializer;

import java.io.*;
import java.net.Socket;

import static main.Settings.SETTINGS;

public class FileSenderClientHandler extends Thread {

    private final Socket socket;
    private final Gson gson;
    private final BufferedWriter bufferedWriter;

    public FileSenderClientHandler(Socket socket) throws IOException {
        this.socket = socket;

        final RuntimeTypeAdapterFactory<IMessage> typeFactory = RuntimeTypeAdapterFactory.of(IMessage.class, "type")
                .registerSubtype(FileRequest.class)
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

        try {
            String line = reader.readLine();
            IMessage message = gson.fromJson(line, IMessage.class);

            System.out.println("Received Message");
            if(message instanceof FileRequest) {
                FileRequest request = ((FileRequest) message);

                File toSend = getFileFromName(request.fileName);

                if(toSend == null) {
                    // TODO Send back error;
                    System.err.println("Received Request for Non-Existent File");
                    socket.close();
                    return;
                }

                InputStream inputStream = new FileInputStream(toSend);
                int amount;
                byte[] buffer = new byte[65500];
                while((amount = inputStream.read(buffer)) > 0) {
                    socket.getOutputStream().write(buffer,0,amount);
                }
                System.out.println("Done Sending File");
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private File getFileFromName(String fileName) {
        for (File f : SETTINGS.searchableDirectories) {
            File[] array = f.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.equals(fileName);
                }
            });
            if(array.length > 0)
                return array[0];
        }
        return null;
    }
}
