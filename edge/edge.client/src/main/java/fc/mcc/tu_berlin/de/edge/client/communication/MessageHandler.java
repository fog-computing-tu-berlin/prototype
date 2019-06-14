package fc.mcc.tu_berlin.de.edge.client.communication;

import java.io.File;

import fc.mcc.tu_berlin.de.edge.client.communication.message.Message;
import fc.mcc.tu_berlin.de.edge.client.communication.message.MessagePersistor;

/**
 * @author Fabian Lehmann
 *
 */
public abstract class MessageHandler {
	
	public static final int SERVER_REQ_PORT = 5555;

	private static String id;
	
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
	
	private static Object id_helper = new Object();
	
	public static void setId(String id) {
		synchronized (id_helper) {
			if(id == null) {
				MessageHandler.id = id;
				id_helper.notifyAll();
			}
		}
	}
	
	public static String getId() {
		synchronized (id_helper) {
			while(id == null) {
				try {
					id_helper.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return id;
		}
	}
}
