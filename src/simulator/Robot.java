package simulator;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.plugins.blender.BlenderLoader;
import com.jme3.scene.shape.Cylinder;

public class Robot extends BaseAppState {
	private SimMain app;
	private Node robotNode;
	private Spatial robotBase;
	private VehicleControl robotControl;
	float accelerationValue = 0f;
	float steeringValue = 0f;

	private final float robotAcceleration = 2f;

	private final AnalogListener analogListener = new AnalogListener() {
		@Override
		public void onAnalog(String name, float value, float tpf) {
			if (name.equals("forwardMove")) {
				accelerationValue += robotAcceleration;
			} else if(name.equals("backwardMove")) {
				accelerationValue -= robotAcceleration;
			} 
			if(name.equals("leftMove")) {
				steeringValue += 5f;
			} else if(name.equals("rightMove")) {
				steeringValue -= 5f;
			}
			if(name.equals("pause")) {
				if(app.isPaused()) {
					app.resume();
					System.out.println("Unpausing");
				} else{
					app.pause();
					System.out.println("Pausing");
				}
			}
		}
	};

	@Override
	protected void initialize(Application _app) {

		app = (SimMain) _app;
		Node rootNode = app.getRootNode();

		app.getPhysicsSpace().setGravity(new Vector3f(0f, 0f, -9.81f));
		AssetManager assetManager = app.getAssetManager();
		assetManager.registerLocator("assets.zip", ZipLocator.class);
		assetManager.registerLoader(BlenderLoader.class, "blend");


		robotNode = new Node("vehicleNode");
		robotBase = assetManager.loadModel("assets/Models/RobotBase/RobotBase.blend");
		CollisionShape robotShape = CollisionShapeFactory.createMeshShape(robotBase);
		robotControl = new VehicleControl(robotShape);
		robotNode.attachChild(robotBase);

		robotBase.addControl(robotControl);

		float stiffness = 15.0f;
		float compValue = .5f;
		float dampValue = .7f;
		robotControl.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
		robotControl.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
		robotControl.setSuspensionStiffness(stiffness);
		robotControl.setMaxSuspensionForce(10000.0f);		

		robotControl.setPhysicsLocation(new Vector3f(6f, 2f, 5f));
//		robotControl.setPhysicsRotation(new Quaternion(5, 0, 5, 3));
		addWheels();

		rootNode.attachChild(robotBase);
		app.getPhysicsSpace().add(robotBase);


		// You must add a light to make the model visible
		DirectionalLight sun = new DirectionalLight();
		sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
		sun.setColor(new ColorRGBA(.5f,.5f,.5f,1));
		DirectionalLight sun2 = new DirectionalLight();
		sun2.setDirection(new Vector3f(0.1f, 0.7f, -1.0f));
		sun2.setColor(new ColorRGBA(.4f,.4f,.4f,1));
		DirectionalLight sun3 = new DirectionalLight();
		sun3.setDirection(new Vector3f(0.1f, 0.7f, 1.0f));
		sun3.setColor(new ColorRGBA(.3f,.3f,.3f,1));

		rootNode.addLight(sun);
		rootNode.addLight(sun2);
		rootNode.addLight(sun3);
		getControls();
	}

	@Override
	public void update(float tpf) {      
		//    	Vector3f forward = robotBase.getLocalRotation().mult(Vector3f.UNIT_Z).multLocal(vertVelocity).multLocal(tpf);
		//    	robotControl.setLinearVelocity(robotControl.getLinearVelocity().add(forward));
		//    	robotControl.setAngularVelocity(robotControl.getAngularVelocity().add(new Vector3f(0f, 0f, rotate * tpf)));
		//    	rotate *= .05f;

		robotControl.accelerate(accelerationValue);
		robotControl.steer(steeringValue);

		steeringValue *= 0.5f;
		if(steeringValue < 0.01f && steeringValue > -0.01f) {
			steeringValue = 0f;
		}
		accelerationValue *= 0.75f;
		if(accelerationValue < 0.01f && accelerationValue > -.01f) {
			accelerationValue = 0;
		}
	}

	@Override
	protected void cleanup(Application _app) {

	}

	private void addWheels() {
		Vector3f wheelDirection = new Vector3f(0, -1, 0);
		Vector3f wheelAxle = new Vector3f(-1, 0, 0);
		float radius = .5f;
		float restLength = .3f;
		float yOff = .6f;
		float xOff = .4f;
		float zOff = .4f;

		Mesh wheelMesh = new Cylinder(16, 16, radius, radius * .06f, true);
		int sideWheel;
		int backWheel;
		for(int i = 0; i < 4; i++) {
			Node wheelNode = new Node("wheel 1 node");
			Geometry wheel = new Geometry("wheel 1", wheelMesh);
			wheelNode.attachChild(wheel);
			//			wheel.rotate(0f, FastMath.HALF_PI, 0f);
			//			wheel.setMaterial(mat);

			robotNode.attachChild(wheelNode);
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
			robotControl.addWheel(wheelNode, new Vector3f(sideWheel*xOff, yOff, backWheel*zOff), wheelDirection, wheelAxle, restLength, radius, backWheel == 1);
		}
	}

	private void getControls() {
		InputManager manager = app.getInputManager();

		manager.addMapping("forwardMove", new KeyTrigger(KeyInput.KEY_W));
		manager.addMapping("backwardMove", new KeyTrigger(KeyInput.KEY_S));
		manager.addMapping("rightMove", new KeyTrigger(KeyInput.KEY_D));
		manager.addMapping("leftMove", new KeyTrigger(KeyInput.KEY_A));
		manager.addMapping("pause", new KeyTrigger(KeyInput.KEY_0));

		manager.addListener(analogListener, "forwardMove", "backwardMove", "rightMove", "leftMove");

	}

	@Override
	protected void onDisable() {

	}

	@Override
	protected void onEnable() {

	}


}
