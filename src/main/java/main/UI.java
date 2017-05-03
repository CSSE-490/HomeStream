package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UI extends Application {

    public static void startup() {
        new Thread(UI::launch).start();
    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(new UIWindow());
        primaryStage.setScene(scene);

        primaryStage.show();
        primaryStage.setMinHeight(scene.getHeight());
        primaryStage.setMinWidth(scene.getWidth());
    }
}
