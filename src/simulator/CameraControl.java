package simulator;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

public class CameraControl extends BaseAppState {
	boolean camMode1;
    boolean camMode2;
    boolean camMode3;
	private Camera cam;
	private boolean leftPressed = false;
	Vector3f center = new Vector3f(0,0,0);
	Robot robot;
	
	@Override
	public void update(float tpf) {
		if(camMode1 ==true) 
		{
			center.set(0, 0, 0);
		} else if(camMode2 == true) {
		cam.setLocation(robot.getRobotBase().getWorldTranslation().add(0f, 0f, 1f)) ;
    	cam.setRotation(robot.getRobotBase().getWorldRotation());
		} else if(camMode3 == true) {
		center.set(robot.getRobotBase().getWorldTranslation().add(0f, 0f, 1f));
		}
	}
	@Override
	protected void cleanup(Application _app) {

	}

	@Override
	protected void initialize(Application _app) {
		SimpleApplication app = (SimpleApplication) _app;
		cam = app.getCamera();
		cam.setLocation(new Vector3f(5,-15,10));
		cam.lookAt(center, Vector3f.UNIT_Z);
		
		robot = app.getStateManager().getState(Robot.class);
		
		InputManager manager = app.getInputManager();
		
		manager.addMapping("leftButtonMouse",  new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		
		manager.addMapping("upMouse", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
		manager.addMapping("downMouse", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
		
		manager.addMapping("rightMouse", new MouseAxisTrigger(MouseInput.AXIS_X, true));
		manager.addMapping("leftMouse", new MouseAxisTrigger(MouseInput.AXIS_X, false));
		
		manager.addMapping("wheelUp", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
		manager.addMapping("wheelDown", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
		
		manager.addMapping("number1", new KeyTrigger(KeyInput.KEY_1));
		manager.addMapping("number2", new KeyTrigger(KeyInput.KEY_2));
		manager.addMapping("number3", new KeyTrigger(KeyInput.KEY_3));
		
		manager.addListener(analogListener, "upMouse", "downMouse", "rightMouse", "leftMouse", "wheelUp", "wheelDown");
		manager.addListener(actionListener, "leftButtonMouse");
		manager.addListener(keyListener, "number1", "number2","number3");	       
	}
	
	private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("leftButtonMouse")) {
            	leftPressed = keyPressed;
            }
        }
    };
    
	private final ActionListener keyListener = new ActionListener() {
        @Override
       
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("number1")) {
            	camMode1 = true;
            	camMode2 = false;
            	camMode3 = false;
         		cam.lookAt(center, Vector3f.UNIT_Z);
            } else if (name.equals("number2")) {           
            	camMode2 = true;
            	camMode1 = false;
            	camMode3 = false;
            } else if (name.equals("number3")) {
            	camMode3 = true;
            	camMode1 = false;
            	camMode2 = false;
            	center.set(robot.getRobotBase().getWorldTranslation().add(0f, 0f, 1f));
            	cam.lookAt(center, Vector3f.UNIT_Z);
            }	
        }
    };
    
	private final AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
        	if(leftPressed)
        	{
            	float rotateAmount = FastMath.PI * value;
        		Vector3f location = cam.getLocation().subtract(center);
        		Quaternion amount = new Quaternion();
        		float direction = 1;
       		
            	if(name.equals("upMouse")) {
            		if(Math.abs(location.y) < Math.abs(location.x))
            		{
                		if(location.x < 0)
                		{
                			direction = -1;
                		}
            			amount.fromAngleAxis(direction * -rotateAmount, Vector3f.UNIT_Y);
            		}
            		else
            		{
                  		if(location.y < 0)
                		{
                			direction = -1;
                		}
                		amount.fromAngleAxis(direction * rotateAmount, Vector3f.UNIT_X);
            		}
            	} else if(name.equals("downMouse")) {
            		if(Math.abs(location.y) < Math.abs(location.x))
            		{
                		if(location.x < 0)
                		{
                			direction = -1;
                		}
            			amount.fromAngleAxis(direction * rotateAmount, Vector3f.UNIT_Y);
            		}
            		else
            		{
                		if(location.y < 0)
                		{
                			direction = -1;
                		}
                		amount.fromAngleAxis(direction * -rotateAmount, Vector3f.UNIT_X);
            		}
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
        		Vector3f location = cam.getLocation().subtract(center);
            	if(name.equals("wheelUp")) {
            		location.multLocal(1.1f); 		
            	} else if(name.equals("wheelDown")) {
            		location.multLocal(0.9f);  		
            	}
        		cam.setLocation(location.add(center));
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
