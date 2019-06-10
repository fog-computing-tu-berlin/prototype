package fc.mcc.tu_berlin.de.edge.client;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import fc.mcc.tu_berlin.de.edge.client.sensors.Sensor;
import fc.mcc.tu_berlin.de.edge.client.sensors.SensorType;

/**
 * @author Fabian Lehmann
 *
 */
public class App {
	
	public static final boolean devMode = true;

	/**
	 * Call this method with your sensors as tuple
	 * For example: (t,abc) (h,cde) (u,fgh)
	 * to run a TemperatureSensor with id abc, Humidity with cde and UV with id fgh
	 * @param args
	 */
	public static void main(String[] args) {
		
		Orchestrator orchestrator = new Orchestrator();
		String[] sensors = Arrays.copyOfRange(args, 1, args.length);
		orchestrator.work(args[0], parseArgsToSensors(sensors));
	}
	
	private static List<Sensor> parseArgsToSensors(String[] args) {
		List<Sensor> sensors = new LinkedList<Sensor>();
		
		for (String s : args) {
			
			if(s.length() == 7 && s.startsWith("(") && s.endsWith(")") && s.contains(",") && s.indexOf(',') == s.lastIndexOf(',')){
				
				s = s.substring(1, s.length() - 1);
				String[] sa = s.split(",");
				
				SensorType st;
				
				switch (sa[0].toLowerCase()) {
					
				case "h": st = SensorType.HUMIDITY; break;
				case "t": st = SensorType.TEMPERATURE; break;
				case "u": st = SensorType.UV; break;
				default: throw new IllegalArgumentException(sa[0] + " is not a valid sensor type!");
				
				}
				
				sensors.add(new Sensor(st, sa[1]));
				
			}else throw new IllegalArgumentException("Wrong declaration for sensor tuple");
			
		}
		
		return sensors;
		
		
	}

}
