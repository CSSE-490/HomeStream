package networking.handlers;

import networking.*;
import networking.protocol.FoundFile;
import networking.protocol.IMessage;
import networking.protocol.SearchCommandRequest;
import networking.protocol.SearchCommandResponse;

import java.util.List;

public class SearchCommandHandler implements IMessageEventHandler {

    @Override
    public void handleMessageEvent(IMessage message, ClientHandler clientHandler) {
        if(message instanceof SearchCommandResponse) {
            SearchResultHelper.searchResultReceived((SearchCommandResponse) message);
        } else if(message instanceof SearchCommandRequest) {
            List<FoundFile> files = SearchDirectoryHelper.getFiles(((SearchCommandRequest) message).fileNameRegex);

            clientHandler.sendMessage(new SearchCommandResponse(files, Server.host, FileServer.host, ((SearchCommandRequest) message).uniqueIdentifier));
        }
    }
}
