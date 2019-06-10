package fc.mcc.tu_berlin.de.edge.client.communication;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

/**
 * @author Fabian Lehmann
 *
 */
public class MessageSender extends MessageHandler implements Runnable {
	
	private final String host;
	private final int port;
	private final Object addHelper = new Object();
	
	public MessageSender(String host, int port) {
		this.host = host;
		this.port = port;
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		try (ZContext context = new ZContext()) {
			
			Socket requester = context.createSocket(SocketType.REQ);
			requester.connect("tcp://"+ host + ":" + port);
			
			while(true) {
				
				while(!messages.isEmpty() && !Thread.interrupted()) {
					Message message = messages.poll();
					
					if(message != null) {
						requester.send(message.toJson(), 0);
						String reply = requester.recvStr(0);
						System.out.println(
								"Received reply [" + reply + "]"
								);
					}
					
				}
				
				try {
					synchronized (addHelper) {
						addHelper.wait();
					}
				} catch (InterruptedException e1) {
					e1.printStackTrace();
					Thread.currentThread().interrupt();
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void send(Message message){
		this.messages.add(message);
		synchronized (addHelper) {
			addHelper.notifyAll();
		}
	}

}
