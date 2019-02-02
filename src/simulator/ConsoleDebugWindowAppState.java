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
            	  if(value != null && value.equals("true"))
            	  {
            		  physics.setDebugEnabled(true);
            	  }
            	  else if(value != null && value.equals("false"))
            	  {
            		  physics.setDebugEnabled(false);
            	  }
            	  else
            	  {
            		  console.appendConsoleError("You must specify either 'true' or 'false' for the phyDebug command.");
            	  }
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
