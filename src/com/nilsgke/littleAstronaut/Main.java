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
  JUMP_KING, JETPACK
}

public class Main implements Game {
  static final int PLAYER_SPEED = 10;
  static final double GRAVITY = 1.5;
  static final double MIN_JUMP = 6;
  static final double MAX_JUMP = 15;

  static final double CAM_MARGIN_PERCENTAGE = 0.4;

  Player player;
  List<List<? extends GameObj>> gameObjects;
  private int width;
  private int height;

  private Level currentLevel;
  private int currentLevelIndex = 1;

  private boolean DEBUG_MODE = false;

  //GameMap gameMap;
  Camera camera;

  Controls controls = Controls.JUMP_KING;

  boolean canJump = false;
  double jumpValue = 0;
  ArrayList<Integer> pressedKeys = new ArrayList<>();

  int konami_pressed = 0;
  static final int[] konami_sequence = {38, 38, 40, 40, 37, 39, 37, 39, 66, 65}; // up, up, down, down, left, right, left, right, b, a

  public static void main(String[] args) throws Exception {
    new Main().play();
  }

  public Main() {
    this.height = 700;
    this.width = 1000;

    this.player = new Player(new Vertex(0, 0), new Vertex(0, 0), 70, 70);
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

    this.pressedKeys.clear();
    this.currentLevel = newLevel;
    player.pos().moveTo(this.currentLevel.startPos);
    player.velocity().moveTo(new Vertex(0, 0));
  }

  @Override
  public void paintTo(Graphics g) {
    g.setColor(new Color(0,100, 255));
    g.setColor(Color.CYAN);
    g.fillRect(0, 0, width(), height());

    // minigame overlay
    if (this.currentLevel.minigameStatus == Level.Status.STARTED) {
      g.setColor(Color.DARK_GRAY);
      g.fillRoundRect(0, 0, Math.max(width / 3, 150), Math.max(height / 3, 100), 20, 20);
      g.setColor(Color.WHITE);
      g.setFont(new Font("Arial", Font.BOLD, 30));
      g.drawString("Minigame Running", width / 2 - 150, height / 2);
      return;
    }


    g.translate(-(int) (camera.pos().x - width() / 2.0), -(int) (camera.pos().y - height() / 2.0));

    g.setColor(Color.WHITE);
    for (var gos : goss()) gos.forEach(go -> go.paintTo(g));


    for (var platform : currentLevel.platforms)
      platform.paintTo(g);


    g.setColor(Color.BLUE);
    g.drawRect((int) player.pos().x, (int) (player.pos().y + player.height()), (int) player.width(), 5);
    g.fillRect((int) player.pos().x, (int) (player.pos().y + player.height()), (int) ((player.width() / MAX_JUMP) * Math.min(jumpValue, MAX_JUMP)), 5);

    currentLevel.additionalPaint(g);

    player().paintTo(g);

    currentLevel.paintRocket(g);


    if (DEBUG_MODE) {
      // draw camera margin
      g.setColor(Color.BLACK);
      int camMarginTop = (int) (height() * CAM_MARGIN_PERCENTAGE);
      int camMarginLeft = (int) (width() * CAM_MARGIN_PERCENTAGE);
      g.drawLine(0, camMarginTop, width(), camMarginTop);
      g.drawLine(0, height() - camMarginTop, width(), height() - camMarginTop);
      g.drawLine(camMarginLeft, 0, camMarginLeft, height());
      g.drawLine(width() - camMarginLeft, 0, width() - camMarginLeft, height());

      // paint completed zone
      g.setColor(Color.MAGENTA);
      g.drawRect((int) currentLevel.completeZone.pos().x, (int) currentLevel.completeZone.pos().y, (int) currentLevel.completeZone.width(), (int) currentLevel.completeZone.height());
    }


  }

