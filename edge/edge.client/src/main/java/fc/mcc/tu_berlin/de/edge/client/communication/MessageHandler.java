package fc.mcc.tu_berlin.de.edge.client.communication;

import java.io.File;

import fc.mcc.tu_berlin.de.edge.client.communication.message.Message;
import fc.mcc.tu_berlin.de.edge.client.communication.message.MessagePersistor;

/**
 * @author Fabian Lehmann
 *
 */
public abstract class MessageHandler {
	
	private static String id = null;
	
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
			if(MessageHandler.id == null) {
				System.out.println("Got id: " + id);
				MessageHandler.id = id;
				id_helper.notifyAll();
			}
		}
	}
	
	public static String getId() {
		synchronized (id_helper) {
			while(id == null) {
				System.out.println("Wait for ID");
				try {
					id_helper.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("Received ID");
			}
			return id;
		}
	}
}
