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
	private boolean leftPressed = false;
	Vector3f center = new Vector3f(0,0,0);
	
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
		cam.setLocation(new Vector3f(1,-10,1));
		cam.lookAt(center, Vector3f.UNIT_Z);
		InputManager manager = app.getInputManager();
		
		manager.addMapping("leftButtonMouse",  new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		
		manager.addMapping("upMouse", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
		manager.addMapping("downMouse", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
		
		manager.addMapping("rightMouse", new MouseAxisTrigger(MouseInput.AXIS_X, true));
		manager.addMapping("leftMouse", new MouseAxisTrigger(MouseInput.AXIS_X, false));
		
		manager.addMapping("wheelUp", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
		manager.addMapping("wheelDown", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
		
		manager.addListener(analogListener, "upMouse", "downMouse", "rightMouse", "leftMouse", "wheelUp", "wheelDown");
		manager.addListener(actionListener, "leftButtonMouse");
		       
	}
	
	private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("leftButtonMouse")) {
            	leftPressed = keyPressed;
            }
        }
    };

	
	private final AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
        	if(leftPressed)
        	{
            	float rotateAmount = FastMath.PI * value;
        		
        		Vector3f location = cam.getLocation();
        		Quaternion amount = new Quaternion();
        		float direction = 1;
        		
        		if(location.y < 0)
        		{
        			direction = -1;
        		}
        		
            	if(name.equals("upMouse")) {
            		amount.fromAngleAxis(direction * rotateAmount, Vector3f.UNIT_X);
            	} else if(name.equals("downMouse")) {
            		amount.fromAngleAxis(direction * -rotateAmount, Vector3f.UNIT_X);
            	} else if(name.equals("leftMouse")) {
            		amount.fromAngleAxis(-rotateAmount, Vector3f.UNIT_Z);
            	} else if(name.equals("rightMouse")) {
            		amount.fromAngleAxis(rotateAmount, Vector3f.UNIT_Z);    		
            	}
            	
        		Vector3f newLocation = amount.mult(location);
        		cam.setLocation(center.add(newLocation));
        		cam.lookAt(center, Vector3f.UNIT_Z);
        	}
        	else if(name.equals("wheelUp") || name.equals("wheelDown"))
        	{
        		Vector3f location = cam.getLocation();
            	if(name.equals("wheelUp")) {
            		location.multLocal(0.9f); 		
            	} else if(name.equals("wheelDown")) {
            		location.multLocal(1.1f);  		
            	}
        		cam.setLocation(location);
        		cam.lookAt(center, Vector3f.UNIT_Z);
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