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
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import simulator.PairedDoubleFactory.PairedDouble;

public class Robot extends BaseAppState {
	private SimMain app;
	private Node robotNode;
	private Node habClimberNode;
	private Spatial robotBase;
	private final float HATCH_PICKUP_RANGE = 2f;
	private RigidBodyControl linkedHatch = null;
	private AssetManager assetManager;
	private Spatial habLifter1;
	private Spatial habLifter2;
	private Spatial leadScrew;
	private Vector3f hatchHoldingPosition;
	private VehicleControl robotControl;
	private CollisionShape robotShape;
	boolean hatchPickedUp = false;
	private final float Z_GRAVITY = -9.81f;
	private PairedDouble accelerationValueLeft = PairedDoubleFactory.getInstance().createPairedDouble("leftSideDrives",
			true, 0.0);
	private PairedDouble accelerationValueRight = PairedDoubleFactory.getInstance()
			.createPairedDouble("rightSideDrives", true, 0.0);
	private PairedDouble leftEncoder = PairedDoubleFactory.getInstance().createPairedDouble("leftEncoder", false, 0.0);
	private PairedDouble rightEncoder = PairedDoubleFactory.getInstance().createPairedDouble("rightEncoder", false,
			0.0);
	private Vector3f lastLeftLocation = Vector3f.ZERO;
	private Vector3f lastRightLocation = Vector3f.ZERO;
	private float lifterChange = FastMath.HALF_PI / 3;
	private float leadScrewPosition = 2f;
	private float leadScrewChange = 0.25f;
	private boolean lifterMoveDown = false;
	private boolean lifterMoveUp = false;
	private boolean leadScrewDown = false;
	private boolean leadScrewUp = false;
	private final float ROBOT_ACCELERATION = 150f;

	private final ActionListener actionListener = new ActionListener() {

		@Override
		public void onAction(String name, boolean pressed, float tpf) {
			if (name.equals("leftDrivesForward") && pressed) {
				accelerationValueLeft.value = 1;
			} else if (name.equals("leftDrivesBackward") && pressed) {
				accelerationValueLeft.value = -1;
			} else if (name.indexOf("leftDrives") != -1 && !pressed) {
				accelerationValueLeft.value = 0;
			}

			else if (name.equals("rightDrivesForward") && pressed) {
				accelerationValueRight.value = 1;
			} else if (name.equals("rightDrivesBackward") && pressed) {
				accelerationValueRight.value = -1;
			} else if (name.indexOf("rightDrives") != -1 && !pressed) {
				accelerationValueRight.value = 0;
			}

			else if (name.equals("pause") && pressed) {
				if (app.isPaused()) {
					app.resume();
				} else {
					app.pause();
				}
			}

			else if (name.equals("reset") && pressed) {
				robotControl.setPhysicsRotation(new Quaternion(3, 0, 0, 3));
				robotControl.setPhysicsLocation(new Vector3f(4f, 0f, .5f));
				accelerationValueRight.value = 0;
				accelerationValueLeft.value = 0;
				leadScrewPosition = 2f;
				habLifter1.setLocalRotation(new Quaternion().fromAngles(FastMath.HALF_PI * 2, 0f, FastMath.HALF_PI * 2));
				habLifter2.setLocalRotation(new Quaternion().fromAngles(FastMath.HALF_PI * 2, 0f, FastMath.HALF_PI * 2));
				if (linkedHatch != null) {
					unlinkHatch((Spatial) linkedHatch.getUserObject());
				}
			}

			else if (name.equals("pickupHatch") && pressed) {
				detectHatch();
			} else if (name.equals("dropHatch") && pressed) {
				if (linkedHatch != null) {
					unlinkHatch((Spatial) linkedHatch.getUserObject());
				}
			}

			else if (name.equals("printInfo") && pressed) {
				printRotation(robotControl.getPhysicsRotation());
			}
		}
	};

