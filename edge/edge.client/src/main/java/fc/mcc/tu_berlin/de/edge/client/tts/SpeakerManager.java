package fc.mcc.tu_berlin.de.edge.client.tts;

/**
 * 
 * @author Fabian Lehmann
 *
 */
public class SpeakerManager extends Speaker {
	
	Speaker speaker;
	
	public SpeakerManager() {
		//TODO change later
		String system = System.getProperty("os.name");
		System.out.println("System: " + system);
		if(system.toLowerCase().contains("windows 10"))
			speaker = new WindowsSpeaker();
		else if(system.toLowerCase().contains("mac"))
			speaker = new MacSpeaker();
		else speaker = new DefaultSpeaker();
	}

	@Override
	protected void speekImpl(String s, int rate) {
		speaker.speekImpl(s, rate);
	}

}
