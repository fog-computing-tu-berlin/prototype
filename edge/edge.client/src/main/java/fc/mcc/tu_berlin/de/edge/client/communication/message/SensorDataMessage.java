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
		return String.format("%s,%d,%d,%s", 
				this.name,
				firstTimeStamp,
				lastTimeStamp - firstTimeStamp,
				sensorResult.ultraShortString());
	}

	public static Message parseMessage(String string) {
		try {
			String[] split = string.split(",");
			String name = split[0];
			long first = Long.parseLong(split[1]);
			long second = first + Long.parseLong(split[2]);
			
			
			final Map<Sensor, Double> results = new HashMap<Sensor, Double>();
			for (int i = 3; i < split.length; i++) {
				addToResults(results, split[i]);
			}
			
			
			return new SensorDataMessage(name, new SensorResult(results), first, second);
			
		} catch (Exception e) {
			throw new IllegalArgumentException("Can't parse msg: " + string);
		}
	}
	
	private static void addToResults(Map<Sensor, Double> results, String in) {
		String[] strings = in.split(";");
		SensorType type = SensorType.values()[Integer.parseInt(strings[0])];
		String id = strings[1];
		double value = Double.parseDouble(strings[2]);
		results.put(new Sensor(type, id), value);
	}

	@Override
	public MessageTypes getType() {
		return MessageTypes.SENSOR_DATA_MESSAGE;
	}
	
	

}
