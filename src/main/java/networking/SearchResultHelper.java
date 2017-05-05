package networking;

import com.sun.glass.ui.Application;
import com.sun.javafx.collections.ObservableListWrapper;
import com.sun.jna.platform.win32.Guid;
import javafx.collections.ListChangeListener;
import networking.protocol.FoundFile;
import networking.protocol.SearchCommandResponse;
import org.controlsfx.control.StatusBar;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import static networking.NetworkMap.NETWORK_MAP;

public class SearchResultHelper {

    private static Map<Guid.GUID, ObservableListWrapper<FileSearchResult>> responses;
    private static Map<Guid.GUID, ObservableListWrapper<Host>> responsedHosts;

    static {
        responses = new Hashtable<>();
        responsedHosts = new Hashtable<>();
    }

    private static ListChangeListener<Host> hostListener;
    private static Guid.GUID previousGuid;

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

    public synchronized static void bindSearchResults(final Guid.GUID guid, final StatusBar statusBar) {
        if(previousGuid != null) {
            responsedHosts.get(previousGuid).removeListener(hostListener);
        }

        final int currentHostCount = NETWORK_MAP.getHostSet().size();

        hostListener = new ListChangeListener<Host>() {
            int hostResponded = 0;

            @Override
            public void onChanged(Change<? extends Host> c) {
                hostResponded++;
                if(hostResponded == currentHostCount) {
                    Application.invokeAndWait(() -> statusBar.setText("Search Complete"));
                }
                Application.invokeAndWait(() -> statusBar.setProgress(hostResponded / (double)currentHostCount));
            }
        };

        Application.invokeAndWait(() -> statusBar.setText("Searching ..."));
        verifyHostMap(guid);
        responsedHosts.get(guid).addListener(hostListener);
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
