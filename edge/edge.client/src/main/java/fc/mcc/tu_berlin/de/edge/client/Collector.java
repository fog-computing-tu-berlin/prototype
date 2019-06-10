package fc.mcc.tu_berlin.de.edge.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import fc.mcc.tu_berlin.de.edge.client.communication.MessageSender;
import fc.mcc.tu_berlin.de.edge.client.communication.message.SensorDataMessage;
import fc.mcc.tu_berlin.de.edge.client.sensors.Sensor;
import fc.mcc.tu_berlin.de.edge.client.sensors.SensorReader;
import fc.mcc.tu_berlin.de.edge.client.sensors.SensorResult;

/**
 * The collector collects all data of one plant
 * @author Fabian Lehmann
 *
 */
public class Collector implements Runnable {

	private final MessageSender messageSender;
	private final int collectAllMs;
	private final SensorResult[] sensorResults;
	private final SensorReader sr;
	private int index = 0;
	private String name;
	
	public Collector(String name, MessageSender messageSender, int collectAllMs, int sendAllXMeasurements, SensorReader sr) {
		super();
		this.messageSender = messageSender;
		this.collectAllMs = collectAllMs;
		sensorResults = new SensorResult[sendAllXMeasurements];
		this.name = name;
		this.sr = sr;
	}
	
	public SensorResult getCurrentState() {
		return sensorResults[index];
	}

	@Override
	public void run() {

		while(!Thread.interrupted()) {
			
			try {
			
				sensorResults[index++] = sr.read();
				
				if(index == sensorResults.length) {
					
					this.accumulateAndSend();
					index = 0;
					
				}
			
				Thread.sleep(collectAllMs);
				
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	public void accumulateAndSend() {
		Map<Sensor, Double> results = new HashMap<Sensor, Double>();
		
		for (SensorResult sensorResult : sensorResults) {
			Map<Sensor, Double> r = sensorResult.results;
			for (Entry<Sensor, Double> entry : r.entrySet()) {
				Double n = results.get(entry.getKey());
				n = (n == null) ? entry.getValue() : n + entry.getValue();
				results.put(entry.getKey(), n);
			}
		}
		
		for (Entry<Sensor, Double> entry : results.entrySet()) {
			results.put(entry.getKey(), entry.getValue() / sensorResults.length);
		}
		
		SensorDataMessage message = new SensorDataMessage(
				this.name,
				new SensorResult(results), 
				sensorResults[0].createTime, 
				sensorResults[sensorResults.length - 1].createTime) ;
		
		messageSender.send(message);
		
	}
	
	

}
