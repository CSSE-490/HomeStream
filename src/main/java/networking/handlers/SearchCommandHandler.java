package networking.handlers;

import networking.MessageHandler;
import networking.SearchDirectoryHelper;
import networking.SearchResultHelper;
import networking.Server;
import networking.protocol.FoundFile;
import networking.protocol.IMessage;
import networking.protocol.SearchCommandRequest;
import networking.protocol.SearchCommandResponse;

import java.util.List;

public class SearchCommandHandler implements IMessageEventHandler {

    @Override
    public void handleMessageEvent(IMessage message, MessageHandler messageHandler) {
        if(message instanceof SearchCommandResponse) {
            SearchResultHelper.searchResultReceived((SearchCommandResponse) message);
        } else if(message instanceof SearchCommandRequest) {
            List<FoundFile> files = SearchDirectoryHelper.getFiles(((SearchCommandRequest) message).fileNameRegex);

            messageHandler.sendMessage(new SearchCommandResponse(files, Server.host, ((SearchCommandRequest) message).uniqueIdentifier));
        }
    }
}
