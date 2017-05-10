package networking.protocol;

/**
 * Created by Jesse Shellabarger on 4/26/2017.
 */
public class FileRequest implements IMessage {
    public String fileName;
    public byte[] checksum;

    public FileRequest() {}
}
