package fc.mcc.tu_berlin.de.edge.client.communication.message;

/**
 * 
 * @author Fabian Lehmann
 *
 */
public class RegisterMessage implements Message{
	
	public final String id;

	public RegisterMessage(String id) {
		super();
		this.id = id;
	}

	@Override
	public String toJson() {
		return String.format("{\"id\":\"%s\"}",  id);
	}

	@Override
	public String toShortMessage() {
		return id;
	}

	@Override
	public MessageTypes getType() {
		return MessageTypes.REGISTER_MESSAGE;
	}

	public static Message parseMessage(String string) {
		return new RegisterMessage(string);
	}
	
}
