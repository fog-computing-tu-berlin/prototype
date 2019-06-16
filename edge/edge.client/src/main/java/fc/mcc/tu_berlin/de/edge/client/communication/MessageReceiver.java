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
	
	private final int port;
	private final String host;
	
	public MessageReceiver(String host, int port, String name) {
		super("receiver_" + name);
		this.port = port;
		this.host = host;
		new Thread(this).start();
	}
	
	private Object accessHelper = new Object();
	
	@Override
	public void run() {
		
		try {

			ZContext context = new ZContext();
			Socket subscriber = context.createSocket(SocketType.SUB);
		
	        subscriber.connect("tcp://" + host + ":" + port);
	        subscriber.subscribe(MessageHandler.getId().getBytes(ZMQ.CHARSET));
	        
	        while (!Thread.interrupted()) {
	            //  Wait for next request from client
	            byte[] request = subscriber.recv(0);
	            String string = new String(request);
	            addToMessages(string);
	        }
	        
	        subscriber.close();
	        context.close();
	        
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void addToMessages(String msg) {
		
		if(msg.startsWith(MessageHandler.getId())) {
			msg = msg.substring(MessageHandler.getId().length() + 1);
		}
		
		synchronized (accessHelper) {
			Message m = CommandMessage.parseMessage(msg);
			if(m != null) this.add(m);
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
