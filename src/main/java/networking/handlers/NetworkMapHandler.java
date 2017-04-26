package networking.handlers;

import networking.Host;
import networking.MessageHandler;
import networking.NetworkMap;
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
            messageHandler.sendMessage(new NetworkMapResponse(NetworkMap.NETWORK_MAP.hostMap.keySet()));
        } else if (message instanceof NetworkMapResponse) {
            NetworkMapResponse response = ((NetworkMapResponse) message);
            for (Host h : response.networkHosts) {
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
