package fc.mcc.tu_berlin.de.edge.client.communication.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MessagePersistorTest {

	String path = "test" + File.separatorChar;
	@Test
	public void test() throws IOException {
		
		MessagePersistor mp = new MessagePersistor(path);
		System.out.println(mp.size());
		
		
		for (int i = 0; i < 150; i++) {
			mp.add(new CommandMessage(true, (short) 2));
			Message m = mp.poll();
			assertTrue(m instanceof CommandMessage);
			CommandMessage c = (CommandMessage) m;
			System.out.println(i % 100);
		}
		
	}
	
//	@Before
	public void clean() {
		File f = new File(path);
		File[] files = f.listFiles();
		for (File file : files) {
			file.delete();
		}
//		f.delete();
	}

}
