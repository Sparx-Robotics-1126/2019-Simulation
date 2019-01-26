package simulator;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.renderer.Camera;

import strongdk.jme.appstate.console.CommandEvent;
import strongdk.jme.appstate.console.CommandListener;
import strongdk.jme.appstate.console.CommandParser;
import strongdk.jme.appstate.console.ConsoleAppState;

public class ConsoleCommandsAppState extends BaseAppState {
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
		
		console.registerCommand("help", commandListener);
		console.registerCommand("cam", commandListener);
		console.registerCommand("hide", commandListener);
				
	}
	
    private CommandListener commandListener = new CommandListener() {
        @Override
        public void execute(CommandEvent evt) {
              final CommandParser parser = evt.getParser();
              if (evt.getCommand().equals("help")) {
            	  console.appendConsole("help: this message.");
            	  console.appendConsole("hide: hide the console.");
            	  console.appendConsole("cam: display camera location.");
              } else if (evt.getCommand().equals("hide")) {
                  console.setVisible(false);
              } else if (evt.getCommand().equals("cam")) {
            	  Camera cam = app.getCamera();
            	  String info = cam.getLocation().toString();
            	  console.appendConsole("Cam: " + info);
              } else if (evt.getCommand().equals("rotation")) {
                    Integer value = parser.getInt(0);
                    if(value != null){
                  
                          console.appendConsole("Rotation speed changed: " + value);
                    }else{
                          console.appendConsoleError("Could not change speed, not a valid number: " + parser.getString(0));
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
