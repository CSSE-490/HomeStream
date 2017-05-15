package main;

import com.sun.jna.platform.win32.Guid;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import networking.FileReceiverClientHandler;
import networking.Host;
import networking.ClientHandler;
import networking.SearchResultHelper;
import networking.protocol.NetworkMapRequest;
import networking.protocol.SearchCommandRequest;
import org.controlsfx.control.StatusBar;
import org.controlsfx.control.TaskProgressView;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.*;
import java.util.concurrent.FutureTask;

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
    private Button getItButton;
    @FXML
    private Label connectedPeers;
    @FXML
    private TaskProgressView taskProgressView;

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
        NETWORK_MAP.bindCount(connectedPeers);


        hostColumn.setCellValueFactory(new PropertyValueFactory<>("provider"));
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        sizeColumn.setCellValueFactory((data) ->
            new ReadOnlyObjectWrapper(humanReadableByteCount(data.getValue().length,false))
        );

        addFolderButton.setOnAction((ae) -> addSearchFolder());
        viewFoldersButton.setOnAction((ae) -> viewSearchFolders());
        viewPeersButton.setOnAction((ae) -> viewPeers());
        getItButton.setOnAction((ae) -> getIt());
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
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

        centerStage(newStage);
    }

    private void viewSearchFolders() {
        List<File> directories = SETTINGS.searchableDirectories;
        ObservableList<String> directoryNames = FXCollections.observableArrayList();

        ListView<String> folders = new ListView();
        folders.setPrefSize(250, 400);
        for (File f : directories) {
            directoryNames.add(f.getPath());
        }
        folders.setItems(directoryNames);

        folders.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                String directoryName = folders.getSelectionModel().getSelectedItem();
                for (File directory : directories) {
                    if(directoryName.equals(directory.getPath())) {
                        directoryNames.remove(directory.getPath());
                        Settings.SETTINGS.searchableDirectories.remove(directory);
                    }
                }
            }
        });

        Scene scene = new Scene(folders);
        Stage newStage = new Stage();
        newStage.initOwner(this.getScene().getWindow());
        newStage.setScene(scene);
        newStage.setResizable(true);
        newStage.setTitle("Searchable Folders");
        newStage.show();

        centerStage(newStage);
    }

    private void centerStage(Stage newStage) {
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

        if (newSearchDirectory != null && newSearchDirectory.exists() && newSearchDirectory.isDirectory())
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

                    ClientHandler handler = new ClientHandler(host);
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
        SearchTask task = new SearchTask(text,guid,NETWORK_MAP.getHostSet().size());
        taskProgressView.getTasks().add(task);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
        NETWORK_MAP.broadcast(request);
        this.tableView.setItems(SearchResultHelper.getSearchResultsForGUID(guid));
    }

    public void getIt() {
        FileSearchResult result = tableView.getSelectionModel().getSelectedItem();

        if(result == null)
            return;

        try {
            Socket socket = new Socket(result.provider.hostname, result.provider.port);
            Task t = new FileReceiverClientHandler(socket, result);
            taskProgressView.getTasks().add(t);
            Thread thread = new Thread(t);
            thread.setDaemon(true);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
