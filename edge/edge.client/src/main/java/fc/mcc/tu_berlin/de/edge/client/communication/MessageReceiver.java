package fc.mcc.tu_berlin.de.edge.client.communication;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

import fc.mcc.tu_berlin.de.edge.client.communication.message.CommandMessage;
import fc.mcc.tu_berlin.de.edge.client.communication.message.Message;
import zmq.ZMQ;

/**
 * @author Fabian Lehmann
 *
 */
public class MessageReceiver extends MessageHandler implements Runnable {
	
	private final int PORT;
	
	public MessageReceiver(int PORT, String name) {
		super("receiver_" + name);
		this.PORT = PORT;
		new Thread(this).start();
	}
	
	private Object accessHelper = new Object();
	
	@Override
	public void run() {
		
		try {

			ZContext context = new ZContext();
			Socket subscriber = context.createSocket(SocketType.SUB);
		
	        subscriber.connect("tcp://0.0.0.0:" + PORT);
	        subscriber.subscribe(MessageHandler.getId().getBytes(ZMQ.CHARSET));
	        
	        while (!Thread.interrupted()) {
	            //  Wait for next request from client
	            byte[] request = subscriber.recv(0);
	            String string = new String(request);
	            System.out.println("Received request: ["+string+"].");
	            addToMessages(string);
	
	            subscriber.send("1".getBytes(), 0);
	        }
	        
	        subscriber.close();
	        context.close();
	        
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void addToMessages(String msg) {
		
		synchronized (accessHelper) {
			this.add(CommandMessage.parseMessage(msg));
			accessHelper.notifyAll();
		}
	}
	
	public Message getOldestMessage(long wait) {
		synchronized (accessHelper) {
			if(this.isEmpty()) {
				try {
					accessHelper.wait(wait);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					e.printStackTrace();
				}
			}
			return this.poll();
		}
	}
	
}
