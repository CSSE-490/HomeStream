package networking.handlers;

import networking.ClientHandler;
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
    public void handleMessageEvent(IMessage message, ClientHandler clientHandler) {
        if (message instanceof IdentificationReceive) {
            NetworkMap.NETWORK_MAP.addHostToMap(((IdentificationReceive) message).host, clientHandler);
            clientHandler.host = ((IdentificationReceive) message).host;
            clientHandler.sendMessage(new NetworkMapRequest());
        } else if (message instanceof IdentificationSend) {
            clientHandler.host = ((IdentificationSend) message).host;
            clientHandler.sendMessage(new IdentificationReceive(Server.host));
            NetworkMap.NETWORK_MAP.addHostToMap(((IdentificationSend) message).host, clientHandler);
        }
    }
}
