package fc.mcc.tu_berlin.de.edge.client.communication.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fc.mcc.tu_berlin.de.edge.client.sensors.Sensor;
import fc.mcc.tu_berlin.de.edge.client.sensors.SensorResult;
import fc.mcc.tu_berlin.de.edge.client.sensors.SensorType;

public class MessagePersistorTest {

	String path = "test" + File.separatorChar;
	@Test
	public void restart() throws IOException {
		
		MessagePersistor mp = new MessagePersistor(path);
		
		for (int i = 0; i < 150; i++) {
			mp.add(genDemoMessage(i));
		}

		mp = null;
		System.gc();
		
		mp = new MessagePersistor(path);
		
		for (int i = 0; i < 120; i++) {
			Message m = mp.poll();
			compareMesages(genDemoMessage(i).toShortMessage(), m.toShortMessage());
		}
		
		mp = null;
		System.gc();
		
		File file = new File(path);
		assertEquals(2, file.listFiles().length);
		
		mp = new MessagePersistor(path);
		
		for (int i = 120; i < 150; i++) {
			Message m_0 = mp.peek();
			Message m = mp.poll();
			assertEquals(m_0, m);
			compareMesages(genDemoMessage(i).toShortMessage(), m.toShortMessage());
		}
		
		for (int i = 0; i < 100; i++) {
			assertNull(mp.poll());
		}
		
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
	
	private SensorDataMessage genDemoMessage(int i) {
		Map<Sensor, Double> results = new HashMap<Sensor, Double>();
		
		results.put(new Sensor(SensorType.HUMIDITY, "hum"), (double) i);
		results.put(new Sensor(SensorType.TEMPERATURE, "tem"), (double) i);
		results.put(new Sensor(SensorType.UV, "uvv"), (double) i);
		
		SensorResult sensorResult = new SensorResult(results );
		return new SensorDataMessage("test", sensorResult , i, i + 1);
	}
	
	@After
	@Before
	public void clean() {
		File f = new File(path);
		File[] files = f.listFiles();
		for (File file : files) {
			file.delete();
		}
		f.delete();
	}

}