  @Override
  public void doChecks(int deltaTime) {
    var playerVelocity = this.player.velocity();

    // gravity
    playerVelocity.y += GRAVITY * (deltaTime / 50.0);

    // handle minigame stuff
    if (currentLevel.minigameStatus != Level.Status.NOT_STARTED) {
      var centerOfRocket = new Vertex((int) (this.currentLevel.completeZone.pos().x + (this.currentLevel.completeZone.width() / 2)), (int) (this.currentLevel.completeZone.pos().y + (this.currentLevel.completeZone.height() / 2)));

      if (player.pos().x != centerOfRocket.x || player.pos().y != centerOfRocket.y) {
        var vectorToRocket = new Vertex((int) (centerOfRocket.x - (this.player.pos.x + this.player.width / 2)) * .1, // center player on rocket
                player.velocity.y != 0 ? player.velocity.y : (int) (centerOfRocket.y + this.currentLevel.completeZone.height() / 2 - this.player.pos.y - this.player.height) * .1 // get player on bottom of rocket
        );

        this.player.velocity.moveTo(vectorToRocket);
      }

      if (currentLevel.minigame.ended()) {
        if (currentLevel.minigame.lost()) this.loadLevel(currentLevelIndex); // redo current level if lost
        else this.loadLevel(currentLevelIndex + 1); // next level if won
      }
    }

    var startedMinigame = currentLevel.checkIfInCompletionZone(player);
    if (startedMinigame) {
      this.player.velocity().moveTo(new Vertex(0, 0));
      return;
    }

    // reset level if player is below 10000
    if (player.pos().y > 1000) {
      player.velocity().moveTo(new Vertex(0, 0));
      player.pos().moveTo(currentLevel.startPos);
      return;
    }


    if (playerVelocity.y > 0) canJump = true;
    if (playerVelocity.y < 0) canJump = false;

    boolean onGround = false;

    // collision
    for (var platform : currentLevel.platforms) {
      if (platform.stoodOnBy(player)) onGround = true;
      platform.applyCollision(player);
    }


    // walking
    if (currentLevel.minigameStatus == Level.Status.NOT_STARTED && (controls == Controls.JETPACK || (onGround && jumpValue == 0))) {
      // slowly increasing the velocity in the direction the user is holidng but cap max speed
      if (pressedKeys.contains(KeyEvent.VK_D) || pressedKeys.contains(KeyEvent.VK_RIGHT))
        playerVelocity.x += PLAYER_SPEED * deltaTime / 70.0;
      if (pressedKeys.contains(KeyEvent.VK_A) || pressedKeys.contains(KeyEvent.VK_LEFT))
        playerVelocity.x -= PLAYER_SPEED * deltaTime / 70.0;
      if (playerVelocity.x > PLAYER_SPEED) playerVelocity.x = PLAYER_SPEED;
      if (playerVelocity.x < -PLAYER_SPEED) playerVelocity.x = -PLAYER_SPEED;
    }

    // no movement on spacekey
    if (controls == Controls.JUMP_KING && jumpValue != 0) playerVelocity.x = 0;


    // jump force
    if (controls == Controls.JUMP_KING && onGround && pressedKeys.contains(KeyEvent.VK_SPACE) && canJump)
      jumpValue += PLAYER_SPEED * deltaTime / 100.0;

    // jump jump-king
    if (onGround && canJump && jumpValue > 0 && controls == Controls.JUMP_KING && !pressedKeys.contains(KeyEvent.VK_SPACE)) {
      player.velocity().y = -Math.max(Math.min(jumpValue, MAX_JUMP), MIN_JUMP);

      if (pressedKeys.contains(KeyEvent.VK_A) || pressedKeys.contains(KeyEvent.VK_LEFT))
        playerVelocity.x -= PLAYER_SPEED;
      if (pressedKeys.contains(KeyEvent.VK_D) || pressedKeys.contains(KeyEvent.VK_RIGHT))
        playerVelocity.x += PLAYER_SPEED;

      jumpValue = 0;
      canJump = false;
    }

    // jetpack
    if (controls == Controls.JETPACK && pressedKeys.contains(KeyEvent.VK_SPACE)) {
      player.velocity().y -= PLAYER_SPEED * .05 * deltaTime;
      if (player.velocity().y <= -PLAYER_SPEED) player.velocity().y = -PLAYER_SPEED; // max jetpack speed

      canJump = false;
    }

    // floor drag
    if (onGround && !pressedKeys.contains(KeyEvent.VK_A) && !pressedKeys.contains(KeyEvent.VK_D) && !pressedKeys.contains(KeyEvent.VK_LEFT) && !pressedKeys.contains(KeyEvent.VK_RIGHT))
      if (controls == Controls.JETPACK) playerVelocity.x *= Math.pow(0.9, deltaTime / 10.0);
      else if (controls == Controls.JUMP_KING) playerVelocity.x = 0;

    if (playerVelocity.x != 0.0)
      player.lastDirection = player.velocity.x > 0 ? Player.XDirection.RIGHT : Player.XDirection.LEFT;


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

    if (camera.pos().y > currentLevel.minCamY())
      camera.pos().moveTo(new Vertex(camera.pos().x, currentLevel.minCamY()));
  }

  @Override
  public void keyPressedReaction(KeyEvent keyEvent) {
    int key = keyEvent.getKeyCode();
    if (pressedKeys.contains(key)) return;
    if(key == KeyEvent.VK_I) DEBUG_MODE = !DEBUG_MODE;
    pressedKeys.add(key);
  }

  @Override
  public void keyReleasedReaction(KeyEvent keyEvent) {
    pressedKeys.remove(pressedKeys.indexOf(keyEvent.getKeyCode()));

    // konami code
    if (konami_sequence[konami_pressed] == keyEvent.getKeyCode()) konami_pressed++;
    else konami_pressed = 0;
    if (konami_pressed == konami_sequence.length) {
      // sequence completed
      konami_pressed = 0;
      controls = controls == Controls.JETPACK ? Controls.JUMP_KING : Controls.JETPACK;
    }

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