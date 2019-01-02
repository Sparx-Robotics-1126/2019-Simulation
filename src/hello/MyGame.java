package hello;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.audio.AudioListenerState;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.math.ColorRGBA;

public class MyGame extends SimpleApplication {
	
	public MyGame() {
		super(new Robot(),
				new StatsAppState(), 
				new FlyCamAppState(), 
				new AudioListenerState(), 
				new DebugKeysAppState());
	}

	@Override
	public void simpleInitApp() {

	}

	public static void main(String[] args) {
		MyGame app = new MyGame();
		app.start();
	}

}
