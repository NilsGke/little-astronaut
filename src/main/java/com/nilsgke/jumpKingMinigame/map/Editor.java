package com.nilsgke.jumpKingMinigame.map;

import com.nilsgke.jumpKingMinigame.Main;
import name.panitz.game2d.Vertex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Editor extends JPanel {

  GameMap gameMap;
  double x = 0;
  double y = 0;
  double zoom = 1;

  Platform selectedPlatform = null;

  public static void main(String[] args) {
    new Editor();
  }

  private JPanel createJPanel() {
    JPanel commandPanel = new JPanel();
    commandPanel.setLayout(new FlowLayout(FlowLayout.LEFT)); // Align buttons to the left

    JButton newPlatformButton = new JButton("new Platform");
    newPlatformButton.addActionListener(e -> {
      gameMap.platforms.add(new Platform(
              new Vertex(x, y),
              new Vertex(getWidth() / 2.0 + x, getHeight() / 2.0 + y),
              20, 50
      ));
      paintComponent(getGraphics());
    });


    JButton saveButton = new JButton("SAVE");
    saveButton.addActionListener(e -> {
      var stringBuilder = new StringBuilder();
      stringBuilder.append("{\n");
      stringBuilder.append("\"platforms\": [\n");
      for (var platform : gameMap.platforms) stringBuilder.append(platform.toJSON()).append("\n");
      stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length() - 1); // remove trailing comma
      stringBuilder.append("]\n");
      stringBuilder.append("}");

      System.out.println("\n");
      System.out.println(stringBuilder);
    });

    JButton launchButton = new JButton("launch game");
    launchButton.addActionListener(e -> {
      try {
        new Main().play();
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    });


    commandPanel.add(newPlatformButton);
    commandPanel.add(saveButton);
    commandPanel.add(launchButton);
    return commandPanel;
  }

  Editor() {

    try {
      gameMap = GameMap.fromJson();
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }

    setFocusable(true);
    requestFocusInWindow();

    addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        System.out.println("Editor gained focus");
      }
    });

    addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        var component = e.getComponent();
      }
    });
    addMouseWheelListener(e -> {
      zoom += e.getWheelRotation() * -0.01;
      paintComponent(getGraphics());
    });
    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(java.awt.event.MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        selectedPlatform = null;

        for (var platform : gameMap.platforms) {
          if (!platform.isLeftOf(mouseX / zoom - x) && !platform.isRightOf(mouseX / zoom - x) && !platform.isAbove(mouseY / zoom - y) && !platform.isUnderneath(mouseY / zoom - y))
            selectedPlatform = platform;
        }

        paintComponent(getGraphics());
        requestFocusInWindow();
      }
    });
    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        super.keyPressed(e);

        boolean shiftPressed = (e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0;
        boolean ctrlPressed = (e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0;

        switch (e.getKeyCode()) {
          // move camera
          case KeyEvent.VK_W:
            y += 100 * (1 / zoom);
            break;
          case KeyEvent.VK_S:
            y -= 100 * (1 / zoom);
            break;
          case KeyEvent.VK_A:
            x += 100 * (1 / zoom);
            break;
          case KeyEvent.VK_D:
            x -= 100 * (1 / zoom);
            break;

          // move platforms
          case KeyEvent.VK_UP:
            if (selectedPlatform != null)
              selectedPlatform.move(0, shiftPressed ? -1 : -10);
            break;

          case KeyEvent.VK_DOWN:
            if (selectedPlatform != null)
              selectedPlatform.move(0, shiftPressed ? 1 : 10);
            break;

          case KeyEvent.VK_LEFT:
            if (selectedPlatform != null)
              selectedPlatform.move(shiftPressed ? -1 : -10, 0);
            break;

          case KeyEvent.VK_RIGHT:
            if (selectedPlatform != null)
              selectedPlatform.move(shiftPressed ? 1 : 10, 0);
            break;

          case KeyEvent.VK_X:
            if (selectedPlatform != null)
              selectedPlatform.setWidth(selectedPlatform.width() + (ctrlPressed ? -1 : 1) * (shiftPressed ? 1 : 10));
            break;

          case KeyEvent.VK_Y:
            if (selectedPlatform != null)
              selectedPlatform.setHeight(selectedPlatform.height() + (ctrlPressed ? -1 : 1) * (shiftPressed ? 1 : 10));
            break;

          case KeyEvent.VK_BACK_SPACE:
            if(selectedPlatform != null)
              gameMap.platforms.remove(selectedPlatform);

          default:
            break;
        }
        paintComponent(getGraphics());
      }
    });

    repaint();
    getToolkit().sync();
    requestFocus();


    var f = new javax.swing.JFrame();
    f.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

    // control buttons
    JPanel commandPanel = createJPanel();

    // Set up the frame layout
    f.setLayout(new BorderLayout());
    f.add(commandPanel, BorderLayout.NORTH); // Add command panel at the top
    f.add(this, BorderLayout.CENTER); // Add editor in the center

    f.pack();
    f.setVisible(true);

    // make canvas focusable
    this.requestFocusInWindow();


  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(new Dimension(800, 600));
  }


  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, getWidth(), getHeight());

    for (var platform : gameMap.platforms) {
      g.setColor(Color.BLACK);
      if (selectedPlatform == platform) g.setColor(Color.RED);
      g.fillRect((int) (zoom * (platform.pos().x + x)), (int) (zoom * (platform.pos().y + y)), (int) (zoom * platform.width()), (int) (zoom * platform.height()));

    }
  }
}

