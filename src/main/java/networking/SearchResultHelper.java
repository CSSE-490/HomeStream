package networking;

import com.sun.javafx.collections.ObservableListWrapper;
import com.sun.jna.platform.win32.Guid;
import networking.protocol.SearchCommandResponse;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class SearchResultHelper {

    private static Map<Guid.GUID, ObservableListWrapper<SearchCommandResponse>> responses;

    static {
        responses = new Hashtable<>();
    }

    public static void searchResultReceived(SearchCommandResponse response) {
        verifyMap(response.uniqueIdentifier);

        responses.get(response.uniqueIdentifier).add(response);

        System.out.println("I Received: " + response);
    }

    private static void verifyMap(Guid.GUID guid) {
        if(!responses.containsKey(guid)){
            responses.put(guid, new ObservableListWrapper<>(new ArrayList<>()));
        }
    }

    public static ObservableListWrapper<SearchCommandResponse> getSearchResultsForGUID(Guid.GUID guid) {
        verifyMap(guid);

        return responses.get(guid);
    }
}
