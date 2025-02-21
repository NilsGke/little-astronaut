package com.nilsgke.littleAstronaut.toasts;

import com.nilsgke.littleAstronaut.sprites.Text;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.Timer;

import static com.nilsgke.littleAstronaut.toasts.Toast.Type;

public class Toaster {
  static final ArrayList<Toast> toasts = new ArrayList<>();
  private static final int TEXT_HEIGHT = Text.getHeight(2);

  private static void addToast(String content, Toast.Type type, int duration) {
    var toast = new Toast(content, type, duration);
    toasts.add(toast);
    Timer timer = new Timer(duration, e -> removeToast(toast));
    timer.setRepeats(false);
    timer.start();
  }

  private static void removeToast(Toast toast) {
    toasts.remove(toast);
  }

  public static void info(String content, int duration) {
    addToast(content, Toast.Type.INFO, duration);
  }

  public static void tip(String content, int duration) {
    addToast(content, Toast.Type.TIP, duration);
  }

  public static void error(String content, int duration) {
    addToast(content, Toast.Type.ERROR, duration);
  }

  public static void updateToasts() {
    toasts.forEach(Toast::update);
  }

  public static void paintToastsTo(Graphics2D g, int screenWidth, int screenHeight) {
    int y = 0;
    g.setStroke(new BasicStroke(2));

    for (Toast toast : toasts) {
      int textX = screenWidth / 2 - toast.textWidth / 2;
      int textY =  40 - y - toast.yOffset;

      var color = getBackgroundColor(toast.type);
      color = modifyColorOpacity(color, toast.opacity);

      int BORDER_RADIUS = 20;

      g.setColor(modifyColorOpacity(Color.BLACK, toast.opacity));
      g.fillRoundRect(textX - 10, textY - 10, toast.textWidth + 10 * 2, TEXT_HEIGHT + 10 * 2, BORDER_RADIUS, BORDER_RADIUS);
      g.setColor(color);

      g.drawRoundRect(textX - 10, textY - 10, toast.textWidth + 10 * 2, TEXT_HEIGHT + 10 * 2, BORDER_RADIUS, BORDER_RADIUS);


      Text.paintTo(g, toast.content, textX, textY, 2, modifyColorOpacity(Color.WHITE, toast.opacity));

      g.setColor(modifyColorOpacity(Color.WHITE, toast.opacity));

      double runningSince = System.currentTimeMillis() - toast.createdAt;
      double percentage = (toast.duration - runningSince) / toast.duration;
      g.fillRoundRect(textX, textY + TEXT_HEIGHT + 3, (int) (toast.textWidth * percentage), 3, 3, 3);

      y -= 50;
    }
  }

  private static Color getBackgroundColor(Toast.Type type) {
    return switch (type) {
      case Type.INFO -> Color.GREEN;
      case Type.TIP -> Color.YELLOW;
      case Type.ERROR -> Color.RED;
    };
  }

  private static Color modifyColorOpacity(Color c, float opacity) {
    return new Color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, opacity);
  }

}
