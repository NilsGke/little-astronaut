package com.nilsgke.littleAstronaut.levels;

import com.nilsgke.littleAstronaut.Player;
import com.nilsgke.littleAstronaut.map.Platform;
import com.nilsgke.littleAstronaut.minigames.Minigame;
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
  public enum Status {NOT_STARTED, STARTING, STARTED}

  public Vertex startPos;
  public Platform[] platforms;
  public Minigame minigame;
  public Platform completeZone;
  public Vertex rocketPos;
  public Status minigameStatus = Status.NOT_STARTED;
  public Animation minigameAnimation;

  static BufferedImage rocketImage;

  static {
    try {
      rocketImage = ImageIO.read(new File("assets/rocket/rocket.png"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  abstract public int minCamY();


  public Level(Platform[] platforms, Minigame minigame, Vertex startPos, Vertex rocketPos) {
    this.platforms = platforms;
    this.minigame = minigame;
    this.startPos = startPos;
    this.rocketPos = rocketPos;
    this.completeZone = new Platform((int) rocketPos.x + 30, (int) rocketPos.y, 50.0, 100.0);
    try {
      BufferedImage rocketEnterAnimation = ImageIO.read(new File("assets/rocket/rocket-enter-animation.png"));
      rocketEnterAnimation = ImageHelper.toBufferedImage(rocketEnterAnimation.getScaledInstance(rocketEnterAnimation.getWidth() * 2, rocketEnterAnimation.getHeight() * 2, Image.SCALE_DEFAULT));
      minigameAnimation = new Animation(rocketEnterAnimation, 6, 500, true);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean checkIfInCompletionZone(Player player) {
    if (this.minigameStatus != Status.NOT_STARTED || !player.touches(this.completeZone)) return false;

    System.out.println("start minigame");
    this.startMinigame();
    return true;
  }

  public void startMinigame() {
    this.minigameStatus = Status.STARTING;
    this.minigameAnimation.restart();

    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        minigameStatus = Status.STARTED;
        minigame.play();
      }
    }, 3000);
  }

  public void paintRocket(Graphics g) {
    if (this.minigameStatus != Status.STARTING)
      g.drawImage(rocketImage, (int) this.rocketPos.x, (int) this.rocketPos.y, rocketImage.getWidth() * 4, rocketImage.getHeight() * 4, null);
    else
      // FIXME: why do i have to offset the image if it is just two times the size of the og one??
      minigameAnimation.paintTo(g,
              (int) (completeZone.pos().x - (minigameAnimation.frameWidth() * 2.0 - completeZone.width()) / 2.0) + 1,
              (int) (completeZone.pos().y - (minigameAnimation.frameHeight() * 2.0 - completeZone.height()) / 2.0) + 2
      );
  }

  abstract public void additionalPaint(Graphics g);
}