	public Spatial getRobotBase() {
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
		Geometry robot_geo = (Geometry) ((Node) ((Node) ((Node) robotBase).getChild(0)).getChild(0)).getChild(0);
		robot_geo.setLocalRotation(new Quaternion(1, 0, 0, 1));
		((CompoundCollisionShape) robotShape)
				.addChildShape(new BoxCollisionShape(new Vector3f(.3302f, .09355f, .3302f)), new Vector3f(0f, 0f, 0f));
		robotControl = new VehicleControl(robotShape, 60);
		robotNode.attachChild(robotBase);
		robotNode.addControl(robotControl);

		Material yellowColor = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		yellowColor.setBoolean("UseMaterialColors", true);
		yellowColor.setColor("Diffuse", ColorRGBA.Yellow);

		Material orangeColor = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		orangeColor.setBoolean("UseMaterialColors", true);
		orangeColor.setColor("Diffuse", ColorRGBA.Orange);

		habClimberNode = new Node("climbingNode");
		habLifter1 = assetManager.loadModel("Models/RobotBase/habLifter1.blend");
		habLifter1.scale(0.15f);
		habLifter1.setLocalTranslation(0.25f, 0, .31f);
		habLifter1.rotate(FastMath.HALF_PI * 2, 0, FastMath.HALF_PI * 2);
		habLifter1.setMaterial(yellowColor);
		habClimberNode.attachChild(habLifter1);

		habLifter2 = assetManager.loadModel("Models/RobotBase/habLifter1.blend");
		habLifter2.scale(0.15f);
		habLifter2.setLocalTranslation(-0.25f, 0f, .31f);
		habLifter2.rotate(FastMath.HALF_PI * 2, 0f, FastMath.HALF_PI * 2);
		habLifter2.setMaterial(orangeColor);
		habClimberNode.attachChild(habLifter2);

		leadScrew = assetManager.loadModel("Models/RobotBase/leadScrew.blend");
		leadScrew.setLocalTranslation(4f, 0, leadScrewPosition);
		leadScrew.scale(0.3f);
		leadScrew.rotate(FastMath.HALF_PI, 0, 0);
		leadScrew.setMaterial(yellowColor);
		rootNode.attachChild(leadScrew);
		habClimberNode.attachChild(leadScrew);

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
		robotNode.attachChild(habClimberNode);
		rootNode.attachChild(robotNode);
		app.getPhysicsSpace().add(robotNode);

		robotControl.steer(0, -.25f);
		robotControl.steer(1, .25f);
		robotControl.steer(2, .25f);
		robotControl.steer(3, -.25f);

		getControls();
		// app.pause();
	}

	@Override
	public void update(float tpf) {
		robotControl.accelerate(0, (float) (ROBOT_ACCELERATION * accelerationValueLeft.value));
		robotControl.accelerate(2, (float) (ROBOT_ACCELERATION * accelerationValueLeft.value));
		robotControl.accelerate(1, (float) (ROBOT_ACCELERATION * accelerationValueRight.value));
		robotControl.accelerate(3, (float) (ROBOT_ACCELERATION * accelerationValueRight.value));
		Vector3f currentLeftLocation = robotControl.getPhysicsLocation();
		Vector3f currentRightLocation = robotControl.getPhysicsLocation();
		float[] offsets = getOffsets(.25f);
		float xOffset = offsets[0];
		float yOffset = offsets[1];

		currentLeftLocation.setX(currentLeftLocation.getX() + xOffset);
		currentLeftLocation.setY(currentLeftLocation.getY() + yOffset);
		currentLeftLocation.setZ(currentLeftLocation.getZ() + 0.75f);

		currentRightLocation.setX(currentRightLocation.getX() - xOffset);
		currentRightLocation.setY(currentRightLocation.getY() - yOffset);
		currentRightLocation.setZ(currentRightLocation.getZ() + 0.75f);

		leftEncoder.value += currentLeftLocation.distance(lastLeftLocation);
		rightEncoder.value += currentRightLocation.distance(lastRightLocation);

		lastLeftLocation = currentLeftLocation;
		lastRightLocation = currentRightLocation;
		if (lifterMoveDown) {
			habLifter1.rotate(-lifterChange * tpf, 0f, 0f);
			habLifter2.rotate(-lifterChange * tpf, 0f, 0f);
		}
		if (lifterMoveUp) {
			habLifter1.rotate(lifterChange * tpf, 0f, 0f);
			habLifter2.rotate(lifterChange * tpf, 0f, 0f);
		}
		if (leadScrewDown) {
			leadScrewPosition -= leadScrewChange * tpf;
		}
		if (leadScrewUp) {
			leadScrewPosition += leadScrewChange * tpf;
		}

		if(leadScrewUp || leadScrewDown)
		{
			leadScrew.setLocalTranslation(leadScrew.getLocalTranslation().x, leadScrewPosition,
					leadScrew.getLocalTranslation().z);
		}

		if (linkedHatch != null) {
			hatchHoldingPosition = locInFrontOfRobot(0.5f);
			hatchPickedUp = false;
			linkedHatch.setGravity(new Vector3f(0, 0, 0));

			pickupHatch();

			if (distanceTo(robotControl.getPhysicsLocation(), linkedHatch.getPhysicsLocation()) > HATCH_PICKUP_RANGE) {
				unlinkHatch((Spatial) linkedHatch.getUserObject());
			}
		}
	}

