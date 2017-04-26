package networking.protocol;


import networking.Host;

public class IdentificationReceive implements IMessage {
    public Host host;

    public IdentificationReceive() {}

    public IdentificationReceive(String hostname, int port) {
        this.host = new Host(hostname, port);
    }
}
