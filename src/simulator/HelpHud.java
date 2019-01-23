package simulator;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.screen.DefaultScreenController;



public class HelpHud extends BaseAppState {
	Nifty nifty;

	@Override
	protected void cleanup(Application arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void initialize(Application _app) {
		SimpleApplication app = (SimpleApplication) _app;
		
		NiftyJmeDisplay niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(
		        getApplication().getAssetManager(),
		        getApplication().getInputManager(),
		        getApplication().getAudioRenderer(),
		        getApplication().getGuiViewPort());

		nifty = niftyDisplay.getNifty();
		getApplication().getGuiViewPort().addProcessor(niftyDisplay);

		nifty.loadStyleFile("nifty-default-styles.xml");
		nifty.loadControlFile("nifty-default-controls.xml");

		nifty.gotoScreen("start");
		// TODO Auto-generated method stub
		nifty.addScreen("start", new ScreenBuilder("start") {{
		    controller(new DefaultScreenController());
		}}.build(nifty));
		nifty.addScreen("hud", new ScreenBuilder("hud") {{
		    controller(new DefaultScreenController());
		}}.build(nifty));
		
		nifty.addScreen("hide", new ScreenBuilder("hide") {{
		    controller(new DefaultScreenController());
		    }}.build(nifty));
		
		    
		nifty.addScreen("help", new ScreenBuilder("help") {{
		    controller(new DefaultScreenController());
		    // layer added
		    layer(new LayerBuilder("background") {{
		        childLayoutCenter();
		
		    }});
		    
		    layer(new LayerBuilder("foreground") {{
		        childLayoutCenter();
		       

		        panel(new PanelBuilder("panel_mid") {{
		            childLayoutVertical();
		            alignCenter();
		            height("100%");
		            width("90%");
		   
		            // add text
		            text(new TextBuilder() {{
		                text("left move forward = q\n "
		                   + "left move back = a\n"
		                   + "right move forward = e\n"
		                   + "right move back = d\n" 
		                   + "pause  = p\n"
		                   + "resume = space\n"
		                   + "(you must resume after you start befor the robot will move)"
		                   + " reset = r\n"
		                   + "help = h\n"
		                   + "move camera = left mouse\n"
		                   + "zoom = middle mouse\n"
		                   + "pick up hatch = f\n"
		                   + "debug window = t");
		                font("Interface/Fonts/Default.fnt");
		                wrap(true);
		                color("#0066ff");
		                height("200%");
		                width("225%");
		                onActiveEffect(new EffectBuilder("textSize"){{
                            effectParameter("endSize", "1.5");
                        }});
		        	}});
		        }});
            }});
		}}.build(nifty));
		
		InputManager manager = app.getInputManager();
		manager.addMapping("h", new KeyTrigger(KeyInput.KEY_H));
		manager.addListener(keyListener,"h");
	};
		
	private final ActionListener keyListener = new ActionListener() {
		boolean hudVisible;	
			
		public void onAction(String name, boolean keyPressed, float tpf) {
			if (name.equals("h") && keyPressed) {
				if(hudVisible == false) {
				    nifty.gotoScreen("help");	
				    hudVisible = true;
				} else {
					nifty.gotoScreen("hide");
					hudVisible = false;
				}
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
		
	

