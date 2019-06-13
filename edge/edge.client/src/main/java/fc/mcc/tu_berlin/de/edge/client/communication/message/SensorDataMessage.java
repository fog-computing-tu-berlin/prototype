package fc.mcc.tu_berlin.de.edge.client.communication.message;

import java.util.HashMap;
import java.util.Map;

import fc.mcc.tu_berlin.de.edge.client.sensors.Sensor;
import fc.mcc.tu_berlin.de.edge.client.sensors.SensorResult;
import fc.mcc.tu_berlin.de.edge.client.sensors.SensorType;

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

	public static Message parseMessage(String string) {
		try {
			
			String[] split = string.split(",");
			String name = split[0];
			final Map<Sensor, Double> results = new HashMap<Sensor, Double>();
			for (int i = 1; i < split.length - 2; i++) {
				addToResults(results, split[i]);
			}
			
			long first = Long.parseLong(split[split.length - 2]);
			long second = first + Long.parseLong(split[split.length - 1]);
			
			return new SensorDataMessage(name, new SensorResult(results), first, second);
			
		} catch (Exception e) {
			throw new IllegalArgumentException("Can't parse msg: " + string);
		}
	}
	
	private static void addToResults(Map<Sensor, Double> results, String in) {
		SensorType type = SensorType.values()[in.charAt(0) - '0'];
		String id = in.substring(1, 4);
		double value = Double.parseDouble(in.substring(4));
		results.put(new Sensor(type, id), value);
	}
	
	

}
