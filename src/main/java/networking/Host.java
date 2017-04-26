package networking;

/**
 * Created by Jesse Shellabarger on 4/26/2017.
 */
public class Host {
    public String hostname;
    public int port;

    public Host() {}

    public Host(String networkHost) {
        String[] args = networkHost.split(":");
        hostname = args[0];
        port = Integer.parseInt(args[1]);
    }

    public Host (String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Host host = (Host) o;

        if (port != host.port) return false;
        return hostname.equals(host.hostname);
    }

    @Override
    public int hashCode() {
        int result = hostname.hashCode();
        result = 31 * result + port;
        return result;
    }
}
