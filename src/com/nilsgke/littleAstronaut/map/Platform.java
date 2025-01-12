package com.nilsgke.littleAstronaut.map;

import com.nilsgke.littleAstronaut.sprites.ImageTileset;
import name.panitz.game2d.GameObj;
import name.panitz.game2d.Vertex;

import java.awt.*;
import java.util.Objects;

public final class Platform implements GameObj {
  private final Vertex pos;
  private final Vertex velocity;
  private double height;
  private double width;
  private ImageTileset tileset;

  public Platform(Vertex pos, Vertex velocity, double height, double width) {
    this.pos = pos;
    this.velocity = velocity;
    this.height = height;
    this.width = width;
  }

  // platform with tile image
  public Platform(Vertex pos, Vertex velocity, double width, double height, ImageTileset tileset) {
    this.pos = pos;
    this.velocity = velocity;
    this.height = height;
    this.width = width;
    this.tileset = tileset;
  }

  // platform with tile image
  public Platform(int x, int y, double width, double height, ImageTileset tileset) {
    this(new Vertex(x, y), new Vertex(0, 0), width, height, tileset);
  }

  public Platform(int x, int y, double width, double height) {
    this(new Vertex(x, y), new Vertex(0, 0), height, width);
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


}