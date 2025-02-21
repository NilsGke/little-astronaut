package com.nilsgke.littleAstronaut;


import com.nilsgke.littleAstronaut.toasts.Toaster;
import com.nilsgke.littleAstronaut.connection.WSClient;
import com.nilsgke.littleAstronaut.connection.WSData;
import com.nilsgke.littleAstronaut.connection.WSServer;
import com.nilsgke.littleAstronaut.levels.*;
import com.nilsgke.littleAstronaut.menu.Menu;
import name.panitz.game2d.Game;
import name.panitz.game2d.GameObj;
import name.panitz.game2d.Vertex;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main implements Game {
  enum Controls {JUMP_KING, JETPACK}

  static final int PLAYER_SPEED = 4;
  static final double GRAVITY = 1.5;
  static final double MIN_JUMP = 6;
  static final double MAX_JUMP = 15;

  static final double CAM_MARGIN_PERCENTAGE = 0.4;

  final Player player;
  final List<List<? extends GameObj>> gameObjects;
  public static int WIDTH = 1000;
  public static int HEIGHT = 700;

  private Level currentLevel;
  private boolean gameFinished = false;

  private boolean DEBUG_MODE = false;

  private final Menu menu;
  private BufferedImage gameImage;
  private boolean gameImageBlurred = false;

  //GameMap gameMap;
  final Camera camera;

  Controls controls = Controls.JUMP_KING;

  boolean canJump = false;
  double jumpValue = 0;
  final ArrayList<Integer> pressedKeys = new ArrayList<>();

  int konami_pressed = 0;
  static final int[] konami_sequence = {38, 38, 40, 40, 37, 39, 37, 39, 66, 65}; // up, up, down, down, left, right, left, right, b, a

  WSServer wsServer;
  WSClient wsClient;

  public static void main(String[] args) {
    new Main().play();
  }

  public Main() {
    this.player = new Player((byte) 0, new Vertex(0, 0), new Vertex(0, 0));
    this.camera = new Camera(new Vertex(player.pos().x, player.pos().y));
    this.gameObjects = new ArrayList<>();

    this.wsClient = new WSClient(this.player);
    this.wsServer = new WSServer(this.player);

    this.menu = new Menu(WIDTH, HEIGHT, wsClient, wsServer);
    this.gameImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);

    loadLevel(player.level);
  }

  private void loadLevel(byte levelNumber) {
    player.level = levelNumber;
    System.out.printf("Loading level %d", levelNumber);

    Level newLevel = switch (levelNumber) {
      case 1 -> new Level_1();
      case 2 -> new Level_2();
      case 3 -> new Level_3();
      case 4 -> new Level_4();
      default -> null;
    };

    if (newLevel == null) {
      gameFinished = true;
      newLevel = new FinishLevel();
    }

    this.pressedKeys.clear();
    this.currentLevel = newLevel;
    player.pos().moveTo(this.currentLevel.startPos);
    camera.pos().moveTo(this.currentLevel.startPos);
    player.velocity().moveTo(new Vertex(0, 0));
  }

  @Override
  public void paintTo(Graphics2D g) {
    if (gameImage != null && menu.isOpen()) {
      if (!gameImageBlurred) { // only blur image once and then reuse the blurred one
        gameImage = Menu.applyBlur(gameImage, 4, 0.3f);
        gameImageBlurred = true;
      }

      g.drawImage(gameImage, 0, 0, null); // draw blurred game image
      menu.paintTo(g, WIDTH, HEIGHT);
      return;
    }


    gameImageBlurred = false;
    Graphics2D g2d = gameImage.createGraphics();
    drawGame(g2d);
    if (DEBUG_MODE) drawDebugUI(g2d);
    g.drawImage(gameImage, 0, 0, null);

  }

  private void drawGame(Graphics2D g2d) {
    g2d.setColor(currentLevel.backgroundColor());
    g2d.fillRect(0, 0, width(), height());

    int offsetX = -(int) (camera.pos().x - width() / 2.0);
    int offsetY = -(int) (camera.pos().y - height() / 2.0);

    g2d.translate(offsetX, offsetY);

    currentLevel.paintBackdropTo(g2d, camera.pos());


    g2d.setColor(Color.WHITE);
    for (var gos : goss()) gos.forEach(go -> go.paintTo(g2d));


    for (var platform : currentLevel.platforms)
      platform.paintTo(g2d);

    currentLevel.paintPlanetSign(g2d);

    currentLevel.additionalPaint(g2d);

    g2d.setColor(Color.magenta);
    // draw WSClient players
    if (wsClient.getStatus() == WSClient.Status.CONNECTED)
      for (var entry : wsClient.players.entrySet()) {
        var remoteId = entry.getKey();
        if (remoteId != player.id) { // prevent painting own player
          var remotePlayer = entry.getValue();
          if(remotePlayer.level == this.player.level)
            remotePlayer.paintTo(g2d);
        }
      }


    // draw WSServer players
    if (wsServer.getStatus() == WSServer.Status.RUNNING)
      for (var entry : WSServer.players.entrySet()) {
        var remotePlayer = entry.getValue();
        if (remotePlayer.id != 0 && remotePlayer.level == this.player.level) remotePlayer.paintTo(g2d);
      }

    if (currentLevel.animationState != Level.AnimationState.FLYING) player().paintTo(g2d);

    currentLevel.paintRocket(g2d);

    if (DEBUG_MODE) drawDebugStuff(g2d);

    // ONLY HUD AND UI FROM HERE ON
    g2d.translate(-offsetX, -offsetY); // reset offset to fix element to the screen

    Toaster.paintToastsTo(g2d, WIDTH, HEIGHT);

    currentLevel.paintBlackScreen(g2d, (int) camera.pos().x, (int) camera.pos().y, WIDTH, HEIGHT);
  }

  private void drawDebugStuff(Graphics2D g2d) {
    // jump state bar
    g2d.setColor(Color.MAGENTA);
    final int BAR_WIDTH = (int) player.width;
    final int BAR_HEIGHT = 5;
    g2d.drawRect((int) (player.pos.x + player.width / 2 - BAR_WIDTH / 2.0), (int) (player.pos.y + player.height), BAR_WIDTH, BAR_HEIGHT);
    g2d.fillRect((int) (player.pos.x + player.width / 2 - BAR_WIDTH / 2.0), (int) (player.pos.y + player.height), (int) (BAR_WIDTH * (Math.min(jumpValue, MAX_JUMP) / MAX_JUMP)), BAR_HEIGHT);

    g2d.setColor(Color.RED);
    g2d.drawRect(
            (int) currentLevel.completeZone.pos().x,
            (int) currentLevel.completeZone.pos().y,
            (int) currentLevel.completeZone.width(),
            (int) currentLevel.completeZone.height()
    );
  }

  private void drawDebugUI(Graphics2D g2d) {
    // draw camera margin
    g2d.setColor(Color.YELLOW);
    int camMarginTop = (int) (height() * CAM_MARGIN_PERCENTAGE);
    int camMarginLeft = (int) (width() * CAM_MARGIN_PERCENTAGE);
    g2d.drawLine(0, camMarginTop, width(), camMarginTop);
    g2d.drawLine(0, height() - camMarginTop, width(), height() - camMarginTop);
    g2d.drawLine(camMarginLeft, 0, camMarginLeft, height());
    g2d.drawLine(width() - camMarginLeft, 0, width() - camMarginLeft, height());
  }

  @Override
  public void doChecks(long deltaTime) {
    if (menu.isOpen()) return;


    // move all game objects with velocity
    for (var gos : goss()) gos.forEach(GameObj::move);
    player().move();


    var playerVelocity = this.player.velocity();

    // gravity
    playerVelocity.y += GRAVITY * (deltaTime / 50.0);

    currentLevel.checkIfInCompletionZone(player);


    // level end stuff
    if (currentLevel.finishAnimation) {
      switch (currentLevel.animationState) {
        case Level.AnimationState.NOT_STARTED -> {

        }
        case Level.AnimationState.STARTING ->
          // move player horizontally to rocket
          this.player.pos.add(new Vertex(((this.player.pos.x + this.player.width / 2) - (this.currentLevel.completeZone.pos().x + this.currentLevel.completeZone.width() / 2)) * -0.1, 0));

        case Level.AnimationState.FLYING -> {
          currentLevel.completeZone.velocity().add(new Vertex(0, -0.2));
          currentLevel.completeZone.pos().add(currentLevel.completeZone.velocity());
          player.pos().moveTo(currentLevel.completeZone.pos());
          player.pos().add(new Vertex(10, 10)); // offset so that player is not at top left corner of rocket
          return; // prevent camera updates
        }
        case Level.AnimationState.DONE -> {
          loadLevel((byte) (player.level + 1));
          return;
        }
      }
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
    if (!currentLevel.finishAnimation && (controls == Controls.JETPACK || (onGround && jumpValue == 0))) {
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
      jumpValue += PLAYER_SPEED * deltaTime / 150.0;

    // jump jump-king
    if (!currentLevel.finishAnimation && onGround && canJump && jumpValue > 0 && controls == Controls.JUMP_KING && !pressedKeys.contains(KeyEvent.VK_SPACE)) {
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

    currentLevel.additionalChecks(deltaTime, player);


    // update camera
    if (!gameFinished)
      camera.updateCamera(deltaTime, player, width(), height());

    if (camera.pos().y > currentLevel.minCamY())
      camera.pos().moveTo(new Vertex(camera.pos().x, currentLevel.minCamY()));


    if (this.wsClient.getStatus() == WSClient.Status.CONNECTED) {
      // send player data to server
      try {
        byte[] playerData = WSData.Player.encodeWithIdentifier(this.player.id, this.player.level, this.player.pos.x, this.player.pos.y, this.player.velocity.x, this.player.velocity.y);
        wsClient.sendBytes(playerData);
      } catch (IOException e) {
        System.err.println("could not send data to server");
        System.err.println(e.getMessage());
      }

      // simulate remote player physics
      for (var remotePlayer : wsClient.players.entrySet())
        remotePlayer.getValue().move();
    }
    Toaster.updateToasts();
  }

  @Override
  public void keyPressedReaction(KeyEvent keyEvent) {
    this.menu.keyTyped(keyEvent);
    int key = keyEvent.getKeyCode();
    if (pressedKeys.contains(key)) return;
    if (key == KeyEvent.VK_I && !menu.isOpen()) DEBUG_MODE = !DEBUG_MODE;
    pressedKeys.add(key);
  }

  @Override
  public void keyReleasedReaction(KeyEvent keyEvent) {
    pressedKeys.remove((Integer) keyEvent.getKeyCode());

    // konami code
    if (konami_sequence[konami_pressed] == keyEvent.getKeyCode()) konami_pressed++;
    else konami_pressed = 0;
    if (konami_pressed == konami_sequence.length) {
      // sequence finished
      konami_pressed = 0;
      controls = controls == Controls.JETPACK ? Controls.JUMP_KING : Controls.JETPACK;

      if(controls == Controls.JETPACK) Toaster.info("Jetpack aktiviert", 4000);
      else Toaster.error("Jetpack deaktiviert", 4000);
    }

  }

  @Override
  public void mouseClickedReaction(MouseEvent mouseEvent) {

  }

  @Override
  public void mousePressedReaction(MouseEvent mouseEvent) {
    menu.mousePressed(mouseEvent);
  }

  @Override
  public void mouseReleasedReaction(MouseEvent mouseEvent) {
    menu.mouseReleased(mouseEvent);
  }

  @Override
  public int width() {
    return WIDTH;
  }

  @Override
  public int height() {
    return HEIGHT;
  }

  @Override
  public void setHeight(int height) {
    HEIGHT = height;
  }

  @Override
  public void setWidth(int width) {
    WIDTH = width;
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