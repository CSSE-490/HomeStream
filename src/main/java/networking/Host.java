package networking;

/**
 * Created by Jesse Shellabarger on 4/26/2017.
 */
public class Host {
    public String hostname;
    public int port;

    public Host(String networkHost) {
        String[] args = networkHost.split(":");
        hostname = args[0];
        port = Integer.parseInt(args[1]);
    }

    public Host (String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }
}
