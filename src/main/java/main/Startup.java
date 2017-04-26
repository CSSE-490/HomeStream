package main;

import networking.Host;
import networking.MessageHandler;
import networking.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

        repl();
    }

    private static void repl() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            String line = reader.readLine();

            switch (line) {
                case "help":
                    System.out.println("COMMANDS: " +
                            "\n\teonnect: Connect to a node in the network" +
                            "\n\texit: close the application" +
                            "\n\thelp: view this help menu");
                    break;
                case "connect":
                    System.out.println("Please enter the hostname:port of an existing node of the network");
                    String hostname = reader.readLine();
                    String[] args = hostname.split(":");
                    MessageHandler handler = new MessageHandler(new Host(args[0], Integer.parseInt(args[1])));
                    handler.identify();
                    break;
                case "exit":
                    System.out.println("Goodbye!");
                    System.exit(1);
                    break;
                default:
                    System.out.println("Invalid command. Use the help command to view a list of available commands.");
            }



        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
