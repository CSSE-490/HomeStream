package networking.handlers;

import networking.MessageHandler;
import networking.NetworkMap;
import networking.Server;
import networking.protocol.IMessage;
import networking.protocol.IdentificationReceive;
import networking.protocol.IdentificationSend;
import networking.protocol.NetworkMapRequest;

/**
 * Created by Jesse Shellabarger on 4/26/2017.
 */
public class IdentificationMessageHandler implements IMessageEventHandler {

    @Override
    public void handleMessageEvent(IMessage message, MessageHandler messageHandler) {
        if (message instanceof IdentificationReceive) {
            NetworkMap.NETWORK_MAP.addHostToMap(((IdentificationReceive) message).host, messageHandler);
            messageHandler.sendMessage(new NetworkMapRequest());
        } else if (message instanceof IdentificationSend) {
            messageHandler.sendMessage(new IdentificationReceive(Server.host));
            NetworkMap.NETWORK_MAP.addHostToMap(((IdentificationSend) message).host, messageHandler);
            messageHandler.host = ((IdentificationSend) message).host;
        }
    }
}
