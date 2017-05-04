package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Jesse Shellabarger on 5/4/2017.
 */
public class FolderListView extends ListView<String> implements Initializable {

    List<File> directories = new LinkedList();
    ObservableList<String> directoryNames = FXCollections.observableArrayList();

    public FolderListView(List<File> folders) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/folderListView.fxml"));
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        directories = folders;
        for (File f : folders) {
            directoryNames.add(f.getPath());
        }

        this.setItems(directoryNames);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                this.removeDirectory();
            }
        });
    }

    private void removeDirectory() {
        String directoryName = this.getSelectionModel().getSelectedItem();
        for (File directory : directories) {
            if (directoryName.equals(directory.getPath())) {
                directoryNames.remove(directory.getPath());
                Settings.SETTINGS.searchableDirectories.remove(directory);
            }
        }
    }
}
