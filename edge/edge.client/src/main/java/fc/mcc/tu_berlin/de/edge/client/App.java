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
	
	public static boolean devMode = false;

	/**
	 * Call this method with your plant name, button uid and sensors as tuple
	 * For example: 192.168.99.100 myplant but (t,abc) (h,cde) (u,fgh)
	 * to run a TemperatureSensor with id abc, Humidity with cde and UV with id fgh
	 * @param args
	 */
	public static void main(String[] args) {
		
		if(args.length < 5) {
			System.out.println("At least 4 elements required.");
			System.exit(1091283);
		}
		
		if(args[args.length - 1].toLowerCase().equals("dev")) {
			devMode = true;
			System.out.println("DEV MODE");
		}
		
		Orchestrator orchestrator = new Orchestrator();
		String[] sensors = Arrays.copyOfRange(args, 3, devMode ? args.length - 1 : args.length);
		orchestrator.work(args[1], args[2], parseArgsToSensors(sensors), args[0]);
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
				
			} else throw new IllegalArgumentException("Wrong declaration for sensor tuple");
			
		}
		System.out.println("Found " + sensors.size() + " sensors!");
		
		return sensors;
	}
}
