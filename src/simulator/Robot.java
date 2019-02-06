package simulator;

import java.util.ArrayList;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Robot extends BaseAppState {
	private SimMain app;
	private Node robotNode;
	private Spatial robotBase;
	private final float HATCH_PICKUP_RANGE = 20f;
	private RigidBodyControl linkedHatch = null;
	private AssetManager assetManager;

	private VehicleControl robotControl;
	private CollisionShape robotShape;
	boolean hatchPickedUp = false;
	private final float Z_GRAVITY = -9.81f;
	private Vector3f hatchHoldingPosition;
	private float accelerationValueLeft = 0f;
	private float accelerationValueRight = 0f;
	private final float ROBOT_ACCELERATION = 150f;
	private final ActionListener actionListener = new ActionListener() {

		@Override
		public void onAction(String name, boolean pressed, float tpf) {
			if (name.equals("leftDrivesForward") && pressed) {
				accelerationValueLeft = ROBOT_ACCELERATION;
			} else if(name.equals("leftDrivesBackward") && pressed) {
				accelerationValueLeft = -ROBOT_ACCELERATION;
			} else if(name.indexOf("leftDrives") != -1 && !pressed){
				accelerationValueLeft = 0;
			}

			else if(name.equals("rightDrivesForward") && pressed) {
				accelerationValueRight = ROBOT_ACCELERATION;
			} else if(name.equals("rightDrivesBackward") && pressed){
				accelerationValueRight = -ROBOT_ACCELERATION;			
			} else if(name.indexOf("rightDrives") != -1 && !pressed){
				accelerationValueRight = 0;
			}

			else if(name.equals("pause") && pressed) {
				if(app.isPaused()) {
					app.resume();
				} else {
					app.pause();
				}
			} 

			else if(name.equals("reset") && pressed) {
				robotControl.setPhysicsRotation(new Quaternion(3, 0, 0, 3));
				robotControl.setPhysicsLocation(new Vector3f(4f, 0f, .5f));
				accelerationValueRight = 0;
				accelerationValueLeft = 0;
				if(linkedHatch != null) {
					unlinkHatch((Spatial)linkedHatch.getUserObject());
				}
			}

			else if(name.equals("pickupHatch") && pressed) {
				detectHatch();
			} else if(name.equals("dropHatch") && pressed) {
				//System.out.println("\n\n\nangle [0]: " + robotControl.getPhysicsRotation().toAngles(null)[0]);// * (180 / FastMath.PI));
				//System.out.println("angle [1]: " + robotControl.getPhysicsRotation().toAngles(null)[1]);// * (180 / FastMath.PI));
				//System.out.println("angle [2]: " + robotControl.getPhysicsRotation().toAngles(null)[2]);// * (180 / FastMath.PI));

				if(linkedHatch != null) {
					unlinkHatch((Spatial)linkedHatch.getUserObject());
				}
			}
		}
	};
	
	public Spatial getRobotBase ()
	{
		return robotBase;
	}
	
	@Override
	protected void initialize(Application _app) {

		app = (SimMain) _app;
		Node rootNode = app.getRootNode();


		app.getPhysicsSpace().setGravity(new Vector3f(0f, 0f, Z_GRAVITY));
		assetManager = app.getAssetManager();

		robotNode = new Node("vehicleNode");
		robotBase = assetManager.loadModel("Models/RobotBase/RobotDriveBase.blend");
		robotBase.scale(.5f);
		robotShape = new CompoundCollisionShape();
		Geometry robot_geo = (Geometry)((Node)((Node)((Node)robotBase).getChild(0)).getChild(0)).getChild(0);
		robot_geo.setLocalRotation(new Quaternion(1, 0, 0, 1));
		((CompoundCollisionShape)robotShape).addChildShape(new BoxCollisionShape(new Vector3f(.3302f, .09355f, .3302f)), new Vector3f(0f, 0f, 0f));
		robotControl = new VehicleControl(robotShape, 60);
		robotNode.attachChild(robotBase);
		robotBase.addControl(robotControl);

		float stiffness = 800.0f;
		float compValue = .6f;
		float dampValue = .7f;
		robotControl.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
		robotControl.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
		robotControl.setSuspensionStiffness(stiffness);
		robotControl.setMaxSuspensionForce(1000.0f);		

		robotControl.setPhysicsRotation(new Quaternion(3, 0, 0, 3));
		robotControl.setPhysicsLocation(new Vector3f(4f, 0f, .5f));
		addWheels();
		robotBase.rotate(FastMath.HALF_PI, 0f, 0f);
		rootNode.attachChild(robotNode);
		app.getPhysicsSpace().add(robotBase);

		robotControl.steer(0, -.25f);
		robotControl.steer(1, .25f);
		robotControl.steer(2, .25f);
		robotControl.steer(3, -.25f);

		getControls();
		//		app.pause();
	}

	@Override
	public void update(float tpf) {  

		//    	Vector3f forward = robotBase.getLocalRotation().mult(Vector3f.UNIT_Z).multLocal(vertVelocity).multLocal(tpf);
		//    	robotControl.setLinearVelocity(robotControl.getLinearVelocity().add(forward));
		//    	robotControl.setAngularVelocity(robotControl.getAngularVelocity().add(new Vector3f(0f, 0f, rotate * tpf)));
		//    	rotate *= .05f;
		robotControl.accelerate(0, accelerationValueLeft);
		robotControl.accelerate(2, accelerationValueLeft);
		robotControl.accelerate(1, accelerationValueRight);
		robotControl.accelerate(3, accelerationValueRight);

		//		accelerationValueLeft *= .7;
		//		accelerationValueRight *= .7;

		hatchHoldingPosition = generateHoldingPosition();


		if(linkedHatch != null) {
			hatchPickedUp = false;
			linkedHatch.setGravity(new Vector3f(0, 0, 0));

			pickupHatch();

			if(distanceTo(robotControl, linkedHatch) > HATCH_PICKUP_RANGE) {
				unlinkHatch((Spatial)linkedHatch.getUserObject());
			}
		}
	}

	private Vector3f generateHoldingPosition() {

		Vector3f holdingPosition = robotControl.getPhysicsLocation(); 

		if(robotControl.getPhysicsRotation().toAngles(null)[0] > 0) {
			if(robotControl.getPhysicsRotation().toAngles(null)[2] > 0) {
				holdingPosition.setY((float) (holdingPosition.getY() + (2 * (1-linearizedCos((FastMath.PI/2)-robotControl.getPhysicsRotation().toAngles(null)[2])))));
			}	else {
				holdingPosition.setY((float) (holdingPosition.getY() - (2 * linearizedCos(robotControl.getPhysicsRotation().toAngles(null)[2]))));
			}
		} else {
			if(robotControl.getPhysicsRotation().toAngles(null)[2] > 0) {
				holdingPosition.setY((float) (holdingPosition.getY() - (2 * (1-linearizedCos((FastMath.PI/2)-robotControl.getPhysicsRotation().toAngles(null)[2])))));
			} else {
				holdingPosition.setY((float) (holdingPosition.getY() + (2 * linearizedCos(robotControl.getPhysicsRotation().toAngles(null)[2]))));
			}
		}

		if(robotControl.getPhysicsRotation().toAngles(null)[2] > 0) {
			holdingPosition.setX((float) (holdingPosition.getX() - (2 * (1 - linearizedCos(robotControl.getPhysicsRotation().toAngles(null)[2])))));
		} else {
			holdingPosition.setX((float) (holdingPosition.getX() - (2 * (1 - linearizedCos(robotControl.getPhysicsRotation().toAngles(null)[2])))));
		}

		holdingPosition.setZ(holdingPosition.getZ() + 0.75f);
		return holdingPosition;
	}

	@Override
	protected void cleanup(Application _app) {

	}

	private float linearizedCos(float x) {
		return Math.abs(x * 0.63661977237f + 1);	
	}

	private void detectHatch() {
		FieldAppState field = app.getStateManager().getState(FieldAppState.class);
		ArrayList<RigidBodyControl> hatchList = field.getHatchCtrlList();
		float smallest = Float.MAX_VALUE;
		RigidBodyControl operatingCtrl = null;
		for(RigidBodyControl ctrl: hatchList) {
			float distance = distanceTo(robotControl, ctrl);
			if(distance < HATCH_PICKUP_RANGE && distance < smallest) {
				smallest = distance;
				operatingCtrl = ctrl;
			}
		}
		if(!(operatingCtrl == null)) {
			linkedHatch = operatingCtrl;
			//			linkedHatch.setGravity(new Vector3f(0f, 0f, 0f));
		}
	}

	private void unlinkHatch(Spatial hatchSpatial) {
		if(linkedHatch != null) {
			Node rootNode = app.getRootNode();
			linkedHatch.setGravity(new Vector3f(0, 0, Z_GRAVITY));
			linkedHatch.setLinearVelocity(new Vector3f(0f, 0f, 0f));
			linkedHatch.setAngularVelocity(new Vector3f(0f, 0f, 0f));
			hatchPickedUp = false;
			linkedHatch = null;
			robotNode.detachChild(hatchSpatial);
			rootNode.attachChild(hatchSpatial);
		}

	}

	private void pickupHatch() {
		final float TRANSLATE_SPEED = 1f;
		System.out.println("Picking up hatch");
		
		
		

		Vector3f translateVector = createItemTranslationVector(robotControl, linkedHatch).mult(TRANSLATE_SPEED);
		Quaternion translateQuat = createItemRotationQuaternion(robotControl, linkedHatch);
		linkedHatch.setPhysicsLocation(linkedHatch.getPhysicsLocation().add(translateVector));
		linkedHatch.setPhysicsRotation(translateQuat);

	}											


	private Quaternion createItemRotationQuaternion(VehicleControl robot, RigidBodyControl item) {
		float robotYRot = robot.getPhysicsRotation().toAngles(null)[1];
		//		float[] angles = {robot.getPhysicsRotation().toAngles(null)[0], robot.getPhysicsRotation().toAngles(null)[1], robotZRot};
		//		float[] angles = {robot.getPhysicsRotation().toAngles(null)[0], robotZRot, robot.getPhysicsRotation().toAngles(null)[2]};

		return new Quaternion(new float[] {robotYRot, robot.getPhysicsRotation().toAngles(null)[1], robot.getPhysicsRotation().toAngles(null)[2]}); 
	}

	private Vector3f createItemTranslationVector(VehicleControl robot, RigidBodyControl item) {
		Vector3f ret = new Vector3f();
		Vector3f itemLoc = item.getPhysicsLocation();
		ret.setX(hatchHoldingPosition.getX() - itemLoc.getX());
		ret.setY(hatchHoldingPosition.getY() - itemLoc.getY());
		ret.setZ(hatchHoldingPosition.getZ() - itemLoc.getZ());

		return ret;
	}

	private float distanceTo(VehicleControl robot, RigidBodyControl item) {
		Vector3f robotLoc = robot.getPhysicsLocation();
		Vector3f itemLoc = item.getPhysicsLocation();
		float xDist = (float) Math.pow(itemLoc.getX() - robotLoc.getX(), 2);
		float yDist = (float) Math.pow(itemLoc.getY() - robotLoc.getY(), 2);
		float zDist = (float) Math.pow(itemLoc.getZ() - robotLoc.getZ(), 2);
		return FastMath.sqrt(xDist + yDist + zDist);
	}


	private void addWheels() {
		Vector3f wheelDirection = new Vector3f(0, -1, 0);
		Vector3f wheelAxle = new Vector3f(-1, 0, 0);
		float radius = .06f;
		float restLength = .1f;
		float yOff = .0f;
		float xOff = .4f;
		float zOff = .25f;

		int sideWheel;
		int backWheel;
		for(int i = 0; i < 4; i++) {
			Node wheelNode = new Node("wheel " + i + " node");

			if(i%2 == 0) {
				sideWheel = 1;
			} else {
				sideWheel = -1;
			}
			if(i > 1) {
				backWheel = -1;
			} else {
				backWheel = 1;
			}
			Vector3f position = new Vector3f(sideWheel*xOff, yOff, backWheel*zOff);
			robotControl.addWheel(wheelNode, position, wheelDirection, wheelAxle, restLength, radius, false);
			robotNode.attachChild(wheelNode);
		}
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

		manager.addListener(actionListener, "leftDrivesForward", "leftDrivesBackward",
				"rightDrivesForward", "rightDrivesBackward", "pause", "reset", "pickupHatch", "dropHatch");

	}

	public void setDrivesPowerLeft(float power) {
		accelerationValueLeft = power;
	}
	
	public float getDrivesPowerLeft() {
		return accelerationValueLeft;
	}
	
	@Override
	protected void onDisable() {

	}

	public void setDrivesPowerRight(float power) {
		accelerationValueRight = power;
	}
	
	public float getDrivesPowerRight() {
		return accelerationValueRight;
	}

	
	@Override
	protected void onEnable() {

	}
}
