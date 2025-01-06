package com.nilsgke.littleAstronaut.minigames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;


public abstract class Minigame {

  public Thread thread = null;

  abstract int width();

  abstract int height();


  abstract void init();

  abstract void doChecks(int deltaTime);

  abstract void keyPressedReaction(KeyEvent keyEvent);

  abstract void keyReleasedReaction(KeyEvent keyEvent);

  abstract void mouseClick(MouseEvent mouseEvent);

  public abstract boolean won();

  public abstract boolean lost();

  public boolean ended() {
    return won() || lost();
  }

  abstract void paintTo(Graphics g);

  public void play() {
    var self = this;
    this.thread = new Thread(() -> {
      System.out.println("in thread");

      // Initialize game state
      init();

      // Create and configure the JFrame on the EDT
      var f = new JFrame("Minigame");
      f.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      MiniSwingScreen screen = new MiniSwingScreen(self);
      f.add(screen);
      f.pack();
      f.setResizable(false);
      f.setLocationRelativeTo(null);
      f.setAlwaysOnTop(true);
      f.setVisible(true);

      // loop to check if game has ended
      new Timer(16, e -> {
        if (ended()) {
          ((Timer) e.getSource()).stop();
          f.dispose(); // closes JFrame
          System.out.println(won() ? "Minigame won!" : "Minigame lost!");
        } else {
          screen.repaint();
        }
      }).start();

    });

    this.thread.start();
  }
}

