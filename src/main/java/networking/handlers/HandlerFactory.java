package networking.handlers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jesse Shellabarger on 4/26/2017.
 */
public class HandlerFactory {

    static private List<IMessageEventHandler> defaultHandlers;

    static {
       defaultHandlers = new ArrayList<>();
       defaultHandlers.add(new IdentificationMessageHandler());
       defaultHandlers.add(new NetworkMapHandler());
       defaultHandlers.add(new FileRequestHandler());
       defaultHandlers.add(new SearchCommandHandler());
    }

    public static List<IMessageEventHandler> getdefaultHandlers() {
        return defaultHandlers;
    }
}
