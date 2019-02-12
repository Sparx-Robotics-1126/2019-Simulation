package simulator;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.renderer.Camera;

import simulator.PairedDoubleFactory.PairedDouble;
import strongdk.jme.appstate.console.CommandEvent;
import strongdk.jme.appstate.console.CommandListener;
import strongdk.jme.appstate.console.CommandParser;
import strongdk.jme.appstate.console.ConsoleAppState;

public class ConsoleDebugWindowAppState extends BaseAppState {
	private SimpleApplication app;
	private ConsoleAppState console;
	private RobotCodeCommunication robotCodeComm;
	private PairedDoubleFactory pairedDoubleCreator = PairedDoubleFactory.getInstance();
	
	@Override
	protected void cleanup(Application arg0) {
	}

	@Override
	protected void initialize(Application _app) {
		app = (SimpleApplication) _app;
		console = app.getStateManager().getState(ConsoleAppState.class);
		console.setConsoleNumLines(40);
		robotCodeComm = app.getStateManager().getState(RobotCodeCommunication.class);

		console.registerCommand("help", commandListener);
		console.registerCommand("cam", commandListener);
		console.registerCommand("hide", commandListener);
		console.registerCommand("phyDebug", commandListener);
		console.registerCommand("startTables", commandListener);
		console.registerCommand("listTables", commandListener);
		console.registerCommand("listObjects", commandListener);
		console.registerCommand("displayTable", commandListener);
		console.registerCommand("hideTable", commandListener);
		console.registerCommand("pair", commandListener);
		console.registerCommand("unpair", commandListener);
		console.registerCommand("clear", commandListener);
	}

	private CommandListener commandListener = new CommandListener() {
		@Override
		public void execute(CommandEvent evt) {
			final CommandParser parser = evt.getParser();
			if (evt.getCommand().equals("help")) {
				console.appendConsole("help: this message.");
				console.appendConsole("hide: hide the console.");
				console.appendConsole("clear: removes text from console");
				console.appendConsole("cam: display camera location.");
				console.appendConsole("phyDebug true/false: enable/disable physics debug.");
				console.appendConsole("clear: clears text");
				console.appendConsole("escape: exits console");
				console.appendConsole("`: starts console");
				console.appendConsole("up: copys previous input");
				console.appendConsole("PgUp: scrolls history up");
				console.appendConsole("PgDown: scrolls history down");

				if(!robotCodeComm.isStarted()) {
					console.appendConsole("startTables: starts the network tables.");
				}else {
					console.appendConsole("listTables: display all data in the tables");
					console.appendConsole("listObjects: Lists all objects that can be paired in the robot simulator.");
					console.appendConsole("displayTable tableKey: displays one network table key value pair in the updtaing info display box");
					console.appendConsole("pair simulationObject networkTableKey: The simulationObject should be from the list found in listObjects, "
							+ "and the networkTableKey should be from the listTables command. Pairing them will link the values together.");
					console.appendConsole("unpair simulationObject: Disconnects a simulation object from its connection if it has one.");
					console.appendConsole("hideTable: hides display table");
					console.appendConsole("loadProperties: Loads in pairedDoubles and their connections from saved file.");
					console.appendConsole("saveProperties: Saves pairedDoubles to a file.");
				}

			} else if (evt.getCommand().equals("hide")) {
				console.setVisible(false);
			} else if (evt.getCommand().equals("cam")) {
				Camera cam = app.getCamera();
				String info = "location " + cam.getLocation().toString();
				info += " direction " + cam.getDirection().toString();
				console.appendConsole("Cam: " + info);
			} else if (evt.getCommand().equals("phyDebug")) {
				BulletAppState physics = app.getStateManager().getState(BulletAppState.class);
				String value = parser.get(0);
				if (value != null && value.equals("true")) {
					physics.setDebugEnabled(true);
				} else if (value != null && value.equals("false")) {
					physics.setDebugEnabled(false);
				} else {
					console.appendConsoleError("You must specify either 'true' or 'false' for the phyDebug command.");
				}
			} else if(evt.getCommand().equals("startTables")) {
				if(!robotCodeComm.isStarted()) {
					console.appendConsole(robotCodeComm.run() ? "Network table client successfully started" : "Network table client failed to start; nothing to connect to.");
				}
			} else if (evt.getCommand().equals("hideTable")){
				if(evt.getParser().get(0) != null) {
					InfoDisplay id = app.getStateManager().getState(InfoDisplay.class);
					boolean removed = id.removeNetworkTableValue(evt.getParser().get(0));
					
					if(removed == false) {
						console.appendConsoleError("You were unable to remove the display table key");
					}
				} else
					console.appendConsoleError("You must specify a network table key, all keys can be found with listTables command");
			} else if(evt.getCommand().equals("listObjects")) {
				for(PairedDouble pairedDouble : pairedDoubleCreator.getPairedDoubles()){
					console.appendConsole(pairedDouble.toString());
				}
			} else if (evt.getCommand().equals("clear")) {
				console.clearConsole();
			} else if(!robotCodeComm.isStarted()) {
				console.appendConsoleError("Network tables are not started. Use startTables first.");
			} else{
				if(evt.getCommand().equals("listTables")) {
					for(String key : robotCodeComm.keys()){
						console.appendConsole(key + " : " + robotCodeComm.getValue(key));
					}
				} else if(evt.getCommand().equals("displayTable")) {
					String parameter = evt.getParser().get(0);
					if(parameter != null || !robotCodeComm.keys().contains(parameter)) {
						app.getStateManager().getState(InfoDisplay.class).setDisplayedNetworkValue(parameter);
					} else{
						console.appendConsoleError("You must specify a network table key, all keys can be found with listTables command");
					}
				} else if(evt.getCommand().equals("pair")) {
					String simObjectParameter = evt.getParser().get(0);
					String networkTableParameter = evt.getParser().get(1);
					if(simObjectParameter != null && pairedDoubleCreator.pairedDoubleNames().contains(simObjectParameter)) {
						if(networkTableParameter != null && robotCodeComm.keys().contains(networkTableParameter)){
							console.appendConsole(pairedDoubleCreator.createConnection(simObjectParameter, networkTableParameter) ? "Successful pair" : "unsuccessful pair");
						} else{
							console.appendConsoleError("You need to specify a network table key to map it to, from listTables.");	
						}
					} else{
						console.appendConsoleError("You must specify both a simulation object, which must be found in the listObjects command, and a network table key to map it to, from listTables.");		
					}
				} else if(evt.getCommand().equals("unpair")) {
					String simObjectParameter = evt.getParser().get(0);
					if(simObjectParameter != null && pairedDoubleCreator.pairedDoubleNames().contains(simObjectParameter)) {
						if(pairedDoubleCreator.breakConnection(simObjectParameter)) {
							console.appendConsole("Connection on " + simObjectParameter + " is now unlinked.");
						} else{
							console.appendConsole("No connection existed on " + simObjectParameter + ".");
						}
					} else{
						console.appendConsoleError(simObjectParameter + " is not a valid SimObject from the list found in listObjects.");
					}
				} else if(evt.getCommand().equals("loadProperies")){
					pairedDoubleCreator.loadProperties();
				} else if(evt.getCommand().equals("saveProperties")) {
					pairedDoubleCreator.saveProperties();
				}
			}
		}
	};


	@Override
	protected void onDisable() {
	}

	@Override
	protected void onEnable() {
	}

}
