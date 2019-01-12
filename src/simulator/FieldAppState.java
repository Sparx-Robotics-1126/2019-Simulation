package simulator;


import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.FastMath;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.plugins.blender.BlenderLoader;

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
		Node catchNet = new Node();
		CollisionShape plane = new PlaneCollisionShape();
		RigidBodyControl ctrl = new RigidBodyControl(0);

		rootNode.addControl(ctrl);
		
		AssetManager assetManager = app.getAssetManager();
		assetManager.registerLocator("assets.zip", ZipLocator.class);
		assetManager.registerLoader(BlenderLoader.class, "blend");
		
		Spatial field = assetManager.loadModel("assets/Models/Field/FullField.blend");
		rootNode.attachChild(field);
		field.rotate(FastMath.PI / 2, 0, 0);
		CollisionShape fieldShape = CollisionShapeFactory.createDynamicMeshShape(field);
		RigidBodyControl ctrl2 = new RigidBodyControl(fieldShape);
		ctrl2.setKinematic(true);
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
