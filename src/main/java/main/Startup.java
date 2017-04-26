package main;

import networking.Host;
import networking.MessageHandler;
import networking.Server;
import networking.protocol.IdentificationSend;

import java.io.IOException;

/**
 * Created by Jesse Shellabarger on 4/26/2017.
 */
public class Startup {

    public static void main(String[] args) throws IOException {
        int localPort = Integer.parseInt(args[0]);
        new Server(localPort).start();

        if (args.length >= 2) {
            String networkHost = args[1];
            Host host = new Host(networkHost);
            MessageHandler handler = new MessageHandler(host);
            handler.identify();
        }
    }
}
