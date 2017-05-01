package networking.protocol;


import networking.Host;

public class IdentificationReceive implements IMessage {
    public Host host;

    public IdentificationReceive() {}

    public IdentificationReceive(Host host) {
        this.host = host;
    }
}
