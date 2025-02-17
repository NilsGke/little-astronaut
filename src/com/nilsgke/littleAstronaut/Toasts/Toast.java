package com.nilsgke.littleAstronaut.Toasts;

import com.nilsgke.littleAstronaut.sprites.Text;


public class Toast {
  public enum Type {
    INFO, TIP, ERROR
  }

  final int maxYOffset = 20;
  final int animationDuration = 200;

  /// takes in a value between 0 and 1 and should output a value between 0 and 1<br>
  /// [Easing function](https://www.desmos.com/calculator/frk1ukvwf0)
  final TimingFunction offsetTimingFunction = (t) -> (float) (.5 * (1 - Math.cos(t * Math.PI)));
  final TimingFunction opacityTimingFunction = (t) -> (float) (.5 * (1 - Math.cos(t * Math.PI)));

  final Type type;
  final long createdAt;
  final int duration;
  final String content;
  final int textWidth;
  int yOffset = 0;
  float opacity = 1;

  public Toast(String content, Type type, int duration) {
    this.type = type;
    this.createdAt = System.currentTimeMillis();
    this.content = content;
    this.duration = duration;
    this.textWidth = Text.getCharWidth(2) * content.length();
  }

  public void update() {
    long timeAlive = System.currentTimeMillis() - createdAt;
    long deathIn = this.duration - timeAlive;

    long easeInTime = Math.min(timeAlive, animationDuration);
    long easeOutTime = Math.min(deathIn, animationDuration);

    long t = deathIn <= timeAlive ? easeOutTime : easeInTime;

    this.yOffset = maxYOffset - (int) (maxYOffset * offsetTimingFunction.eval((float) t / animationDuration));// math.max with ensures we dont divide by zero
    this.opacity = (opacityTimingFunction.eval(t / (float) animationDuration));
  }


  interface TimingFunction {
    float eval(float x);
  }
}
