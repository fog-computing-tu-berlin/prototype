package fc.mcc.tu_berlin.de.edge.client.util;

import java.util.Random;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

/**
 * 
 * @author Fabian Lehmann
 *
 */
public class Speaker {

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
	
	private final String[] current_values = {
			"It is %s grad celsius, I am %s percent wet and have a solar radiation of %s milliwatt."
	};

	private Random random = new Random();
	private static boolean init = false;
	Voice voice;

	public Speaker() {
		if(!init) {
			init = true;
			init();
		}
		VoiceManager vm = VoiceManager.getInstance();
		voice = vm.getVoice("kevin16");
		voice.allocate();
	}
	
	private void init() {
		System.setProperty("logLevel", "OFF"); // INFO or WARN are also valid
		System.setProperty("FreeTTSSynthEngineCentral", "com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");
		System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
	}

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
	
	public void currentValues(double temp, double humdity, double uv) {
		
		String c = getStringFromArray(current_values);
		speek(String.format(c, temp, humdity, uv), 100);
		
	}

	private void speek(String s, int rate) {
		voice.setPitchRange(130 + random.nextInt(9) - 4);
		voice.setRate(rate + random.nextInt(9) - 4);
		voice.speak(s);
	}

	private String getStringFromArray(String[] s) {
		return s[random.nextInt(s.length)];
	}
}
