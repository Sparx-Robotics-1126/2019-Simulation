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
		subtableData = new ConcurrentHashMap<String, Double>();
		NetworkTableInstance inst = NetworkTableInstance.getDefault();
		inst.startClient("CommsTable");
		String[] subtableNames = table.getSubTables().toArray(new String[0]);
		for(int i = 0; i < subtableNames.length; i++) {
			subtableData.put(subtableNames[i], table.getEntry(subtableNames[i]).getDouble(0.0));
		}
		System.out.println(table.getKeys().size());
		System.out.println(table.getSubTables().size());
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
		return subtableData;
	}
}
