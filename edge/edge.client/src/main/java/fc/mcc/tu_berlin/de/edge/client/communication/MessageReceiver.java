package fc.mcc.tu_berlin.de.edge.client.communication;

/**
 * @author Fabian Lehmann
 *
 */
public class MessageReceiver extends MessageHandler implements Runnable {
	
	public MessageReceiver() {
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		//Write to Queue
	}
	
	public boolean hasMessage() {
		
		return !this.messages.isEmpty();
		
	}
	
	public Message getOldestMessage() {
		return this.messages.poll();
	}
	
}
