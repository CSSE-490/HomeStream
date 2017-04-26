package streamingPoC;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

import javax.swing.*;

public class Tutorial {

    private final JFrame frame;

    private final EmbeddedMediaPlayerComponent mediaPlayerComponent;

    public static void main(String[] args) {
        new NativeDiscovery().discover();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Tutorial();
            }
        });
    }

    public Tutorial() {
        frame = new JFrame("My First Media Player");
        frame.setBounds(100, 100, 600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        frame.setContentPane(mediaPlayerComponent);
        frame.setVisible(true);

        //mediaPlayerComponent.getMediaPlayer().playMedia(new RandomAccessFileMedia(new File("F:\\Recordings\\WoW\\Raid\\2016-10-13 20-33-59.flv"), 1024*1024));
        mediaPlayerComponent.getMediaPlayer().playMedia("http://portalnumber9.com/TEMP/testFile.flv");
    }
}