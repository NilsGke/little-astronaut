package com.nilsgke.littleAstronaut.map;

import name.panitz.game2d.GameObj;
import name.panitz.game2d.Vertex;

import java.awt.*;
import java.util.Objects;

public final class Platform implements GameObj {
  private final Vertex pos;
  private final Vertex velocity;
  private double height;
  private double width;

  public Platform(
          Vertex pos,
          Vertex velocity,
          double height,
          double width
  ) {
    this.pos = pos;
    this.velocity = velocity;
    this.height = height;
    this.width = width;
  }

  public Platform(int x, int y, double width, double height) {
    this(new Vertex(x,y), new Vertex(0,0), height, width);
  }

  public void paintTo(Graphics g) {
    g.setColor(Color.GREEN);
    g.fillRect((int) pos.x, (int) pos.y, (int) width, (int) height);
  }

  public void move(double x, double y) {
    this.pos.moveTo(new Vertex(pos.x + x, pos.y + y));
  }

  public String toCode() {
    return "new Platform(" +
            (int) pos.x + ", " +
            (int) pos.y + ", " +
            (int) width + ", " +
            (int) height + ")";
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
    return Objects.equals(this.pos, that.pos) &&
            Objects.equals(this.velocity, that.velocity) &&
            Double.doubleToLongBits(this.height) == Double.doubleToLongBits(that.height) &&
            Double.doubleToLongBits(this.width) == Double.doubleToLongBits(that.width);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pos, velocity, height, width);
  }

  @Override
  public String toString() {
    return "Platform[" +
            "pos=" + pos + ", " +
            "velocity=" + velocity + ", " +
            "height=" + height + ", " +
            "width=" + width + ']';
  }


}