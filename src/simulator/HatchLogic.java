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

//WHAT DOESN'T WORK:
//When you hit the hatch while it's on a wall it wiggles
//Probably a bunch of other stuff but thats all i could find lmk if you find anything
//Sincerely- the bug exterminator
public class HatchLogic extends BaseAppState {
	private SimMain app;
	private final Vector3f[] HATCH_POSITIONS = {
			//hatch positions on left side
			new Vector3f(0.5686435f, -0.58888614f, 0.4826f),
			new Vector3f(1.0236804f, -0.58888614f, 0.4826f), 
			new Vector3f(1.5357443f, -0.58888614f, 0.4826f), 

			new Vector3f(-0.568f, -0.58888614f, 0.4826f), 
			new Vector3f(-1.023f, -0.58888614f, 0.4826f),
			new Vector3f(-1.53f, -0.58888614f, 0.4826f), 

			//hatch positions on right side
			new Vector3f(1.6084193f, 0.9f, 0.4826f),
			new Vector3f(1.0872313f, 0.9f, 0.4826f),
			new Vector3f(0.54335785f, 0.9f, 0.4826f),

			new Vector3f(-0.470719f, 0.9f, 0.4826f),
			new Vector3f(-0.9991004f, 0.9f, 0.4826f),
			new Vector3f(-1.5221257f, 0.9f, 0.4826f),

			//rocket ships
			new Vector3f(-2.3483214f, -2.979838f, 0.7995826f),
			new Vector3f(2.3483214f, -2.979838f, 0.7995826f),
			new Vector3f(2.4085102f, 3.3236604f, 0.79957944f),
			new Vector3f(-2.3036504f, 3.318036f, 0.7995946f),

			//Cargo ends
			new Vector3f(2.7003956f, -0.100122765f, 0.4826f),
			new Vector3f(2.6530423f, 0.40617636f, 0.4826f),

			new Vector3f(-2.7003956f, -0.100122765f, 0.4826f),
			new Vector3f(-2.6530423f, 0.40617636f, 0.4826f),

	};
	private float HATCH_HOLDING_DISTANCE = .35f;//0.5f;
	private VehicleControl robotControl;
	private final float Z_GRAVITY = -9.81f;
	private RigidBodyControl linkedHatch = null;
	private final float HATCH_PICKUP_RANGE = 2f;
	private boolean translatingHatch = true;
	Robot robot;


	@Override
	public void update(float tpf) {
		if(linkedHatch != null) {
			moveHatch();
			if(translatingHatch) {
				translatingHatch = shouldTranslate();
			}
		}
	}

	@Override
	protected void initialize(Application _app) {
		app = (SimMain) _app;
		robot = app.getStateManager().getState(Robot.class);
		robotControl = robot.getRobotControl();
	}


	private void detectHatch() {
		FieldAppState field = app.getStateManager().getState(FieldAppState.class);
		ArrayList<RigidBodyControl> hatchList = field.getHatchCtrlList();

		float smallest = Float.MAX_VALUE;
		RigidBodyControl operatingCtrl = null;

		for(RigidBodyControl ctrl: hatchList) {
			float distance = SimUtilities.distanceTo(robotControl.getPhysicsLocation(), ctrl.getPhysicsLocation());

			if(distance < HATCH_PICKUP_RANGE && distance < smallest && robotIsFacingHatch(ctrl)) {
				smallest = distance;
				operatingCtrl = ctrl;

			}
		}
		if(operatingCtrl != null) {
			linkedHatch = operatingCtrl;
			linkedHatch.setKinematic(false);
			linkedHatch.setGravity(new Vector3f(0, 0, 0));
		}
	}

	private boolean robotIsFacingHatch(RigidBodyControl hatch) {
		float distanceInFront = 1f;
		while(distanceInFront < HATCH_PICKUP_RANGE) {
			if(SimUtilities.distanceTo(hatch.getPhysicsLocation(), SimUtilities.getPointAtAngleAndOffsetOfObject(robotControl.getPhysicsLocation(), robotControl.getPhysicsRotation(), 0f, distanceInFront, 0.4826f)) < 1f) {
				return true;
			}
			distanceInFront += 0.1;
		}
		return false;
	}



