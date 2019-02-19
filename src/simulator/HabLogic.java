package simulator;

import java.util.Vector;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

public class HabLogic extends BaseAppState{
	
	private SimMain app;
	private final float HAB_HEIGHT = 1f;
	private Robot robot;
	private VehicleControl robotControl;
	private boolean climbing = false;
	
	private final ActionListener actionListener = new ActionListener() {

		@Override
		public void onAction(String name, boolean pressed, float tpf) {
			if(name.equals("climb") && pressed) {
				climbing = true;
			}
		}
	};
	
	@Override
	protected void initialize(Application _app) {
		app = (SimMain) _app;
		robot = app.getStateManager().getState(Robot.class);
		robotControl = robot.getRobotControl();
		getControls();
	}

	@Override
	public void update(float tpf) {
		if(climbing) {
			robotControl.setGravity(new Vector3f(0f, 0f, 0f));
			climb();
		} else {
			robotControl.setGravity(new Vector3f(0f, 0f, -9.81f));
		}
		
	}

	public void climb() {
		if(!robotIsInStartingPosition() || !isRightAngleToClimb()) {
			//maybe there's a better way to do this...
			System.out.println("LINE THE ROBOT UP");
			climbing = false;
		} else {
			if(robotControl.getPhysicsLocation().getZ() >= HAB_HEIGHT) {
				translateRobotForward();
				if(robotControl.getPhysicsLocation().getX() > 7.2f) {
					climbing = false;
				}
			} else {
				translateRobotUpwards();
			}
		}
	}
	
	public boolean robotIsInStartingPosition() {
		if((robotControl.getPhysicsLocation().getY() < -0.26715878) || (robotControl.getPhysicsLocation().getY() > 0.26715878)
				|| robotControl.getPhysicsLocation().getX() < 6.2641973f) {
			return false;
		}
		return true;
	}
	
	public boolean isRightAngleToClimb() {
		float robotZRot = robotControl.getPhysicsRotation().toAngles(null)[2];
		if(robotZRot > (FastMath.PI / 4)) {
			return true;
		}
		return false;
	}
	
	public void translateRobotUpwards() {
		Vector3f loc = robotControl.getPhysicsLocation();
		loc.setZ(loc.getZ() + 0.001f);
		robotControl.setPhysicsLocation(loc);
	}
	
	public void translateRobotForward() {
		Vector3f loc = robotControl.getPhysicsLocation();
		loc.setX(loc.getX() + 0.001f);
		robotControl.setPhysicsLocation(loc);
	}
	
	private void getControls() {
		InputManager manager = app.getInputManager();

		manager.addMapping("climb", new KeyTrigger(KeyInput.KEY_TAB));

		manager.addListener(actionListener, "climb");
	}


	@Override
	protected void onDisable() {
		// TODO Auto-generated method stub
		//guess who's also not doing these
	}

	@Override
	protected void onEnable() {
		// TODO Auto-generated method stub
		//guess who's also not doing these
	}


	@Override
	protected void cleanup(Application arg0) {
		// TODO Auto-generated method stub
		//guess who's also not doing these
	}
}
