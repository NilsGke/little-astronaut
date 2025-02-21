package com.nilsgke.littleAstronaut.levels;

import com.nilsgke.littleAstronaut.Player;
import com.nilsgke.littleAstronaut.map.Platform;
import com.nilsgke.littleAstronaut.map.Wall;
import com.nilsgke.littleAstronaut.sprites.Animation;
import com.nilsgke.littleAstronaut.sprites.ImageHelper;
import com.nilsgke.littleAstronaut.sprites.Tilesets;
import name.panitz.game2d.Vertex;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Level_4 extends Level {
  public static final Animation planetAnimation;
  private static final BufferedImage backdrop;

  static {
    try {
      planetAnimation = new Animation(
              ImageHelper.readImageFileAt("/assets/planets/nether.png"),
              100, 20000, true);

      BufferedImage bd = ImageHelper.readImageFileAt("/assets/backdrops/level_4.png");
      backdrop = ImageHelper.toBufferedImage(bd.getScaledInstance(bd.getWidth() *2, bd.getHeight() *2, Image.SCALE_DEFAULT));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  public Level_4() {
    super(
            new Platform[]{
                    Platform.createHorizontalTilePlatform(-700, 0, 30, Tilesets.netherFloor),
                    Platform.createHorizontalTilePlatform(250, -170, 2, Tilesets.darkRedPlatform),
                    Platform.createHorizontalTilePlatform(150, -420, 1, Tilesets.darkRedPlatform),
                    Platform.createHorizontalTilePlatform(350, -460, 2, Tilesets.darkRedPlatform),
                    Platform.createHorizontalTilePlatform(220, -600, 3, Tilesets.darkRedPlatform),
                    Wall.createVerticalTileWall(600, -700, 3, Tilesets.darkRedPlatform),
                    Platform.createHorizontalTilePlatform(0, -800, 1, Tilesets.darkRedPlatform),
                    Platform.createHorizontalTilePlatform(220, -1000, 5, Tilesets.darkRedPlatform),
            },
            new Vertex(0, -30),
            new Vertex(400, -1160),
            new Vertex(-300, -200),
            planetAnimation,
            backdrop
    );
  }

  @Override
  public int minCamY() {
    return -300;
  }

  public Color backgroundColor() {
    return new Color(131, 34, 26);
  }

  @Override
  public void additionalPaint(Graphics2D g) {
    g.setColor(new Color(131, 34, 26, 80));
    g.fillRect(-300, -190, 200, 190);
  }

  @Override
  public void additionalChecks(long deltaTime, Player p) {
  }
}
