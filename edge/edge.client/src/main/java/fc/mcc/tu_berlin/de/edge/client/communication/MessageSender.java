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
	
	public MessageSender(String host, int port) {
		this.host = host;
		this.port = port;
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		while(!Thread.interrupted()) {
			try (ZContext context = new ZContext()) {
				//  Socket to talk to server
				Socket requester = context.createSocket(SocketType.REQ);
				requester.connect("tcp://"+ host + ":" + port);
				
				Message message = messages.poll();
				
				if(message != null) {
					requester.send(message.toJson(), 0);
					String reply = requester.recvStr(0);
					System.out.println(
							"Received reply [" + reply + "]"
							);
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}
	
	public void send(Message message){
		this.messages.add(message);
	}

}
