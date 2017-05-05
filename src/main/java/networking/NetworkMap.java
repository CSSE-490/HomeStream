package networking;

import javafx.beans.property.StringProperty;
import javafx.scene.text.Text;
import networking.protocol.IMessage;
import org.controlsfx.control.StatusBar;

import java.net.Inet4Address;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum NetworkMap {
    NETWORK_MAP;

    private Map<Host, MessageHandler> hostMap = new HashMap<>();
    private StringProperty hostCount;

    public synchronized void addHostToMap(Host host, MessageHandler handler) {
        hostMap.put(host, handler);
        updateCount();
    }

    public synchronized void removeHostFromMap(Host host) {
        hostMap.remove(host);
        updateCount();
    }

    public Set<Host> getHostSet() {
        return hostMap.keySet();
    }

    private synchronized void updateCount() {
        System.out.println("Updating Called");
        if(hostCount != null) {
            System.out.println("Updating Text");
            hostCount.set(String.valueOf(hostMap.size()));
        }
    }

    public void bindCount(StatusBar bar) {
        Text hostText = new Text("0");
        bar.getLeftItems().add(hostText);
        hostCount = hostText.textProperty();

    }

    public void broadcast(IMessage message) {
        synchronized (hostMap) {
            hostMap.values().stream().forEach((mh) -> mh.sendMessage(message));
        }
    }
}
