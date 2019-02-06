package simulator;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.renderer.Camera;

import strongdk.jme.appstate.console.CommandEvent;
import strongdk.jme.appstate.console.CommandListener;
import strongdk.jme.appstate.console.CommandParser;
import strongdk.jme.appstate.console.ConsoleAppState;

public class ConsoleDebugWindowAppState extends BaseAppState {
	private SimpleApplication app;
	private ConsoleAppState console;

	@Override
	protected void cleanup(Application arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void initialize(Application _app) {
		app = (SimpleApplication) _app;
		console = app.getStateManager().getState(ConsoleAppState.class);
		console.setConsoleNumLines(40);

		console.registerCommand("help", commandListener);
		console.registerCommand("cam", commandListener);
		console.registerCommand("hide", commandListener);
		console.registerCommand("phyDebug", commandListener);
		console.registerCommand("startTables", commandListener);
		console.registerCommand("listTables", commandListener);
		console.registerCommand("displayTable", commandListener);
		console.registerCommand("clear", commandListener);
	}

	private CommandListener commandListener = new CommandListener() {
		@Override
		public void execute(CommandEvent evt) {
			final CommandParser parser = evt.getParser();
			if (evt.getCommand().equals("help")) {
				console.appendConsole("help: this message.");
				console.appendConsole("hide: hide the console.");
				console.appendConsole("cam: display camera location.");
				console.appendConsole("phyDebug true/false: enable/disable physics debug.");
				console.appendConsole("startTables: starts the network tables.");
				console.appendConsole("listTables: display all data in the tables");
				console.appendConsole("displayTable tableKey: displays one network table key value pair in the updtaing info display box");
				console.appendConsole("clear: removes text from console");
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
				if(!RobotCodeCommunication.isStarted()) {
					RobotCodeCommunication.run();
				}
			} else if(evt.getCommand().equals("listTables")) {
				if(RobotCodeCommunication.isStarted()) {
					for(String key : RobotCodeCommunication.keys()){
						console.appendConsole(key);
					}
				} else {
					console.appendConsole("Network tables are not started. Use startTables first.");
				}
			} else if(evt.getCommand().equals("displayTable")) {
				if(RobotCodeCommunication.isStarted()) {
					if(evt.getParser().get(0) != null) {
						System.out.println(evt.getParser().get(0));
						app.getStateManager().getState(InfoDisplay.class).setDisplayedNetworkValue(evt.getParser().get(0));
					} else
						console.appendConsoleError("You must specify a network table key, all keys can be found with listTables command");
				} else
					console.appendConsoleError("Network tables are not started. Use startTables first.");
			} else if (evt.getCommand().equals("clear")) {
				console.clearConsole();
			}

		}
	};

	@Override
	protected void onDisable() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onEnable() {
		// TODO Auto-generated method stub

	}

}
