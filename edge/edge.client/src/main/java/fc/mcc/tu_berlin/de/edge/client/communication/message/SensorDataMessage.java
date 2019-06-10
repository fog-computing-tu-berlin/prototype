package fc.mcc.tu_berlin.de.edge.client.communication.message;

import fc.mcc.tu_berlin.de.edge.client.sensors.SensorResult;

/**
 * @author Fabian Lehmann
 *
 */
public class SensorDataMessage implements Message {
	
	private final SensorResult sensorResult;
	private long firstTimeStamp;
	private long lastTimeStamp;
	private String name;
	
	public SensorDataMessage(String name, SensorResult sensorResult, long firstTimeStamp, long lastTimeStamp) {
		this.sensorResult = sensorResult;
		this.firstTimeStamp = firstTimeStamp;
		this.lastTimeStamp = lastTimeStamp;
		this.name = name;
		
	}
	
	public String toJson(){
		return String.format("{\"n\":\"%s\",\"m\":{\"sr\":%s,\"ft\":%d,\"lt\":%d}}", 
				this.name,
				sensorResult.toJson(),
				firstTimeStamp,
				lastTimeStamp);
	}
	
	@Override
	public String toString() {
		return toJson();
	}

	@Override
	public String toShortMessage() {
		return String.format("%s,%s,%d,%d", 
				this.name,
				sensorResult.ultraShortString(),
				firstTimeStamp,
				lastTimeStamp - firstTimeStamp);
	}

}
