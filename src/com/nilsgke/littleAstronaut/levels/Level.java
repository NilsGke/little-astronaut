
package com.nilsgke.littleAstronaut.levels;

import com.nilsgke.littleAstronaut.Main;
import com.nilsgke.littleAstronaut.Player;
import com.nilsgke.littleAstronaut.map.Platform;
import com.nilsgke.littleAstronaut.sprites.Animation;
import com.nilsgke.littleAstronaut.sprites.ImageHelper;
import name.panitz.game2d.Vertex;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public abstract class Level {


  public enum AnimationState {NOT_STARTED, STARTING, FLYING, DONE}

  public Vertex startPos;
  public Platform[] platforms;
  public Platform completeZone;
  public Vertex rocketPos;
  public Boolean finishAnimation = false;
  public AnimationState animationState = AnimationState.NOT_STARTED;
  public Boolean finished = false;
  private final Vertex planetSignPos;
  private final Animation planetAnimation;
  private short blackScreenOpacity = 0;
  protected final BufferedImage backdrop;

  private static final BufferedImage rocketImage;
  private static final BufferedImage planetSign;
  private static final Animation fireAnimation;

  static {
    try {
      rocketImage = ImageHelper.readImageFileAt("/assets/rocket/rocket.png");

      BufferedImage fireAnimationImage = ImageHelper.readImageFileAt("/assets/rocket/fire-animation.png");
      fireAnimationImage = ImageHelper.toBufferedImage(fireAnimationImage.getScaledInstance(fireAnimationImage.getWidth() * 2, fireAnimationImage.getHeight() * 2, Image.SCALE_DEFAULT));
      fireAnimation = new Animation(fireAnimationImage, 19, 1000, true);

      var planetSignImage = ImageHelper.readImageFileAt("/assets/planets/planet-sign.png");
      planetSign = ImageHelper.toBufferedImage(planetSignImage.getScaledInstance(
              planetSignImage.getWidth() * 2,
              planetSignImage.getHeight() * 2,
              Image.SCALE_DEFAULT
      ));

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  abstract public int minCamY();

  abstract public Color backgroundColor();

  public Level(Platform[] platforms, Vertex startPos, Vertex rocketPos, Vertex planetSignPos, Animation planetAnimation, BufferedImage backdrop) {
    this.platforms = platforms;
    this.startPos = startPos;
    this.rocketPos = rocketPos;
    this.planetSignPos = planetSignPos;
    this.planetAnimation = planetAnimation;
    this.completeZone = new Platform((int) rocketPos.x, (int) rocketPos.y, rocketImage.getWidth() * 4, rocketImage.getHeight() * 4);
    this.backdrop = backdrop;

    this.blackScreenOpacity = 255;
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
      @Override
      public void run() {
        if (blackScreenOpacity > 0) blackScreenOpacity--;
        else timer.cancel();
      }
    };
    timer.schedule(task, 0, 1000 / 255);
  }

  public void checkIfInCompletionZone(Player player) {
    if (finishAnimation || finished || !player.touches(this.completeZone)) return;

    System.out.println("finish Animation");

    startFinishAnimation();
  }

  public void startFinishAnimation() {
    this.finishAnimation = true;
    animationState = AnimationState.STARTING;

    // fade in blackscreen
    Timer timer = new Timer();
    TimerTask t1 = new TimerTask() {
      public void run() {
        animationState = AnimationState.FLYING;
      }
    };
    TimerTask t2 = new TimerTask() {
      public void run() {
        animationState = AnimationState.DONE;
        timer.cancel();
      }
    };

    TimerTask updateBlackScreen = new TimerTask() {
      @Override
      public void run() {
        if (blackScreenOpacity < 255) blackScreenOpacity++;
      }
    };

    timer.schedule(t1, 1000);
    timer.schedule(t2, 3000);

    // blackScreen
    blackScreenOpacity = 0;
    timer.schedule(updateBlackScreen, 2000, 1000 / 255);

  }

  public void paintRocket(Graphics g) {
    if (!finished && animationState != AnimationState.FLYING)
      g.drawImage(rocketImage, (int) this.rocketPos.x, (int) this.rocketPos.y, rocketImage.getWidth() * 4, rocketImage.getHeight() * 4, null);

    else {
      fireAnimation.paintTo(g,
              (int) (completeZone.pos().x + completeZone.width() / 2 - fireAnimation.frameWidth() - 2),
              (int) (completeZone.pos().y + completeZone.height() - 18)
      );

      g.drawImage(rocketImage, (int) this.completeZone.pos().x, (int) this.completeZone.pos().y, rocketImage.getWidth() * 4, rocketImage.getHeight() * 4, null);

    }
  }

  public void paintPlanetSign(Graphics g) {
    paintPlanetSign(g, planetSignPos, planetAnimation);
  }

  public static void paintPlanetSign(Graphics g, Vertex planetSignPos, Animation planetAnimation) {
    g.drawImage(planetSign, (int) planetSignPos.x, (int) planetSignPos.y, null);
    planetAnimation.paintTo(g, (int) (planetSignPos.x + ((double) planetSign.getWidth() / 2) - ((double) planetAnimation.frameWidth())), (int) planetSignPos.y + 45);
  }

  public void paintBlackScreen(Graphics g, int x, int y, int width, int height) {
    g.setColor(new Color(0, 0, 0, blackScreenOpacity));
    g.fillRect(0, 0, width, height);
  }

  public void paintBackdropTo(Graphics g, Vertex camPosition) {
    double x = camPosition.x - Main.WIDTH / 2.0;
    double y = camPosition.y + Main.HEIGHT - backdrop.getHeight();

    // offset
    x -= camPosition.x * .7;
    y -= camPosition.y * .7;


    g.drawImage(backdrop, (int) x - backdrop.getWidth(), (int) y, null);
    g.drawImage(backdrop, (int) x, (int) y, null);
    g.drawImage(backdrop, (int) x + backdrop.getWidth(), (int) y, null);
  }

  abstract public void additionalPaint(Graphics2D g);

  abstract public void additionalChecks(long deltaTime, Player p);
}
