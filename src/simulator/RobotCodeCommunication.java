package simulator;

import java.util.Set;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class RobotCodeCommunication {
	private static final NetworkTable  table = NetworkTableInstance.getDefault().getTable("CommsTable");
	private static boolean running = false;
	
	public static void run() {
		NetworkTableInstance.getDefault().startClient("localhost");
		running = true;
	}
	
	public static Set<String> keys(){
		return table.getSubTables();
	}
	
	public static double getValue(String key) {
		return table.getSubTable(key).getEntry("Value").getDouble(0.0);
	}
	
	public static boolean isStarted() {
		return running;
	}
}
