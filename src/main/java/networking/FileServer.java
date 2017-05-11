package networking;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class FileServer extends Thread {
    public static Host host;
    private ServerSocket socket;
    private volatile boolean running;

    public FileServer(int port) {
        try {
            this.host = new Host(InetAddress.getLocalHost().getHostName(), port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.running = true;
    }

    @Override
    public void run() {
        try {
            socket = new ServerSocket(host.port);
        } catch (IOException e) {
            System.err.println("Error when starting File Server");
            e.printStackTrace();
            return;
        }

        while(running) {
            try {
                Socket client = socket.accept();
                new FileSenderClientHandler(client).start();
            } catch (IOException e) {
                System.out.println("Error when accepting client on File Server");
                e.printStackTrace();
            }
        }
    }
}
