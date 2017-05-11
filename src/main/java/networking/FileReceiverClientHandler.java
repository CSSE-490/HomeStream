package networking;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import com.sun.jna.platform.win32.Guid;
import networking.protocol.*;
import util.GUIDDeserializer;
import util.GUIDSerializer;

import java.awt.*;
import java.io.*;
import java.net.Socket;

import static networking.SearchResultHelper.FileSearchResult;

public class FileReceiverClientHandler extends Thread {

    private final Socket socket;
    private final FileSearchResult file;
    private final Gson gson;

    public FileReceiverClientHandler(Socket socket, FileSearchResult file) {
        this.socket = socket;
        this.file = file;


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
    }

    @Override
    public void run() {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            System.out.println("Sending Request");
            writer.write(gson.toJson(new FileRequest(file.fileName, file.checksum), IMessage.class));
            writer.newLine();
            writer.flush();

            InputStream stream = socket.getInputStream();

            File tempFile = File.createTempFile("Homeflix-",file.fileName);
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            int read;
            byte[] buffer = new byte[65500];
            while((read = stream.read(buffer)) > 0) {
                outputStream.write(buffer,0,read);
            }
            System.out.println("Received file");
            outputStream.close();
            socket.close();

            System.out.println("Opening File: " + tempFile.toString());
            Desktop.getDesktop().open(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
