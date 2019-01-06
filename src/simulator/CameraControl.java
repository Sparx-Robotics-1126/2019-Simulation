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
import com.jme3.input.controls.*;
import com.jme3.renderer.Camera;

public class CameraControl extends BaseAppState {
	private Camera cam;
	private boolean middlePressed = false;
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
		
		manager.addMapping("middleButton",  new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
		
		manager.addMapping("up", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
		manager.addMapping("down", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
		
		manager.addMapping("right", new MouseAxisTrigger(MouseInput.AXIS_X, true));
		manager.addMapping("left", new MouseAxisTrigger(MouseInput.AXIS_X, false));
		
		manager.addListener(analogListener, "up", "down", "right", "left");
		manager.addListener(actionListener, "middleButton");
		       
	}
	
	private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("middleButton")) {
            	middlePressed = keyPressed;
            }
        }
    };

	
	private final AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
        	if(middlePressed)
        	{
            	float rotateAmount = FastMath.PI * value;
        		Vector3f center = new Vector3f(0,0,0);
        		Vector3f location = cam.getLocation();
        		Quaternion amount = new Quaternion();
        		
            	if(name.equals("up")) {
            		amount.fromAngleAxis(-rotateAmount, Vector3f.UNIT_X);
            	} else if(name.equals("down")) {
            		amount.fromAngleAxis(rotateAmount, Vector3f.UNIT_X);
            	} else if(name.equals("left")) {
            		amount.fromAngleAxis(-rotateAmount, Vector3f.UNIT_Y);
            	} else if(name.equals("right")) {
            		amount.fromAngleAxis(rotateAmount, Vector3f.UNIT_Y);    		
            	}
        		Vector3f newLocation = amount.mult(location);
        		cam.setLocation(center.add(newLocation));
        		cam.lookAt(center, Vector3f.UNIT_Y);
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
