package com.nilsgke.littleAstronaut.menu;

import java.awt.event.MouseEvent;

public class Clickable {
  int x;
  int y;
  int width;
  int height;
  Runnable onClickAction;
  boolean mouseIsDown;

  public Clickable(int x, int y, int width, int height, Runnable onClickAction) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.onClickAction = onClickAction;
  }

  public void mousePressed(MouseEvent e) {
    if(checkClickInBounds(e.getX(), e.getY()))
      mouseIsDown = true;
  }

  public void mouseReleased(MouseEvent e) {
    if(!mouseIsDown) return;
    mouseIsDown = false;
    if(checkClickInBounds(e.getX(), e.getY()))
      onClickAction.run();
  }

  public boolean checkClickInBounds(int clickX, int clickY) {
    return (x < clickX && x + width > clickX && y < clickY && y + height > clickY);
  }
}
