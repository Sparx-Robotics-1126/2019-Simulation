package simulator;

import java.util.concurrent.ConcurrentHashMap;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableType;

public class RobotCodeCommunication {
	private static ConcurrentHashMap<String, Double> subtableData;
	private static final NetworkTable  table = NetworkTableInstance.getDefault().getTable("CommsTable");
	
	
	public static void run() {
		NetworkTableInstance.getDefault().startClient("localhost");
		subtableData = new ConcurrentHashMap<String, Double>();
		getMap();
		addFlags(table);
	}

	private static void addFlags(NetworkTable table) {
		subtableData.forEachKey(50, subTable->
		{
			table.getSubTable(subTable).addEntryListener((changedTable, key, entry, value, flags)->
			{
				if(entry.getType() == NetworkTableType.kDouble) {
					subtableData.put(subTable, entry.getDouble(0.0));
				}
			}, EntryListenerFlags.kUpdate);
		});
	}
	
	public static ConcurrentHashMap<String, Double> getMap() {
		String[] subtableNames = table.getSubTables().toArray(new String[0]);
		for(int i = 0; i < subtableNames.length; i++) {
			subtableData.put(subtableNames[i], table.getEntry(subtableNames[i]).getDouble(0.0));
		}
		return subtableData;
	}
}
