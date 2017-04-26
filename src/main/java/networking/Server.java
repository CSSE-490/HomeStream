package networking;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Jesse Shellabarger on 4/26/2017.
 */
public class Server extends Thread{
    public static int port;
    public static String hostname;
    private boolean run;

    public Server(int port) {
        Server.port = port;
        this.run = true;
    }

    @Override
    public void run() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        while (run) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                new MessageHandler(socket).start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
