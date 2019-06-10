package fc.mcc.tu_berlin.de.edge.client.tts;

import java.util.Random;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

/**
 * 
 * @author Fabian Lehmann
 *
 */
class DefaultSpeaker extends Speaker {

	private static boolean init = false;
	Voice voice;
	
	private void init() {
		System.setProperty("logLevel", "OFF"); // INFO or WARN are also valid
		System.setProperty("FreeTTSSynthEngineCentral", "com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");
		System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
	}

	public DefaultSpeaker() {
		if(!init) {
			init = true;
			init();
		}
		VoiceManager vm = VoiceManager.getInstance();
		voice = vm.getVoice("kevin16");
		voice.allocate();
	}
	
	private Random random = new Random();
	
	protected void speekImpl(String s, int rate) {
		voice.setPitchRange(130 + random.nextInt(9) - 4);
		voice.setRate(rate);
		voice.speak(s);
	}
	
}
