package simulator;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ClasspathLocator;
import com.jme3.audio.AudioListenerState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.plugins.blender.BlenderLoader;

import strongdk.jme.appstate.console.ConsoleAppState;

public class SimMain extends SimpleApplication {
	private BulletAppState bulletAppState;
	private static Process testingControlsRunner;

	public SimMain() {
		super(	new Robot(),
				new FieldAppState(), 
				new AudioListenerState(), 
				new DebugKeysAppState(),
				new CameraControl(),
				new ConsoleAppState(),
				new HelpDisplay(),
				new InfoDisplay(),
				new ConsoleDebugWindowAppState(),
				new HatchLogic(),
				new HabLogic()
				);
		bulletAppState = new BulletAppState();
		getStateManager().attach(bulletAppState);
		bulletAppState.getPhysicsSpace().setWorldMax(new Vector3f(16f, 16f, 16f));
		bulletAppState.getPhysicsSpace().setWorldMin(new Vector3f(-16f, -16f, -16f));
		bulletAppState.getPhysicsSpace().setAccuracy(1f/300f);
	}

	@Override
	public void simpleInitApp() {
		assetManager.registerLocator("/", ClasspathLocator.class);
		assetManager.registerLoader(BlenderLoader.class, "blend");
		//instead of colors between 0-255 it is 0.0-0.999... DUMB!
		viewPort.setBackgroundColor(new ColorRGBA(0.427f, 0.5921f, 0.6901f, 1f));
		
	}

	public PhysicsSpace getPhysicsSpace() {
		return bulletAppState.getPhysicsSpace();
	}
	
	public void pause() {
		bulletAppState.setSpeed(0);
	}
	
	public void resume() {
		bulletAppState.setSpeed(1);
	}
	
	public boolean isPaused() {
		return bulletAppState.getSpeed() == 0f;
	}
	
	public void setControlsProcess(Process testingControlsProcess) {
		testingControlsRunner = testingControlsProcess;
	}
	
	public void destroy() {
		super.destroy();
		RobotCodeCommunication connection = RobotCodeCommunication.getInstance();
		if(connection.isStarted()) {
			connection.close();
		}
		testingControlsRunner.destroy();
	}
}
