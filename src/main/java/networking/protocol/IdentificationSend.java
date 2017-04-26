package networking.protocol;

import networking.Host;

/**
 * Created by Jesse Shellabarger on 4/26/2017.
 */
public class IdentificationSend implements IMessage {
    public Host host;

    public IdentificationSend() {}

    public IdentificationSend(Host host) {
        this.host = host;
    }
}
