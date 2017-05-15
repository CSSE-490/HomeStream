package main;

import com.sun.jna.platform.win32.Guid;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import networking.SearchResultHelper;

public class SearchTask extends Task {

    private final Guid.GUID guid;
    private final int maxHosts;
    private IntegerProperty respondedProperty;


    public SearchTask(String searchText, Guid.GUID guid, int maxHosts) {
        this.guid = guid;
        this.maxHosts = maxHosts;
        this.respondedProperty = new SimpleIntegerProperty();
        SearchResultHelper.bindSearchResults(guid,this.respondedProperty,maxHosts);
        updateTitle("Searching for: " + searchText);
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
        while(respondedProperty.get() < maxHosts) {
            updateProgress(respondedProperty.get(),maxHosts);
            Thread.sleep(100);
        }
        return null;
    }




}
