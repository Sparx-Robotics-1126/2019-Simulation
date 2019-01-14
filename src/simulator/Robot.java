package simulator;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
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
import com.jme3.texture.Texture;

public class Robot extends BaseAppState {
	private SimMain app;
	private Node robotNode;
	private Spatial robotBase;
	private VehicleControl robotControl;
	//	private CompoundCollisionShape robotShape;
	private CollisionShape robotShape;
	float accelerationValue = 0f;
	float steeringValue = 0f;

	private final float robotAcceleration = 400f;

	private final ActionListener actionListener = new ActionListener() {

		@Override
		public void onAction(String name, boolean pressed, float tpf) {
			if (name.equals("forwardMove")) {
				if(pressed) {
					accelerationValue = robotAcceleration;
				}
			} else if(name.equals("backwardMove") && pressed) {
				if(pressed) {
					accelerationValue = -robotAcceleration;
				}
			}
			if(name.equals("leftMove") && pressed) {
					steeringValue += .3f;
			} else if(name.equals("rightMove") && pressed){
				steeringValue -= .3f;			
			}
			if(name.equals("pause")) {
				app.pause();
			} else if(name.equals("resume")){
				app.resume();
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
		robotShape = new CompoundCollisionShape();
		//		robotShape.addChildShape(new BoxCollisionShape(new Vector3f(.4f, .5f, .4f)), new Vector3f(0, .1f, 0));
		robotShape = CollisionShapeFactory.createDynamicMeshShape(robotBase);
		robotControl = new VehicleControl(robotShape, 30);
		robotNode.attachChild(robotBase);

		robotBase.addControl(robotControl);

		float stiffness = 150.0f;
		float compValue = .4f;
		float dampValue = .5f;
		robotControl.setSuspensionCompression(compValue * 2.0f * FastMath.sqrt(stiffness));
		robotControl.setSuspensionDamping(dampValue * 2.0f * FastMath.sqrt(stiffness));
		robotControl.setSuspensionStiffness(stiffness);
		robotControl.setMaxSuspensionForce(1000.0f);		

		robotControl.setPhysicsRotation(new Quaternion(3, 0, 0, 3));
		robotControl.setPhysicsLocation(new Vector3f(4f, 0f, .5f));
		addWheels();
		robotBase.rotate(FastMath.HALF_PI, 0f, 0f);
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
		app.pause();
	}

	@Override
	public void update(float tpf) {      
		//    	Vector3f forward = robotBase.getLocalRotation().mult(Vector3f.UNIT_Z).multLocal(vertVelocity).multLocal(tpf);
		//    	robotControl.setLinearVelocity(robotControl.getLinearVelocity().add(forward));
		//    	robotControl.setAngularVelocity(robotControl.getAngularVelocity().add(new Vector3f(0f, 0f, rotate * tpf)));
		//    	rotate *= .05f;

		robotControl.accelerate(accelerationValue);
		robotControl.steer(steeringValue);
		
		accelerationValue *= .7;
		steeringValue *= .1;
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
			Node wheelNode = new Node("wheel " + i + " node");
			Spatial wheel = new Geometry("wheel " + i, wheelMesh);
//			wheel = app.getAssetManager().loadModel("assets/Models/RobotBase/BadWheel.blend");
			Material cube = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
			Texture texture = app.getAssetManager().loadTexture("assets/Models/RobotBase/BlueMat.jpg");
			cube.setTexture("ColorMap", texture);			
			wheel.setMaterial(cube);
			wheel.rotate(0f, FastMath.HALF_PI, 0f);
			
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
			robotControl.addWheel(wheelNode, position, wheelDirection, wheelAxle, restLength, radius, backWheel == 1);
			wheelNode.attachChild(wheel);
			robotNode.attachChild(wheelNode);
		}
	}

	private void getControls() {
		InputManager manager = app.getInputManager();

		manager.addMapping("forwardMove", new KeyTrigger(KeyInput.KEY_W));
		manager.addMapping("backwardMove", new KeyTrigger(KeyInput.KEY_S));
		manager.addMapping("rightMove", new KeyTrigger(KeyInput.KEY_D));
		manager.addMapping("leftMove", new KeyTrigger(KeyInput.KEY_A));
		manager.addMapping("pause", new KeyTrigger(KeyInput.KEY_P));
		manager.addMapping("resume", new KeyTrigger(KeyInput.KEY_SPACE));

		manager.addListener(actionListener, "forwardMove", "backwardMove", "rightMove", "leftMove", "pause", "resume");

	}

	@Override
	protected void onDisable() {

	}

	@Override
	protected void onEnable() {

	}


}
