package com.nilsgke.littleAstronaut;


import com.nilsgke.littleAstronaut.levels.Level;
import com.nilsgke.littleAstronaut.levels.Level_1;
import name.panitz.game2d.Game;
import name.panitz.game2d.GameObj;
import name.panitz.game2d.Vertex;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

enum Controls {
  JUMP_KING, NORMAL,
}

public class Main implements Game {
  static final int PLAYER_SPEED = 4;
  static final double GRAVITY = 1.5;
  static final double MIN_JUMP = 6;
  static final double MAX_JUMP = 12;

  static final double CAM_MARGIN_PERCENTAGE = 0.4;

  Player player;
  List<List<? extends GameObj>> gameObjects;
  private int width;
  private int height;

  private Level currentLevel;
  private int currentLevelIndex = 1;

  private boolean debugMode = false;

  //GameMap gameMap;
  Camera camera;

  Controls controls = Controls.JUMP_KING;

  boolean canJump = false;
  double jumpValue = 0;
  ArrayList<String> pressedKeys = new ArrayList<>();


  public static void main(String[] args) throws Exception {
    new Main().play();
  }

  public Main() throws Exception {
    this.height = 700;
    this.width = 1000;

    this.player = new Player(new Vertex(0, 0), new Vertex(0, 0), 20, 20);
    this.camera = new Camera(new Vertex(player.pos().x, player.pos().y));
    this.gameObjects = new ArrayList<>();

    loadLevel(1);
  }

  private void loadLevel(int levelNumber) {
    System.out.printf("Loading level %d", levelNumber);

    Level newLevel = switch (levelNumber) {
      case 1 -> new Level_1();
//      case 2 -> new Level_2();
//      case 3 -> new Level_3();
      default -> null;
    };

    if (newLevel == null) {
      System.out.println("COMPLETED!");
      return;
    }

    this.currentLevel = newLevel;
    player.pos().moveTo(this.currentLevel.startPos);
    player.velocity().moveTo(new Vertex(0, 0));
  }

  @Override
  public void paintTo(Graphics g) {
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, width(), height());

    // minigame overlay
    if(this.currentLevel.minigameStarted) {
      g.setColor(Color.DARK_GRAY);
      g.fillRoundRect(width/3, height/3, Math.max(width/3, 150), Math.max(height/3, 100), 20, 20);
      g.setColor(Color.WHITE);
      g.setFont(new Font("Arial", Font.BOLD, 30));
      g.drawString("Minigame Running", width/2 - 150, height / 2 );
      return;
    }


    if (debugMode) {
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


    for (var platform : currentLevel.platforms)
      platform.paintTo(g);

    // paint completed zone
    g.setColor(Color.MAGENTA);
    g.drawRect((int) currentLevel.completeZone.pos().x, (int) currentLevel.completeZone.pos().y, (int) currentLevel.completeZone.width(), (int) currentLevel.completeZone.height());


    player().paintTo(g);
    g.setColor(Color.BLUE);
    g.drawRect((int) player.pos().x, (int) (player.pos().y + player.height()), (int) player.width(), 5);
    g.fillRect((int) player.pos().x, (int) (player.pos().y + player.height()), (int) ((player.width() / MAX_JUMP) * Math.min(jumpValue, MAX_JUMP)), 5);
  }

