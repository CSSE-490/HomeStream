package networking.handlers;

import networking.ClientHandler;
import networking.protocol.IMessage;

/**
 * Created by Jesse Shellabarger on 4/26/2017.
 */
public interface IMessageEventHandler {

    void handleMessageEvent(IMessage message, ClientHandler clientHandler);
}