	private void unlinkHatch() {
		if(linkedHatch != null) {
			Vector3f dropoffPos = closestHatchDropoffPosition();
			linkedHatch.setLinearVelocity(new Vector3f(0f, 0f, 0f));
			linkedHatch.setAngularVelocity(new Vector3f(0f, 0f, 0f));
			if(dropoffPos == null) {
				linkedHatch.setGravity(new Vector3f(0, 0, Z_GRAVITY));
			} else {
				linkedHatch.setPhysicsLocation(dropoffPos);
				linkedHatch.setPhysicsRotation(hatchDropoffRotation());
			}
			linkedHatch	= null;
			translatingHatch = true;
		}
	}

	private Vector3f closestHatchDropoffPosition() {
		Vector3f hatchLocation = linkedHatch.getPhysicsLocation();
		float lowest = Float.MAX_VALUE;
		Vector3f bestPoss = null;
		for(Vector3f dropoffPos: HATCH_POSITIONS) {
			float distBetween = Math.abs(SimUtilities.distanceTo(dropoffPos, hatchLocation));
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
		Vector3f translateVector = createItemTranslationVector(robotControl, linkedHatch);
		Quaternion translateQuat = createItemRotationQuaternion(robotControl, linkedHatch);
		if(translatingHatch) {
			linkedHatch.setPhysicsLocation(linkedHatch.getPhysicsLocation().add(translateVector.mult(0.05f)));
		} else {
			linkedHatch.setPhysicsLocation(linkedHatch.getPhysicsLocation().add(translateVector));
		}
		linkedHatch.setPhysicsRotation(translateQuat);
		if(linkedHatch.getPhysicsLocation().getZ() > 1f) {
			//Every once and a while the hatch goes too high
			//idk why but this fixes it so don't remove it!
			translatingHatch = false;
		}
	}

	private boolean shouldTranslate() {
//		System.out.println(utilities.distanceTo(linkedHatch.getPhysicsLocation(), robotControl.getPhysicsLocation()));
		if(SimUtilities.distanceTo(linkedHatch.getPhysicsLocation(), robotControl.getPhysicsLocation()) < 0.75f) {
			return false;
		} else {
			return true;
		}
	}

	private Quaternion createItemRotationQuaternion(VehicleControl robot, RigidBodyControl item) {
		//I made this by mistake but it works so �\_(O_O)_/�
		float robotYRot = robot.getPhysicsRotation().toAngles(null)[1];
		return new Quaternion(new float[] {robotYRot, robot.getPhysicsRotation().toAngles(null)[1], robot.getPhysicsRotation().toAngles(null)[2]}); 
	}

	private Vector3f createItemTranslationVector(VehicleControl robot, RigidBodyControl item) {
		Vector3f hatchHoldingPosition = SimUtilities.getPointAtAngleAndOffsetOfObject(robotControl.getPhysicsLocation(), robotControl.getPhysicsRotation(), 0, HATCH_HOLDING_DISTANCE, .20f);//0.4826f);
		Vector3f ret = new Vector3f();
		Vector3f itemLoc = item.getPhysicsLocation();
		ret.setX(hatchHoldingPosition.getX() - itemLoc.getX());
		ret.setY(hatchHoldingPosition.getY() - itemLoc.getY());
		ret.setZ(hatchHoldingPosition.getZ() - itemLoc.getZ());

		return ret;
	}


	public RigidBodyControl getHatch() {
		return linkedHatch;
	}

	public void dropHatch() {
		unlinkHatch();
	}

	public void pickupHatch() {
		detectHatch();
	}
	
	@Override
	protected void cleanup(Application arg0) {
		//not doing these hehehe
	}

	@Override
	protected void onDisable() {
		//not doing these hehehe
	}

	@Override
	protected void onEnable() {
		//not doing these hehehe
	}


}
