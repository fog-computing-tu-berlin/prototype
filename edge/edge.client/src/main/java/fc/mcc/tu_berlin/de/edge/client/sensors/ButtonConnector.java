package fc.mcc.tu_berlin.de.edge.client.sensors;

import java.util.Scanner;

import com.tinkerforge.BrickletDualButton;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import fc.mcc.tu_berlin.de.edge.client.App;
import fc.mcc.tu_berlin.de.edge.client.tts.Speaker;
import fc.mcc.tu_berlin.de.edge.client.tts.SpeakerManager;

/**
 * 
 * @author Fabian Lehmann
 *
 */
public class ButtonConnector {

	private final BrickletDualButton button;
	private final Speaker speaker = new SpeakerManager();
	private final StatusHolder statusHolder;
	private BlinkThread blinkThread = new BlinkThread();
	
	private boolean blinkedL = false;
	private boolean blinkedR = false;
	private Object changeHelper = new Object();
	
	private boolean buttonLOn = false;
	private boolean buttonROn = false;
	
	public ButtonConnector(String id, IPConnection ipcon, StatusHolder statusHolder){
		blinkThread.start();
		this.statusHolder = statusHolder;
		this.button = new BrickletDualButton(id, ipcon);
		
		if(!App.devMode) {
			button.addStateChangedListener(new BrickletDualButton.StateChangedListener() {
				public void stateChanged(short buttonL, short buttonR, short ledL, short ledR) {
					if(buttonL == BrickletDualButton.BUTTON_STATE_PRESSED) {
						leftClick();
					}else if(buttonR == BrickletDualButton.BUTTON_STATE_PRESSED) {
						rightClick();
					}
				}
			});
		}else {
			new Thread() {
				
				public void run() {
					
					Scanner sc = new Scanner(System.in);
					while(!Thread.interrupted()) {
						String l = sc.nextLine();
						if(l.equals("l")) {
							leftClick();
						}else if (l.equals("r")) {
							rightClick();
						}
					}
					
					sc.close();
					
				};
				
			}.start();
		}
		
		setLeds();
		
		
	}
	
	private void leftClick() {
		
		System.out.println("leftClick");
		speaker.currentValues(statusHolder.sensorResult);
		
	}
	
	private void rightClick() {
		
		System.out.println("rightClick");
		
		boolean fine = true;
		
		if(statusHolder.needsWater) {
			speaker.needWater();
			fine = false;
		}
		
		switch (statusHolder.getUv()) {
		
		case 0: speaker.lessUV(); fine = false; break;
		case 2: speaker.moreUV(); fine = false;
		
		}
		
		if(fine) speaker.fine();
		
	}
	
	public void needsWater() {
		
		synchronized (changeHelper) {
			buttonLOn = true;
			blinkedL = false;
			if(App.devMode) {
				System.out.println("need water");
			}
		}
		setLeds();
	}
	
	public void needsWaterLocal() {
		
		synchronized (changeHelper) {
			if(App.devMode) {
				System.out.println("need water local");
			}
			if(!blinkedL) {
				buttonLOn = false;
				buttonROn = false;
				blinkedL = true;
				setLeds();
				changeHelper.notifyAll();
			}
		}
	}
	
	public void needNoWater() {
		synchronized (changeHelper) {
			buttonLOn = false;
			blinkedL = false;
			if(App.devMode) {
				System.out.println("need no water");
			}
			setLeds();
		}	
	}
	
	public void lessSun() {
		synchronized (changeHelper) {
			if(App.devMode) {
				System.out.println("need less sun");
			}
			if(!blinkedR) {
				buttonLOn = false;
				buttonROn = false;
				blinkedR = true;
				setLeds();
				changeHelper.notifyAll();
			}
		}
	}
	
	public void noSunChange() {
		synchronized (changeHelper) {		
			buttonROn = false;
			if(App.devMode) {
				System.out.println("need no sun change");
			}
		}
		setLeds();
	}
	
	public void moreSun() {
		synchronized (changeHelper) {
			buttonROn = true;
			if(App.devMode) {
				System.out.println("need more sun");
			}
		}
		setLeds();
	}
	
	private void setLeds() {
		if(App.devMode) {
			System.out.println((buttonLOn ? "on" : "off") + " " + (buttonROn ? "on" : "off"));
		} else {
			try {
				button.setLEDState((short) (buttonLOn ? 2 : 3), (short) (buttonROn ? 2 : 3));
			} catch (TimeoutException e) {
				e.printStackTrace();
			} catch (NotConnectedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class BlinkThread extends Thread{
		
		@Override
		public void run() {
			while(!Thread.interrupted()) {
				
				boolean sleep = false;
				
				synchronized (changeHelper) {
					if(blinkedL || blinkedR) {
						if(blinkedL && blinkedR) {
							buttonLOn = buttonLOn != true;
							buttonROn = buttonROn != true;
						}else if(blinkedL) {
							buttonLOn = buttonLOn != true;
						}else {
							buttonROn = buttonROn != true;
						}
						setLeds();
						sleep = true;
					}else {
						try {
							changeHelper.wait();
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							e.printStackTrace();
						}
					}
				}
				
				if(sleep) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						e.printStackTrace();
					}
				}
				
			}
		}
		
	}
}
