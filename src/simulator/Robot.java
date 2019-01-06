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
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

public class Robot extends BaseAppState {
	private Spatial robotBase;
	private SimpleApplication app;
	float horizVelocity = 0f;
	float vertVelocity = 0f;
	@Override
	public void update(float tpf) {      
    	robotBase.move(horizVelocity, vertVelocity, 0f);
    	horizVelocity *= 0.95f;
    	vertVelocity *= 0.95f;
	}

	@Override
	protected void cleanup(Application _app) {

	}

	@Override
	protected void initialize(Application _app) {
		app = (SimpleApplication) _app;
		Node rootNode = app.getRootNode();

		AssetManager assetManager = app.getAssetManager();
		assetManager.registerLocator("assets.zip", ZipLocator.class);
		assetManager.registerLoader(BlenderLoader.class, "blend");
		
		robotBase = assetManager.loadModel("assets/Models/RobotBase/RobotBase.blend");
		rootNode.attachChild(robotBase);
		robotBase.rotate(FastMath.PI / 2, 0, 0);
		
		
		Box floor = new Box(6f,10f, 0.1f);
        Geometry floorGeom = new Geometry("Floor", floor);
        Material floorMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        floorMat.setBoolean("UseMaterialColors",true);
        floorMat.setColor("Diffuse", new ColorRGBA(0f,0.6f,0f,1));
        floorMat.setColor("Specular", ColorRGBA.White);
        floorGeom.setMaterial(floorMat);
        rootNode.attachChild(floorGeom);
       
        
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
	
	private void getControls() {
		InputManager manager = app.getInputManager();
		
		manager.addMapping("forwardMove", new KeyTrigger(KeyInput.KEY_W));
		manager.addMapping("backwardMove", new KeyTrigger(KeyInput.KEY_S));
		manager.addMapping("rightMove", new KeyTrigger(KeyInput.KEY_D));
		manager.addMapping("leftMove", new KeyTrigger(KeyInput.KEY_A));
		
		manager.addListener(analogListener, "forwardMove", "backwardMove", "rightMove", "leftMove");

	}
	
	private final AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
        	final float movementSpeed = 0.007f;
            if (name.equals("forwardMove")) {
            	vertVelocity -= movementSpeed;
            } else if(name.equals("backwardMove")) {
            	vertVelocity += movementSpeed;
            } else if(name.equals("leftMove")) {
            	horizVelocity += movementSpeed;
            } else if(name.equals("rightMove")) {
            	horizVelocity -= movementSpeed;
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
