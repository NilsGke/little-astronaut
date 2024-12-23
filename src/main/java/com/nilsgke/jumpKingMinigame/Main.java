package com.nilsgke.jumpKingMinigame;


import com.nilsgke.jumpKingMinigame.map.GameMap;
import name.panitz.game2d.Game;
import name.panitz.game2d.GameObj;
import name.panitz.game2d.Vertex;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

enum Controls {
  JUMP_KING,
  NORMAL,
}

public class Main implements Game {
  static final int PLAYER_SPEED = 3;
  static final double GRAVITY = 1;

  static final double CAM_MARGIN_PERCENTAGE = 0.3;

  Player player;
  List<List<? extends GameObj>> gameObjects;
  private int width;
  private int height;


  private boolean debugMode = false;


  GameMap gameMap;
  Camera camera;

  Controls controls = Controls.NORMAL;

  boolean canJump = false;
  ArrayList<String> pressedKeys = new ArrayList<>();


  public static void main(String[] args) throws FileNotFoundException {
    new Main().play();
  }

  Main() {
    this.height = 600;
    this.width = 800;

    this.player = new Player(
            new Vertex(width() / 2.0, height() - 100),
            new Vertex(0, 0),
            20,
            20
    );
    this.camera = new Camera(new Vertex(player.pos().x, player.pos().y));
    this.gameObjects = new ArrayList<>();


    try {
      this.gameMap = GameMap.fromJson();
    } catch (FileNotFoundException e) {
      System.err.printf(e.getMessage());
      System.exit(1);
    }

  }

  @Override
  public void paintTo(Graphics g) {
    g.setColor(Color.DARK_GRAY);
    g.fillRect(0, 0, width(), height());


    if(debugMode) {
      // draw camera margin
      g.setColor(Color.BLACK);
      int camMarginTop = (int) (height() * CAM_MARGIN_PERCENTAGE);
      int camMarginLeft = (int) (width() * CAM_MARGIN_PERCENTAGE);
      g.drawLine(0, camMarginTop, width(), camMarginTop);
      g.drawLine(0, height() - camMarginTop, width(), height() - camMarginTop);
      g.drawLine(camMarginLeft, 0, camMarginLeft, height());
      g.drawLine(width() - camMarginLeft, 0, width() - camMarginLeft, height());
    }


    g.translate(-(int) (camera.pos().x - width() / 2.0), -(int) (camera.pos().y - height() / 2.0));

    g.setColor(Color.WHITE);
    for (var gos : goss()) gos.forEach(go -> go.paintTo(g));
    player().paintTo(g);
    for (var platform : gameMap.platforms)
      platform.paintTo(g);
  }

  @Override
  public void doChecks(int deltaTime) {
    var playerVelocity = this.player.velocity();

    // gravity
    playerVelocity.y += GRAVITY * (deltaTime / 100.0);

    if (playerVelocity.y > 0) canJump = true;
    if (playerVelocity.y < 0) canJump = false;

    for (var platform : gameMap.platforms) {
      if (!player.isAbove(platform) && !player.isUnderneath(platform) && !player.isLeftOf(platform) && !player.isRightOf(platform)) {
        // player is touching a platform

        player.velocity().y = 0;
        player.pos().y = platform.pos().y - player.height();

        // floor drag
        playerVelocity.x *= 0.95;

        // jump
        if (canJump && pressedKeys.contains(" ")) {
          player.velocity().y = -PLAYER_SPEED;
          canJump = false;
        }


        break; // no need to check other platforms
      }
    }


    // walking
    if (controls == Controls.NORMAL) {
      // slowly increasing the velocity in the direction the user is holidng but cap max speed
      if (pressedKeys.contains("d")) playerVelocity.x += PLAYER_SPEED * deltaTime / 70.0;
      if (pressedKeys.contains("a")) playerVelocity.x -= PLAYER_SPEED * deltaTime / 70.0;
      if (playerVelocity.x > PLAYER_SPEED) playerVelocity.x = PLAYER_SPEED;
      if (playerVelocity.x < -PLAYER_SPEED) playerVelocity.x = -PLAYER_SPEED;
    }

    // update camera
    camera.acceleration().x *= 0.9;
    camera.acceleration().y *= 0.9;
    camera.velocity().x *= 0.9;
    camera.velocity().y *= 0.9;

    // calc distance from cam to player
    double camXDistanceToPlayer = player.pos().x + player.width() / 2 - camera.pos().x;
    double xMarginFromCenter = width() / 2.0 - width() * CAM_MARGIN_PERCENTAGE;
    if (Math.abs(camXDistanceToPlayer) > xMarginFromCenter)
      // accelerate camera to the direction of the player
      camera.acceleration().x += (camXDistanceToPlayer - (Math.signum(camXDistanceToPlayer) * xMarginFromCenter ))  * deltaTime / 100.0;


    double camYDistanceToPlayer = player.pos().y + player.height() / 2 - camera.pos().y;
    double yMarginFromCenter = height() / 2.0 - height() * CAM_MARGIN_PERCENTAGE;
    if (Math.abs(camYDistanceToPlayer) > yMarginFromCenter)
      camera.acceleration().y += (camYDistanceToPlayer - (Math.signum(camYDistanceToPlayer) * yMarginFromCenter)) * deltaTime / 14.0;
    else
      camera.velocity().y *= 0.5; // prevents hitting ground camera bounce to bottom and back


    camera.update(deltaTime);

  }

  @Override
  public void keyPressedReaction(KeyEvent keyEvent) {
    if (pressedKeys.contains(keyEvent.getKeyChar() + "")) return;
    pressedKeys.add(keyEvent.getKeyChar() + "");
  }

  @Override
  public void keyReleasedReaction(KeyEvent keyEvent) {
    pressedKeys.remove(keyEvent.getKeyChar() + "");
  }

  @Override
  public int width() {
    return this.width;
  }

  @Override
  public int height() {
    return this.height;
  }

  @Override
  public void setHeight(int height) {
    this.height = height;
  }

  @Override
  public void setWidth(int width) {
    this.width = width;
  }

  @Override
  public GameObj player() {
    return this.player;
  }

  @Override
  public List<List<? extends GameObj>> goss() {
    return List.of();
  }

  @Override
  public void init() {
  }

  @Override
  public boolean won() {
    return false;
  }

  @Override
  public boolean lost() {
    return false;
  }
}