package fc.mcc.tu_berlin.de.edge.client.sensors;

/**
 * 
 * @author Fabian Lehmann
 *
 */
public class StatusHolder {
	
	SensorResult sensorResult;
	boolean needsWater = false;
	//0: less uv, 1: no cange, 2: more uv
	short uv = 1;
	
	public void setSensorResult(SensorResult sr) {
		this.sensorResult = sr;
	}
	
	public SensorResult getSensorResult() {
		return sensorResult;
	}
	
	public void setNeedsWater(boolean needsWater) {
		this.needsWater = needsWater;
	}
	
	public boolean needsWater() {
		return needsWater;
	}
	
	public void setUv(short uv) {
		this.uv = uv;
	}
	
	public short getUv() {
		return uv;
	}
	

}
