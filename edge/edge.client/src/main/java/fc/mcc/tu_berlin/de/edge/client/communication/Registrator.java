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
			this.add(new RegisterMessage(reply));
			MessageHandler.setId(reply);
		}catch(Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

}
