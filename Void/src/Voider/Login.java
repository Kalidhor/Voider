package Voider;

import XMLDialoge.LoginScreenDefinition;
import com.jme3.app.SimpleApplication;
import static com.jme3.app.SimpleApplication.INPUT_MAPPING_EXIT;
import com.jme3.bullet.BulletAppState;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.effect.ParticleEmitter;
import com.jme3.monkeyzone.Globals;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.system.AppSettings;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.builder.ControlBuilder;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.ElementBuilder.ChildLayoutType;
import de.lessvoid.nifty.builder.HoverEffectBuilder;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.PopupBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.StyleBuilder;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.FocusGainedEvent;
import de.lessvoid.nifty.controls.FocusLostEvent;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.controls.TextFieldChangedEvent;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.controls.console.builder.ConsoleBuilder;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.controls.textfield.builder.TextFieldBuilder;
import de.lessvoid.nifty.controls.window.builder.WindowBuilder;
import de.lessvoid.nifty.examples.controls.chatcontrol.ChatControlDialogDefinition;
import de.lessvoid.nifty.examples.controls.common.CommonBuilders;
import de.lessvoid.nifty.examples.controls.common.DialogPanelControlDefinition;
import de.lessvoid.nifty.examples.controls.common.MenuButtonControlDefinition;
import de.lessvoid.nifty.examples.controls.dragndrop.DragAndDropDialogDefinition;
import de.lessvoid.nifty.examples.controls.dropdown.DropDownDialogControlDefinition;
import de.lessvoid.nifty.examples.controls.listbox.ListBoxDialogControlDefinition;
import de.lessvoid.nifty.examples.controls.scrollpanel.ScrollPanelDialogControlDefinition;
import de.lessvoid.nifty.examples.controls.sliderandscrollbar.SliderAndScrollbarDialogControlDefinition;
import de.lessvoid.nifty.examples.controls.textfield.TextFieldDialogControlDefinition;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.input.NiftyInputEvent;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 *  Layer for the login screen.
 *  Maybe it's not the best way to implement this. I've to fix it as soon as possible.
 * @author john (aka Kalidhor)
 * @version 1.1 Beta
 */
public class Login extends SimpleApplication implements ScreenController {
    private static Login app;
    private static LoadScreen load;
    
    public static void main(String[] args) {      
        app = new Login();
        load = new LoadScreen();
        app.setShowSettings(false);
        
      
            app.start();
            app.settings.setResolution(450, 500);
            // We need to restart the gui, so it can assume the changes.
            app.restart();
            app.setDisplayStatView(false);
            app.getInputManager().deleteMapping(INPUT_MAPPING_EXIT);
            app.setDisplayFps(false);
        
    }
    
    protected boolean check;
    protected Nifty nifty;
    protected NiftyJmeDisplay niftyDisplay;
    protected static CommonBuilders builders = new CommonBuilders();
    protected BulletAppState bulletState;
    protected Screen screen;
    protected ButtonBuilder button;
    protected TextField texter;
    protected TextField maintext;    
    protected TextFieldBuilder texts;
    
    public void changeCursor(String path) {
        Texture cursorTexture = assetManager.loadTexture(path);
        Image image = cursorTexture.getImage();
        ByteBuffer imgByteBuff = image.getData(0);
        imgByteBuff.rewind();
        IntBuffer imgintBuff = imgByteBuff.asIntBuffer();
        
        JmeCursor cursor = new JmeCursor();
        cursor.setHeight(image.getHeight());
        cursor.setWidth(image.getWidth());
        cursor.setNumImages(1);
        cursor.setyHotSpot(image.getHeight()-3);
        cursor.setxHotSpot(3);
        cursor.setImagesData(imgintBuff);
        
        inputManager.setMouseCursor(cursor);

        inputManager.setCursorVisible(true);
    }
    
    public AppSettings getSettings() {
        return this.settings;
    }
    
    @Override
    public void simpleInitApp() {
        niftyDisplay = new NiftyJmeDisplay(
            assetManager, inputManager, audioRenderer, guiViewPort);
        nifty = niftyDisplay.getNifty();
        guiViewPort.addProcessor(niftyDisplay);
        
        settings.setMinWidth(800);
        
        settings.setWidth(200);
        
        settings.setResolution(900, 200);
        
        context.getSettings().setWidth(200);
        context.getSettings().setHeight(200);
        
        nifty.loadStyleFile("nifty-default-styles.xml");
        nifty.loadControlFile("nifty-default-controls.xml");
        nifty.registerSound("intro", "Interface/sound/19546__tobi123__Gong_mf2.wav");
        nifty.registerMusic("credits", "Interface/sound/Loveshadow_-_Almost_Given_Up.ogg");
        nifty.registerMouseCursor("hand", "Interface/mouse-cursor-hand.png", 0x05, 0x04);
        nifty.registerEffect("particle", ParticleEmitter.class.getName());
            
        changeCursor("Interface/mouse-cursor-hand.png");
        
        bulletState = new BulletAppState();

        if (Globals.PHYSICS_THREADED) {
            bulletState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        }

        getStateManager().attach(bulletState);
        bulletState.getPhysicsSpace().setAccuracy(Globals.PHYSICS_FPS);

        if (Globals.PHYSICS_DEBUG) {
            bulletState.getPhysicsSpace().enableDebug(assetManager);
        }
        
        // Register some helper controls.
        MenuButtonControlDefinition.register(nifty);
        DialogPanelControlDefinition.register(nifty);

        // Register the dialog controls.
        ListBoxDialogControlDefinition.register(nifty);
        DropDownDialogControlDefinition.register(nifty);
        ScrollPanelDialogControlDefinition.register(nifty);
        ChatControlDialogDefinition.register(nifty);
        TextFieldDialogControlDefinition.register(nifty);
        SliderAndScrollbarDialogControlDefinition.register(nifty);
        DragAndDropDialogDefinition.register(nifty);
        LoginScreenDefinition.register(nifty);

        registerMenuButtonHintStyle(nifty);
        registerStyles(nifty);
        registerConsolePopup(nifty);

        this.screen = startNifty(nifty);
        
        nifty.gotoScreen("demo");
        
        // Make the mouse cursor visible.
        flyCam.setDragToRotate(true);

        initElements();
    }
    
