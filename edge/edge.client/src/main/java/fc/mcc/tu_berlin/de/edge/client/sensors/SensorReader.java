package fc.mcc.tu_berlin.de.edge.client.sensors;

/**
 * That reader reads all data from sensors
 * depending on the devMode value in App it will try to connect to sensors or not
 * @author Fabian Lehmann
 *
 */
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import com.tinkerforge.BrickletHumidity;
import com.tinkerforge.BrickletTemperature;
import com.tinkerforge.BrickletUVLight;
import com.tinkerforge.Device;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import fc.mcc.tu_berlin.de.edge.client.App;

public class SensorReader {
	
	private final Map<Sensor, Device> sensors = new HashMap<Sensor, Device>();
	private final StatusHolder statusHolder;
	
	public SensorReader(List<Sensor> sensors, StatusHolder statusHolder, IPConnection ipcon) {
		
		this.statusHolder = statusHolder;
		
		Device d = null;
		
		for (Sensor sensor : sensors) {
			
			switch (sensor.sensorType){
				
				case HUMIDITY:    d = new BrickletHumidity(sensor.uid, ipcon); break;
				case TEMPERATURE: d = new BrickletTemperature(sensor.uid, ipcon); break;
				case UV:          d = new BrickletUVLight(sensor.uid, ipcon);
				
			}
			
			this.sensors.put(sensor, d);
			
		}
		
	}
	
	public SensorResult read() {
		
		Map<Sensor, Double> results = new HashMap<>();
		
		for (Entry<Sensor, Device> entry : sensors.entrySet()) {
			
			Double value = null;
			
			switch (entry.getKey().sensorType){
			
			case HUMIDITY:    value = readHumidity(entry.getValue()); break;
			case TEMPERATURE: value = readTemperature(entry.getValue()); break;
			case UV:          value = readUV(entry.getValue());
			
			}
			if(value != null)
				results.put(entry.getKey(), value);
			
		}
		SensorResult sr = new SensorResult(results);
		statusHolder.setSensorResult(sr);
		return sr;
	}
	
	Random devModeRan =  new Random();
	
	private int ranHumidity = devModeRan.nextInt(10001);
	private int hum_dead = 0;
	private boolean hum_was_dead = false;
	
	private Double readHumidity(Device d) {
		if(App.devMode) {
			ranHumidity += (devModeRan.nextInt(9) - 4);
			ranHumidity = Math.min(Math.max(ranHumidity, 0), 10000);
			return (double) ranHumidity;
		}else {
			if(hum_dead == 0) {
				try {
					return (double) ((BrickletHumidity) d).getHumidity();
				} catch (TimeoutException | NotConnectedException e) {
					hum_dead++;
					if(!hum_was_dead) {
						e.printStackTrace();
						hum_was_dead = true;
					}
				}
			}else {
				hum_dead = (hum_dead + 1) % 1000;
			}
		}
		return null;
	}
	
	//Up to 35°C
	private int ranTemperature = devModeRan.nextInt(4500) - 10;
	private int temp_dead = 0;
	private boolean temp_was_dead = false;
	
	private Double readTemperature(Device d) {
		if(App.devMode) {
			ranTemperature += (devModeRan.nextInt(3) - 1);
			ranTemperature = Math.min(Math.max(ranTemperature, -100), 350);
			return (double) ranTemperature;
		}else {
			if(temp_dead == 0) {
				try {
					short temp = ((BrickletTemperature) d).getTemperature();
					//if not connected
					if(temp == -9443) return null;
					return (double) temp;
				} catch (TimeoutException | NotConnectedException e) {
					temp_dead++;
					if(!temp_was_dead) {
						e.printStackTrace();
						temp_was_dead = true;
					}
				}
			}else {
				temp_dead = (temp_dead + 1) % 1000;
			}
		}
		return null;
		
	}
	
	private int ranUV = devModeRan.nextInt(1251);
	private int uv_dead = 0;
	private boolean uv_was_dead = false;
	
	private Double readUV(Device d) {
		if(App.devMode) {
			ranUV += (devModeRan.nextInt(5) - 2);
			ranUV = Math.min(Math.max(ranUV, 0), 1250);
			return (double) ranUV;
		}else {
			if(uv_dead == 0) {
				try {
					return (double) ((BrickletUVLight) d).getUVLight();
				} catch (TimeoutException | NotConnectedException e) {
					uv_dead++;
					if(!uv_was_dead) {
						e.printStackTrace();
						uv_was_dead = true;
					}
				}
			}else {
				uv_dead = (uv_dead + 1) % 1000;
			}
			return null;
		}
		
	}

}
