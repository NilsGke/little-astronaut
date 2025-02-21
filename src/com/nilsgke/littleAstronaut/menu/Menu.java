package com.nilsgke.littleAstronaut.menu;

import com.nilsgke.littleAstronaut.connection.WSClient;
import com.nilsgke.littleAstronaut.connection.WSServer;
import com.nilsgke.littleAstronaut.sprites.ImageHelper;
import com.nilsgke.littleAstronaut.sprites.Text;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.IOException;
import java.util.Arrays;

public class Menu {
  private boolean isOpen = false;
  public Clickable[] clickables;
  private InputBox ipInput;

  private static final BufferedImage startIcon;
  private static final BufferedImage terminateIcon;

  private final WSServer wsServer;
  private final WSClient wsClient;

  static {
    try {
      terminateIcon = ImageHelper.readImageFileAt("/assets/icons/Close.png");
      startIcon = ImageHelper.readImageFileAt("/assets/icons/Play.png");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Menu(int width, int height, WSClient client, WSServer server) {
    this.wsServer = server;
    this.wsClient = client;


    clickables = new Clickable[]{
            // close button
            new Clickable(width - 50 - 30, 30, 50, 50, () -> close()),

            // server start button
            new Clickable(350, 587, startIcon.getWidth() * 2, startIcon.getHeight() * 2, () -> {
              if (wsClient.getStatus() != WSClient.Status.IDLE && wsClient.getStatus() != WSClient.Status.ERROR)
                return; // no action if client is connected to server

              switch (wsServer.getStatus()) {
                case STOPPED -> wsServer.start();
                case STARTING -> {
                }
                case RUNNING -> wsServer.stop();
              }
            }),

            // client connect button
            new Clickable(860, 627, startIcon.getWidth() * 2, startIcon.getHeight() * 2, () -> {
              if (wsServer.getStatus() == WSServer.Status.RUNNING) return; // no action if user is hosting the server

              if (wsClient.getStatus() == WSClient.Status.CONNECTED) {
                wsClient.disconnect();
                return;
              }

              try {
                var strings = ipInput.getContent().split(":", 2);
                String host = strings[0];
                String port = strings[1];

                if (host == null) throw new Exception("could not get hostname from your input");

                wsClient.connect(host, port);
                wsClient.listenForMessages();

              } catch (Exception e) {
                wsClient.setStatusToError();
                System.err.println(e.getMessage());
                System.err.println(Arrays.toString(e.getStackTrace()));
              }


            })};

    this.ipInput = new InputBox(520, 635, 320, 2, "161.420.787.069:1234", "ABCDEF0123456789.:");
  }


  public void mousePressed(MouseEvent mouseEvent) {
    for (var clickable : clickables) clickable.mousePressed(mouseEvent);
    this.ipInput.mousePressed(mouseEvent);
  }

  public void mouseReleased(MouseEvent mouseEvent) {
    for (var clickable : clickables) clickable.mouseReleased(mouseEvent);
  }

  public void keyTyped(KeyEvent keyEvent) {
    if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE && !this.ipInput.isFocused()) this.toggle();
    this.ipInput.keyTyped(keyEvent);
    if (this.ipInput.isFocused() && this.wsClient.getStatus() == WSClient.Status.ERROR) this.wsClient.disconnect();
  }

  public void paintTo(Graphics2D g, int width, int height) {
    if (!isOpen) return;
    g.setColor(new Color(0, 0, 0, 150));
    g.fillRect(0, 0, width, height);

    // close button
    g.setColor(new Color(0, 0, 0, 50));
    g.fillRect(width - 50 - 30, 30, 50, 50);
    g.setColor(Color.BLACK);
    g.setStroke(new BasicStroke(5));
    g.drawRoundRect(width - 50 - 30, 30, 50, 50, 5, 5);
    Text.paintTo(g, "x", width - 50 - 30 + 13, 30 + 10, 3);

    Text.paintTo(g, "Drücke ESC um weiterzuspielen", 270, 200, 2);

    g.setColor(Color.WHITE);
    g.drawLine(30, 520, width - 30, 520);
    Text.paintTo(g, "Local Multiplayer", 40, 550, 3);
    Text.paintTo(g, "Starte einen Server", 40, 600, 2);
    Text.paintTo(g, "verbinde dich mit einem Server", 40, 640, 2);

    // server start button
    if (wsServer.getStatus() == WSServer.Status.STOPPED)
      g.drawImage(startIcon, 350, 587, startIcon.getWidth() * 2, startIcon.getHeight() * 2, null);
    else {
      g.drawImage(terminateIcon, 350, 587, startIcon.getWidth() * 2, startIcon.getHeight() * 2, null);
      Text.paintTo(g, String.format("Running at: %s", wsServer.getURI()), 400, 600, 2, new Color(56, 209, 56));
    }

    // client connect button
    if (wsClient.getStatus() == WSClient.Status.IDLE)
      g.drawImage(startIcon, 860, 627, startIcon.getWidth() * 2, startIcon.getHeight() * 2, null);
    else if (wsClient.getStatus() == WSClient.Status.CONNECTED)
      g.drawImage(terminateIcon, 860, 627, startIcon.getWidth() * 2, startIcon.getHeight() * 2, null);
    else Text.paintTo(g, "ERROR!", 860, 640, 2, Color.RED);

    // ip input box
    ipInput.paintTo(g);
  }

  /// generated by GPT 4o mini
  public static BufferedImage applyBlur(BufferedImage image, float blurAmount, float scale) {
    // Ensure blurAmount is at least 1 to avoid division by zero
    blurAmount = Math.max(1, blurAmount);

    // Create a scaled version of the original image
    int scaledWidth = (int) (image.getWidth() * scale);
    int scaledHeight = (int) (image.getHeight() * scale);
    BufferedImage scaledImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = scaledImage.createGraphics();
    g2d.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);
    g2d.dispose();

    // Create a kernel size based on the blur amount
    int kernelSize = (int) (blurAmount * 2) + 1; // Make it odd
    float[] matrix = new float[kernelSize * kernelSize];
    float weight = 0;

    // Fill the kernel with values
    for (int y = 0; y < kernelSize; y++) {
      for (int x = 0; x < kernelSize; x++) {
        // Calculate the weight for the Gaussian distribution
        float value = (float) Math.exp(-((x - blurAmount) * (x - blurAmount) + (y - blurAmount) * (y - blurAmount)) / (2 * blurAmount * blurAmount));
        matrix[y * kernelSize + x] = value;
        weight += value;
      }
    }

    // Normalize the kernel
    for (int i = 0; i < matrix.length; i++) {
      matrix[i] /= weight;
    }

    Kernel kernel = new Kernel(kernelSize, kernelSize, matrix);
    ConvolveOp op = new ConvolveOp(kernel);
    BufferedImage blurredImage = op.filter(scaledImage, null); // Apply blur to the scaled image

    // Create a new image of the original size
    BufferedImage finalImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D finalG2d = finalImage.createGraphics();
    // Draw the blurred image onto the final image, scaling it back to original size
    finalG2d.drawImage(blurredImage, 0, 0, image.getWidth(), image.getHeight(), null);
    finalG2d.dispose();

    return finalImage; // Return the final image with original dimensions
  }

  public void open() {
    isOpen = true;
  }

  public void close() {
    isOpen = false;
  }

  public void toggle() {
    isOpen = !isOpen;
  }

  public boolean isOpen() {
    return isOpen;
  }

}