    private void initElements() {
        // Implement the Can't-Log-In button.
        ButtonBuilder loginprob = new ButtonBuilder("problems");
        loginprob.set("label", "Can't Log In?");
        loginprob.width("150px");
        loginprob.height("40px");

        loginprob.marginRight("60px");
        loginprob.backgroundColor("#363664");

        loginprob.onActiveEffect(new EffectBuilder("border") {{
            effectParameter("color","#626262");
        }});
        
        loginprob.onActiveEffect(new EffectBuilder("textColorAnimated") {{
            length(150);
            effectParameter("startColor","#0fa9f1");
            effectParameter("endColor","#0fa9f1");
            post(true);
        }});

        loginprob.onHoverEffect(new HoverEffectBuilder("border") {{
            effectParameter("color",Color.WHITE.getColorString());
            post(true);
        }});

        loginprob.onHoverEffect(new HoverEffectBuilder("textColorAnimated") {{
            effectValue("length","100");
            effectParameter("startColor",Color.WHITE.getColorString());
            effectParameter("endColor",Color.WHITE.getColorString());
            post(true);
        }});
        
        loginprob.onActiveEffect(new EffectBuilder("fade") {{
            effectParameter("start", "#d");
            effectParameter("end", "#d");
            inherit();
            neverStopRendering(true);
        }});

         loginprob.onHoverEffect(new HoverEffectBuilder("fade") {{
             effectParameter("start", "#a");
             effectParameter("end", "#f");
             inherit();
             neverStopRendering(true);
        }});
        
        // Implement the button for Offline- and Online-Modus.
        ButtonBuilder offon = new ButtonBuilder("neu");
        offon.alignCenter();
        offon.childLayout(ChildLayoutType.Horizontal);
        offon.width("100%");
        offon.height("100%");

        offon.onActiveEffect(new EffectBuilder("border") {{
            effectParameter("color","#848484");
        }});
        
        texts = new TextFieldBuilder("texter");
        texts.alignCenter();
        texts.childLayoutCenter();
        texts.height("25px");
        texts.width("180%");
        texts.set("text", "Void-Email-Address"); 
                
        texts.onFocusEffect(new EffectBuilder("fade") {{
            effectParameter("start", "#f");
            effectParameter("end", "#f");
            inherit();
            neverStopRendering(true);
        }});
        
        ButtonBuilder createnew = new ButtonBuilder("ne");
        createnew.set("label", "Create a Free Account");
        createnew.width("86%");
        createnew.height("40px");

        createnew.alignCenter();
        createnew.valignCenter();
        createnew.backgroundColor("#363664");
        createnew.marginTop("-30px");
 
        createnew.onActiveEffect(new EffectBuilder("border") {{
            effectParameter("color","#626262");
        }});
        
        createnew.onActiveEffect(new EffectBuilder("textColorAnimated") {{
            length(150);
            effectParameter("startColor","#0fa9f1");
            effectParameter("endColor","#0fa9f1");
            post(true);
        }});

        createnew.onHoverEffect(new HoverEffectBuilder("border") {{
            effectParameter("color",Color.WHITE.getColorString());
            post(true);
        }});

        createnew.onHoverEffect(new HoverEffectBuilder("textColorAnimated") {{
            effectValue("length","100");
            effectParameter("startColor",Color.WHITE.getColorString());
            effectParameter("endColor",Color.WHITE.getColorString());
            post(true);
        }});
        
        createnew.onActiveEffect(new EffectBuilder("fade") {{
            effectParameter("start", "#d");
            effectParameter("end", "#d");
            inherit();
            neverStopRendering(true);
        }});
        
        createnew.onHoverEffect(new HoverEffectBuilder("fade") {{
            effectParameter("start", "#a");
            effectParameter("end", "#f");
            inherit();
            neverStopRendering(true);
        }});
        
        texts.onActiveEffect(new EffectBuilder("fade") {{
            effectParameter("start", "#e");
            effectParameter("end", "#e");
            inherit();
            neverStopRendering(true);
        }});
                
        texts.onFocusEffect(new EffectBuilder("border") {{
            effectParameter("color","#5abbfd");
        }});
                
        texts.onHoverEffect(new HoverEffectBuilder("border") {{
            effectParameter("color","#a8a5a5");
        }});
        
        texts.onActiveEffect(new EffectBuilder("border") {{
            effectParameter("color","#626262");
        }});
        
        // Invisible button for the Log-In.
        button = new ButtonBuilder("LogInDark");
        button.set("label", "Log In");
        button.width("150px");
        button.height("40px");
        button.alignLeft();
        button.focusable(false);
        button.invisibleToMouse();
        button.backgroundImage("Interface/Dunkel.png");

        button.onHoverEffect(new HoverEffectBuilder("border") {{
            effectParameter("color",Color.NONE.getColorString());
        }});
        
        button.build(nifty, this.screen, this.screen.findElementByName("layer").findElementByName("login"));
        loginprob.build(nifty, this.screen, this.screen.findElementByName("layer").findElementByName("login").findElementByName("log"));
        offon.build(nifty, this.screen, this.screen.findElementByName("userl"));
        texts.build(nifty, this.screen, screen.findElementByName("user"));
        createnew.build(nifty, this.screen, screen.findElementByName("ws"));
    }
    
    /**
     * Registers different styles for buttons, textfield usw.
     * Don't register it twice, it would cause warnings for double creating
     * the styles.
     * @param nifty parameter for the nifty, which will assume this styles.
     */
    private static void registerMenuButtonHintStyle(final Nifty nifty) {
      new StyleBuilder() {{
        id("special-hint");
        base("nifty-panel-bright");
        childLayoutCenter();
        onShowEffect(new EffectBuilder("fade") {{
            length(0x96);
            effectParameter("start", "#0");
            effectParameter("end", "#d");
            inherit();
            neverStopRendering(true);
          }
        });
        
        onShowEffect(new EffectBuilder("move") {{
            length(0x96);
            inherit();
            neverStopRendering(true);
            effectParameter("mode", "fromOffset");
            effectParameter("offsetY", "-15");
        }});
        
        onCustomEffect(new EffectBuilder("fade") {{
            length(0x96);
            effectParameter("start", "#d");
            effectParameter("end", "#0");
            inherit();
            neverStopRendering(true);
        }});
        
        onCustomEffect(new EffectBuilder("move") {{
            length(0x96);
            inherit();
            neverStopRendering(true);
            effectParameter("mode", "toOffset");
            effectParameter("offsetY", "-15");
          }
        });
      }
    }.build(nifty);
      
   new StyleBuilder() {{
       id("nifty-vertical-scrollbar#position");
       childLayout(ChildLayoutType.Center);
       width("1%");
   }}.build(nifty);
   
   new StyleBuilder() {{
       id("nifty-label#text");
       alignCenter();
       set("textLineHeight","23px");
       set("textMinHeight","23px");

       onActiveEffect(new EffectBuilder("textColorAnimated") {{
            length(150);
            effectParameter("startColor","#0fa9f1");
            effectParameter("endColor","#0fa9f1");
            post(true);
        }});
   }}.build(nifty);
   
   new StyleBuilder() {{
       id("nifty-vertical-scrollbar#background");
       childLayout(ChildLayoutType.Absolute);
       width("1%");
   }}.build(nifty);
   
   new StyleBuilder() {{
        id("special-hint#hint-text");
        font("Interface/Fonts/wsa.fnt");
        alignLeft();
        valignCenter();
        textHAlignLeft();
        paddingLeft("20px");
        marginLeft("16px");
        color(Color.WHITE);
   }}.build(nifty);
  }
    
