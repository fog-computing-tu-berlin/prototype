package fc.mcc.tu_berlin.de.edge.client.sensors;

/**
 * @author Fabian Lehmann
 *
 */
public enum SensorType {
	
	HUMIDITY("humidity", "percent"), TEMPERATURE("temperature", "degree celsius"), UV("ultraviolet", "milliwatt");
	
	public final String name;
	public final String unit;
	
	private SensorType(String name, String unit) {
		this.name = name;
		this.unit = unit;
	}

}