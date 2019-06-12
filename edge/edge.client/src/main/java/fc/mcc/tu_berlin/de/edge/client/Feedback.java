package fc.mcc.tu_berlin.de.edge.client;

import java.util.Optional;

import com.tinkerforge.IPConnection;

import fc.mcc.tu_berlin.de.edge.client.communication.MessageReceiver;
import fc.mcc.tu_berlin.de.edge.client.communication.message.CommandMessage;
import fc.mcc.tu_berlin.de.edge.client.communication.message.Message;
import fc.mcc.tu_berlin.de.edge.client.sensors.ButtonConnector;
import fc.mcc.tu_berlin.de.edge.client.sensors.SensorResult;
import fc.mcc.tu_berlin.de.edge.client.sensors.SensorType;
import fc.mcc.tu_berlin.de.edge.client.sensors.StatusHolder;

/**
 * That class returns feedback for one plant
 * @author Fabian Lehmann
 *
 */
public class Feedback implements Runnable {

	private final MessageReceiver messageReceiver;
	private final int checkAllMs;
	private final StatusHolder statusHolder;
	private final ButtonConnector buttonConnector;
	private boolean local = false;
	
	public Feedback(MessageReceiver messageReceiver, StatusHolder statusHolder, String button_ID, IPConnection ipcon, int checkAllMs) {
		this.messageReceiver = messageReceiver;
		this.statusHolder = statusHolder;
		this.checkAllMs = checkAllMs;
		this.buttonConnector = new ButtonConnector(button_ID, ipcon, statusHolder);
	}

	@Override
	public void run() {

		while(!Thread.interrupted()) {
			
			Message m = messageReceiver.getOldestMessage(checkAllMs);
			if(m == null) {
				selfCalculation();
			}else {
				processMessage(m);
			}
			statusToButton();
			
		}
		
	}
	
	private void statusToButton() {
		
		if(statusHolder.needsWater()) {
			if(local) {
				buttonConnector.needsWaterLocal();
			}else {
				buttonConnector.needsWater();
			}
		}else {
			buttonConnector.needNoWater();
		}
		
		switch (statusHolder.getUv()) {
		
		case 0: buttonConnector.lessSun(); break;
		case 1: buttonConnector.noSunChange(); break;
		case 2: buttonConnector.moreSun(); break;
		
		}
	}
	
	private void selfCalculation() {
		System.out.println("Local Calculation");
		SensorResult sr = statusHolder.getSensorResult();
		Optional<Double> humidity = sr.results.entrySet()
		.stream()
		.filter(x -> x.getKey().sensorType == SensorType.HUMIDITY)
		.map(x -> x.getValue())
		.findFirst();
		if(humidity.isPresent()) {
			local = true;
			statusHolder.setNeedsWater(humidity.get() < 5500);
		}
	}
	
	private void processMessage(Message msg) {
		local = false;
		if(msg instanceof CommandMessage) {
			CommandMessage m = (CommandMessage) msg;
			this.statusHolder.setNeedsWater(m.needsWater);
			this.statusHolder.setUv(m.uv);
		}else {
			throw new IllegalArgumentException("Has to be a command Message");
		}
	}
	
}
