package simulator;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.audio.AudioListenerState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;

public class SimMain extends SimpleApplication {
	private BulletAppState bulletAppState;
	
	public SimMain() {
		super(new Robot(),
				new FieldAppState(),
				new StatsAppState(), 
				new AudioListenerState(), 
				new DebugKeysAppState(),
				new CameraControl());
		bulletAppState = new BulletAppState();
		getStateManager().attach(bulletAppState);
	}

	@Override
	public void simpleInitApp() {

	}

	public PhysicsSpace getPhysicsSpace() {
		return bulletAppState.getPhysicsSpace();
	}
	
	public static void main(String[] args) {
		SimMain app = new SimMain();
		app.start();
	}
	
	

}
