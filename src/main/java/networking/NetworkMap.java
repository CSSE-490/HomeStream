package networking;

import com.sun.glass.ui.Application;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import networking.protocol.IMessage;
import org.controlsfx.control.StatusBar;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum NetworkMap {
    NETWORK_MAP;

    private Map<Host, ClientHandler> hostMap = new HashMap<>();
    private StringProperty hostCount;

    public synchronized void addHostToMap(Host host, ClientHandler handler) {
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
        if(hostCount != null) {
            Application.invokeAndWait(() -> hostCount.set(String.valueOf(hostMap.size())));
        }
    }

    public void bindCount(Label label) {
        hostCount = label.textProperty();
        hostCount.set(String.valueOf(hostMap.size()));
    }

    public void broadcast(IMessage message) {
        synchronized (hostMap) {
            hostMap.values().stream().forEach((mh) -> mh.sendMessage(message));
        }
    }
}
