package fc.mcc.tu_berlin.de.edge.client.communication;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import fc.mcc.tu_berlin.de.edge.client.communication.message.CommandMessage;
import fc.mcc.tu_berlin.de.edge.client.communication.message.Message;

/**
 * @author Fabian Lehmann
 *
 */
public class MessageReceiver extends MessageHandler implements Runnable {
	
	private final int PORT;
	
	public MessageReceiver(int PORT) {
		this.PORT = PORT;
		new Thread(this).start();
	}
	
	private Object accessHelper = new Object();
	
	@Override
	public void run() {
		
		try {

			ZContext context = new ZContext();
			Socket responder = context.createSocket(SocketType.REP);
		
	        responder.connect("tcp://0.0.0.0:" + PORT);
	        
	        while (!Thread.interrupted()) {
	            //  Wait for next request from client
	            byte[] request = responder.recv(0);
	            String string = new String(request);
	            System.out.println("Received request: ["+string+"].");
	            addToMessages(string);
	
	            responder.send("1".getBytes(), 0);
	        }
	        
	        responder.close();
	        context.close();
	        
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void addToMessages(String msg) {
		
		boolean needsWater = false;
		short uv;
		try {
			short s = Short.parseShort(msg);
			if(s >= 0 && s <= 5) {
				if(s >= 3) {
					needsWater = true;
					s = (short) (s - 3);
				}
				uv = s;
			}else {
				throw new IllegalArgumentException();
			}
		}catch (Exception e) {
			System.out.println("Can't parse msg: " + msg); return;
		}
		
		synchronized (accessHelper) {
			this.messages.add(new CommandMessage(needsWater, uv));
			accessHelper.notifyAll();
		}
	}
	
	public Message getOldestMessage(long wait) {
		synchronized (accessHelper) {
			if(messages.isEmpty()) {
				try {
					accessHelper.wait(wait);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					e.printStackTrace();
				}
			}
			return this.messages.poll();
		}
	}
	
}