	private Vector3f locInFrontOfRobot(float offset) {
		Vector3f holdingPosition = robotControl.getPhysicsLocation();
		float robotZRot = robotControl.getPhysicsRotation().toAngles(null)[2];
		float xOffset = 0;
		float yOffset = 0;

		if (robotZRot > 0) {
			xOffset = (float) (Math.sin(Math.abs(robotZRot)) * offset);
		} else {
			xOffset = -1f * (float) (Math.sin(Math.abs(robotZRot)) * offset);
		}

		if (robotControl.getPhysicsRotation().toAngles(null)[0] > 0) {
			yOffset = -1f * (float) (Math.cos(robotZRot) * offset);
		} else {
			yOffset = (float) (Math.cos(robotZRot) * offset);
		}

		holdingPosition.setX(holdingPosition.getX() + xOffset);
		holdingPosition.setY(holdingPosition.getY() + yOffset);
		holdingPosition.setZ(holdingPosition.getZ() + 0.75f);

		return holdingPosition;
	}

	private float[] getOffsets(float mainOffset) {
		float robotZRot = robotControl.getPhysicsRotation().toAngles(null)[2] + FastMath.HALF_PI;
		float xOffset = 0;
		float yOffset = 0;

		if (robotControl.getPhysicsRotation().toAngles(null)[0] > 0) {
			xOffset = (float) (FastMath.sin(FastMath.abs(robotZRot)) * mainOffset);
		} else {
			xOffset = -1f * (float) (FastMath.sin(FastMath.abs(robotZRot)) * mainOffset);
		}

		yOffset = -1 * (float) (Math.cos(robotZRot) * mainOffset);
		return new float[] { xOffset, yOffset };
	}

	public float[] getRotation() {
		return robotControl.getPhysicsRotation().toAngles(null);
	}

	private void printRotation(Quaternion quat) {
		System.out.println("\nAngles[0]: " + quat.toAngles(null)[0]);
		System.out.println("Angles[1]: " + quat.toAngles(null)[1]);
		System.out.println("Angles[2]: " + quat.toAngles(null)[2]);
	}

	@Override
	protected void cleanup(Application _app) {

	}

	private void detectHatch() {
		FieldAppState field = app.getStateManager().getState(FieldAppState.class);
		ArrayList<RigidBodyControl> hatchList = field.getHatchCtrlList();

		float smallest = Float.MAX_VALUE;
		RigidBodyControl operatingCtrl = null;

		for (RigidBodyControl ctrl : hatchList) {
			float distance = distanceTo(robotControl.getPhysicsLocation(), ctrl.getPhysicsLocation());

			if (distance < HATCH_PICKUP_RANGE && distance < smallest && robotIsFacingHatch(ctrl)) {
				smallest = distance;
				operatingCtrl = ctrl;
			}
		}

		linkedHatch = operatingCtrl;
	}

	private boolean robotIsFacingHatch(RigidBodyControl hatch) {
		float distanceInFront = 1f;
//		Vector3f rotCol = robotControl.getPhysicsRotation().getRotationColumn(2);
		while (distanceInFront < HATCH_PICKUP_RANGE) {
			if (distanceTo(hatch.getPhysicsLocation(), locInFrontOfRobot(distanceInFront)) < 1f) {
				return true;
			}
			distanceInFront += 0.1;
		}
		return false;
	}

