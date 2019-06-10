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
			
			results.put(entry.getKey(), value);
			
		}
		SensorResult sr = new SensorResult(results);
		statusHolder.setSensorResult(sr);
		return sr;
	}
	
	Random devModeRan =  new Random();
	
	private int ranHumidity = devModeRan.nextInt(1001);
	
	private Double readHumidity(Device d) {
		if(App.devMode) {
			ranHumidity += (devModeRan.nextInt(9) - 4);
			ranHumidity = Math.min(Math.max(ranHumidity, 0), 1000);
			return (double) ranHumidity;
		}else {
			try {
				return (double) ((BrickletHumidity) d).getHumidity();
			} catch (TimeoutException | NotConnectedException e) {
				e.printStackTrace();
				return null;
			}
		}
		
	}
	
	//Up to 35°C
	private int ranTemperature = devModeRan.nextInt(4500) - 10;
	
	private Double readTemperature(Device d) {
		if(App.devMode) {
			ranTemperature += (devModeRan.nextInt(3) - 1);
			ranTemperature = Math.min(Math.max(ranTemperature, -10), 35);
			return (double) ranTemperature;
		}else {
			try {
				return (double) ((BrickletTemperature) d).getTemperature();
			} catch (TimeoutException | NotConnectedException e) {
				e.printStackTrace();
				return null;
			}
		}
		
	}
	
	private int ranUV = devModeRan.nextInt(1251);
	
	private Double readUV(Device d) {
		if(App.devMode) {
			ranUV += (devModeRan.nextInt(5) - 2);
			ranUV = Math.min(Math.max(ranUV, 0), 1250);
			return (double) ranUV;
		}else {
			try {
				return (double) ((BrickletUVLight) d).getUVLight();
			} catch (TimeoutException | NotConnectedException e) {
				e.printStackTrace();
				return null;
			}
		}
		
	}

}
