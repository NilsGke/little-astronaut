package com.nilsgke.littleAstronaut.sprites;

import name.panitz.game2d.Vertex;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/// will take an image, split it evenly by `frameCount` and then render it based on time
public class Animation {
  BufferedImage sprite;
  int frameCount;
  long startedAt;
  int frameWidth;
  int frameHeight;
  BufferedImage[] frames;
  int durationMs;
  boolean repeat;


  public Animation(BufferedImage sprite, int frameCount, int durationMs, boolean repeat) {
    this.sprite = sprite;
    this.frameCount = frameCount;
    this.durationMs = durationMs;
    this.repeat = repeat;

    frames = new BufferedImage[frameCount];
    this.frameWidth = sprite.getWidth() / frameCount;
    this.frameHeight = sprite.getHeight();
    for (int i = 0; i < frameCount; i++) {
      System.out.println(frameWidth * (i + 1));
      frames[i] = sprite.getSubimage(frameWidth * i, 0, frameWidth, frameHeight);
    }

    this.startedAt = System.currentTimeMillis();
  }

  public void paintTo(Graphics g, int x, int y, boolean mirrored) {
    long timeSinceStart = System.currentTimeMillis() - startedAt;
    long timeSinceLastRestart = timeSinceStart % durationMs;
    int currentFrameIndex = !repeat && timeSinceLastRestart != timeSinceStart ? // dont repeat if finished once
            frameCount - 1 :
            (int) (timeSinceLastRestart / (durationMs / frameCount) % frameCount);

    paintFrameTo(currentFrameIndex, g, x, y, mirrored);
  }

  public void paintTo(Graphics g, int x, int y) {
    paintTo(g, x, y, false);
  }

  public void paintFrameTo(int frame, Graphics g, int x, int y, boolean mirrored) {
    Graphics2D g2d = (Graphics2D) g;

    if (mirrored) {
      AffineTransform originalTransform = g2d.getTransform();

      // flip image horizontally
      g2d.translate(x + frameWidth * 2, y);
      g2d.scale(-1, 1);

      g2d.drawImage(frames[frame], 0, 0, frameWidth * 2, frameHeight * 2, null);

      // restore the original transform
      g2d.setTransform(originalTransform);
    } else {
      // draw normally
      g2d.drawImage(frames[frame], x, y, frameWidth * 2, frameHeight * 2, null);
    }
  }

  public void paintFrameTo(int frame, Graphics g, int x, int y) {
    paintFrameTo(frame, g, x, y, false);
  }

  public void restart() {
    this.startedAt = System.currentTimeMillis();
  }

  public void paintTo(Graphics g, Vertex pos) {
    this.paintTo(g, (int) pos.x, (int) pos.y);
  }

  public int frameWidth() {
    return frameWidth;
  }

  public int frameHeight() {
    return frameHeight;
  }

}
