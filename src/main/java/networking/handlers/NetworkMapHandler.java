package networking.handlers;

import networking.Host;
import networking.ClientHandler;
import networking.NetworkMap;
import networking.Server;
import networking.protocol.IMessage;
import networking.protocol.NetworkMapRequest;
import networking.protocol.NetworkMapResponse;

import java.io.IOException;

public class NetworkMapHandler implements IMessageEventHandler {

    @Override
    public void handleMessageEvent(IMessage message, ClientHandler clientHandler) {
        if (message instanceof NetworkMapRequest){
            System.out.println("Received request for NetworkMap update from " + clientHandler.host);
            clientHandler.sendMessage(new NetworkMapResponse(NetworkMap.NETWORK_MAP.getHostSet()));
        } else if (message instanceof NetworkMapResponse) {
            System.out.println("Received NetworkMap from " + clientHandler.host);
            NetworkMapResponse response = ((NetworkMapResponse) message);
            for (Host h : response.networkHosts) {
                if (NetworkMap.NETWORK_MAP.getHostSet().contains(h) || h.equals(Server.host)) continue;
                try {
                    System.out.println("Connecting to new host: " + h.toString());
                    ClientHandler handler = new ClientHandler(h);
                    handler.identify();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