    /**
     * Registers styles for the nifty elements.
     * It's possible to override styles from the standard nifty elements.
     * @param nifty parameter for the nifty, which will assume this styles.
     */
  private static void registerStyles(final Nifty nifty) {
    new StyleBuilder() {{
        id("base-font-link");
        base("base-font");
        color("#8fff");
        interactOnRelease("$action");
        onHoverEffect(new HoverEffectBuilder("changeMouseCursor") {{
            effectParameter("id", "hand");
        }});
    }}.build(nifty);

    new StyleBuilder() {{
        id("creditsImage");
        alignCenter();
    }}.build(nifty);

    new StyleBuilder() {{
        id("creditsCaption");
        font("Interface/verdana-48-regular.fnt");
        width("100%");
        textHAlignCenter();
    }}.build(nifty);
    
    new StyleBuilder() {{
        id("nifty-drop-down#panel-text");
        childLayoutCenter();
        width("0%");
    }}.build(nifty);
    
    new StyleBuilder() {{
        id("nifty-drop-down#list-panel");
        childLayout(ChildLayoutType.Vertical);
        width("200px");
    }}.build(nifty);
    
    new StyleBuilder() {{
        id("nifty-drop-down#text-item");
        base("base-font");
        visible(false);
        width("50px");
    }}.build(nifty);
    
    new StyleBuilder() {{
      id("nifty-checkbox-style#panel");  
      width("23px");
      height("23px");
      childLayout(ChildLayoutType.Center);
      backgroundColor("#363664");  
      onActiveEffect(new EffectBuilder("border") {{
          effectParameter("color","#404040");
      }});
      onHoverEffect(new HoverEffectBuilder("border") {{
           effectParameter("color","#c0c0c0");
      }});
    }}.build(nifty);
    
    new StyleBuilder() {{
        id("nifty-vertical-scrollbar#panel");
        childLayout(ChildLayoutType.Vertical);
        width("0%");
    }}.build(nifty);
    
    new StyleBuilder() {{
        id("nifty-vertical-scrollbar#up");
        width("0%");
    }}.build(nifty);
    
    new StyleBuilder() {{
        id("nifty-vertical-scrollbar#down");
        width("0%");
    }}.build(nifty);
    
    new StyleBuilder() {{
        id("nifty-drop-down#icon");
        width("100%");
        filename("scrollbar/scrollbar.png");
        imageMode("sprite:23,23,0");
    }}.build(nifty);
    
    new StyleBuilder() {{
        id("nifty-drop-down#list-panel");
        childLayout(ChildLayoutType.Vertical);
        width("200px");
    }}.build(nifty);
    
    new StyleBuilder() {{
        id("nifty-textfield#field");
        width("60%");
        childLayout(ChildLayoutType.Overlay);
        valignCenter();
        backgroundColor(Color.NONE);
        height("20px");
        width("98%");
    }}.build(nifty);
    
    new StyleBuilder() {{
        id("nifty-textfield#panel");
        backgroundColor("#363664");
        height("25px");
        paddingLeft("10px");
        width("62px");
        childLayout(ChildLayoutType.Overlay);
    }}.build(nifty);
    
    new StyleBuilder() {{
        id("nifty-button#panel");
        childLayoutCenter();
        visibleToMouse(true);
    }}.build(nifty);
    
    new StyleBuilder() {{
        id("nifty-panel-bright");
        height("50px");
        valignTop();
        backgroundColor(Color.NONE);
        
        onActiveEffect(new EffectBuilder("fade") {{
            effectParameter("start", "#f");
            effectParameter("end", "#f");
            inherit();
            neverStopRendering(true);
        }});
        width("290px");
        childLayout(ChildLayoutType.Center);
        backgroundImage("Interface/Zeiger 2.png");       
    }}.build(nifty);
    
    new StyleBuilder() {{
        id("nifty-default-hint#hint-text");
        alignLeft();
        valignTop();
        textHAlignLeft();
        textVAlignTop();
        color(Color.WHITE);
        backgroundColor(Color.WHITE);
    }}.build(nifty);
    
    new StyleBuilder() {{
        id("nifty-button#text");
        font("Interface/Fonts/URWChanceryL.fnt");
        alignCenter();
        valignCenter();
        textHAlignCenter();
        textVAlignCenter();
        color(Color.WHITE.setAlpha(2));
        visibleToMouse(false);
    }}.build(nifty);
    
    new StyleBuilder() {{
        id("wes#text");
        childClip(true);
        textHAlignLeft();
        textVAlignCenter();
        marginLeft("20px");
        color("#777777");
        childLayout(ChildLayoutType.Overlay);
        base("base-font");
    }}.build(nifty);
    
    new StyleBuilder() {{
        id("nifty-textfield#text");
        childClip(true);
        textHAlignLeft();
        textVAlignCenter();
        marginLeft("20px");
        color("#777777");
        childLayout(ChildLayoutType.Overlay);
             font("Interface/Fonts/URWChanceryL.fnt");
        
        onFocusEffect(new EffectBuilder("textColorAnimated") {{
            length(150);
            effectParameter("startColor",Color.WHITE.getColorString());
            effectParameter("endColor",Color.WHITE.getColorString());
            post(false);
        }});
        
        onGetFocusEffect(new EffectBuilder("textColorAnimated") {{
            length(150);
            effectParameter("startColor",Color.WHITE.getColorString());
            effectParameter("endColor", Color.WHITE.getColorString());
            post(false);
        }});
        
        onActiveEffect(new EffectBuilder("fade") {{
            effectParameter("start", "#f");
            effectParameter("end", "#f");
            inherit();
            neverStopRendering(true);
        }});
    }}.build(nifty);
    
    new StyleBuilder() {{
        id("texter#text");
        childClip(true);
        color(Color.WHITE);
        childLayout(ChildLayoutType.Overlay);
        base("base-font");
    }}.build(nifty);
    
    new StyleBuilder() {{
        id("creditsCenter");
        base("base-font");
        width("100%");
        textHAlignCenter();
    }}.build(nifty);
  }

