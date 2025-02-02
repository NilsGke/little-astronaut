package name.panitz.game2d;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
public class SwingScreen extends JPanel{
  private static final long serialVersionUID = 1403492898373497054L;
  Game logic;
  Timer t;

  public SwingScreen(Game gl) {
    this.logic = gl;


    t = new Timer(1000/60, (ev)->{
        //logic.move();
        logic.doChecks(t.getDelay());
        repaint();
        getToolkit().sync();
      });
      t.start();

		
    addKeyListener(new KeyAdapter() {
        @Override public void keyPressed(KeyEvent e) {
          logic.keyPressedReaction(e);
        }
      @Override public void keyReleased(KeyEvent e) {
          logic.keyReleasedReaction(e);
        }
      });
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        logic.mouseClickedReaction(e);
      }

      @Override
      public void mousePressed(MouseEvent e) {
        logic.mousePressedReaction(e);
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        logic.mouseReleasedReaction(e);
      }
    });
    setFocusable(true);
    requestFocus();
    addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        var component = e.getComponent();
        logic.setHeight(component.getHeight());
        logic.setWidth(component.getWidth());
      }
    });
    }

	
  @Override public Dimension getPreferredSize() {
    return new Dimension((int)logic.width(),(int)logic.height());
  }

	
  @Override protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    logic.paintTo(g);
  }
}

