package simulator;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
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
	private RaySensorsControl rays; 
	private AssetManager assetManager;

	private Spatial habLifter1;
	private Spatial habLifter2;
	private Spatial leadScrew;
	private VehicleControl robotControl;
	private CollisionShape robotShape;
	private final float Z_GRAVITY = -9.81f;
	private PairedDouble accelerationValueLeft = PairedDoubleFactory.getInstance().createPairedDouble("leftSideDrives", true, 0.0);
	private PairedDouble accelerationValueRight = PairedDoubleFactory.getInstance().createPairedDouble("rightSideDrives", true, 0.0);
	private PairedDouble leftEncoder = PairedDoubleFactory.getInstance().createPairedDouble("leftEncoder", false, 0.0);
	private PairedDouble rightEncoder = PairedDoubleFactory.getInstance().createPairedDouble("rightEncoder", false, 0.0);
	private Vector3f lastLeftLocation = Vector3f.ZERO;
	private Vector3f lastRightLocation = Vector3f.ZERO;
	private float lifterChange = FastMath.HALF_PI / 3;
	private float leadScrewPosition = -.75f;
	private float leadScrewChange = 0.25f;
	private boolean lifterMoveDown = false;
	private boolean lifterMoveUp = false;
	private boolean leadScrewDown = false;
	private boolean leadScrewUp = false;
	private HatchLogic hatchLogic;
	private PairedDouble pickUpHatch = PairedDoubleFactory.getInstance().createPairedDouble("hatchFlipper", true, 0.0);
	private PairedDouble gearShifter = PairedDoubleFactory.getInstance().createPairedDouble("gearShift", true, 0.0);
	private float rightAngle = FastMath.HALF_PI;
	private float robotAcceleration = 150f;


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
			else if(name.equals("close") && pressed) {
				accelerationValueRight.value = 900f;
				accelerationValueLeft.value = 900f;
			}
			else if(name.equals("open") && pressed) {
				accelerationValueRight.value = 0f;
				accelerationValueLeft.value = 0f;
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
				leadScrew.setLocalTranslation(0f, leadScrewPosition, -0.2f);
				hatchLogic.dropHatch();
				leadScrewPosition = 2f;
				habLifter1.setLocalRotation(new Quaternion().fromAngles(FastMath.HALF_PI * 2, 0f, FastMath.HALF_PI * 2));
				habLifter2.setLocalRotation(new Quaternion().fromAngles(FastMath.HALF_PI * 2, 0f, FastMath.HALF_PI * 2));
				hatchLogic.dropHatch();
			}
			else if(name.equals("pickupHatch") && pressed) {
				hatchLogic.pickupHatch();
			} else if(name.equals("dropHatch") && pressed) {
				hatchLogic.dropHatch();
			}


			else if(name.equals("printInfo") && pressed) {
				System.out.println(SimUtilities.quaternionToString(robotControl.getPhysicsRotation()));
				System.out.println("Hatch location: " + (hatchLogic.getHatch() == null ? " no attached hatch": hatchLogic.getHatch().getPhysicsLocation().toString()));


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
		Geometry robot_geo = (Geometry)((Node)((Node)((Node)robotBase).getChild(0)).getChild(0)).getChild(0);
		((CompoundCollisionShape)robotShape).addChildShape(new CapsuleCollisionShape(.18f, .33f, 2), new Vector3f(-.2f, 0f, 0f));//new BoxCollisionShape(new Vector3f(.3302f, .09355f, .3302f)), new Vector3f(0f, 0f, 0f));
		((CompoundCollisionShape)robotShape).addChildShape(new CapsuleCollisionShape(.18f, .33f, 2), new Vector3f(.2f, 0f, 0f));
		robot_geo.setLocalRotation(new Quaternion(1, 0, 0, 1));
		robotControl = new VehicleControl(robotShape, 60);
		robotNode.attachChild(robotBase);
		robotNode.addControl(robotControl);
		rays = new RaySensorsControl(app, robotControl);
		robotNode.addControl(rays);

		createWheels();

		habClimberNode = new Node("climbingNode");
		habLifter1 = assetManager.loadModel("Models/RobotBase/habLifter1.blend");
		habLifter1.scale(0.15f);
		habLifter1.setLocalTranslation(0.25f, 0, .31f);
		habLifter1.rotate(FastMath.HALF_PI * 2, 0, FastMath.HALF_PI * 2);
		habClimberNode.attachChild(habLifter1);

		habLifter2 = assetManager.loadModel("Models/RobotBase/habLifter1.blend");
		habLifter2.scale(0.15f);
		habLifter2.setLocalTranslation(-0.25f, 0f, .31f);
		habLifter2.rotate(FastMath.HALF_PI * 2, 0f, FastMath.HALF_PI * 2);
		habClimberNode.attachChild(habLifter2);

		leadScrew = assetManager.loadModel("Models/RobotBase/leadScrew.blend");
		leadScrew.setLocalTranslation(0f, leadScrewPosition, -0.2f);
		leadScrew.scale(0.3f);
		leadScrew.rotate(FastMath.HALF_PI * 2, 0, 0);
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


		setUpKeyControls();
		hatchLogic = app.getStateManager().getState(HatchLogic.class);
		setEncoders(false);
	}


	public void toggleRays(boolean toggle) {
		rays.toggleDebugLines(toggle);
	}

	private void createWheels() {
		wheel(.325f,-.15f,0.25f);
		wheel(.325f,-.15f,-0.25f);
		wheel(-0.325f,-.15f,0.25f);
		wheel(-0.325f,-.15f,-0.25f);

	}

	private void wheel(float wheelX, float wheelY, float wheelZ) {
		Material wheelColor = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		wheelColor.setBoolean("UseMaterialColors", true);
		wheelColor.setColor("Diffuse", ColorRGBA.Yellow);

		Cylinder wheel = new Cylinder(100, 100, 0.125f, 0.0625f, true, false);
		Geometry wheelGeometry = new Geometry("wheel", wheel);
		wheelGeometry.setMaterial(wheelColor);
		wheelGeometry.setLocalTranslation(wheelX, wheelY, wheelZ);
		wheelGeometry.rotate(0, FastMath.HALF_PI, 0);
		robotNode.attachChild(wheelGeometry);
	}

	@Override
	public void update(float tpf) {
		robotAcceleration = (float)(gearShifter.value * 150 + 150);
		robotControl.accelerate(0, (float) (robotAcceleration * accelerationValueLeft.value));
		robotControl.accelerate(2, (float) (robotAcceleration * accelerationValueLeft.value));
		robotControl.accelerate(1, (float) (robotAcceleration * accelerationValueRight.value));
		robotControl.accelerate(3, (float) (robotAcceleration * accelerationValueRight.value));

		setEncoders(true);

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

		if (leadScrewUp || leadScrewDown) {
			leadScrew.setLocalTranslation(leadScrew.getLocalTranslation().x, leadScrewPosition,
					leadScrew.getLocalTranslation().z);
		}

		if(pickUpHatch.value == 1) {
			if(hatchLogic.getHatch() == null){
				hatchLogic.pickupHatch();
			} else{
				hatchLogic.dropHatch();
			}
		}
		hatchLogic.update(tpf);
	}

	private void setEncoders(boolean notFirstRun) {
		Vector3f currentLeftLocation = robotControl.getPhysicsLocation();
		Vector3f currentRightLocation = robotControl.getPhysicsLocation();
		float[] offsets = getOffsets(.25f);
		float xOffset = offsets[0];
		float yOffset = offsets[1];

		currentLeftLocation.setX(currentLeftLocation.getX() + xOffset);
		currentLeftLocation.setY(currentLeftLocation.getY() + yOffset);

		currentRightLocation.setX(currentRightLocation.getX() - xOffset);
		currentRightLocation.setY(currentRightLocation.getY() - yOffset);
		if(notFirstRun) {
			leftEncoder.value += currentLeftLocation.distance(lastLeftLocation)*41.52;
			rightEncoder.value += currentRightLocation.distance(lastRightLocation)*41.52;
		}

		lastLeftLocation = currentLeftLocation;
		lastRightLocation = currentRightLocation;



	}


	private float[] getOffsets(float mainOffset) {
		float robotZRot  = robotControl.getPhysicsRotation().toAngles(null)[2] + rightAngle;
		float xOffset = 0;
		float yOffset = 0;

		if(robotControl.getPhysicsRotation().toAngles(null)[0] > 0) {
			xOffset = (float) (FastMath.sin(FastMath.abs(robotZRot)) * mainOffset);
		} else {
			xOffset = -1f * (float) (FastMath.sin(FastMath.abs(robotZRot)) * mainOffset);
		}

		yOffset = -1 * (float) (Math.cos(robotZRot) * mainOffset);
		return new float[] { xOffset, yOffset };
	}

	@Override
	protected void cleanup(Application _app) {

	}

	private void addWheels() {
		Vector3f wheelDirection = new Vector3f(0, -1, 0);
		Vector3f wheelAxle = new Vector3f(-1, 0, 0);
		float radius = .06f;
		float restLength = .1f;
		float yOff = -.1f;
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

	private void setUpKeyControls() {
		InputManager manager = app.getInputManager();

		manager.addMapping("leftDrivesForward", new KeyTrigger(KeyInput.KEY_Q));
		manager.addMapping("leftDrivesBackward", new KeyTrigger(KeyInput.KEY_A));
		manager.addMapping("rightDrivesForward", new KeyTrigger(KeyInput.KEY_E));
		manager.addMapping("rightDrivesBackward", new KeyTrigger(KeyInput.KEY_D));
		manager.addMapping("pause", new KeyTrigger(KeyInput.KEY_P));
		manager.addMapping("reset", new KeyTrigger(KeyInput.KEY_R));
		manager.addMapping("printInfo", new KeyTrigger(KeyInput.KEY_Z));
		manager.addMapping("lifterDown", new KeyTrigger(KeyInput.KEY_Z));
		manager.addMapping("lifterUp", new KeyTrigger(KeyInput.KEY_X));
		manager.addMapping("leadScrewDown", new KeyTrigger(KeyInput.KEY_C));
		manager.addMapping("leadScrewUp", new KeyTrigger(KeyInput.KEY_V));

		manager.addListener(actionListener, "leftDrivesForward", "leftDrivesBackward", "rightDrivesForward",
				"rightDrivesBackward", "pause", "reset", "pickupHatch", "dropHatch", "printInfo");
		manager.addListener(keyListener, "lifterDown", "lifterUp", "leadScrewUp", "leadScrewDown");
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

	public VehicleControl getRobotControl() {
		return robotControl;
	}


	@Override
	protected void onEnable() {

	}
}





