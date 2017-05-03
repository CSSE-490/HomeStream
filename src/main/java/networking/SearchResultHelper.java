package networking;

import com.sun.javafx.collections.ObservableListWrapper;
import com.sun.jna.platform.win32.Guid;
import networking.protocol.FoundFile;
import networking.protocol.SearchCommandResponse;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class SearchResultHelper {

    private static Map<Guid.GUID, ObservableListWrapper<FileSearchResult>> responses;
    private static Map<Guid.GUID, ObservableListWrapper<Host>> responsedHosts;

    static {
        responses = new Hashtable<>();
        responsedHosts = new Hashtable<>();
    }

    public synchronized static void searchResultReceived(SearchCommandResponse response) {
        verifyMap(response.uniqueIdentifier);
        verifyHostMap(response.uniqueIdentifier);

        responsedHosts.get(response.uniqueIdentifier).add(response.provider);
        try {
            for (FoundFile f : response.files) {
                try {
                    responses.get(response.uniqueIdentifier).add(new FileSearchResult(f.fileName, f.checksum, f.length, response.provider));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        System.out.println("I Received: " + response);
    }

    private synchronized static void verifyHostMap(Guid.GUID guid) {
        if (!responsedHosts.containsKey(guid)) {
            responsedHosts.put(guid, new ObservableListWrapper<>(new ArrayList<>()));
        }
    }

    private synchronized static void verifyMap(Guid.GUID guid) {
        if (!responses.containsKey(guid)) {
            responses.put(guid, new ObservableListWrapper<>(new ArrayList<>()));
        }
    }

    public synchronized static ObservableListWrapper<FileSearchResult> getSearchResultsForGUID(Guid.GUID guid) {
        verifyMap(guid);

        return responses.get(guid);
    }

    public static class FileSearchResult {
        public String fileName;
        public byte[] checksum;
        public long length;
        public Host provider;

        public FileSearchResult(String fileName, byte[] checksum, long length, Host provider) {
            this.fileName = fileName;
            this.checksum = checksum;
            this.length = length;
            this.provider = provider;
        }

        public long getLength() {
            return length;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getChecksum() {
            return checksum;
        }

        public Host getProvider() {
            return provider;
        }
    }
}
