package fc.mcc.tu_berlin.de.edge.client.communication.message;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

public class MessageTest {
	
	@Test
	public void parseTest() {
		
		String message = "myplant,1abc28,0cde1685,2dgh445,1560422534691,2477";
		
		String s = Message.MessageTypes.SENSOR_DATA_MESSAGE.ordinal() + ";" + message ;
		
		String b = Message.parseMessage(s).toShortMessage();
		
		compareMesages(message, b);
		
		
	}
	
	private void compareMesages(String a, String b) {
		String[] a_s = a.split(",");
		
		String[] b_s = b.split(",");
		
		Set<String> s1 = Arrays.stream(a_s).collect(Collectors.toSet());
		
		Set<String> s2 = Arrays.stream(b_s).collect(Collectors.toSet());
		
		assertEquals(a_s[0], b_s[0]);
		assertEquals(a_s[a_s.length - 1], b_s[b_s.length - 1]);
		assertEquals(a_s[a_s.length - 2], b_s[b_s.length - 2]);
		
		assertEquals(s1, s2);
	}

}
