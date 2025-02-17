package com.nilsgke.littleAstronaut.menu;

import com.nilsgke.littleAstronaut.sprites.Text;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class InputBox {
  private final int x;
  private final int y;
  private final int width;
  private final int height;
  private final String placeholder;
  private final int textScale;
  private final int maxCharacters;
  private final String allowedCharacters;

  private boolean focused = false;
  private String content = "";

  private static final int padding = 4;

  public InputBox(int x, int y, int width, int textScale, String placeholder, String allowedCharacters) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = Text.getHeight(textScale);
    this.textScale = textScale;
    this.placeholder = placeholder;
    this.maxCharacters = width / Text.getCharWidth(textScale);
    this.allowedCharacters = allowedCharacters;
  }

  public void paintTo(Graphics2D g) {
    g.setColor(focused ? Color.WHITE : Color.GRAY);
    g.setStroke(new BasicStroke(2));
    g.drawRect(x, y, width + padding * 2, height + padding * 2);

    if (content.isEmpty()) Text.paintTo(g, placeholder, x + padding, y + padding, textScale, Color.GRAY);
    else
      Text.paintTo(g, content.substring(Math.max(0, content.length() - maxCharacters)), x + padding, y + padding, textScale);
  }

  public void mousePressed(MouseEvent mouseEvent) {
    int mouseX = mouseEvent.getX(), mouseY = mouseEvent.getY();
    this.focused = mouseX > this.x && mouseX < this.x + this.width + padding * 2 && mouseY > this.y && mouseY < this.y + this.height + padding * 2;

  }

  public void keyTyped(KeyEvent keyEvent) {
    if (!this.focused) return;
    int keyCode = keyEvent.getKeyCode();
    switch (keyCode) {
      case KeyEvent.VK_ESCAPE -> this.blur();
      case KeyEvent.VK_BACK_SPACE -> this.content = this.content.substring(0, Math.max(0, this.content.length() - 1));
    }

    char character = keyEvent.getKeyChar();
    if (allowedCharacters.contains(character + "")) this.content += character;
  }

  public String getContent() {
    return this.content;
  }

  public boolean isFocused() {
    return this.focused;
  }

  public void focus() {
    this.focused = true;
  }

  public void blur() {
    this.focused = false;
  }
}
