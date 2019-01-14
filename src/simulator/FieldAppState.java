package simulator;


import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.plugins.blender.BlenderLoader;
import com.jme3.scene.shape.Box;

public class FieldAppState extends BaseAppState {
	private SimMain app;
	
	@Override
	public void update(float tpf) {
        
	}

	@Override
	protected void cleanup(Application _app) {

	}

	@Override
	protected void initialize(Application _app) {
		app = (SimMain) _app;
		Node rootNode = app.getRootNode();
		
		Geometry floor = new Geometry("floor", new Box(10f, 0.1f, 5f));
		RigidBodyControl floorCtrl = new RigidBodyControl(new BoxCollisionShape(new Vector3f(100f, 0.1f, 500f)), 0f);
		floor.addControl(floorCtrl);
		floorCtrl.setPhysicsRotation(new Quaternion(3, 0, 0, 3));
		floorCtrl.setPhysicsLocation(new Vector3f(0, 0, -3f));
		app.getPhysicsSpace().add(floor);
		
		AssetManager assetManager = app.getAssetManager();
		assetManager.registerLocator("assets.zip", ZipLocator.class);
		assetManager.registerLoader(BlenderLoader.class, "blend");
		
		Spatial field = assetManager.loadModel("assets/Models/Field/FieldWithoutFloor.blend");
		rootNode.attachChild(field);
		field.rotate(FastMath.HALF_PI, 0, 0);
		CollisionShape fieldShape = CollisionShapeFactory.createMeshShape(field);
		RigidBodyControl ctrl2 = new RigidBodyControl(fieldShape, 0);
		ctrl2.setKinematic(false);
		field.addControl(ctrl2);
		app.getPhysicsSpace().add(field);
	}
	
	@Override
	protected void onDisable() {

	}

	@Override
	protected void onEnable() {

	}
	
}
