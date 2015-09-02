/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Voider;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.Controller;
import de.lessvoid.nifty.controls.FocusGainedEvent;
import de.lessvoid.nifty.controls.FocusLostEvent;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.controls.TextFieldChangedEvent;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.PanelRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.xml.xpp3.Attributes;
import java.util.Properties;

/**
 *
 * @author john
 */
public class LoginController implements ScreenController {
    private TextField username;
    private PanelRenderer dr;
    private Screen screen;
    private Nifty nifty;

    public void init(Properties parameter, Attributes controlDefinitionAttributes) {
        this.username.setText("email-address");
    }
    
      
     @NiftyEventSubscriber(id="neu")
     public void onLo(final String id, FocusLostEvent event) {
        nifty.removeElement(this.screen, nifty.getScreen("demo").findElementByName("dr"));
        
        nifty.removeElement(this.screen, nifty.getScreen("demo").findElementByName("anfu")); 
     }
     
     @NiftyEventSubscriber(id="texter")
     public void onFocu(final String id, FocusGainedEvent event) {
         System.out.println("Geht");
         nifty.getScreen("demo").findElementByName("layer").findNiftyControl("texter",TextField.class).setText("-");
     }
     
     @NiftyEventSubscriber(id="mainTextField")
     public void onTextinside(final String id, TextFieldChangedEvent event) {
  
        ButtonBuilder button2 = new ButtonBuilder("le button");
        button2.set("label", "Login");
        button2.width("100px");
        button2.height("60px");
        button2.alignLeft();
        button2.backgroundImage("Interface/Hell.png");
  
         if (event.getText().isEmpty() || this.screen.findNiftyControl("mainTextField", TextField.class).getDisplayedText().isEmpty() || this.screen.findNiftyControl("mainTextField", TextField.class).getRealText().isEmpty() || event.getText().length() == 0 || this.screen.findNiftyControl("mainTextField", TextField.class).getText().length() == 0 || this.screen.findNiftyControl("mainTextField", TextField.class).getText().isEmpty()) {
            nifty.removeElement(this.screen, nifty.getScreen("demo").findElementByName("layer").findElementByName("le button"));
         } else {
             if (nifty.getScreen("demo").findElementByName("le button") == null) {
                nifty.createElementFromType(this.screen, button2.build(nifty, screen, screen.findElementByName("layer").findElementByName("login")), button2.buildElementType());
             }
         }
     }

    public void onStartScreen() {
    }

    public void onFocus(boolean getFocus) {
    }

    public boolean inputEvent(NiftyInputEvent inputEvent) {
        return false;
    }

    public void bind(Nifty nifty, Screen screen) {
        this.username = screen.findNiftyControl("texter", TextField.class);
        this.screen = screen;
        this.nifty = nifty;
    }

    public void onEndScreen() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
