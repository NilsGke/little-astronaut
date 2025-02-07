package com.nilsgke.littleAstronaut.map;

import com.nilsgke.littleAstronaut.Player;
import com.nilsgke.littleAstronaut.sprites.ImageTileset;
import name.panitz.game2d.Vertex;

public class MovingPlatform extends Platform {
  final int initialX;
  final int initialY;
  int moveX;
  int moveY;
  final int moveTime;

  protected MovingPlatform(int x, int y, double width, double height, int moveX, int moveY, int moveTime, ImageTileset tileset) {
    super(new Vertex(x, y), new Vertex(0, 0), width, height, tileset);

    this.initialX = x;
    this.initialY = y;
    this.moveX = moveX;
    this.moveY = moveY;
    this.moveTime = moveTime;
  }

  public static MovingPlatform createHorizontalMovingTilePlatform(int x, int y, int tileRepeat, int moveX, int moveY, int moveTime, ImageTileset tileset) {
    return new MovingPlatform(
            x, y,
            tileset.leftEnd.getWidth() * 2 + tileset.rightEnd.getWidth() * 2 + tileset.tile.getWidth() * 2 * tileRepeat,
            tileset.tile.getHeight() * 2,
            moveX, moveY, moveTime,
            tileset
    );
  }

  public void update(Player player) {
    // https://www.desmos.com/calculator/v8qlljmofo
    long time = System.currentTimeMillis();

    double xOffset = ((moveX / 2.0) * (1 + Math.cos(time * ((2 * Math.PI) / ((double) moveTime)))));
    double deltaX = ((this.initialX + xOffset) - this.pos.x);

    var yOffset = ((moveY / 2.0) * (1 + Math.cos(time * ((2 * Math.PI) / ((double) moveTime)))));
    var deltaY = ((this.initialY + yOffset) - this.pos.y);

    if (this.stoodOnBy(player)) player.pos().moveTo(new Vertex(player.pos().x + deltaX, player.pos().y + deltaY));

    this.pos.moveTo(new Vertex(this.initialX + xOffset, this.initialY + yOffset));
  }
}
