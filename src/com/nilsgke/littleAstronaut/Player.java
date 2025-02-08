package com.nilsgke.littleAstronaut;

import com.nilsgke.littleAstronaut.sprites.Animation;
import com.nilsgke.littleAstronaut.sprites.ImageHelper;
import name.panitz.game2d.GameObj;
import name.panitz.game2d.Vertex;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;



public class Player implements GameObj {

  enum XDirection { LEFT, RIGHT }
  public byte id;

  Vertex pos;
  Vertex velocity;
  double width;
  double height;
  XDirection lastDirection;

  Animation animation_walkRight;
  Animation animation_walkLeft;
  Animation animation_jump;
  Animation animation_idle;

  public Player(byte id, Vertex pos, Vertex velocity) {
    this.id = id;
    this.pos = pos;
    this.velocity = velocity;
    this.width = 70;
    this.height = 70;

    try {
      BufferedImage spriteRunRight = ImageIO.read(new File("assets/player/astronaut-run.png"));
      spriteRunRight = ImageHelper.toBufferedImage(spriteRunRight.getScaledInstance(spriteRunRight.getWidth() * 2, spriteRunRight.getHeight() * 2, Image.SCALE_DEFAULT));
      this.animation_walkRight = new Animation(spriteRunRight, 6, 600, true);

      BufferedImage spriteRunLeft = ImageIO.read(new File("assets/player/astronaut-run.png"));
      spriteRunLeft = ImageHelper.toBufferedImage(spriteRunLeft.getScaledInstance(spriteRunLeft.getWidth() * 2, spriteRunLeft.getHeight() * 2, Image.SCALE_DEFAULT));
      this.animation_walkLeft = new Animation(spriteRunLeft, 6, 600, true);

      BufferedImage spriteJump = ImageIO.read(new File("assets/player/astronaut-jump.png"));
      spriteJump = ImageHelper.toBufferedImage(spriteJump.getScaledInstance(spriteJump.getWidth() * 2, spriteJump.getHeight() * 2, Image.SCALE_DEFAULT));
      this.animation_jump = new Animation(spriteJump, 5, 600, false);

      BufferedImage spriteIdle = ImageIO.read(new File("assets/player/astronaut-idle.png"));
      spriteIdle = ImageHelper.toBufferedImage(spriteIdle.getScaledInstance(spriteIdle.getWidth() * 2, spriteIdle.getHeight() * 2, Image.SCALE_DEFAULT));
      this.animation_idle = new Animation(spriteIdle, 6, 2000, true);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  @Override
  public void paintTo(Graphics g) {
    if (animation_walkRight == null) {
      g.drawPolygon(new int[]{(int) (this.pos.x + this.width / 2), (int) (this.pos.x), (int) (this.pos.x + this.width)}, new int[]{(int) this.pos.y, (int) (this.pos.y + this.height), (int) (this.pos.y + this.height)}, 3);
      return;
    }

    if (velocity.y != 0) { // jumping
      boolean goingLeft = this.velocity.x < 0 || lastDirection == XDirection.LEFT ;
      if (velocity.y > .5) animation_jump.paintFrameTo(1, g, (int) this.pos().x, (int) this.pos().y - 10, goingLeft); // going up
      else if (velocity.y < -0.5) animation_jump.paintFrameTo(3, g, (int) this.pos().x, (int) this.pos().y - 10, goingLeft); // falling down
      else animation_jump.paintFrameTo(2, g, (int) this.pos().x, (int) this.pos().y - 10, goingLeft); // at peak
    } else if (velocity.x > 0.5) animation_walkRight.paintTo(g, (int) this.pos().x, (int) this.pos().y - 10); // walking right
    else if (velocity.x < -0.5) animation_walkLeft.paintTo(g, (int) this.pos().x, (int) this.pos().y - 10, true); // walking left
    else animation_idle.paintTo(g, (int) this.pos().x, (int) this.pos().y - 10, lastDirection == XDirection.LEFT); // idle
  }



  @Override
  public Vertex pos() {
    return pos;
  }

  @Override
  public Vertex velocity() {
    return velocity;
  }

  @Override
  public double width() {
    return width;
  }

  @Override
  public double height() {
    return height;
  }
}

