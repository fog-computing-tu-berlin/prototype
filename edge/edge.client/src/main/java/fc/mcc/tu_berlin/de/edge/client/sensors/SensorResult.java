package fc.mcc.tu_berlin.de.edge.client.sensors;

/**
 * @author Fabian Lehmann
 *
 */
import java.util.Map;
import java.util.stream.Collectors;

public class SensorResult {

	public final Map<Sensor, Double> results;
	
	public final long createTime;

	public SensorResult(Map<Sensor, Double> results) {
		super();
		this.results = results;
		this.createTime = System.currentTimeMillis();
	}
	
	@Override
	public String toString() {
		return toJson();
	}
	
	public String toJson() {
		return stringCreator("{\"st\":%d,\"id\":\"%s\",\"v\":%s}");
	}
	
	public String ultraShortString() {
		return stringCreator("%d%s%s");
	}
	
	//Creates an int out of the double, to have smaller messages
	private String stringCreator(String formatString) {
		return 
				results.entrySet().stream().map(x -> 
					String.format(formatString,
						x.getKey().sensorType.ordinal(),
						x.getKey().uid,
						x.getValue().intValue())).collect(Collectors.joining(","));
	}
	
}
