package networking.protocol;

import networking.Host;

import java.util.List;
import java.util.Set;

/**
 * Created by Jesse Shellabarger on 4/26/2017.
 */
public class NetworkMapResponse implements IMessage {

    public Set<Host> networkHosts;

    public NetworkMapResponse() {}

    public NetworkMapResponse(Set<Host> hosts) {
        this.networkHosts = hosts;
    }
}
