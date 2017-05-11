package streamingPoC;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.media.callback.nonseekable.FileInputStreamMedia;
import uk.co.caprica.vlcj.player.media.callback.seekable.RandomAccessFileMedia;

import javax.swing.*;
import java.io.File;

public class MediaSample extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        boolean found = new NativeDiscovery().discover();
        System.out.println(found);
        System.out.println(LibVlc.INSTANCE.libvlc_get_version());

        EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();

        JFrame frame = new JFrame("My First Media Player");
        frame.setBounds(100, 100, 600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(mediaPlayerComponent);
        frame.setVisible(true);

        mediaPlayerComponent.getMediaPlayer().playMedia(new FileInputStreamMedia(new File("F:\\Recordings\\WoW\\Raid\\2016-10-13 20-33-59.flv"), 1024*1024));
    }
}