
package com.nilsgke.littleAstronaut.levels;

import com.nilsgke.littleAstronaut.Player;
import com.nilsgke.littleAstronaut.map.Platform;
import com.nilsgke.littleAstronaut.sprites.Animation;
import com.nilsgke.littleAstronaut.sprites.ImageHelper;
import name.panitz.game2d.Vertex;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
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


  private static final BufferedImage rocketImage;
  private static final BufferedImage planetSign;
  private static final Animation rocketEnterAnimation;

  static {
    try {
      rocketImage = ImageIO.read(new File("assets/rocket/rocket.png"));

      BufferedImage rocketEnterAnimationImage = ImageIO.read(new File("assets/rocket/rocket-enter-animation.png"));
      rocketEnterAnimationImage = ImageHelper.toBufferedImage(rocketEnterAnimationImage.getScaledInstance(rocketEnterAnimationImage.getWidth() * 2, rocketEnterAnimationImage.getHeight() * 2, Image.SCALE_DEFAULT));
      rocketEnterAnimation = new Animation(rocketEnterAnimationImage, 6, 500, true);

      var planetSignImage = ImageIO.read(new File("assets/planets/planet-sign.png"));
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

  public Level(Platform[] platforms, Vertex startPos, Vertex rocketPos, Vertex planetSignPos, Animation planetAnimation) {
    this.platforms = platforms;
    this.startPos = startPos;
    this.rocketPos = rocketPos;
    this.planetSignPos = planetSignPos;
    this.planetAnimation = planetAnimation;
    this.completeZone = new Platform((int) rocketPos.x + 30, (int) rocketPos.y, 50.0, 100.0);
  }

  public void checkIfInCompletionZone(Player player) {
    if (finishAnimation || finished || !player.touches(this.completeZone)) return;

    System.out.println("finish Animation");

    startFinishAnimation();
  }

  public void startFinishAnimation() {
    this.finishAnimation = true;
    animationState = AnimationState.STARTING;

    Timer timer = new Timer();
    TimerTask t1 = new TimerTask() {
      public void run() {
        animationState = AnimationState.FLYING;
      }
    };
    TimerTask t2 = new TimerTask() {
      public void run() {
        animationState = AnimationState.DONE;
      }
    };

    timer.schedule(t1, 1000);
    timer.schedule(t2, 3000);

  }

  public void paintRocket(Graphics g) {
    if (!finishAnimation && !finished)
      g.drawImage(rocketImage, (int) this.rocketPos.x, (int) this.rocketPos.y, rocketImage.getWidth() * 4, rocketImage.getHeight() * 4, null);
    else
      // FIXME: why do i have to offset the image if it is just two times the size of the og one??
      rocketEnterAnimation.paintTo(g,
              (int) (completeZone.pos().x - (rocketEnterAnimation.frameWidth() * 2.0 - completeZone.width()) / 2.0) + 1,
              (int) (completeZone.pos().y - (rocketEnterAnimation.frameHeight() * 2.0 - completeZone.height()) / 2.0) + 2
      );
  }

  public void paintPlanetSign(Graphics g) {
    paintPlanetSign(g, planetSignPos, planetAnimation);
  }

  public static void paintPlanetSign(Graphics g, Vertex planetSignPos, Animation planetAnimation) {
    g.drawImage(planetSign, (int) planetSignPos.x, (int) planetSignPos.y, null);
    planetAnimation.paintTo(g, (int) (planetSignPos.x + ((double) planetSign.getWidth() / 2) - ((double) planetAnimation.frameWidth())), (int) planetSignPos.y + 45);
  }

  abstract public void additionalPaint(Graphics g);
}
