package main;

import networking.Host;

/**
 * Created by Jesse Shellabarger on 4/26/2017.
 */
public class Startup {

    public static void main(String[] args) {
        int localPort = Integer.parseInt(args[0]);
        String networkHost = args[1];
        Host host = new Host(networkHost);


    }
}