  @Override
  public void doChecks(int deltaTime) {
    // handle minigame stuff
    if (currentLevel.minigameStarted) {
      if (currentLevel.minigame.ended()) {
        if (currentLevel.minigame.lost())
          this.loadLevel(currentLevelIndex); // redo current level if lost
        else
          this.loadLevel(currentLevelIndex + 1); // next level if won
      }

      return; // don't do any more checks if minigame is running
    }

    var startedMinigame = currentLevel.checkIfInCompletionZone(player);
    if(startedMinigame) {
      this.player.velocity().moveTo(new Vertex(0,0));
      return;
    }

    // reset level if player is below 10000
    if (player.pos().y > 1000) {
      player.velocity().moveTo(new Vertex(0, 0));
      player.pos().moveTo(currentLevel.startPos);
      System.out.println("updated: " + player.pos().y);
      return;
    }

    var playerVelocity = this.player.velocity();

    // gravity
    playerVelocity.y += GRAVITY * (deltaTime / 50.0);

    if (playerVelocity.y > 0) canJump = true;
    if (playerVelocity.y < 0) canJump = false;

    boolean onGround = false;

    // collision
    for (var platform : currentLevel.platforms) {
      if (!player.isAbove(platform) && !player.isUnderneath(platform) && !player.isLeftOf(platform) && !player.isRightOf(platform)) {
        // player is touching a platform

        double overlapTop = player.pos().y + player.height() - platform.pos().y;
        double overlapBottom = platform.pos().y + platform.height() - player.pos().y;
        double overlapLeft = player.pos().x + player.width() - platform.pos().x;
        double overlapRight = platform.pos().x + platform.width() - player.pos().x;
        double minOverlap = Math.min(Math.min(overlapTop, overlapBottom), Math.min(overlapLeft, overlapRight));

        if (minOverlap == overlapTop) { // touching top (on ground)
          player.velocity().y = 0;
          player.pos().y = platform.pos().y - player.height();
          onGround = true;
        } else if (minOverlap == overlapBottom) { // touching bottom
          player.pos().y = platform.pos().y + platform.height();
          player.velocity().y = Math.abs(player.velocity().y);

        } else if (minOverlap == overlapLeft) { // touching left
          player.pos().x = platform.pos().x - player.width();
          player.velocity().x = Math.abs(player.velocity().x) * -1;
        } else if (minOverlap == overlapRight) { // touching right
          player.pos().x = platform.pos().x + platform.width();
          player.velocity().x = Math.abs(player.velocity().x);
        }
      }
    }


    // walking
    if (controls == Controls.NORMAL || (controls == Controls.JUMP_KING && onGround && jumpValue == 0)) {
      // slowly increasing the velocity in the direction the user is holidng but cap max speed
      if (pressedKeys.contains("d")) playerVelocity.x += PLAYER_SPEED * deltaTime / 70.0;
      if (pressedKeys.contains("a")) playerVelocity.x -= PLAYER_SPEED * deltaTime / 70.0;
      if (playerVelocity.x > PLAYER_SPEED) playerVelocity.x = PLAYER_SPEED;
      if (playerVelocity.x < -PLAYER_SPEED) playerVelocity.x = -PLAYER_SPEED;
    }

    // no movement on spacekey
    if (controls == Controls.JUMP_KING && jumpValue != 0) playerVelocity.x = 0;


    // jump force
    if (controls == Controls.JUMP_KING && onGround && pressedKeys.contains(" ") && canJump)
      jumpValue += PLAYER_SPEED * deltaTime / 100.0;

    // jump jump-king
    if (onGround && canJump && jumpValue > 0 && controls == Controls.JUMP_KING && !pressedKeys.contains(" ")) {
      System.out.printf("jump value: %f\n", jumpValue);
      player.velocity().y = -Math.max(Math.min(jumpValue, MAX_JUMP), MIN_JUMP);

      if (pressedKeys.contains("a")) playerVelocity.x -= PLAYER_SPEED;
      if (pressedKeys.contains("d")) playerVelocity.x += PLAYER_SPEED;

      jumpValue = 0;
      canJump = false;
    }

    // jump normal
    if (onGround && canJump && controls == Controls.NORMAL && pressedKeys.contains(" ")) {
      player.velocity().y = -PLAYER_SPEED * 2;
      canJump = false;
    }

    // floor drag
    if (onGround && !pressedKeys.contains("d") && !pressedKeys.contains("a"))
      if (controls == Controls.NORMAL) playerVelocity.x *= Math.pow(0.9, deltaTime / 10.0);
      else if (controls == Controls.JUMP_KING) playerVelocity.x = 0;


    // update camera
    // calc distance from cam to player
    double camXDistanceToPlayer = player.pos().x + player.width() / 2 - camera.pos().x;
    double xMarginFromCenter = width() / 2.0 - width() * CAM_MARGIN_PERCENTAGE;
    if (Math.abs(camXDistanceToPlayer) > xMarginFromCenter)
      // accelerate camera to the direction of the player
      camera.acceleration().x += (camXDistanceToPlayer - (Math.signum(camXDistanceToPlayer) * xMarginFromCenter)) * deltaTime / 100.0;


    double camYDistanceToPlayer = player.pos().y + player.height() / 2 - camera.pos().y;
    double yMarginFromCenter = height() / 2.0 - height() * CAM_MARGIN_PERCENTAGE;
    if (Math.abs(camYDistanceToPlayer) > yMarginFromCenter)
      camera.acceleration().y += (camYDistanceToPlayer - (Math.signum(camYDistanceToPlayer) * yMarginFromCenter)) * deltaTime / 14.0;
    else camera.velocity().y *= 0.5; // prevents hitting ground camera bounce to bottom and back


    camera.update(deltaTime);

  }

  @Override
  public void keyPressedReaction(KeyEvent keyEvent) {
    if (pressedKeys.contains(keyEvent.getKeyChar() + "")) return;
    if (keyEvent.getKeyChar() == 'c') controls = controls == Controls.NORMAL ? Controls.JUMP_KING : Controls.NORMAL;
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