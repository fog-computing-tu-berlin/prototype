package fc.mcc.tu_berlin.de.edge.client.communication.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
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
		
		add(mp, 0, 60);

		mp = newMP(mp, 0);
		
		poll(mp, 0, 40);
		
		assertEquals(20, mp.size());
		
		mp = newMP(mp, 0);
		
		add(mp, 60, 150);
		
		mp = newMP(mp, 0);
		
		assertEquals(150 - 60 + 20, mp.size());
		
		add(mp, 150, 3005);
		
		mp = newMP(mp, 0);
		
		assertEquals(2965, mp.size());
		
		File file = new File(path);
		assertEquals(32, file.listFiles().length);
		
		poll(mp, 40, 150);
		mp = newMP(mp, 100);
		
		file = new File(path);
		assertEquals(31, file.listFiles().length);
		
		poll(mp, 150, 3005);
		
		for (int i = 0; i < 100; i++) {
			assertNull(mp.poll());
		}
		
	}
	
	private void add(MessagePersistor mp, int a, int b) {
		for (int i = a; i < b; i++) {
			mp.add(genDemoMessage(i));
		}
	}
	
	private void poll(MessagePersistor mp, int a, int b) {
		for (int i = a; i < b; i++) {
			Message m_0 = mp.peek();
			Message m = mp.poll();
			assertEquals(m_0, m);
			compareMesages(genDemoMessage(i).toShortMessage(), m.toShortMessage());
		}
	}
	
	private MessagePersistor newMP(MessagePersistor oldMP, int offset) {
		long nextM = getLongFromField("nextMessage", oldMP);
		long pointer = getLongFromField("pointer", oldMP);
		int size = oldMP.size();
		
		MessagePersistor new_mp = new MessagePersistor(path);
		
		long nextM1 = getLongFromField("nextMessage", new_mp);
		long pointer1 = getLongFromField("pointer", new_mp);
		
		assertEquals(nextM - offset, nextM1);
		assertEquals(pointer - offset, pointer1);
		assertEquals(new_mp.size(), size);
		return new_mp;
	}
	
	private long getLongFromField(String field, MessagePersistor obj) {
		try {
			Field f = MessagePersistor.class.getDeclaredField(field);
			f.setAccessible(true);
			return f.getLong(obj);
		} catch (Exception e) {
			e.printStackTrace();
			return obj.hashCode();
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
		if(!f.exists()) return;
		File[] files = f.listFiles();
		for (File file : files) {
			file.delete();
		}
		f.delete();
	}

}
