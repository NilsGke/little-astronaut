package com.nilsgke.jumpKingMinigame.minigames;

import name.panitz.game2d.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MiniSwingScreen extends JPanel {

  private static final long serialVersionUID = 1403492898373497054L;
  Minigame logic;
  Timer t;

  public MiniSwingScreen(Minigame gl) {
    this.logic = gl;


    t = new Timer(13, (ev) -> {
      logic.doChecks(t.getDelay());
      repaint();
      getToolkit().sync();
      requestFocus();
    });
    t.start();


    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        logic.keyPressedReaction(e);
      }

      @Override
      public void keyReleased(KeyEvent e) {
        logic.keyReleasedReaction(e);
      }
    });
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseReleased(MouseEvent e) {
        logic.mouseClick(e);
      }
    });
    setFocusable(true);
    this.
    requestFocus();
  }


  @Override
  public Dimension getPreferredSize() {
    return new Dimension( logic.width(), logic.height());
  }


  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    logic.paintTo(g);
  }
}



