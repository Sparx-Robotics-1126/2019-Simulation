package simulator;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;

public class Arm {
			
		Object motor = new motor();
		
		public void initDefaultCommand() {
			
		}
		
		public void Close() {
			setTimeout(.5);
		}
		private void setTimeout(double d) {
			
		}
		protected void initialize(){
			Close1();
		}
		private void Close1() {
		}
		protected void execute() {
		}
		protected boolean isFinished() {
			return false;
		}
		protected boolean isTimedout() {
			return false;
		}
		protected void end() {
		}
		protected void interrupted() {
			end();
		}
		public void Open() {
			setTimeout(.5);
		}
		protected void initialize1(){
			Open1();
		}
		private void Open1() {
		}
		protected void execute1() {
		}
		protected boolean isFinished1() {
			return false;
		}
		protected boolean isTimedout1() {
			return false;
		}
		protected void end1() {
		}
		protected void interrupted1() {
			end();
		}
		
		
		@SuppressWarnings({ "unused", "null" })
		private void getControls() {
			Object app = null;
			InputManager manager = ((no) app).getInputManager();
			manager.addMapping("open", new KeyTrigger(KeyInput.KEY_Z));
			manager.addMapping("close", new KeyTrigger(KeyInput.KEY_X));
			
				
		}
}
