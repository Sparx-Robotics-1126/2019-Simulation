package simulator;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class MoveControl extends AbstractControl{

	private RigidBodyControl objCtrl;
	private float speed = .2f;
	
	public MoveControl(InputManager manager) {
		manager.addMapping("objRight", new KeyTrigger(KeyInput.KEY_RIGHT));
		manager.addMapping("objLeft", new KeyTrigger(KeyInput.KEY_LEFT));
		manager.addMapping("objFwd", new KeyTrigger(KeyInput.KEY_UP));
		manager.addMapping("objBkwd", new KeyTrigger(KeyInput.KEY_DOWN));
		manager.addMapping("objUp", new KeyTrigger(KeyInput.KEY_Z));
		manager.addMapping("objDown", new KeyTrigger(KeyInput.KEY_X));
		manager.addMapping("printPlace", new KeyTrigger(KeyInput.KEY_S));
		
		manager.addListener(new ActionListener(){

			@Override
			public void onAction(String key, boolean pressed, float tpf){
				if(key.equals("objRight") && pressed) {
					objCtrl.setPhysicsLocation(objCtrl.getPhysicsLocation().add(new Vector3f(0, speed, 0)));
				}
				else if(key.equals("objLeft") && pressed) {
					objCtrl.setPhysicsLocation(objCtrl.getPhysicsLocation().add(new Vector3f(0, -speed, 0)));
				}
				else if(key.equals("objFwd") && pressed) {
					objCtrl.setPhysicsLocation(objCtrl.getPhysicsLocation().add(new Vector3f(-speed, 0, 0)));
				}
				else if(key.equals("objBkwd") && pressed) {
					objCtrl.setPhysicsLocation(objCtrl.getPhysicsLocation().add(new Vector3f(speed, 0, 0)));
				}
				else if(key.equals("objUp") && pressed) {
					objCtrl.setPhysicsLocation(objCtrl.getPhysicsLocation().add(new Vector3f(0, 0, speed)));
				}
				else if(key.equals("objDown") && pressed) {
					objCtrl.setPhysicsLocation(objCtrl.getPhysicsLocation().add(new Vector3f(0, 0, -speed)));
				}
				else if(key.equals("printPlace")) {
					System.out.println(objCtrl.getPhysicsLocation());
				}
			}

		}, "objFwd", "objBkwd", "objRight", "objLeft", "objUp", "objDown", "printPlace");
	}
	
	@Override
	protected void controlUpdate(float arg0) {
		// TODO Auto-generated method stub
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		if(spatial != null) {
			objCtrl = spatial.getControl(RigidBodyControl.class);
			objCtrl.setGravity(Vector3f.ZERO);
		}
	}

	@Override
	protected void controlRender(RenderManager arg0, ViewPort arg1) {	
	}

}
