package networking.protocol;

public class SearchCommand implements IMessage {

    public String fileNameRegex;
    public byte[] checksum;
    public String hostname;
    public int port;

}