  /**
   * Registers the structure of an console popup.
   * There's nothing to say about. :-D
   * @param nifty parameter for the nifty, which will assume this builder.
   */
  private static void registerConsolePopup(Nifty nifty) {
    new PopupBuilder("consolePopup") {{
        childLayoutAbsolute();
        panel(new PanelBuilder() {{
            childLayoutCenter();
            width("100%");
            height("100%");
            alignCenter();
            valignCenter();
            control(new ConsoleBuilder("console") {{
                width("80%");
                lines(0x19);
                alignCenter();
                valignCenter();
        
                onStartScreenEffect(new EffectBuilder("move") {{
                    length(0x96);
                    inherit();
                    neverStopRendering(true);
                    effectParameter("mode", "in");
                    effectParameter("direction", "top");
                }});
                
                onEndScreenEffect(new EffectBuilder("move") {{
                    length(0x96);
                    inherit();
                    neverStopRendering(true);
                    effectParameter("mode", "out");
                    effectParameter("direction", "top");
                }});
            }});
        }});
    }}.registerPopup(nifty);
  }
  
  /**
   * Designs the screen for displaying it.
   * TODO: remove titlebar.
   * @param nifty nifty element on which this screen will be created.
   * @return screen for loading it in the main method.
   */  
    private Screen startNifty(final Nifty nifty) {
     
      screen = new ScreenBuilder("demo") {{
        controller(new Login());
        layer(new LayerBuilder("layer") {{
            control(new WindowBuilder("myWindow", "Title of Window") {{
                closeable(false);
            }});
            onActiveEffect(new EffectBuilder("border") {{
                effectParameter("color","#c0c0c0");
                inherit();
                post(true);
                neverStopRendering(true);
            }});
            
            image(new ImageBuilder() {{
                filename("Interface/Logical.png");
                width("100%");
                height("100%");
                childLayoutOverlay();
            }});
           
            alignCenter();
            childLayoutCenter();
            alignCenter();
 
        panel(new PanelBuilder() {{
            childLayoutVertical();
            alignCenter();
            marginTop("-50px");
        panel(new PanelBuilder("wal") {{
            childLayoutCenter();
            alignLeft();
            paddingLeft("130px");
            width("100%");
            height("20px");
         
            panel(new PanelBuilder("userl") {{
                alignRight();
                height("20px");
                width("20px");
                marginRight("158px");
                visibleToMouse(true);
                focusable(true);
                backgroundImage("Interface/Zahnrad.png");
                childLayout(ChildLayoutType.AbsoluteInside);
            }});
        }});
        
        panel(builders.vspacer());

        panel(new PanelBuilder("user") {{
            childLayoutCenter();
            alignCenter();
            width("49%");
        }});
        
        panel(builders.vspacer());
        
        panel(builders.vspacer());
        
        panel(new PanelBuilder("sicher") {{
            alignCenter();
            childLayoutCenter();
            width("70%");
            backgroundColor(Color.NONE);
        
        panel(new PanelBuilder("pass") {{
            childLayoutCenter();
            alignCenter();
            width("70%");
            control(new TextFieldBuilder("mainTextField") {{
                alignCenter();
                childLayoutCenter();
                backgroundColor("#363664");
                height("25px");
                width("180%");
                
                onFocusEffect(new EffectBuilder("fade") {{
                    effectParameter("start", "#f");
                    effectParameter("end", "#f");
                    inherit();
                    neverStopRendering(true);
                }});
                
                onActiveEffect(new EffectBuilder("fade") {{
                    effectParameter("start", "#e");
                    effectParameter("end", "#e");
                    inherit();
                    neverStopRendering(true);
                }});
                                
                onFocusEffect(new EffectBuilder("border") {{
                    effectParameter("color","#5abbfd");
                }});
                
                onHoverEffect(new HoverEffectBuilder("border") {{
                    effectParameter("color","#a8a5a5");
                }});
                
                onActiveEffect(new EffectBuilder("border") {{
                    effectParameter("color","#626262");
                }}); 
            }});
        }});
        }});
        
        panel(builders.vspacer());
             
        panel(builders.hspacer("30px"));
        
        panel(new PanelBuilder() {{
            childLayout(ChildLayoutType.Horizontal);
            width("50%");
            paddingLeft("30px");
            
            control(new LabelBuilder("Ar","Keep me logged in") {{
                   width("200px");
                   alignLeft();
                   textVAlignCenter();
                   textHAlignLeft();  
                        font("Interface/Fonts/URWChanceryL.fnt");
                onActiveEffect(new EffectBuilder("textColorAnimated") {{
                    length(150);
                    effectParameter("startColor","#0fa9f1");
                    effectParameter("endColor","#0fa9f1");
                    post(false);
                  }});
            }});
            
            panel(builders.hspacer("5px"));            
            control(new ControlBuilder("maxLengthEnableCheckBox", "checkbox") {{
                set("checked", "false");
            }});
            panel(new PanelBuilder("es") {{
                childLayout(ChildLayoutType.Center);
                width("20px");
                height("26px");
                paddingLeft("20px");
                marginLeft("20px");
                alignCenter();
                valignCenter();
                backgroundColor(Color.NONE);
                backgroundImage("Interface/LOgo frgi.png");
                onHoverEffect(new HoverEffectBuilder("hint") {{
                    effectParameter("hintText", "Automatically log in, when you wrote once \n your address. Stay logged in");
                    effectParameter("hintStyle", "special-hint");
                    effectParameter("hintDelay", "400");
                    effectParameter("offsetX", "-50");
                    effectParameter("offsetY", "190");
                }});
            }});
        }});
        
        panel(builders.vspacer());
        
        panel(new PanelBuilder("login") {{
            childLayout(ChildLayoutType.Center);
            alignLeft();
            paddingLeft("27px");
            width("100%");
        
            panel(new PanelBuilder("log") {{
                childLayout(ChildLayoutType.Horizontal);
                alignRight();
            }});
        }});
               
        panel(builders.vspacer());
        
        panel(builders.vspacer()); 
        
        panel(builders.vspacer());
        
        panel(builders.vspacer());
        
        }});
        
        panel(new PanelBuilder("ws") {{
            childLayout(ChildLayoutType.Vertical);
            paddingTop("50px");
            marginTop("80px");
            valignBottom();
            align(Align.Center);
            width("100%");
            height("40%");
            
            control(new LabelBuilder() {{
                text("New at Void?");
                width("100%");
                alignCenter();
                font("Interface/Fonts/URWChanceryL.fnt");
                valignTop();
                marginTop("-30px");
                set("textLineHeight","60px");
                textVAlignTop();
                textHAlignCenter();
                
            }});
            
            onActiveEffect(new EffectBuilder("fade") {{
                length(0x96);
                effectParameter("start", "#f");
                effectParameter("end", "#a");
                inherit();
                neverStopRendering(true);
            }});
            
            onActiveEffect(new EffectBuilder("border") {{
                effectParameter("color","#c0c0c0");
                inherit();
                post(true);
                neverStopRendering(true);
            }});
            
            backgroundColor(Color.BLACK.getColorString());
        }});
        }});
    }}.build(nifty);
       
      return screen;     
    }
    
