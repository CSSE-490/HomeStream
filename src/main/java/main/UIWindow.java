package main;

import com.sun.jna.platform.win32.Guid;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import networking.Host;
import networking.MessageHandler;
import networking.SearchResultHelper;
import networking.protocol.NetworkMapRequest;
import networking.protocol.SearchCommandRequest;
import org.controlsfx.control.StatusBar;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

import static main.Settings.SETTINGS;
import static networking.NetworkMap.NETWORK_MAP;
import static networking.SearchResultHelper.FileSearchResult;
import static networking.SearchResultHelper.bindSearchResults;

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
        searchTextField.setOnAction((ae) -> startSearch());
        refreshNetworkButton.setOnAction((ae) -> refreshNetworks());
        connectPeerButton.setOnAction((ae) -> connectPeer());
        NETWORK_MAP.bindCount(statusBar);


        hostColumn.setCellValueFactory(new PropertyValueFactory<>("provider"));
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("length"));

        addFolderButton.setOnAction((ae) -> addSearchFolder());
        viewFoldersButton.setOnAction((ae) -> viewSearchFolders());
        viewPeersButton.setOnAction((ae) -> viewPeers());
    }

    private void viewPeers() {
        ListView<String> peersList = new ListView();
        peersList.setPrefSize(250, 400);
        Set<Host> hosts = NETWORK_MAP.getHostSet();

        ObservableList<String> data = FXCollections.observableArrayList();
        for (Host h : hosts) {
            data.add(h.toString());
        }

        peersList.setItems(data);
        Scene scene = new Scene(peersList);
        Stage newStage = new Stage();
        newStage.setScene(scene);
        newStage.setTitle("Peers List");
        newStage.show();

        Window parent = this.getScene().getWindow();
        double x = parent.getX();
        double y = parent.getY();

        double xSize = parent.getWidth();
        double ySize = parent.getHeight();

        double centerX = x + xSize / 2;
        double centerY = y + ySize / 2;

        double childX = newStage.getWidth();
        double childY = newStage.getHeight();

        newStage.setX(centerX - childX / 2);
        newStage.setY(centerY - childY / 2);
    }

    private void viewSearchFolders() {
        FolderListView foldersView = new FolderListView(SETTINGS.searchableDirectories);
        Scene scene = new Scene(foldersView);
        Stage newStage = new Stage();
        newStage.initOwner(this.getScene().getWindow());
        newStage.setScene(scene);
        newStage.setResizable(true);
        newStage.setTitle("Searchable Folders");
        newStage.show();

        newStage.setMinHeight(scene.getHeight());
        newStage.setMinWidth(scene.getWidth());

        Window parent = this.getScene().getWindow();
        double x = parent.getX();
        double y = parent.getY();

        double xSize = parent.getWidth();
        double ySize = parent.getHeight();

        double centerX = x + xSize / 2;
        double centerY = y + ySize / 2;

        double childX = newStage.getWidth();
        double childY = newStage.getHeight();

        newStage.setX(centerX - childX / 2);
        newStage.setY(centerY - childY / 2);
    }

    private void addSearchFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        File userHome = new File(System.getProperty("user.home"));
        chooser.setInitialDirectory(userHome);

        File newSearchDirectory = chooser.showDialog(this.getScene().getWindow());

        if (newSearchDirectory.exists() && newSearchDirectory.isDirectory())
            SETTINGS.searchableDirectories.add(newSearchDirectory);
        else
            System.err.println("Does not exist or is not a directory");
    }

    private void connectPeer() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Connect to Peer");
        dialog.setHeaderText("Enter Peer's Hostname and Port");
        dialog.setContentText("In the format of <hostname/ip>:<port>");


        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String input = result.get();

            String[] args = input.split(":");
            if (args.length != 2) {
                showInvalidInput("Peer input was not valid");
            } else {
                try {
                    String hostname = args[0];
                    int port = Integer.parseInt(args[1]);

                    Host host = new Host(hostname, port);

                    MessageHandler handler = new MessageHandler(host);
                    handler.identify();
                } catch (NumberFormatException e) {
                    showInvalidInput("Port was in an invalid format.");
                } catch (IOException e) {
                    showInvalidInput("Unable to connect to peer with given input.");
                }
            }
        }
    }

    private void showInvalidInput(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    private void refreshNetworks() {
        NETWORK_MAP.broadcast(new NetworkMapRequest());
    }

    private void startSearch() {
        String text = searchTextField.getText();

        Guid.GUID guid = Guid.GUID.newGuid();
        SearchCommandRequest request = new SearchCommandRequest(text, guid);
        bindSearchResults(guid, statusBar);
        NETWORK_MAP.broadcast(request);

        this.tableView.setItems(SearchResultHelper.getSearchResultsForGUID(guid));
    }
}
