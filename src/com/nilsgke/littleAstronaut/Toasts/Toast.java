package com.nilsgke.littleAstronaut.Toasts;

import com.nilsgke.littleAstronaut.sprites.Text;

import java.awt.*;



public class Toast {
  public enum Type {
    INFO,
    TIP,
    ERROR
  }

  final Type type;
  final long createdAt;
  final int duration;
  final String content;
  final int textWidth;

  public Toast(String content, Type type, int duration) {
    this.type = type;
    this.createdAt = System.currentTimeMillis();
    this.content = content;
    this.duration = duration;
    this.textWidth = Text.getCharWidth(2) * content.length();
  }

}
