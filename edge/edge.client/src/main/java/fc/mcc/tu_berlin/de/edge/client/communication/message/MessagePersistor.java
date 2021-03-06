package fc.mcc.tu_berlin.de.edge.client.communication.message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * 
 * @author Fabian Lehmann
 *
 */
public class MessagePersistor {
	
	private final Queue<Message> messages = new LinkedBlockingQueue<Message>();
	private Object addHelper = new Object();
	private Object pollHelper = new Object();
	private long nextMessage = 0;
	private final String directory;
	private final int MESSAGES_PER_FILE = 100;
	private final String pointerfile;
	private long pointer = 0;

	public MessagePersistor(String directory) {
		this.directory = directory;
		pointerfile = directory + "pointer";
		File f = new File(directory);
		if(!f.exists() || !f.isDirectory()) {
			f.mkdirs();
			return;
		}
		loadState();
	}
	
	private void loadState() {
		this.pointer = loadPointer();
		if(pointer > MESSAGES_PER_FILE) {
			moveMessagesToZero();
		}
		loadMessages();
		this.nextMessage = this.pointer + messages.size();
	}
	
	private long loadPointer() {
		File tmp_f = new File(pointerfile + "_tmp");
		File f = tmp_f.exists() ? tmp_f : new File(pointerfile);
		if(f.exists()) {
			Scanner sc = null;
			try {
				sc = new Scanner(f);
				return Long.parseLong(sc.nextLine());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}finally {
				if(sc != null)
					sc.close();
			}
		}
		return 0;
	}
	
	private void moveMessagesToZero() {
		long newCounter = 0;
		long counter = pointer;
		File f;
		while((f = new File(directory + getFileFromCounter(counter))).exists()) {
			File f_n = new File(directory + getFileFromCounter(newCounter));
			if(f_n.exists()) f_n.delete();
			f.renameTo(f_n);
			counter +=  MESSAGES_PER_FILE;
			newCounter += MESSAGES_PER_FILE;
		}
		
		this.pointer = this.pointer % MESSAGES_PER_FILE;
		try {
			writePointer(this.pointer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadMessages() {
		long index = pointer + MESSAGES_PER_FILE;
		if(loadMessages(getFileFromCounter(pointer), (int) pointer)) {
			while(loadMessages(getFileFromCounter(index), 0)) {
				index += MESSAGES_PER_FILE;
			}
		}
	}
	
	private boolean loadMessages(String filename, int startIndex) {
		File file = new File(directory + filename);
		if(file.exists()) {
			Scanner sc = null;
			try {
				sc = new Scanner(file);
				int index = 0;
				while(sc.hasNext() && index++ < startIndex) {
					sc.nextLine();
				}
				while(sc.hasNext()) {
					Message m = Message.parseMessage(sc.nextLine());
					this.messages.add(m);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}finally {
				if(sc != null) sc.close();
			}
			return true;
		}else return false;
	}
	
	public void add(Message message) {
		synchronized (addHelper) {
			messages.add(message);
			try {
				FileWriter fileWriter = new FileWriter(directory + getFileFromCounter(nextMessage++), true);
				PrintWriter pr = new PrintWriter(fileWriter);
				pr.println(message.getType().ordinal() + ";" + message.toShortMessage());
				pr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isEmpty() {
		return messages.isEmpty();
	}
	
	public int size() {
		return messages.size();
	}

	public Message poll() {
		synchronized (pollHelper) {
			Message m = messages.poll();
			
			if(m == null) return null;
			
			try {
				
				writePointer(++pointer);
				
				if(pointer % MESSAGES_PER_FILE == 0) {
					File f = new File(directory + getFileFromCounter(pointer - 1));
					if(f.exists()) {
						f.delete();
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return m;
		}
	}

	public Message peek() {
		synchronized (pollHelper) {
			return messages.peek();
		}
	}
	
	private void writePointer(long pointer) throws IOException {
		File old_f = new File(pointerfile);
		boolean wasPresent = old_f.exists();
		String name = wasPresent ? pointerfile + "_tmp" : pointerfile;
		FileWriter fileWriter = new FileWriter(name);
		fileWriter.write(pointer + System.lineSeparator());
		fileWriter.close();
		if(wasPresent) {
			old_f.delete();
			new File(name).renameTo(old_f);
		}
	}

	private String getFileFromCounter(long counter) {
		long begin = counter - (counter % MESSAGES_PER_FILE);
		String file = "messages_" + begin + "-" + (begin + MESSAGES_PER_FILE - 1);
		return file;
	}
}
