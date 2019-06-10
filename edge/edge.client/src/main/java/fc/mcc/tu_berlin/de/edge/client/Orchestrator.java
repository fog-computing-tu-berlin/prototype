package fc.mcc.tu_berlin.de.edge.client;

import java.util.List;

import fc.mcc.tu_berlin.de.edge.client.communication.MessageReceiver;
import fc.mcc.tu_berlin.de.edge.client.communication.MessageSender;
import fc.mcc.tu_berlin.de.edge.client.sensors.Sensor;
import fc.mcc.tu_berlin.de.edge.client.sensors.SensorReader;

/**
 * An orchestrator will orchestrate one plant.
 * @author Fabian Lehmann
 *
 */
public class Orchestrator {

	public void work(String name, List<Sensor> sensors) {
		
		SensorReader sr = new SensorReader(sensors, "localhost", 4223);

		Collector collector = new Collector(name, new MessageSender("192.168.99.100", 5555), 50, 50, sr);
		Thread collectorThread = new Thread(collector);
		collectorThread.start();
		
		Feedback feedback = new Feedback(new MessageReceiver(), collector, 100,10000);
		Thread feedbackThread = new Thread(feedback);
		feedbackThread.start();
		
	}
	
}
