/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Voider;


import com.jme3.monkeyzone.startscreen.StartScreen;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.Border;


/**
 *  Redefine the progress bar for the loading screen at the beginning.
 *  It's a self creation of the primary version.
 * @author john
 */
public class ProgressPane extends JPanel {
  private float progress = 1f;
  private BufferedImage load;
  private BufferedImage loadbar;
  public boolean done;
  protected Login screen;

  public boolean getProgress() {
      return done;
  }
  
  public ProgressPane() {
      try {
          load = ImageIO.read(new File("/home/john/Dokumente/Informatik/Alle Projekte/monkeyzone-master/src/Voider/Logical.png"));
      } catch (IOException ex) {
          ex.printStackTrace();
      }
      
      this.setOpaque(false);
      this.setForeground(Color.WHITE);
      Timer timer = new Timer(50, new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
              progress += 1;
              if (progress >= 100) {
                  ((Timer) e.getSource()).stop();
                  done = true;    
                   screen = new Login();
                   
                  System.out.println(done); 
              }
              repaint();
          }
      });
      timer.setRepeats(true);
      timer.setCoalesce(true);
      timer.start();
  }
  
  @Override
  public void invalidate() {
      super.invalidate();
      loadbar = null;
  }
  
  @Override
  public Dimension getPreferredSize() {
      FontMetrics fm = getFontMetrics(getFont());
      return new Dimension(200, Math.max(50, fm.getHeight() + 4));
  }
  
  protected void createBar() {
      if (loadbar == null) {
          if(getWidth() > 0 && getHeight() > 0) {
              
              FontMetrics fm = getFontMetrics(getFont());
              int height = Math.max(50, fm.getHeight() + 4);
              loadbar = new BufferedImage(getWidth() - 4, height, BufferedImage.TYPE_INT_ARGB );
              Graphics2D g2d = loadbar.createGraphics();
              
              int x = 0;
              int y = (height - load.getHeight()) / 2;
              
              while (x < getWidth() - 4) {
                  g2d.drawImage(load, x, y, this);
                  x += load.getWidth();
              }
              g2d.dispose();
          }
      }
  }
  
  @Override 
  protected void paintComponent(Graphics graphic) {
      createBar();
      super.paintComponent(graphic);
      
      int width = getWidth() - 4;
      int height = getHeight() - 4;
      int x = 2;
      int y = 2;
      
      graphic.setColor(getBackground());
      graphic.fillRect(x, y, width, height);
      graphic.setColor(new Color(1f,0f,0f,0.01f));
      graphic.drawRect(x,y,width,height);   
      
      int progressWidth = (int) (width * (progress/100));
      BufferedImage progressImage = loadbar.getSubimage(0, 0, progressWidth, loadbar.getHeight());
      graphic.drawImage(progressImage, x, y, this);
      
      FontMetrics fm = graphic.getFontMetrics();
      String value = NumberFormat.getPercentInstance().format(progress);
      x = x * ((width - fm.stringWidth(value)) / 2);
      y = y + ((height - fm.getHeight()) / 2);
      
      graphic.setColor(Color.WHITE);
  }
  
  protected void paintString(Graphics graphic) {
      int x = 2;
      int y = 2;
      
      String value = NumberFormat.getPercentInstance().format(progress);
      FontMetrics fm = graphic.getFontMetrics();
      
      graphic.drawString(value, x, y + fm.getAscent());
  }
}
