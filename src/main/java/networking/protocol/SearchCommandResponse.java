package networking.protocol;

import com.sun.jna.platform.win32.Guid;
import networking.Host;

import java.util.Arrays;
import java.util.List;


public class SearchCommandResponse implements IMessage {
    public List<FoundFile> files;
    public Host responder;
    public Host provider;
    public Guid.GUID uniqueIdentifier;

    public SearchCommandResponse(List<FoundFile> files, Host responder, Host provider, Guid.GUID uniqueIdentifier) {
        this.files = files;
        this.responder = responder;
        this.provider = provider;
        this.uniqueIdentifier = uniqueIdentifier;
    }

    @Override
    public String toString() {
        return "SearchCommandResponse{" +
                "files=" + Arrays.toString(files.toArray())+
                ", provider=" + provider +
                ", uniqueIdentifier=" + uniqueIdentifier +
                '}';
    }
}
