package com.nilsgke.jumpKingMinigame.minigames;

import name.panitz.game2d.Game;
import name.panitz.game2d.GameObj;
import name.panitz.game2d.SwingScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;

//public abstract class Minigame {
//  public boolean started = false;
//  Runnable onCompleted;
//
//
//  Minigame(Runnable onCompleted){
//    this.onCompleted = onCompleted;
//  }
//
//  public abstract void start() throws Exception;
//}

//public abstract class Minigame implements Game {
//  public boolean started = false;
//  Runnable onCompleted;
//
//  private int WIDTH;
//  private int HEIGHT;
//
//
//  Minigame(int height, int width, Runnable onCompleted){
//    this.HEIGHT = height;
//    this.WIDTH = width;
//    this.onCompleted = onCompleted;
//  }
//
//
//  @Override
//  public int width() {
//    return WIDTH;
//  }
//
//  @Override
//  public int height() {
//    return HEIGHT;
//  }
//
//  @Override
//  public void setHeight(int height) {
//    this.HEIGHT = height;
//  }
//
//  @Override
//  public void setWidth(int width) {
//  this.WIDTH = width;
//  }
//
//  @Override
//  public GameObj player() {
//    return null;
//  }
//
//  @Override
//  public List<List<? extends GameObj>> goss() {
//    return List.of();
//  }
//
//  @Override
//  public void init() {
//
//  }
//
//  @Override
//  public void doChecks(int deltaTime) {
//
//  }
//
//  @Override
//  public void keyPressedReaction(KeyEvent keyEvent) {
//
//  }
//
//  @Override
//  public void keyReleasedReaction(KeyEvent keyEvent) {
//
//  }
//
//  @Override
//  public boolean won() {
//    return false;
//  }
//
//  @Override
//  public boolean lost() {
//    return false;
//  }
//}


public interface Minigame {

  int width();
  int height();


  void init();

  void doChecks(int deltaTime);

  void keyPressedReaction(KeyEvent keyEvent);

  void keyReleasedReaction(KeyEvent keyEvent);

  void mouseClick(MouseEvent mouseEvent);

  boolean won();

  boolean lost();

  default boolean ended() {
    return won() || lost();
  }


  void paintTo(Graphics g);


  default void play() {
    init();
    var f = new JFrame();
    f.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    f.add(new MiniSwingScreen(this));
    f.pack();
    f.setResizable(false);
    f.setLocationRelativeTo(null);
    f.setVisible(true);
  }
}

