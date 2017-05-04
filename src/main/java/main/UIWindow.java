package main;

import com.sun.jna.platform.win32.Guid;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import networking.Host;
import networking.MessageHandler;
import networking.SearchResultHelper;
import networking.protocol.NetworkMapRequest;
import networking.protocol.SearchCommandRequest;
import networking.protocol.SearchCommandResponse;
import org.controlsfx.control.StatusBar;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

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
        refreshNetworkButton.setOnAction((ae) -> refreshNetworks());
        connectPeerButton.setOnAction((ae) -> connectPeer());

        hostColumn.setCellValueFactory(new PropertyValueFactory<>("provider"));
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("length"));

        viewPeersButton.setOnAction((ae) -> tableView.refresh());
    }

    private void connectPeer() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Connect to Peer");
        dialog.setHeaderText("Enter Peer's Hostname and Port");
        dialog.setContentText("In the format of <hostname/ip>:<port>");


        Optional<String> result = dialog.showAndWait();

        if(result.isPresent()) {
            String input = result.get();

            String[] args = input.split(":");
            if(args.length != 2) {
                showInvalidInput("Peer input was not valid");
            } else {
                try {
                    String hostname = args[0];
                    int port = Integer.parseInt(args[1]);

                    Host host = new Host(hostname,port);

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
        NETWORK_MAP.broadcast(request);

        this.tableView.setItems(SearchResultHelper.getSearchResultsForGUID(guid));
    }
}
