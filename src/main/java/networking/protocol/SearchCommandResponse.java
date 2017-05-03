package networking.protocol;

import com.sun.jna.platform.win32.Guid;
import networking.Host;

import java.util.Arrays;
import java.util.List;


public class SearchCommandResponse implements IMessage {
    public List<FoundFile> files;
    public Host provider;
    public Guid.GUID uniqueIdentifier;

    public SearchCommandResponse() {}

    public SearchCommandResponse(List<FoundFile> files, Host host, Guid.GUID uniqueIdentifier) {
        this.files = files;
        this.provider = host;
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public List<FoundFile> getFiles() {
        return files;
    }

    public Host getProvider() {
        return provider;
    }

    public Guid.GUID getUniqueIdentifier() {
        return uniqueIdentifier;
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
