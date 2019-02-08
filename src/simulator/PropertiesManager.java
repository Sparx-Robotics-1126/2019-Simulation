package simulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

public class PropertiesManager {
	
	private static PropertiesManager propertiesManager;

	private static Vector<PairedDouble> doubles;
	private Set<String> connectionNames;
	private Properties prop;
	private String propertiesLocation;
	private static final String DEFAULTLOCATION = "data" + File.separator + "properties.xml";

	public PropertiesManager getInstance() {
		if(propertiesManager == null) {
			propertiesManager = new PropertiesManager();
		}
		return propertiesManager;
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
				// TODO Auto-generated catch block
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

	public PairedDouble createPairedDouble(String name) {
		return createPairedDouble(name, 0);
	}

	public PairedDouble createPairedDouble(String name, double startingValue) {
		if(prop == null) {
			loadProperties();
		}
		for(PairedDouble dbl: doubles) {
			if(dbl.getName().equals(name)) {
				System.out.println("Variable already exists!");
				return null;
			}
		}
		PairedDouble dbl = new PairedDouble(name, startingValue);
		connectionNames = RobotCodeCommunication.getInstance().keys();
		if(prop.containsKey((dbl.getName()))) {
			if(connectionNames.contains(prop.getProperty(dbl.getName()))) {
				dbl.setConnection(prop.getProperty(dbl.getName()));
			} else {
				prop.remove(dbl.getName());
			}
		}
		doubles.add(dbl);
		return dbl;
	}
	
	public boolean createConnection(PairedDouble pairedDouble, String connectionName) {
		connectionNames = RobotCodeCommunication.getInstance().keys();
		if(!doubles.contains(pairedDouble)) {
			doubles.add(pairedDouble);
		}
		if(connectionNames.contains("connectionName")) {
			for(PairedDouble dbl: doubles) {
				if(dbl.getConnection().contentEquals(connectionName)) {
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
	
	public Vector<String> pairedDoubleNames() {
		Vector<String> pairedDoubleNames = new Vector<String>();
		for(PairedDouble dbl: doubles) {
			pairedDoubleNames.add(dbl.getName());
		}
		return pairedDoubleNames;
	}

	public class PairedDouble {

		private final String name;
		private String connection;
		public volatile double value;

		private PairedDouble(String name, double value) {
			this.name = name;
			this.value = value;
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

	}

}