    /**
     * Declares and binds the elements to the variables.
     * If you try to call an element from the screen, please use the screen
     * variable not nifty.getScreen("demo"). ... this causes many problems.
     * @param nifty
     * @param screen 
     */
    public void bind(Nifty nifty, Screen screen) {
        this.screen = screen;
        this.nifty = nifty;
        texter = screen.findNiftyControl("texter", TextField.class);
        maintext = screen.findNiftyControl("mainTextField", TextField.class);
        
        if (screen.findElementByName("tele") == null) {
            maintext.setText("Enter your password");
        }
        texter.setText("Void-Email-Address");
    }

    public void onStartScreen() {    
    }

    public void onEndScreen() {
    }
    
    /**
     * Fades the menu for the Offline- and Online Modus from the option button.
     * I would recommend to use this variant of method. 
     * @param id
     * @param event 
     */
    @NiftyEventSubscriber(id="evolvente")
    public void onClick(final String id, FocusGainedEvent event) {
        PanelBuilder panel = new PanelBuilder("textim");
        panel.height("20px");
        panel.height(id);
        panel.backgroundColor(Color.BLACK);
        panel.childLayout(ChildLayoutType.Center);
        panel.visible(true);
        nifty.createElementFromType(this.screen, panel.build(nifty, this.screen, this.screen.findElementByName("userl")), panel.buildElementType());
    }
     
    @NiftyEventSubscriber(id="anfu")
    public void onButtonClicked(final String id, ButtonClickedEvent event) {
        check = true;
        System.out.println(check);
    }
    
    @NiftyEventSubscriber(id="conn")
    public void cl(final String id, ButtonClickedEvent event) {
        
        screen.findElementByName("auß").setVisible(false);
        screen.findElementByName("auß").disable();
  
        screen.findElementByName("userl").setVisible(true);
        screen.findElementByName("userl").enable();
    }
    
    @NiftyEventSubscriber(id="auß")
    public void onLoseFocuss(final String id, FocusLostEvent event) {
        if (screen.findElementByName("dir") != null && screen.findElementByName("an") != null) {
            nifty.removeElement(this.screen, nifty.getScreen("demo").findElementByName("dir"));
            nifty.removeElement(this.screen, nifty.getScreen("demo").findElementByName("an")); 
            nifty.removeElement(this.screen, nifty.getScreen("demo").findElementByName("les panel"));
            nifty.removeElement(this.screen, nifty.getScreen("demo").findElementByName("conn"));
            nifty.removeElement(this.screen, nifty.getScreen("demo").findElementByName("nexts"));
        }
    }
    
    @NiftyEventSubscriber(id="auß")
    public void clicked(final String id, ButtonClickedEvent event) {
        ButtonBuilder b = new ButtonBuilder("an");
        b.width("80%");
        b.set("label", "Offline");
        b.childLayout(ChildLayoutType.Horizontal);
        b.height("20px");
        b.backgroundColor(Color.NONE);
        b.valignCenter();
        b.visibleToMouse(true);
        b.x("20");
        b.y("50");
        b.alignCenter();
        b.paddingLeft("50px");
        
        b.onHoverEffect(new HoverEffectBuilder("changeColor") {{
            effectParameter("color",Color.WHITE.getColorString());
            inherit();
        }});
        
        b.onHoverEffect(new HoverEffectBuilder("fade") {{
            effectParameter("start", "#d");
            effectParameter("end", "#4");
            inherit();
            neverStopRendering(true);
        }});
        
        ButtonBuilder bre = new ButtonBuilder("conn");
        bre.width("80%");
        bre.set("label", "Connect");
        bre.childLayout(ChildLayoutType.Horizontal);
        bre.height("20px");
        bre.backgroundColor("20px");
        bre.backgroundColor(Color.NONE);
        bre.valignCenter();
        bre.visibleToMouse(true);
        bre.x("20");
        bre.y("70");
        bre.paddingLeft("50px");

        bre.onHoverEffect(new HoverEffectBuilder("changeColor") {{
            effectParameter("color",Color.WHITE.getColorString());
        }});
        
        bre.onHoverEffect(new HoverEffectBuilder("fade") {{
            effectParameter("start", "#d");
            effectParameter("end", "#4");
            inherit();
            neverStopRendering(true);
        }});
        
        PanelBuilder lepanel = new PanelBuilder("les panel");
        lepanel.width("20px");
        lepanel.height("20px");
        lepanel.childLayout(ChildLayoutType.Overlay);
        lepanel.backgroundColor(Color.NONE);
        lepanel.backgroundImage("Interface/kondensator.png");
        lepanel.valignBottom();
        lepanel.alignRight();
        // Don't try to use paddingRight, because it does nothing.
        lepanel.paddingLeft("-80px");
        lepanel.marginLeft("-130px");
        lepanel.paddingBottom("125px");
        lepanel.marginBottom("329px");
        
        lepanel.onActiveEffect(new EffectBuilder("fade") {{
             length(0x258);
            startDelay(200);
            effectParameter("start", "#0");
            effectParameter("end", "#f");
            inherit();
            neverStopRendering(true);
        }});
        
        PanelBuilder nextpanel = new PanelBuilder("nexts");
        nextpanel.width("20px");
        nextpanel.height("20px");
        nextpanel.childLayout(ChildLayoutType.Overlay);
        nextpanel.backgroundColor(Color.NONE);
        nextpanel.backgroundImage("Interface/Zahnrad.png");
        nextpanel.valignBottom();
        nextpanel.alignRight();
        nextpanel.paddingLeft("-80px");
        nextpanel.marginLeft("-130px");
        nextpanel.paddingBottom("70px");
        nextpanel.marginBottom("309px");
        
        nextpanel.onActiveEffect(new EffectBuilder("fade") {{
            length(0x258);
            startDelay(200);
            effectParameter("start", "#0");
            effectParameter("end", "#f");
            inherit();
            neverStopRendering(true);
        }});
        
        lepanel.onFocusEffect(new EffectBuilder("fade") {{
            length(0x258);
            startDelay(200);
            effectParameter("start", "#0");
            effectParameter("end", "#f");
            inherit();
            neverStopRendering(true);
        }});
        
        b.onActiveEffect(new EffectBuilder("fade") {{
            length(0x258);
            startDelay(200);
            effectParameter("start", "#0");
            effectParameter("end", "#f");
            inherit();
            neverStopRendering(true);
        }});
                
        PanelBuilder drin = new PanelBuilder("dir");
        drin.alignCenter();
        drin.childLayout(ChildLayoutType.AbsoluteInside);
        drin.backgroundImage("Interface/leklein.png");
        drin.visibleToMouse(false);
        drin.focusable(false);
        drin.marginBottom("95px");
        drin.width("180px");
        drin.height("105px");
        drin.marginLeft("140px");
        
        drin.onActiveEffect(new EffectBuilder("move") {{
            length(150);
            timeType("exp");
            effectParameter("factor","5.5f");
            effectParameter("mode","fromOffset");
            effectParameter("offsetY", "-50");
            inherit(true);
        }});
        
        drin.onActiveEffect(new EffectBuilder("fade") {{
            length(0x258);
            startDelay(200);
            effectParameter("start", "#0");
            effectParameter("end", "#f");
            inherit();
            post(false);
            neverStopRendering(true);
        }});
        
        if ((nifty.getScreen("demo").findElementByName("dir") == null && nifty.getScreen("demo").findElementByName("an") == null) || nifty.getScreen("demo").findElementByName("dir") == null) {
            drin.build(nifty, this.screen, this.screen.findElementByName("layer"));
            b.build(nifty, this.screen, this.screen.findElementByName("dir"));
            bre.build(nifty, this.screen, this.screen.findElementByName("dir"));
            lepanel.build(nifty, this.screen, this.screen.findElementByName("layer"));
            nextpanel.build(nifty, this.screen, this.screen.findElementByName("layer"));
        } else {
         
        }
    }
    
