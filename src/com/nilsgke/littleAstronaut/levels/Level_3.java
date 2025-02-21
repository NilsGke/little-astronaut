package com.nilsgke.littleAstronaut.levels;

import com.nilsgke.littleAstronaut.Player;
import com.nilsgke.littleAstronaut.map.MovingPlatform;
import com.nilsgke.littleAstronaut.map.Platform;
import com.nilsgke.littleAstronaut.sprites.Animation;
import com.nilsgke.littleAstronaut.sprites.ImageHelper;
import com.nilsgke.littleAstronaut.sprites.Tilesets;
import name.panitz.game2d.Vertex;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

public class Level_3 extends Level {
  public static final Animation planetAnimation;
  private MovingPlatform[] movingPlatforms;
  private static final BufferedImage backdrop;

  static {
    try {
      planetAnimation = new Animation(
              ImageHelper.readImageFileAt("/assets/planets/ice.png"),
              100, 20000, true);

      BufferedImage bd = ImageHelper.readImageFileAt("/assets/backdrops/level_3.png");
      backdrop = ImageHelper.toBufferedImage(bd.getScaledInstance(bd.getWidth() *2, bd.getHeight() *2, Image.SCALE_DEFAULT));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }



  public Level_3() {
    super(
            new Platform[]{
                    Platform.createHorizontalTilePlatform(-700, 0, 30, Tilesets.iceFloor),
                    Platform.createHorizontalTilePlatform(250, -200, 2, Tilesets.stonePlatform),
                    Platform.createHorizontalTilePlatform(450, -400, 1, Tilesets.stonePlatform),
                    Platform.createHorizontalTilePlatform(230, -480, 1, Tilesets.stonePlatform),
                    Platform.createHorizontalTilePlatform(-550, -830, 1, Tilesets.stonePlatform),
                    Platform.createHorizontalTilePlatform(-1200, -930, 6, Tilesets.stonePlatform),
                    MovingPlatform.createHorizontalMovingTilePlatform(-250, -480, 1, 350, 0, 8000, Tilesets.stonePlatform),
                    MovingPlatform.createHorizontalMovingTilePlatform(-400, -480, 1, 0, -350, 8000, Tilesets.stonePlatform),
            },
            new Vertex(0, -30),
            new Vertex(-1100, -1090),
            new Vertex(-300, -200),
            planetAnimation,
            backdrop
    );

    this.movingPlatforms = Arrays
            .stream(platforms)
            .filter((platform) -> platform instanceof MovingPlatform)
            .toArray(MovingPlatform[]::new);

  }

  @Override
  public int minCamY() {
    return -300;
  }

  public Color backgroundColor() {
    return new Color(185, 233, 255);
  }

  @Override
  public void additionalPaint(Graphics2D g) {
  }

  @Override
  public void additionalChecks(long deltaTime, Player player) {
    for (var movingPlatform : movingPlatforms)
      movingPlatform.update(player);

  }
}
