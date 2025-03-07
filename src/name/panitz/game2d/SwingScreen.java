package name.panitz.game2d;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
public class SwingScreen extends JPanel{
  private static final long serialVersionUID = 1403492898373497054L;
  Game logic;
  Timer t;
  long lastTime = System.currentTimeMillis();

  public SwingScreen(Game gl) {
    this.logic = gl;


    t = new Timer(13, (ev)->{
      long currentTime = System.currentTimeMillis();
      long elapsedTime = currentTime - lastTime;
      lastTime = currentTime;

      logic.doChecks(elapsedTime);
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
    }


  @Override public Dimension getPreferredSize() {
    return new Dimension(logic.width(), logic.height());
  }


  @Override protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    logic.paintTo(g2d);
  }
}

