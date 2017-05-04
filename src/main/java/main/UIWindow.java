package main;

import com.sun.jna.platform.win32.Guid;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import networking.SearchResultHelper;
import networking.protocol.SearchCommandRequest;
import networking.protocol.SearchCommandResponse;
import org.controlsfx.control.StatusBar;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static main.Settings.SETTINGS;
import static networking.NetworkMap.NETWORK_MAP;
import static networking.SearchResultHelper.FileSearchResult;

public class UIWindow extends GridPane implements Initializable {

    @FXML
    private TableView<FileSearchResult> tableView;
    @FXML
    private TableColumn<FileSearchResult, String> hostColumn;
    @FXML
    private TableColumn<FileSearchResult, String> fileNameColumn;
    @FXML
    private TableColumn<FileSearchResult, Long> sizeColumn;
    @FXML
    private Button connectPeerButton;
    @FXML
    private Button refreshNetworkButton;
    @FXML
    private Button viewPeersButton;
    @FXML
    private Button addFolderButton;
    @FXML
    private Button viewFoldersButton;
    @FXML
    private TextField searchTextField;
    @FXML
    private Button searchButton;
    @FXML
    private StatusBar statusBar;

    public UIWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchButton.setOnAction((ae) -> startSearch());

        hostColumn.setCellValueFactory(new PropertyValueFactory<>("provider"));
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("length"));

        addFolderButton.setOnAction((ae) -> addSearchFolder());


    }

    private void addSearchFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        File userHome = new File(System.getProperty("user.home"));
        chooser.setInitialDirectory(userHome);

        File newSearchDirectory = chooser.showDialog(this.getScene().getWindow());

        if(newSearchDirectory.exists() && newSearchDirectory.isDirectory())
            SETTINGS.searchableDirectories.add(newSearchDirectory);
        else
            System.err.println("Does not exist or is not a directory");
    }

    private void startSearch() {
        String text = searchTextField.getText();

        Guid.GUID guid = Guid.GUID.newGuid();
        SearchCommandRequest request = new SearchCommandRequest(text, guid);
        NETWORK_MAP.broadcast(request);

        this.tableView.setItems(SearchResultHelper.getSearchResultsForGUID(guid));
    }
}
