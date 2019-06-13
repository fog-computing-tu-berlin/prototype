package fc.mcc.tu_berlin.de.edge.client.communication;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

import fc.mcc.tu_berlin.de.edge.client.communication.message.Message;

/**
 * @author Fabian Lehmann
 *
 */
public class MessageSender extends MessageHandler implements Runnable {
	
	private final String host;
	private final int port;
	private final Object addHelper = new Object();
	
	public MessageSender(String host, int port, String name) {
		super("sender_" + name);
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
				
				while(!this.isEmpty() && !Thread.interrupted()) {
					Message message = this.peek();
					
					if(message != null) {
//						System.out.println(message.toJson());
//						System.out.println("Send message: [" + message.toShortMessage() + "]");
						requester.send(message.toShortMessage(), 0);
						String reply = requester.recvStr(0);
						if(!reply.equals("1")) {
							System.out.println(
									"Received reply [" + reply + "]"
									);
						}else {
							//Remove if message was successfully transmitted.
							this.poll();
						}
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
		this.add(message);
		synchronized (addHelper) {
			addHelper.notifyAll();
		}
	}

}
