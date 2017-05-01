package networking;

import networking.protocol.IMessage;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Jesse Shellabarger on 4/26/2017.
 */
public enum NetworkMap {
    NETWORK_MAP;

    public Map<Host, MessageHandler> hostMap = new Hashtable<>();

    public void addHostToMap(Host host, MessageHandler handler) {
        hostMap.put(host, handler);
    }


    public void broadcast(IMessage message) {
        synchronized (hostMap) {
            hostMap.values().stream().forEach((mh) -> mh.sendMessage(message));
        }
    }
}
