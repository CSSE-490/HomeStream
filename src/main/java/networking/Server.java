package networking;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Jesse Shellabarger on 4/26/2017.
 */
public class Server extends Thread{
    public static Host host;
    private boolean run;

    public Server(int port) {
        try {
            this.host = new Host(InetAddress.getLocalHost().getHostName(), port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.run = true;
    }

    @Override
    public void run() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(host.port);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        while (run) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                System.out.println("Connection accepted from " + socket.getInetAddress().toString());
                new MessageHandler(socket, null).start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