	private void unlinkHatch(Spatial hatchSpatial) {
		if (linkedHatch != null) {
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
		Vector3f translateVector = createItemTranslationVector(robotControl, linkedHatch).mult(TRANSLATE_SPEED);
		Quaternion translateQuat = createItemRotationQuaternion(robotControl, linkedHatch);
		linkedHatch.setPhysicsLocation(linkedHatch.getPhysicsLocation().add(translateVector));
		linkedHatch.setPhysicsRotation(translateQuat);

	}

	private Quaternion createItemRotationQuaternion(VehicleControl robot, RigidBodyControl item) {
		// I made this by mistake but it works so �\_(O_O)_/�
		float robotYRot = robot.getPhysicsRotation().toAngles(null)[1];
		return new Quaternion(new float[] { robotYRot, robot.getPhysicsRotation().toAngles(null)[1],
				robot.getPhysicsRotation().toAngles(null)[2] });
	}

	private Vector3f createItemTranslationVector(VehicleControl robot, RigidBodyControl item) {
		Vector3f ret = new Vector3f();
		Vector3f itemLoc = item.getPhysicsLocation();
		ret.setX(hatchHoldingPosition.getX() - itemLoc.getX());
		ret.setY(hatchHoldingPosition.getY() - itemLoc.getY());
		ret.setZ(hatchHoldingPosition.getZ() - itemLoc.getZ());

		return ret;
	}

	private float distanceTo(Vector3f loc1, Vector3f loc2) {
		float xDist = (float) Math.pow(loc1.getX() - loc2.getX(), 2);
		float yDist = (float) Math.pow(loc1.getY() - loc2.getY(), 2);
		float zDist = (float) Math.pow(loc1.getZ() - loc2.getZ(), 2);
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
		for (int i = 0; i < 4; i++) {
			Node wheelNode = new Node("wheel " + i + " node");

			if (i % 2 == 0) {
				sideWheel = 1;
			} else {
				sideWheel = -1;
			}
			if (i > 1) {
				backWheel = -1;
			} else {
				backWheel = 1;
			}
			Vector3f position = new Vector3f(sideWheel * xOff, yOff, backWheel * zOff);
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
		manager.addMapping("printInfo", new KeyTrigger(KeyInput.KEY_Z));
		manager.addMapping("lifterDown", new KeyTrigger(KeyInput.KEY_Z));
		manager.addMapping("lifterUp", new KeyTrigger(KeyInput.KEY_X));
		manager.addMapping("leadScrewDown", new KeyTrigger(KeyInput.KEY_C));
		manager.addMapping("leadScrewUp", new KeyTrigger(KeyInput.KEY_V));

		manager.addListener(actionListener, "leftDrivesForward", "leftDrivesBackward", "rightDrivesForward",
				"rightDrivesBackward", "pause", "reset", "pickupHatch", "dropHatch", "printInfo");
		manager.addListener(keyListener, "lifterDown", "lifterUp","leadScrewUp","leadScrewDown");
	}

	private final ActionListener keyListener = new ActionListener() {
		@Override

		public void onAction(String name, boolean keyPressed, float tpf) {

			if (keyPressed) {
				if (name.equals("lifterDown")) {
					lifterMoveDown = true;
				} else if (name.equals("lifterUp")) {
					lifterMoveUp = true;
				} else if (name.equals("leadScrewDown")) {
					leadScrewDown = true;
				} else if (name.equals("leadScrewUp")) {
					leadScrewUp = true;
				}
			} else {
				if (name.equals("lifterDown")) {
					lifterMoveDown = false;
				} else if (name.equals("lifterUp")) {
					lifterMoveUp = false;
				} else if (name.equals("leadScrewDown")) {
					leadScrewDown = false;
				} else if (name.equals("leadScrewUp")) {
					leadScrewUp = false;
				}
			}

		}
	};

	public void setDrivesPowerLeft(float power) {
		accelerationValueLeft.value = power;
	}

	public double getDrivesPowerLeft() {
		return accelerationValueLeft.value;
	}

	@Override
	protected void onDisable() {

	}

	public void setDrivesPowerRight(float power) {
		accelerationValueRight.value = power;
	}

	public double getDrivesPowerRight() {
		return accelerationValueRight.value;
	}

	@Override
	protected void onEnable() {

	}

}
