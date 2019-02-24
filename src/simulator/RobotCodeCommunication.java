package simulator;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import simulator.PairedDoubleFactory.PairedDouble;

public class RobotCodeCommunication extends Thread{
	private final NetworkTable table = NetworkTableInstance.getDefault().getTable("CommsTable");
	private boolean running = false;
	private static RobotCodeCommunication instance;

	public static RobotCodeCommunication getInstance() {
		if(instance == null) {
			instance = new RobotCodeCommunication();
		}
		return instance;

	}

	private RobotCodeCommunication() {
	}

	public boolean runTables() {
		if(!running) {
			NetworkTableInstance.getDefault().startClient("localhost");

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			running = isConnected();

			if(!running) {
				NetworkTableInstance.getDefault().stopClient();
			} else {
				start();
			}
		}
		return running;
	}



	public Set<String> keys(){
		if(!isConnected() || !isStarted())
			return new HashSet<String>();
		return table.getSubTables();
	}

	/**
	 * Gets a value mapped on the table to a specific key
	 * @param key - returns the value 
	 * @return
	 */
	public double getValue(String key) {
		if(!isConnected()) {
			return 0;
		}
		return table.getSubTable(key).getEntry("Value").getDouble(0.0);
	}

	/**
	 * Returns true if network table is connected, false otherwise
	 * @return true if connected, false if not connected.
	 */
	public boolean isConnected() {
		return NetworkTableInstance.getDefault().isConnected();
	}

	/**
	 * Will return true if the network table has been started and false if the network table hasn't been started
	 * @return True or false depending on if table has started.
	 */
	public boolean isStarted() {
		return running;
	}
	
	public void close() {
		if(running) {
			running = !running;
			NetworkTableInstance.getDefault().stopClient();
		}
		this.interrupt();
	}

	public void run() {  
		while(!interrupted()) {
			if(isConnected()) {
				Vector<PairedDouble> pairedDoubles = PairedDoubleFactory.getInstance().getPairedDoubles();
				pairedDoubles.forEach((pairedDoubleObject)->{
					if(!pairedDoubleObject.getConnection().equals("")) {
						if(pairedDoubleObject.updatesFromTable()) {
							pairedDoubleObject.update();
						} else{
							table.getSubTable(pairedDoubleObject.getConnection()).getEntry("Value").setDouble(pairedDoubleObject.value);
						}
					}
				});
			}
		}
	}
}
