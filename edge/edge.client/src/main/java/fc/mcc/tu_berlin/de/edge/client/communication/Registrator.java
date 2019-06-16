package fc.mcc.tu_berlin.de.edge.client.communication;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

import fc.mcc.tu_berlin.de.edge.client.communication.message.Message;
import fc.mcc.tu_berlin.de.edge.client.communication.message.RegisterMessage;

/**
 * 
 * @author Fabian Lehmann
 *
 */
public class Registrator extends MessageHandler implements Runnable{


	private final int port;
	private final String host;
	private final String name;

	public Registrator(String host, int port, String name) {
		super("registration_" + name);
		this.port = port;
		this.host = host;
		this.name = name;
	}

	@Override
	public void run() {
		Message m = this.peek();
		if(m != null && m instanceof RegisterMessage) {
			MessageHandler.setId(((RegisterMessage) m).id);
			return;
		}else {
			while(m != null ) {
				m = this.poll();
			}
		}
		
		try (ZContext context = new ZContext()) {
			Socket requester = context.createSocket(SocketType.REQ);
			requester.connect("tcp://"+ host + ":" + port);
			requester.send(name, 0);
			String reply = requester.recvStr(0);
			if(checkIDValid(reply)) {
				this.add(new RegisterMessage(reply));
				MessageHandler.setId(reply);
			}else {
				System.out.println("Can't receive correct ID");
				System.out.println("Received: " + reply);
				System.exit(500);
			}
		}catch(Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	boolean checkIDValid(String input) {
		for (int i = 0; i < input.length(); i++) {
			if(input.charAt(i) < '0' || input.charAt(i) > '9') return false;
		}
		return true;
	}

}
