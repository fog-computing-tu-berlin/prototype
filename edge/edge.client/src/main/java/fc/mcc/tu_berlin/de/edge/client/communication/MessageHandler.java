package fc.mcc.tu_berlin.de.edge.client.communication;

import java.io.File;

import fc.mcc.tu_berlin.de.edge.client.communication.message.Message;
import fc.mcc.tu_berlin.de.edge.client.communication.message.MessagePersistor;

/**
 * @author Fabian Lehmann
 *
 */
public abstract class MessageHandler {

//	protected final Queue<Message> messages = new LinkedBlockingQueue<Message>();
	private final MessagePersistor messages;
	
	public MessageHandler(String name) {
		messages = new MessagePersistor("data" + File.separatorChar + name + File.separatorChar);
	}
	
	protected void add(Message message) {
		messages.add(message);
	}

	protected Message poll() {
		return messages.poll();
	}

	protected boolean isEmpty() {
		return messages.isEmpty();
	}

	protected Message peek() {
		return messages.peek();
	}
}