    @NiftyEventSubscriber(id="anfu")
    public void offline(final String id, ButtonClickedEvent event) {        
        ButtonBuilder weisf = new ButtonBuilder("auß");
        weisf.height("20px");
        weisf.width("20px");
        weisf.marginLeft("120px");
        weisf.visibleToMouse(true);
        weisf.focusable(true);
        weisf.backgroundImage("Interface/kondensator.png");
        weisf.childLayout(ChildLayoutType.AbsoluteInside);
       
        weisf.onActiveEffect(new EffectBuilder("border") {{
            effectParameter("color","#c0c0c0");
        }});
        
        if (screen.findElementByName("auß") == null) {
            weisf.build(nifty, this.screen, screen.findElementByName("wal"));
            
            screen.findElementByName("userl").setVisible(false);
            screen.findElementByName("userl").disable();
        } else {
            screen.findElementByName("neu").setVisible(false);
            screen.findElementByName("neu").disable();
            screen.findElementByName("auß").setVisible(true);
            screen.findElementByName("auß").enable();
            screen.findElementByName("userl").setVisible(false);
            screen.findElementByName("userl").disable();
        }       
    }
    
    /**
     * EventSubscriber for the option button.
     * Creates an window respectivly, where two options are available:
     * (1) Offline: login for the Offline-Modus.
     * (2) Online:  login for the Online-Modus.
     * @exception Only one Modus can be activated.
     * @param id
     * @param event 
     */
    @NiftyEventSubscriber(id="neu")
    public void onClicked(final String id, ButtonClickedEvent event) {
        // Use padding and margin for a translation of the element.
        ButtonBuilder b = new ButtonBuilder("anfu");
        b.width("80%");
        b.set("label", "Offline");
        b.childLayout(ChildLayoutType.Horizontal);
        b.height("22px");
        b.backgroundColor(Color.NONE);
        b.valignCenter();
        b.visibleToMouse(true);
        b.x("20");
        b.y("50");
        b.paddingLeft("50px");
        
        b.onHoverEffect(new HoverEffectBuilder("changeColor") {{
            effectParameter("color",Color.WHITE.getColorString());
            inherit();
        }});
        
        b.onHoverEffect(new HoverEffectBuilder("fade") {{
            effectParameter("start", "#d");
            effectParameter("end", "#4");
            inherit();
            neverStopRendering(true);
        }});
        
        ButtonBuilder bre = new ButtonBuilder("discon");
        bre.width("80%");
        bre.set("label", "Connect");
        bre.childLayout(ChildLayoutType.Horizontal);
        bre.height("20px");
        bre.backgroundColor("20px");
        bre.backgroundColor(Color.NONE);
        bre.valignCenter();
        bre.visibleToMouse(true);
        bre.x("20");
        bre.y("70");
        bre.paddingLeft("50px");

        bre.onHoverEffect(new HoverEffectBuilder("changeColor") {{
            effectParameter("color",Color.WHITE.getColorString());
        }});
        
        bre.onHoverEffect(new HoverEffectBuilder("fade") {{
            effectParameter("start", "#d");
            effectParameter("end", "#4");
            inherit();
            neverStopRendering(true);
        }});
        
        PanelBuilder lepanel = new PanelBuilder("le panel");
        lepanel.width("20px");
        lepanel.height("20px");
        lepanel.childLayout(ChildLayoutType.Overlay);
        lepanel.backgroundColor(Color.NONE);
        lepanel.backgroundImage("Interface/kondensator.png");
        lepanel.valignBottom();
        lepanel.alignRight();
        // Don't try to use paddingRight, because it does nothing.
        lepanel.paddingLeft("-80px");
        lepanel.marginLeft("-130px");
        lepanel.paddingBottom("125px");
        lepanel.marginBottom("329px");
        
        lepanel.onActiveEffect(new EffectBuilder("fade") {{
             length(0x258);
            startDelay(200);
            effectParameter("start", "#0");
            effectParameter("end", "#f");
            inherit();
            neverStopRendering(true);
        }});
        
        PanelBuilder nextpanel = new PanelBuilder("next");
        nextpanel.width("20px");
        nextpanel.height("20px");
        nextpanel.childLayout(ChildLayoutType.Overlay);
        nextpanel.backgroundColor(Color.NONE);
        nextpanel.backgroundImage("Interface/Zahnrad.png");
        nextpanel.valignBottom();
        nextpanel.alignRight();
        nextpanel.paddingLeft("-80px");
        nextpanel.marginLeft("-130px");
        nextpanel.paddingBottom("70px");
        nextpanel.marginBottom("309px");
        
        nextpanel.onActiveEffect(new EffectBuilder("fade") {{
             length(0x258);
            startDelay(200);
            effectParameter("start", "#0");
            effectParameter("end", "#f");
            inherit();
            neverStopRendering(true);
        }}) ;
        
        lepanel.onFocusEffect(new EffectBuilder("fade") {{
            length(0x258);
            startDelay(200);
            effectParameter("start", "#0");
            effectParameter("end", "#f");
            inherit();
            neverStopRendering(true);
        }});
        
        b.onActiveEffect(new EffectBuilder("fade") {{
            length(0x258);
            startDelay(200);
            effectParameter("start", "#0");
            effectParameter("end", "#f");
            inherit();
            neverStopRendering(true);
        }});
                
        PanelBuilder drin = new PanelBuilder("dr");
        drin.alignCenter();
        drin.childLayout(ChildLayoutType.AbsoluteInside);
        drin.backgroundImage("Interface/leklein.png");
        drin.visibleToMouse(false);
        drin.focusable(false);
        drin.marginBottom("95px");
        drin.width("180px");
        drin.height("105px");
        drin.marginLeft("140px");
        
        drin.onActiveEffect(new EffectBuilder("move") {{
            length(150);
            timeType("exp");
            effectParameter("factor","5.5f");
            effectParameter("mode","fromOffset");
            effectParameter("offsetY", "-50");
            inherit(true);
        }});
        
        drin.onActiveEffect(new EffectBuilder("fade") {{
            length(0x258);
            startDelay(200);
            effectParameter("start", "#0");
            effectParameter("end", "#f");
            inherit();
            post(false);
            neverStopRendering(true);
        }});
            
        if (nifty.getScreen("demo").findElementByName("dr") == null && nifty.getScreen("demo").findElementByName("anfu") == null) {
            drin.build(nifty, this.screen, this.screen.findElementByName("layer"));
            b.build(nifty, this.screen, this.screen.findElementByName("dr"));
            bre.build(nifty, this.screen, this.screen.findElementByName("dr"));
            lepanel.build(nifty, this.screen, this.screen.findElementByName("layer"));
            nextpanel.build(nifty, this.screen, this.screen.findElementByName("layer"));
        }
    }
    
