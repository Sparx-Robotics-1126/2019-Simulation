package simulator;

import com.jme3.app.Application;  
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.plugins.blender.BlenderLoader;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.*;
import com.jme3.renderer.Camera;

public class CameraControl extends BaseAppState {
	private Camera cam;
	@Override
	public void update(float tpf) {
        
	}

	@Override
	protected void cleanup(Application _app) {

	}

	@Override
	protected void initialize(Application _app) {
		SimpleApplication app = (SimpleApplication) _app;
		cam = app.getCamera();
		InputManager manager = app.getInputManager();
		
		manager.addMapping("up", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
		manager.addMapping("down", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
		
		manager.addMapping("right", new MouseAxisTrigger(MouseInput.AXIS_X, true));
		manager.addMapping("left", new MouseAxisTrigger(MouseInput.AXIS_X, false));
		
		manager.addListener(analogListener, "up", "down", "right", "left");
		       
	}
	
	private final AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
        	if(name.equals("up")) {
        		cam.setRotation(cam.getRotation().add(new Quaternion(0,-10f,0,1)));
        	}else if(name.equals("down")) {
        		cam.setRotation(cam.getRotation().add(new Quaternion(0,10f,0,1)));
        	}else if(name.equals("left")) {
        		
        	}else if(name.equals("right")) {
        		
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
