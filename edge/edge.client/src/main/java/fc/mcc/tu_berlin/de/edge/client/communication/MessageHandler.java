package fc.mcc.tu_berlin.de.edge.client.communication;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Fabian Lehmann
 *
 */
public abstract class MessageHandler {

	protected final Queue<Message> messages = new LinkedBlockingQueue<Message>();
	
}
