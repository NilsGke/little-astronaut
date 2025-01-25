package com.nilsgke.littleAstronaut.menu;

public record Clickable(int x, int y, int width, int height, Runnable onClickAction)  {
  public void checkForClick(int clickX, int clickY) {
    if(x < clickX && x + width > clickX && y < clickY && y + height > clickY) onClickAction.run();
  }
}
