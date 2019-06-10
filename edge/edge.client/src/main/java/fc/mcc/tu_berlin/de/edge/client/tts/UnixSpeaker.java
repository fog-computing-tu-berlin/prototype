package fc.mcc.tu_berlin.de.edge.client.tts;

import java.io.IOException;

/**
 * 
 * @author Fabian Lehmann
 *
 */
public class UnixSpeaker extends Speaker {

	@Override
	protected void speekImpl(String s, int rate) {
		Runtime rt = Runtime.getRuntime();
		String command = "spd-say \"" + s + "\"";
		try {
			rt.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