    /**
     * Removes all elements from the faded panel.
     * There's nothing to say about.
     * @param id
     * @param event 
     */
     @NiftyEventSubscriber(id="neu")
     public void onLoseFocus(final String id, FocusLostEvent event) {
        if (screen.findElementByName("dr") != null && screen.findElementByName("anfu") != null) {
            nifty.removeElement(this.screen, nifty.getScreen("demo").findElementByName("dr"));
            nifty.removeElement(this.screen, nifty.getScreen("demo").findElementByName("anfu")); 
            nifty.removeElement(this.screen, nifty.getScreen("demo").findElementByName("le panel"));
            nifty.removeElement(this.screen, nifty.getScreen("demo").findElementByName("discon"));
            nifty.removeElement(this.screen, nifty.getScreen("demo").findElementByName("next"));
        }
     }
     
     /**
      * Redo the invisible text.
      * There're still some problems with the EventSubscriber.
      * @param id
      * @param event 
      */
     @NiftyEventSubscriber(id="texter")
     public void onFocusGain(final String id, FocusGainedEvent event) {
         if (screen.findNiftyControl("texter", TextField.class).getText().equals("Void-Email-Address")) {
             screen.findNiftyControl("texter", TextField.class).setText("");
         }
     }
     
     /**
      * Controller for the second textfield.
      * Creates and manages the username textfield creator.
      * @param id
      * @param event 
      */
     @NiftyEventSubscriber(id="texter")
     public void onRenew(final String id, FocusLostEvent event) {
         if (screen.findNiftyControl("texter", TextField.class).getText().isEmpty()) {
             screen.findNiftyControl("texter", TextField.class).setText("Void-Email-Address");
         } else {
             TextFieldBuilder textse = new TextFieldBuilder("tel");
             textse.alignCenter();
             
             textse.childLayoutCenter();
             textse.backgroundColor("#363664");
             textse.height("25px");
             textse.width("180%");
             textse.set("text", this.screen.findNiftyControl("texter", TextField.class).getText());
             
             textse.onFocusEffect(new EffectBuilder("border") {{
                 effectParameter("color","#5abbfd");
             }});
             
             textse.onActiveEffect(new EffectBuilder("changeTextColor") {{
                 effectParameter("color",Color.WHITE.getColorString());
                 post(true);
                 inherit();
             }});
             
             textse.onFocusEffect(new EffectBuilder("fade") {{    
                 effectParameter("start", "#f");
                 effectParameter("end", "#e");
                 inherit();
                 neverStopRendering(true);
             }});
             
             textse.onActiveEffect(new EffectBuilder("fade") {{
                 effectParameter("start", "#f");
                 effectParameter("end", "#8");
                 inherit();
                 neverStopRendering(true);
             }});
             
             textse.onHoverEffect(new HoverEffectBuilder("border") {{
                 effectParameter("color","#a8a5a5");
             }});
             
             textse.onActiveEffect(new EffectBuilder("border") {{
                 effectParameter("color","#626262");
             }});
             
             new StyleBuilder() {{
                 id("nifty-textfield#text");
                 childClip(true);
                 textHAlignLeft();
                 textVAlignCenter();
                 marginLeft("20px");
                 color(Color.WHITE);
                 childLayout(ChildLayoutType.Overlay);
                 base("base-font");
       
                 onFocusEffect(new EffectBuilder("textColorAnimated") {{
                     length(150);
                     effectParameter("startColor",Color.WHITE.getColorString());
                     effectParameter("endColor",Color.WHITE.getColorString());
                     post(false);
                 }});
                 
                 onGetFocusEffect(new EffectBuilder("textColorAnimated") {{
                     length(150);
                     effectParameter("startColor",Color.WHITE.getColorString());
                     effectParameter("endColor", Color.WHITE.getColorString());
                     post(false);
                 }});
                 
                 onCustomEffect(new EffectBuilder("textColorAnimated") {{
                     length(150);
                     effectParameter("startColor", Color.WHITE.getColorString());
                     effectParameter("endColor", Color.WHITE.getColorString());
                     post(false);
                 }});
             }}.build(nifty);
             
             textse.set("textColor", Color.WHITE.getColorString());
             textse.build(nifty, this.screen, screen.findElementByName("user"));
         }
     }
     
     /**
      * Removes the invisible text for writting your own address.
      * @todo change dependencie of the textfield 
      * @param id
      * @param event 
      */
     @NiftyEventSubscriber(id="mainTextField")
     public void onFocused(final String id, FocusGainedEvent event) {
         if (screen.findNiftyControl("mainTextField", TextField.class).getText().equals("Enter your password")) {
             screen.findNiftyControl("mainTextField", TextField.class).setText("");
         }
     }
     
