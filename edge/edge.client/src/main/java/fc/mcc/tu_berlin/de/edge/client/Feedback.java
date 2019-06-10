package fc.mcc.tu_berlin.de.edge.client;

import fc.mcc.tu_berlin.de.edge.client.communication.MessageReceiver;

/**
 * That class returns feedback for one plant
 * @author Fabian Lehmann
 *
 */
public class Feedback implements Runnable {

	private final MessageReceiver messageReceiver;
	private final int checkAllMs;
	private final Collector collector;
	private final int waitTime;
	
	public Feedback(MessageReceiver messageReceiver, Collector collector, int checkAllMs, int waitTime) {
		super();
		this.collector = collector;
		this.messageReceiver = messageReceiver;
		this.checkAllMs = checkAllMs;
		this.waitTime = waitTime;
	}

	@Override
	public void run() {

		while(!Thread.interrupted()) {
			
			try {
				
				
				Thread.sleep(checkAllMs);
				
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
}
