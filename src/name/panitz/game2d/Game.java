package name.panitz.game2d;
import java.util.List;
import java.awt.event.*;
import java.awt.*;

public interface Game {


  int width();
  int height();

  void setHeight(int height);
  void setWidth(int width);

  GameObj player();

  List<List<? extends GameObj>> goss();

  void init();


  void doChecks(int deltaTime);

  void keyPressedReaction(KeyEvent keyEvent);

  void keyReleasedReaction(KeyEvent keyEvent);

  void mouseClickedReaction(MouseEvent mouseEvent);
  void mousePressedReaction(MouseEvent mouseEvent);
  void mouseReleasedReaction(MouseEvent mouseEvent);

  default void move(){
  	if (ended()) return;
    for (var gos:goss()) gos.forEach(GameObj::move);
    player().move();
  }    

  boolean won();
  boolean lost();

  default boolean ended() {
	return won()||lost();
  }


  default void paintTo(Graphics g){
    for (var gos:goss()) gos.forEach( go -> go.paintTo(g));
    player().paintTo(g);
  }


  default void play(){
    init();
    var f = new javax.swing.JFrame();
    f.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    f.add(new SwingScreen(this));
    f.pack();
    f.setLocationRelativeTo(null); // centers the window on the screen
    f.setVisible(true);
  }
}

