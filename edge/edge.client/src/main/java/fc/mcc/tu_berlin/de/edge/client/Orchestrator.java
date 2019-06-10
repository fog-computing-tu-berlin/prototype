package fc.mcc.tu_berlin.de.edge.client;

import java.util.List;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NetworkException;

import fc.mcc.tu_berlin.de.edge.client.communication.MessageReceiver;
import fc.mcc.tu_berlin.de.edge.client.communication.MessageSender;
import fc.mcc.tu_berlin.de.edge.client.sensors.Sensor;
import fc.mcc.tu_berlin.de.edge.client.sensors.SensorReader;
import fc.mcc.tu_berlin.de.edge.client.sensors.StatusHolder;

/**
 * An orchestrator will orchestrate one plant.
 * @author Fabian Lehmann
 *
 */
public class Orchestrator {

	public void work(String name, String button_ID, List<Sensor> sensors, String serverHost) {
		
		IPConnection ipcon = new IPConnection();
		
		if(!App.devMode) {
			try {
				ipcon.connect("localhost", 4223);
			} catch (NetworkException | AlreadyConnectedException e) {
				e.printStackTrace();
				return;
			}
		}
		
		StatusHolder statusHolder = new StatusHolder();
		SensorReader sr = new SensorReader(sensors, statusHolder, ipcon);
		

		Collector collector = new Collector(name, new MessageSender(serverHost, 5555), 50, 50, sr);
		Thread collectorThread = new Thread(collector);
		collectorThread.start();
		
		Feedback feedback = new Feedback(new MessageReceiver(5556), statusHolder, button_ID, ipcon, 10000);
		Thread feedbackThread = new Thread(feedback);
		feedbackThread.start();
		
	}
	
}
