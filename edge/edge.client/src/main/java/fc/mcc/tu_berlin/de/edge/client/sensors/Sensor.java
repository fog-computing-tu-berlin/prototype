package fc.mcc.tu_berlin.de.edge.client.sensors;

/**
 * @author Fabian Lehmann
 *
 */
public class Sensor {
	
	public final SensorType sensorType;
	public final String uid;
	
	public Sensor(SensorType sensorType, String uid) {
		super();
		this.sensorType = sensorType;
		this.uid = uid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sensorType == null) ? 0 : sensorType.hashCode());
		result = prime * result + ((uid == null) ? 0 : uid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sensor other = (Sensor) obj;
		if (sensorType != other.sensorType)
			return false;
		if (uid == null) {
			if (other.uid != null)
				return false;
		} else if (!uid.equals(other.uid))
			return false;
		return true;
	}
	
}
