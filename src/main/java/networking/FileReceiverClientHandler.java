package networking;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import com.sun.jna.platform.win32.Guid;
import javafx.concurrent.Task;
import networking.protocol.*;
import util.GUIDDeserializer;
import util.GUIDSerializer;

import java.awt.*;
import java.io.*;
import java.net.Socket;

import static networking.SearchResultHelper.FileSearchResult;

public class FileReceiverClientHandler extends Task {

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
        updateTitle("File Download for " + file.fileName);
    }

    /**
     * Invoked when the Task is executed, the call method must be overridden and
     * implemented by subclasses. The call method actually performs the
     * background thread logic. Only the updateProgress, updateMessage, updateValue and
     * updateTitle methods of Task may be called from code within this method.
     * Any other interaction with the Task from the background thread will result
     * in runtime exceptions.
     *
     * @return The result of the background work, if any.
     * @throws Exception an unhandled exception which occurred during the
     *                   background operation
     */
    @Override
    protected Object call() throws Exception {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            updateMessage("Sending Request");
            writer.write(gson.toJson(new FileRequest(file.fileName, file.checksum), IMessage.class));
            writer.newLine();
            writer.flush();

            InputStream stream = socket.getInputStream();

            File tempFile = File.createTempFile("Homeflix-",file.fileName);
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            int read;
            long readTotal = 0;
            byte[] buffer = new byte[65500];
            updateMessage("Downloading File");
            while((read = stream.read(buffer)) > 0) {
                outputStream.write(buffer,0,read);
                readTotal += read;
                updateProgress(readTotal, file.length);
            }
            updateMessage("Received File");
            outputStream.close();
            socket.close();

            updateMessage("Opening File: " + tempFile.toString());
            Desktop.getDesktop().open(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
