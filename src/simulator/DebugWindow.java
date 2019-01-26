package simulator;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.DefaultScreenController;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.elements.render.TextRenderer;


class DebugWindow extends BaseAppState    {
	Nifty nifty;
	Screen textScreen;
	String debugText = new String();
	Vector3f lastPosition;
	Robot robot;
	float speed;
	static DebugWindow myself = null;
	public static DebugWindow getInstance() {
		return myself;
	}
	
	public void log(String logLine) {
		debugText += logLine + "\n";
		Element element = nifty.getScreen("debug").findElementById(
				"Text ID");
		TextRenderer textRenderer = element.getRenderer(TextRenderer.class);
		textRenderer.setText(debugText);
	}
	
	@Override
	protected void cleanup(Application arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void initialize(Application _app) {
		myself = this;
		SimpleApplication app = (SimpleApplication) _app;
		robot = app.getStateManager().getState(Robot.class);
		
		NiftyJmeDisplay niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(
		        getApplication().getAssetManager(),
		        getApplication().getInputManager(),
		        getApplication().getAudioRenderer(),
		        getApplication().getGuiViewPort());
	
			nifty = niftyDisplay.getNifty();
			getApplication().getGuiViewPort().addProcessor(niftyDisplay);
	
			nifty.addScreen("hideDebug", new ScreenBuilder("hideDebug") {{
			    controller(new DefaultScreenController());
			    }}.build(nifty));
			
			nifty.addScreen("debug", new ScreenBuilder("debug") {{
			    controller(new DefaultScreenController());
			    // layer added
			    
			   layer(new LayerBuilder("Layer_ID") {{
				   childLayoutCenter();
				   
				   
//				   filler panel
			        panel(new PanelBuilder() {{
			            childLayoutHorizontal();
			            alignLeft();
			            valignTop();
			            height("50%");
			            width("50%");
			           
			  
	//		    	always show
			        panel(new PanelBuilder() {{
			            childLayoutHorizontal();
			            alignLeft();
			            valignTop();
			            height("50%");
			            width("25%");
			            backgroundColor("00ff00");
			            // add text
//			            text(new TextBuilder("Speed Text") {{
//			                text("the speed = " + speed);
//				            textHAlignCenter();
//				            valignCenter();
//					        font("Interface/Fonts/Default.fnt");
//			                wrap(true);
//			                color("#000000");
//			                onActiveEffect(new EffectBuilder("textSize"){{
//	                            effectParameter("endSize", "1.5");
//	                        }});
			                
	//		        scroll text
			        panel(new PanelBuilder() {{
			            childLayoutHorizontal();
			            alignRight();
			            valignTop();
			            height("50%");
			            width("25%");
			            backgroundColor("#0000FF");
			            // add text
//			            text(new TextBuilder("Text ID") {{
//			                text("scroll\n"
//	                		+ "scroll\n"
//	                		+ "scroll\n"
//	                		+ "scroll");
//				            textHAlignLeft();
//				            textVAlignBottom();
//					        font("Interface/Fonts/Default.fnt");
//			                wrap(true);
//			                color("#000000");
//			                onActiveEffect(new EffectBuilder("textSize"){{
//	                            effectParameter("endSize", "1.5");
//	                    }});
			       
	           
//			        scroll panel
	        }});
//	            always show text
        }});
//	        always show panel
    }});
//        filler panel
   }});
//		layer 	   
		}}.build(nifty));
		
		InputManager manager = app.getInputManager();
		manager.addMapping("t", new KeyTrigger(KeyInput.KEY_T));
		manager.addListener(keyListener,"t");
		   
	};
		
	private final ActionListener keyListener = new ActionListener() {
		boolean hudVisible;	
			
		public void onAction(String name, boolean keyPressed, float tpf) {
			if (name.equals("t") && keyPressed) {
				if(hudVisible == false) {
				    nifty.gotoScreen("debug");	
				    hudVisible = true;
				} else {
					nifty.gotoScreen("hideDebug");
					hudVisible = false;
				}
			}
		}
	};
	public void update(float tpf) {      
		if (lastPosition != null) {
			float distance = robot.getRobotBase().getWorldTranslation().distance(lastPosition); 
			speed = distance/tpf;
			Element element = nifty.getScreen("debug").findElementById(
					"Speed Text");
//			TextRenderer textRenderer = element.getRenderer(TextRenderer.class);
//			textRenderer.setText("the speed = " + Float.toString(speed) 
//			+ " the distance is " + Float.toString(distance));
		}
		lastPosition = robot.getRobotBase().getWorldTranslation();
	}
	@Override
	protected void onDisable() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onEnable() {
		// TODO Auto-generated method stub

	}
}	 
		
