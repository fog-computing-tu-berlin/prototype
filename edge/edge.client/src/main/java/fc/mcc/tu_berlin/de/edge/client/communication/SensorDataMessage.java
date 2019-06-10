package fc.mcc.tu_berlin.de.edge.client.communication;

import fc.mcc.tu_berlin.de.edge.client.sensors.SensorResult;

/**
 * @author Fabian Lehmann
 *
 */
public class SensorDataMessage implements Message {
	
	private final SensorResult sensorResult;
	private long firstTimeStamp;
	private long lastTimeStamp;
	
	public SensorDataMessage(SensorResult sensorResult, long firstTimeStamp, long lastTimeStamp) {
		this.sensorResult = sensorResult;
		this.firstTimeStamp = firstTimeStamp;
		this.lastTimeStamp = lastTimeStamp;
	}
	
	public String toJson(){
		return String.format("{\"sr\":%s,\"ft\":%d,\"lt\":%d}", 
				sensorResult.toJson(),
				firstTimeStamp,
				lastTimeStamp);
	}
	
	@Override
	public String toString() {
		return toJson();
	}

}
