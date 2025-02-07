package com.nilsgke.littleAstronaut.levels;

import com.nilsgke.littleAstronaut.Player;
import com.nilsgke.littleAstronaut.map.MovingPlatform;
import com.nilsgke.littleAstronaut.map.Platform;
import com.nilsgke.littleAstronaut.sprites.Animation;
import com.nilsgke.littleAstronaut.sprites.Tilesets;
import name.panitz.game2d.Vertex;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Level_3 extends Level {
  public static final Animation planetAnimation;
  private MovingPlatform[] movingPlatforms;

  static {
    try {
      planetAnimation = new Animation(
              ImageIO.read(new File("assets/planets/ice.png")),
              100, 20000, true);
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
            new Vertex(-1100, -1065),
            new Vertex(-300, -200),
            planetAnimation
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
  public void additionalPaint(Graphics g) {
  }

  @Override
  public void additionalChecks(long deltaTime, Player player) {
    for (var movingPlatform : movingPlatforms)
      movingPlatform.update(player);

  }
}
