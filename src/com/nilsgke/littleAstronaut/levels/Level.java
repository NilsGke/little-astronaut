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
  public enum AnimationState {NOT_STARTED, STARTING, FLYING, AWAY}

  public Vertex startPos;
  public Platform[] platforms;
  public Platform completeZone;
  public Vertex rocketPos;
  public Animation rocketEnterAnimation;
  public Boolean finishAnimation = false;
  public Boolean finished = false;


  static BufferedImage rocketImage;

  static {
    try {
      rocketImage = ImageIO.read(new File("assets/rocket/rocket.png"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  abstract public int minCamY();


  public Level(Platform[] platforms, Vertex startPos, Vertex rocketPos) {
    this.platforms = platforms;
    this.startPos = startPos;
    this.rocketPos = rocketPos;
    this.completeZone = new Platform((int) rocketPos.x + 30, (int) rocketPos.y, 50.0, 100.0);
    try {
      BufferedImage rocketEnterAnimation = ImageIO.read(new File("assets/rocket/rocket-enter-animation.png"));
      rocketEnterAnimation = ImageHelper.toBufferedImage(rocketEnterAnimation.getScaledInstance(rocketEnterAnimation.getWidth() * 2, rocketEnterAnimation.getHeight() * 2, Image.SCALE_DEFAULT));
      this.rocketEnterAnimation = new Animation(rocketEnterAnimation, 6, 500, true);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void checkIfInCompletionZone(Player player) {
    if (finishAnimation || finished || !player.touches(this.completeZone)) return;
    this.finishAnimation = true;

    System.out.println("finish Animation");

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
      public void run() {
        finished = true;
        finishAnimation = false;
      }
    };

    timer.schedule(task, 5000);
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

  abstract public void additionalPaint(Graphics g);
}
