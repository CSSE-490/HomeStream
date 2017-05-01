package networking.protocol;

import com.sun.jna.platform.win32.Guid;
import networking.Host;

public class SearchCommandRequest implements IMessage {

    public String fileNameRegex;
    public Guid.GUID uniqueIdentifier;

    public SearchCommandRequest() {}


    public SearchCommandRequest(String fileNameRegex, Guid.GUID uniqueIdentifier) {
        this.fileNameRegex = fileNameRegex;
        this.uniqueIdentifier = uniqueIdentifier;
    }
}
