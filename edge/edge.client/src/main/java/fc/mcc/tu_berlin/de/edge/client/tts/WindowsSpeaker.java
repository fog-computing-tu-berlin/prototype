package fc.mcc.tu_berlin.de.edge.client.tts;

import java.io.IOException;
import java.util.Scanner;

/**
 * 
 * @author Fabian Lehmann
 *
 */
public class WindowsSpeaker extends Speaker {
	
	private final String name; 
	
	public WindowsSpeaker() {
		Runtime rt = Runtime.getRuntime();
		String command = "PowerShell -Command \"Add-Type –AssemblyName System.Speech; "
				+ "$speak = (New-Object System.Speech.Synthesis.SpeechSynthesizer); "
				+ "$speak.GetInstalledVoices().VoiceInfo\"";
		String name = null;
		try {
			Process pr = rt.exec(command);
			Scanner sc = new Scanner(pr.getInputStream());
			while(sc.hasNext()) {
				String[] s = sc.nextLine().split(":");
				if(s.length > 0) {
					if(s[0].startsWith("Name"))
						name = s[1].substring(1);
					if(s[0].startsWith("Culture")) {
						String culture = s[1].substring(1);
						if(culture.equals("en-US")) {
							break;
						}
					}
				}
			}
			sc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.name = name;
	}

	@Override
	protected void speekImpl(String s, int rate) {
		
		rate = rate - 100;
		
		double n_rate = rate / 10.0;
		
		Runtime rt = Runtime.getRuntime();
		String command = "PowerShell -Command \"Add-Type –AssemblyName System.Speech; "
				+ "$speak = (New-Object System.Speech.Synthesis.SpeechSynthesizer); "
				+ (name == null ? "" : "$speak.SelectVoice('" + name + "'); ")
				+ "$speak.Rate = " + n_rate + ";"
				+ "$speak.Speak('" + s.replace("'", "''") + "');\"";
		try {
			Process p = rt.exec(command);
			p.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