     /**
      * Refreshes the invisible text of the username textfield and removes
      * the second textfield.
      * This only happens when it's empty.
      * @param id
      * @param event
      * @serialData tel equals the second username textfield.
      */
     @NiftyEventSubscriber(pattern="tel")
     public void RenewEmail(final String id, FocusLostEvent event) {
        if (screen.findNiftyControl("tel", TextField.class).getText().equals("")) {
            nifty.removeElement(screen, screen.findElementByName("tel"));
            screen.findNiftyControl("texter", TextField.class).setText("Void-Email-Address");
        }
     }
     
     /**
      * Creates the second password textfield with white textcolor.
      * It's yet the only way to make the textcolor white, even when the 
      * textfield loses focus.
      * @param id
      * @param event 
      */
     @NiftyEventSubscriber(id="mainTextField")
     public void onLosed(final String id, FocusLostEvent event) {  
         if (screen.findNiftyControl("mainTextField", TextField.class).getText().isEmpty() && screen.findElementByName("tele") == null) {
             screen.findNiftyControl("mainTextField", TextField.class).setText("Enter your password");
         } else {
                      
             TextFieldBuilder textse = new TextFieldBuilder("tele");
             textse.alignCenter();
             
             textse.childLayoutCenter();
             textse.backgroundColor("#363664");
             textse.height("25px");
             textse.width("180%");
             textse.set("text", this.screen.findNiftyControl("mainTextField", TextField.class).getText());
             screen.findNiftyControl("mainTextField", TextField.class).setText("");
            
             textse.onFocusEffect(new EffectBuilder("border") {{
                 effectParameter("color","#5abbfd");
             }});
             
             textse.onActiveEffect(new EffectBuilder("changeTextColor") {{
                 effectParameter("color",Color.WHITE.getColorString());
                 post(true);
                 inherit();
             }});
             
             textse.onHoverEffect(new HoverEffectBuilder("border") {{
                 effectParameter("color","#a8a5a5");
             }});
             
             textse.onActiveEffect(new EffectBuilder("border") {{
                 effectParameter("color","#626262");
             }});
             
             textse.onFocusEffect(new EffectBuilder("fade") {{    
                 effectParameter("start", "#f");
                 effectParameter("end", "#e");
                 inherit();
                 neverStopRendering(true);
             }});
             
             textse.onActiveEffect(new EffectBuilder("fade") {{
                 effectParameter("start", "#f");
                 effectParameter("end", "#8");
                 inherit();
                 neverStopRendering(true);
             }});
             
             new StyleBuilder() {{
                 id("nifty-textfield#text");
                 childClip(true);
                 textHAlignLeft();
                 textVAlignCenter();
                 marginLeft("20px");
                 color(Color.WHITE);
                 childLayout(ChildLayoutType.Overlay);
                 base("base-font");
                
                 onFocusEffect(new EffectBuilder("textColorAnimated") {{
                     length(150);
                     effectParameter("startColor",Color.WHITE.getColorString());
                     effectParameter("endColor",Color.WHITE.getColorString());
                     post(false);
                 }});
                 
                 onGetFocusEffect(new EffectBuilder("textColorAnimated") {{
                     length(150);
                     effectParameter("startColor",Color.WHITE.getColorString());
                     effectParameter("endColor", Color.WHITE.getColorString());
                     post(false);
                 }});
       
             }}.build(nifty);
             
             textse.build(nifty, this.screen, screen.findElementByName("pass"));
         }
     }
     
     /**
      * Refreshes the password textfield after it loses focus.
      * I would prefere to call the mainTextField directly from the screen.
      * <h1> DON'T USE THE ID NOTIFIER FOR THE EVENTSUBSCRIBER INSTEAD OF THE
      * PATTERN NOTIFIER</h1>
      * @param id
      * @param event 
      */
     @NiftyEventSubscriber(pattern="tele")
     public void RemoveElement(final String id, FocusLostEvent event) {
        if (screen.findNiftyControl("tele", TextField.class).getText().equals("")) {
            nifty.removeElement(screen, screen.findElementByName("tele"));
            
            try {
                nifty.removeElement(this.screen, nifty.getScreen("demo").findElementByName("layer").findElementByName("le button"));
            } catch(NullPointerException e){
                System.out.println(e);
            }
            
            screen.findNiftyControl("mainTextField", TextField.class).setText("Enter your password"); 
        }
     }
     
     /**
      * Removes the Log In button when the textfield is empty.
      * Remember that it's important to remove all created buttons.
      * @param id
      * @param event 
      */
     @NiftyEventSubscriber(id="tele")
     public void ControlButton(final String id, TextFieldChangedEvent event) {
          if (screen.findNiftyControl("tele", TextField.class).getText().equals("")) {
              nifty.removeElement(this.screen, nifty.getScreen("demo").findElementByName("layer").findElementByName("le button"));
          }
     }
     
     /**
      * Refreshes the invisible Text.
      * It's only called when the TextField is empty and has focus.
      * @param id
      * @param event 
      */
     @NiftyEventSubscriber(id="tele")
     public void Renew(final String id, FocusGainedEvent event) {
         if (screen.findElementByName("tele") == null) {
             screen.findNiftyControl("mainTextField", TextField.class).setText("Enter your password");
         }
     }
          
     /**
      * Creates the visible button on the dark button.
      * This happens only when the mainTextField is'nt empty.
      * @param id
      * @param event 
      */
     @NiftyEventSubscriber(id="mainTextField")
     public void onTextinside(final String id, TextFieldChangedEvent event) {
  
        ButtonBuilder lebutton = new ButtonBuilder("le button");
        lebutton.set("label", "Log In");
        lebutton.width("150px");
        lebutton.height("40px");
        lebutton.alignLeft();
        lebutton.backgroundImage("Interface/Hell.png");
        
        if (this.screen.findNiftyControl("mainTextField", TextField.class).getText().equals("Enter your password") || this.screen.findNiftyControl("mainTextField", TextField.class).getText().isEmpty()) {
            nifty.removeElement(this.screen, nifty.getScreen("demo").findElementByName("layer").findElementByName("le button"));
        } else {
            if (nifty.getScreen("demo").findElementByName("le button") == null) {
                lebutton.build(nifty, screen, screen.findElementByName("layer").findElementByName("login"));
            }
        }
     }
    
    public boolean inputEvent(NiftyInputEvent inputEvent) {
        return false;
    } 
    
    
}