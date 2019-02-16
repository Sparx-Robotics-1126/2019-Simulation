package simulator;

import java.util.ArrayList;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class HatchLogic extends BaseAppState {
	private SimMain app;
	private final Vector3f[] HATCH_POSITIONS = {
			//hatch positions on left side
			new Vector3f(0.5686435f, -0.58888614f, 0.6995726f),
			new Vector3f(1.0236804f, -0.58888614f, 0.6995726f), 
			new Vector3f(1.5357443f, -0.58888614f, 0.6995726f), 

			new Vector3f(-0.568f, -0.58888614f, 0.6995726f), 
			new Vector3f(-1.023f, -0.58888614f, 0.6995726f),
			new Vector3f(-1.53f, -0.58888614f, 0.6995726f), 
			
			//supposed to be right side
			new Vector3f(1.6084193f, 0.9f, 0.699587f),
			new Vector3f(1.0872313f, 0.9f, 0.699587f),
			new Vector3f(0.54335785f, 0.9f, 0.699587f),
			
			new Vector3f(-0.470719f, 0.9f, 0.699587f),
			new Vector3f(-0.9991004f, 0.9f, 0.699587f),
			new Vector3f(-1.5221257f, 0.9f, 0.699587f)
			};
	private float HATCH_HOLDING_DISTANCE = 0.5f;
	private VehicleControl robotControl;
	private final float Z_GRAVITY = -9.81f;
	private RigidBodyControl linkedHatch = null;
	private final float HATCH_PICKUP_RANGE = 2f;
	Robot robot;
	SimUtilities utilities = new SimUtilities();
	private final ActionListener actionListener = new ActionListener() {
		
	@Override
	public void onAction(String name, boolean pressed, float tpf) {
		if(name.equals("reset") && pressed) {
			if(linkedHatch != null) {
				unlinkHatch();
			}
		}

		else if(name.equals("pickupHatch") && pressed) {
			detectHatch();
		} else if(name.equals("dropHatch") && pressed) {
			if(linkedHatch != null) {
				unlinkHatch();
			}
		}
	}
	};
	
	
	
	@Override
	public void update(float tpf) {
		
		robotControl = robot.getRobotControl();
		
		if(linkedHatch != null) {
			moveHatch();
			if(utilities.distanceTo(robotControl.getPhysicsLocation(), linkedHatch.getPhysicsLocation()) > HATCH_PICKUP_RANGE) {
				unlinkHatch();
			}
		}
	}

	@Override
	protected void initialize(Application _app) {
		app = (SimMain) _app;
		getControls();
		robot = app.getStateManager().getState(Robot.class);
	}
	
	
	
	
	private void detectHatch() {
		FieldAppState field = app.getStateManager().getState(FieldAppState.class);
		ArrayList<RigidBodyControl> hatchList = field.getHatchCtrlList();

		float smallest = Float.MAX_VALUE;
		RigidBodyControl operatingCtrl = null;

		for(RigidBodyControl ctrl: hatchList) {
			float distance = utilities.distanceTo(robotControl.getPhysicsLocation(), ctrl.getPhysicsLocation());

			if(distance < HATCH_PICKUP_RANGE && distance < smallest && robotIsFacingHatch(ctrl)) {
				smallest = distance;
				operatingCtrl = ctrl;
				
			}
		}
		if(operatingCtrl != null) {
			linkedHatch = operatingCtrl;
			linkedHatch.setGravity(new Vector3f(0, 0, 0));
		}
	}

	private boolean robotIsFacingHatch(RigidBodyControl hatch) {
		float distanceInFront = 1f;
		//		Vector3f rotCol = robotControl.getPhysicsRotation().getRotationColumn(2);
		while(distanceInFront < HATCH_PICKUP_RANGE) {
			if(utilities.distanceTo(hatch.getPhysicsLocation(), utilities.locInFrontOfObject(robotControl.getPhysicsLocation(), robotControl.getPhysicsRotation(), distanceInFront)) < 1f) {
				return true;
			}
			distanceInFront += 0.1;
		}
		return false;
	}



	private void unlinkHatch() {
		if(linkedHatch != null) {
			Vector3f dropoffPos = closestHatchDropoffPosition();
			if(dropoffPos == null) {
				linkedHatch.setGravity(new Vector3f(0, 0, Z_GRAVITY));
			} else {
				linkedHatch.setPhysicsLocation(dropoffPos);
				linkedHatch.setPhysicsRotation(hatchDropoffRotation());
			}
			linkedHatch.setLinearVelocity(new Vector3f(0f, 0f, 0f));
			linkedHatch = null;
		}
	}

	private Vector3f closestHatchDropoffPosition() {
		Vector3f hatchLocation = linkedHatch.getPhysicsLocation();
		float lowest = Float.MAX_VALUE;
		Vector3f bestPoss = null;
		for(Vector3f dropoffPos: HATCH_POSITIONS) {
			float distBetween = Math.abs(utilities.distanceTo(dropoffPos, hatchLocation));
			if(distBetween < 0.5f && distBetween < lowest) {
				lowest = distBetween;
				bestPoss = dropoffPos;
			}
		}

		return bestPoss;
	}
	
	private Quaternion hatchDropoffRotation() {
		float hatchZRot = Math.abs(robotControl.getPhysicsRotation().toAngles(null)[2]);
		hatchZRot = (hatchZRot < (FastMath.PI / 4)) ? 0f: (float)(FastMath.PI / 2);
		return new Quaternion(new float[] {Math.abs(linkedHatch.getPhysicsRotation().toAngles(null)[0]), linkedHatch.getPhysicsRotation().toAngles(null)[1], hatchZRot});
	}


	private void moveHatch() {
		final float TRANSLATE_SPEED = 1f;
		Vector3f translateVector = createItemTranslationVector(robotControl, linkedHatch).mult(TRANSLATE_SPEED);
		Quaternion translateQuat = createItemRotationQuaternion(robotControl, linkedHatch);
		linkedHatch.setPhysicsLocation(linkedHatch.getPhysicsLocation().add(translateVector));
		linkedHatch.setPhysicsRotation(translateQuat);
	}
	
	
	
	private Quaternion createItemRotationQuaternion(VehicleControl robot, RigidBodyControl item) {
		//I made this by mistake but it works so �\_(O_O)_/�
		float robotYRot = robot.getPhysicsRotation().toAngles(null)[1];
		return new Quaternion(new float[] {robotYRot, robot.getPhysicsRotation().toAngles(null)[1], robot.getPhysicsRotation().toAngles(null)[2]}); 
	}

	private Vector3f createItemTranslationVector(VehicleControl robot, RigidBodyControl item) {
		Vector3f hatchHoldingPosition = utilities.locInFrontOfObject(robotControl.getPhysicsLocation(), robotControl.getPhysicsRotation(), HATCH_HOLDING_DISTANCE);
		Vector3f ret = new Vector3f();
		Vector3f itemLoc = item.getPhysicsLocation();
		ret.setX(hatchHoldingPosition.getX() - itemLoc.getX());
		ret.setY(hatchHoldingPosition.getY() - itemLoc.getY());
		ret.setZ(hatchHoldingPosition.getZ() - itemLoc.getZ());

		return ret;
	}
	
	private void getControls() {
		InputManager manager = app.getInputManager();

		manager.addMapping("leftDrivesForward", new KeyTrigger(KeyInput.KEY_Q));
		manager.addMapping("leftDrivesBackward", new KeyTrigger(KeyInput.KEY_A));
		manager.addMapping("rightDrivesForward", new KeyTrigger(KeyInput.KEY_E));
		manager.addMapping("rightDrivesBackward", new KeyTrigger(KeyInput.KEY_D));
		manager.addMapping("pause", new KeyTrigger(KeyInput.KEY_P));
		manager.addMapping("reset", new KeyTrigger(KeyInput.KEY_R));
		manager.addMapping("pickupHatch", new KeyTrigger(KeyInput.KEY_W));
		manager.addMapping("dropHatch", new KeyTrigger(KeyInput.KEY_S));
		manager.addMapping("printInfo", new KeyTrigger(KeyInput.KEY_Z));

		manager.addListener(actionListener, "leftDrivesForward", "leftDrivesBackward",
				"rightDrivesForward", "rightDrivesBackward", "pause", "reset", "pickupHatch",
				"dropHatch", "printInfo");
	}
	
	public RigidBodyControl getHatch() {
		return linkedHatch;
	}
	
	public void dropHatch() {
		unlinkHatch();
	}
	
	@Override
	protected void cleanup(Application arg0) {
		// TODO Auto-generated method stub
		//not doing these hehehe
	}
	
	@Override
	protected void onDisable() {
		// TODO Auto-generated method stub
		//not doing these hehehe
	}

	@Override
	protected void onEnable() {
		// TODO Auto-generated method stub
		//not doing these hehehe
		
	}
	

}
