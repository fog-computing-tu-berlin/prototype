package fc.mcc.tu_berlin.de.edge.client.communication.message;

/**
 * 
 * @author Fabian Lehmann
 *
 */
public class CommandMessage implements Message {
	
	public final boolean needsWater;
	//0: less uv, 1: no cange, 2: more uv
	public final short uv;
	
	public CommandMessage(boolean needsWater, short uv) {
		super();
		this.needsWater = needsWater;
		this.uv = uv;
	}

	@Override
	public String toJson() {
		return String.format("{\"w\":%d,\"u\":%d}", needsWater ? 1 : 0, uv);
	}
	
	@Override
	public String toString() {
		return (needsWater ? 1 : 0) + "" + uv;
	}

	/**
	 * 0 = 00
	 * 1 = 01
	 * 2 = 02
	 * 3 = 10
	 * 4 = 11
	 * 5 = 12
	 */
	@Override
	public String toShortMessage() {
		return "" + (needsWater ? 3 : 0) + uv;
	}
	
}
