package com.nilsgke.littleAstronaut.map;

import com.nilsgke.littleAstronaut.Player;
import com.nilsgke.littleAstronaut.sprites.ImageTileset;
import name.panitz.game2d.GameObj;
import name.panitz.game2d.Vertex;

import java.awt.*;
import java.util.Objects;

public class Platform implements GameObj {
  private final Vertex pos;
  private final Vertex velocity;
  private double height;
  private double width;
  private ImageTileset tileset;

  private Platform(Vertex pos, Vertex velocity, double height, double width) {
    this.pos = pos;
    this.velocity = velocity;
    this.height = height;
    this.width = width;
  }

  // platform with tile image
  private Platform(Vertex pos, Vertex velocity, double width, double height, ImageTileset tileset) {
    this.pos = pos;
    this.velocity = velocity;
    this.height = height;
    this.width = width;
    this.tileset = tileset;
  }

  // platform with tile image
  private Platform(int x, int y, double width, double height, ImageTileset tileset) {
    this(new Vertex(x, y), new Vertex(0, 0), width, height, tileset);
  }

  public Platform(int x, int y, double width, double height) {
    this(new Vertex(x, y), new Vertex(0, 0), height, width);
  }



  public static Platform createHorizontalTilePlatform(int x, int y, int tileRepeat, ImageTileset tileset) {
    return new Platform(x, y, tileset.leftEnd.getWidth() + tileset.rightEnd.getWidth() + tileset.tile.getWidth() * tileRepeat, tileset.tile.getHeight(), tileset);
  }

  public void paintTo(Graphics g) {
    if (tileset == null) {
      g.setColor(Color.GREEN);
      g.fillRect((int) pos.x, (int) pos.y, (int) width, (int) height);
      return;
    }

    int tileWidth = this.tileset.tile.getWidth();

    int imgRepeatCount = (int) ((this.width - (this.tileset.leftEnd.getWidth() + this.tileset.rightEnd.getWidth())) / tileWidth);

    int leftOffset = tileset.leftEnd.getWidth();

    // left end
    g.drawImage(tileset.leftEnd, (int) this.pos().x, (int) this.pos().y, tileset.leftEnd.getWidth() * 2, tileset.leftEnd.getHeight() * 2, null);
    // main tile
    for (int i = 0; i < imgRepeatCount; i++)
      g.drawImage(tileset.tile, (int) (this.pos().x + leftOffset + i * tileWidth), (int) this.pos().y, tileset.tile.getWidth() * 2, tileset.tile.getHeight() * 2, null);
    // right end
    g.drawImage(tileset.rightEnd, (int) pos.x + leftOffset + tileWidth * (imgRepeatCount + 1), (int) pos.y, tileset.rightEnd.getWidth() * 2, tileset.rightEnd.getHeight() * 2, null);

  }

  public void move(double x, double y) {
    this.pos.moveTo(new Vertex(pos.x + x, pos.y + y));
  }

  public String toCode() {
    return "new Platform(" + (int) pos.x + ", " + (int) pos.y + ", " + (int) width + ", " + (int) height + ")";
  }

  public void applyCollision(Player player) {
    if (!this.touches(player)) return;

    // player is touching a platform

    var overlap = this.getOverlap(player);

    if (overlap.min() == overlap.top()) { // touching top (on ground)
      player.velocity().y = 0;
      player.pos().y = this.pos().y - player.height();
    } else if (overlap.min() == overlap.bottom()) { // touching bottom
      player.pos().y = this.pos().y + this.height();
      player.velocity().y = Math.abs(player.velocity().y);
    } else if (overlap.min() == overlap.left()) { // touching left
      player.pos().x = this.pos().x - player.width();
      if (player.velocity().y != 0)
        player.velocity().x = Math.abs(player.velocity().x) * -1; // if not on ground, bounce on wall
    } else if (overlap.min() == overlap.right()) { // touching right
      player.pos().x = this.pos().x + this.width();
      if (player.velocity().y != 0) player.velocity().x = Math.abs(player.velocity().x);
    }
  }

  public boolean stoodOnBy(Player player) {
    if (!this.touches(player)) return false;
    var overlap = getOverlap(player);
    return overlap.min() == overlap.top();
  }

  public Overlap getOverlap(Player player) {
    double overlapTop = player.pos().y + player.height() - this.pos().y;
    double overlapBottom = this.pos().y + this.height() - player.pos().y;
    double overlapLeft = player.pos().x + player.width() - this.pos().x;
    double overlapRight = this.pos().x + this.width() - player.pos().x;
    double minOverlap = Math.min(Math.min(overlapTop, overlapBottom), Math.min(overlapLeft, overlapRight));
    return new Overlap(overlapTop, overlapBottom, overlapLeft, overlapRight, minOverlap);
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
  public double height() {
    return height;
  }

  @Override
  public double width() {
    return width;
  }

  public void setHeight(double height) {
    this.height = height;
  }

  public void setWidth(double width) {
    this.width = width;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    var that = (Platform) obj;
    return Objects.equals(this.pos, that.pos) && Objects.equals(this.velocity, that.velocity) && Double.doubleToLongBits(this.height) == Double.doubleToLongBits(that.height) && Double.doubleToLongBits(this.width) == Double.doubleToLongBits(that.width);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pos, velocity, height, width);
  }

  @Override
  public String toString() {
    return "Platform[" + "pos=" + pos + ", " + "velocity=" + velocity + ", " + "height=" + height + ", " + "width=" + width + ']';
  }

  public record Overlap(double top, double bottom, double left, double right, double min) {
  }
}