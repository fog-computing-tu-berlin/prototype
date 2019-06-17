package fc.mcc.tu_berlin.de.edge.client.communication.message;

/**
 * @author Fabian Lehmann
 *
 */
public interface Message {
	
	public String toJson();
	
	public String toShortMessage();
	
	public MessageTypes getType();
	
	static Message parseMessage(String in) {
		try {
			String[] parts = in.split(";");
			MessageTypes type = MessageTypes.values()[Integer.parseInt(parts[0])];
			switch (type){
				case COMMAND_MESSAGE: return CommandMessage.parseMessage(parts[1]);
				case SENSOR_DATA_MESSAGE: return SensorDataMessage.parseMessage(in.substring(parts[0].length() + 1));
				case REGISTER_MESSAGE: return RegisterMessage.parseMessage(parts[1]);
				default: throw new IllegalArgumentException("no return for " + type);
			}
		}catch(Exception e) {
			System.out.println("Can't parse " + in);
			return null;
		}
	}
	
	public enum MessageTypes {
		
		COMMAND_MESSAGE, SENSOR_DATA_MESSAGE, REGISTER_MESSAGE;
		
	}
	
}
