package fc.mcc.tu_berlin.de.edge.client.tts;

import java.io.IOException;

/**
 * 
 * @author Fabian Lehmann
 *
 */
public class MacSpeaker extends Speaker{

	@Override
	protected void speekImpl(String s, int rate) {
		Runtime rt = Runtime.getRuntime();
		String command = "say \"" + s + "\" -v \"Samantha\"";
		System.out.println(command);
		try {
			rt.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
