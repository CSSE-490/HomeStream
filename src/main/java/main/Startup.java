package main;

import com.sun.jna.platform.win32.Guid;
import networking.FileServer;
import networking.Host;
import networking.ClientHandler;
import networking.Server;
import networking.protocol.SearchCommandRequest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import static main.Settings.SETTINGS;
import static networking.NetworkMap.NETWORK_MAP;

public class Startup {

    public static void main(String[] args) throws IOException {
        int localPort = Integer.parseInt(args[0]);
        new Server(localPort).start();
        new FileServer(localPort+100).start();


        String localDirectory = args[1];

        File file = new File(localDirectory);
        if(file.exists() && file.isDirectory())
            SETTINGS.searchableDirectories.add(file);

        if (args.length >= 3) {
            String networkHost = args[2];
            Host host = new Host(networkHost);
            ClientHandler handler = new ClientHandler(host);
            handler.identify();
        }

        repl();
    }

    private static void repl() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Now accepting commands.");
        while (true) {
            try {
                String line = reader.readLine();

                switch (line) {
                    case "help":
                        System.out.println("COMMANDS: " +
                                "\n\tconnect: Connect to a node in the network" +
                                "\n\thosts: View the list of available hosts" +
                                "\n\texit: close the application" +
                                "\n\thelp: view this help menu" +
                                "\n\taddDirectory: Add a directory to the current map"+
                                "\n\tviewDirectories: Views the current list of locally available directories" +
                                "\n\tui: Opens the UI");
                        break;
                    case "connect":
                        System.out.println("Please enter the hostname:port of an existing node of the network");
                        String hostname = reader.readLine();
                        String[] args = hostname.split(":");
                        ClientHandler handler = new ClientHandler(new Host(args[0], Integer.parseInt(args[1])));
                        handler.identify();
                        break;
                    case "hosts":
                        System.out.println(Arrays.toString(NETWORK_MAP.getHostSet().toArray()));
                        break;
                    case "exit":
                        System.out.println("Goodbye!");
                        System.exit(1);
                        break;
                    case "addDirectory":
                        System.out.println("Please enter the directory path");
                        String fileString = reader.readLine();
                        File file = new File(fileString );
                        if(file.exists() && file.isDirectory())
                            SETTINGS.searchableDirectories.add(file);
                        else
                            System.err.println("Does not exist or is not a directory");
                        break;
                    case "viewDirectories":
                        System.out.println(Arrays.toString(SETTINGS.searchableDirectories.toArray()));
                        break;
                    case "search":
                        System.out.println("Please enter the file you are looking for (Regex Allowed):");
                        String fileRegex = reader.readLine();

                        SearchCommandRequest request = new SearchCommandRequest(fileRegex, Guid.GUID.newGuid());
                        NETWORK_MAP.broadcast(request);
                        break;
                    case "ui":
                        UI.startup();
                        break;
                    default:
                        System.err.println("Invalid command. Use the help command to view a list of available commands.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
