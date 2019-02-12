package simulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

public class PairedDoubleFactory {

	private static PairedDoubleFactory propertiesManager;

	private static Vector<PairedDouble> doubles;
	private Set<String> connectionNames;
	private Properties prop;
	private String propertiesLocation;
	private static final String DEFAULTLOCATION = "data" + File.separator + "properties.xml";
	private RobotCodeCommunication robotComm;

	public static PairedDoubleFactory getInstance() {
		if(propertiesManager == null) {
			propertiesManager = new PairedDoubleFactory();
		}
		return propertiesManager;
	}

	public PairedDoubleFactory() {
		loadProperties();
	}

	public void setPropertiesLocation(String location) {
		propertiesLocation = location;
	}

	public void loadProperties() {
		if(doubles == null) {
			doubles = new Vector<PairedDouble>();
		}
		if(prop == null) {
			prop = new Properties();
		}
		if(propertiesLocation == null) {
			propertiesLocation = DEFAULTLOCATION;
		}
		File file = new File(propertiesLocation);
		if(file.exists()) {
			try {
				prop.loadFromXML(new FileInputStream(propertiesLocation));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			prop.clear();
		}

	}

	public void saveProperties() {
		if(prop != null) {
			File file = new File(propertiesLocation);
			try {
				if(!file.exists()) {
					file.createNewFile();
				}
				prop.storeToXML(new FileOutputStream(propertiesLocation), null);
			} catch (IOException e) {
				System.out.println("Failed to save file at location " + file.getAbsolutePath());
			}
		} else {
			System.out.println("Failed to save properties - have you loaded properties first?");
		}
	}

	public PairedDouble createPairedDouble(String name, boolean updateFromTable) {
		return createPairedDouble(name, updateFromTable, 0);
	}

	public PairedDouble createPairedDouble(String name, boolean updateFromTable, double startingValue) {
		for(PairedDouble dbl: doubles) {
			if(dbl.getName().equals(name)) {
				System.out.println("Variable already exists!");
				return null;
			}
		}
		PairedDouble dbl = new PairedDouble(name, startingValue, updateFromTable);
		doubles.add(dbl);
		return dbl;
	}

	public boolean createConnection(PairedDouble pairedDouble, String connectionName) {
		connectionNames = robotComm.keys();
		if(!doubles.contains(pairedDouble)) {
			doubles.add(pairedDouble);
		}
		if(connectionNames.contains(connectionName)) {
			for(PairedDouble dbl: doubles) {
				if(dbl.getConnection().equals(connectionName)) {
					return false;
				}
			}
			pairedDouble.setConnection(connectionName);
			prop.setProperty(pairedDouble.getName(), connectionName);
			return true;
		}
		return false;
	}

	public boolean createConnection(String pairedDouble, String connectionName) {
		for(PairedDouble dbl: doubles) {
			if(dbl.getName().equals(pairedDouble)) {
				return createConnection(dbl, connectionName);
			}
		}
		return false;
	}

	public boolean breakConnection(PairedDouble pairedDouble) {
		if(pairedDouble.getConnection().equals("")) {
			return false;
		} else{
			pairedDouble.setConnection("");
			prop.setProperty(pairedDouble.getName(), "");
			return true;
		}
	}

	public boolean breakConnection(String pairedDouble) {
		for(PairedDouble dbl: doubles) {
			if(dbl.getName().equals(pairedDouble)) {
				return breakConnection(dbl);
			}
		}
		return false;
	}

	public Vector<String> pairedDoubleNames() {
		Vector<String> pairedDoubleNames = new Vector<String>();
		for(PairedDouble dbl: doubles) {
			pairedDoubleNames.add(dbl.getName());
		}
		return pairedDoubleNames;
	}

	public Vector<PairedDouble> getPairedDoubles() {
		return doubles;
	}

	public void setRobotComm(RobotCodeCommunication comms) {
		robotComm = comms;
	}



	public class PairedDouble {

		private final String name;
		private String connection;
		private boolean updateFromTable;
		public volatile double value;

		private PairedDouble(String name, double value, boolean updateFromTable) {
			this.name = name;
			this.value = value;
			this.connection = "";
			this.updateFromTable = updateFromTable;
		}

		public String getName() {
			return name;
		}

		public void setConnection(String connection) {
			this.connection = connection;
		}

		public String getConnection() {
			return connection;
		}

		public void update() {
			value = robotComm.getValue(connection);
		}

		public boolean updatesFromTable() {
			return updateFromTable;
		}

		public String toString() {
			return getName() + " : " + value + ((connection.equals("")) ? " is not connected to a network table value." : " with a connection to " + connection + "."); 
		}
	}

}
