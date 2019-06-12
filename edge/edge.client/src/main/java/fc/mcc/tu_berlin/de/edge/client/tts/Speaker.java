package fc.mcc.tu_berlin.de.edge.client.tts;

import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

import fc.mcc.tu_berlin.de.edge.client.sensors.Sensor;
import fc.mcc.tu_berlin.de.edge.client.sensors.SensorResult;
import fc.mcc.tu_berlin.de.edge.client.sensors.SensorType;

/**
 * 
 * @author Fabian Lehmann
 *
 */
public abstract class Speaker {

	private final String[] commands_water = {
			"I'm thirsty",
			"I like to have some water, please",
			"You can have a beer, but I'd like to have some water, please",
			"I'm dry, make me wet",
			"Give me my elixir of life, water.",
			"I can't even get water, will you do it, please?",
			"I'm dying, I need water.",
			"I need water.",
			"You want me to be happy? Then give me some water!",
			"You give me water, I'll give you oxygen.",
			"I just need air and love. And water damn it.",
			"water, water"
	};

	private final String[] commands_more_uv = {
			"Put me in the sun, please.",
			"It's dark here, can you get out of the sun?",
			"Either you put me in the sun or I get sad and brown"
	};

	private final String[] commands_less_uv = {
			"I'm burning, put me in the shadow, please.",
			"Give me some sun cream. Or reduce the uv light."
	};

	private final String[] commands_everything_ok = {
			"I'm fine.",
			"I don't need water.",
			"Drink your own water, but leave me alone.",
			"No beer befor four. Same for water.",
			"I'm not thirsty at the moment, thanks for asking.",
			"I need to listen to Mozart to become strong. Can you play it, please.",
			"Currently everything is ok. Ask me later again, please."
	};
	
	public void needWater() {
		String c = getStringFromArray(commands_water);
		if(c.equals("water, water")) {
			speek("water", 40);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
			speek("water", 30);
		}else {
			speek(c, 80);
		}
	}

	public void moreUV() {
		String c = getStringFromArray(commands_more_uv);
		speek(c, 110);
	}

	public void lessUV() {
		String c = getStringFromArray(commands_less_uv);
		speek(c, 95);
	}

	public void fine() {
		String c = getStringFromArray(commands_everything_ok);
		speek(c, 100);
	}
	
	public void currentValues(SensorResult sr) {
		
		List<String> list = sr.results.entrySet().stream()
		.map(x -> String.format("%s sensor with id %s has %.1f %s %s", 
				x.getKey().sensorType.name,
				uidToText(x.getKey().uid),
				getValue(x),
				x.getKey().sensorType.unit,
				x.getKey().sensorType.name))
		.collect(Collectors.toList());
		
		int last = list.size() - 1;
		String text = String.join(", and ",
                String.join(", ", list.subList(0, last)),
                list.get(last)).concat(".");
		
		speek(text, 100);
		
	}
	
	private String uidToText(String uid) {
		
		String s = "";
		
		for (int i = 0; i < uid.length() - 1; i++) {
			s = s.concat(uid.charAt(i) + " ");
		}
		
		return s + uid.charAt(uid.length() - 1);
		
	}
	
	private double getValue(Entry<Sensor, Double> x) {
		SensorType type = x.getKey().sensorType;
		Double value = x.getValue();
		switch (type) {
		case HUMIDITY: return value / 100;
		case TEMPERATURE: return value / 100;
		case UV: return value / 250;
		}
		throw new IllegalArgumentException("Wrong sensor type");
	}
	
	private Random random = new Random();
	
	private void speek(String s, int rate) {
		speekImpl(s, rate + random.nextInt(9) - 4);
	}

	protected abstract void speekImpl(String s, int rate);

	private String getStringFromArray(String[] s) {
		return s[random.nextInt(s.length)];
	}
}
