package networking.handlers;

import networking.Host;
import networking.MessageHandler;
import networking.NetworkMap;
import networking.Server;
import networking.protocol.IMessage;
import networking.protocol.NetworkMapRequest;
import networking.protocol.NetworkMapResponse;

import java.io.IOException;

/**
 * Created by Jesse Shellabarger on 4/26/2017.
 */
public class NetworkMapHandler implements IMessageEventHandler {

    @Override
    public void handleMessageEvent(IMessage message, MessageHandler messageHandler) {
        if (message instanceof NetworkMapRequest){
            System.out.println("Received request for NetworkMap update from " + messageHandler.host);
            messageHandler.sendMessage(new NetworkMapResponse(NetworkMap.NETWORK_MAP.hostMap.keySet()));
        } else if (message instanceof NetworkMapResponse) {
            System.out.println("Received NetworkMap from " + messageHandler.host);
            NetworkMapResponse response = ((NetworkMapResponse) message);
            for (Host h : response.networkHosts) {
                if (NetworkMap.NETWORK_MAP.hostMap.containsKey(h) || h.equals(new Host(Server.hostname, Server.port))) continue;
                try {
                    MessageHandler handler = new MessageHandler(h);
                    handler.identify();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
