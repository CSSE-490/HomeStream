package networking;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server extends Thread {
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
            try {
                Socket socket = serverSocket.accept();
                System.out.println("Connection accepted from " + socket.getInetAddress().toString());
                new ClientHandler(socket, null).start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
