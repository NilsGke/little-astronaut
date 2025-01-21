package com.nilsgke.littleAstronaut.map;

import com.nilsgke.littleAstronaut.levels.Level;
import com.nilsgke.littleAstronaut.levels.Level_1;
import name.panitz.game2d.Vertex;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Editor extends JFrame {
  private final JComboBox<String> levelSelector;
  private Level currentLevel;
  private final JPanel drawingPanel;
  private final ArrayList<Platform> platforms = new ArrayList<>();
  private Platform completionZone;
  private Vertex startPosition;

  private double offsetX = 0;
  private double offsetY = 0;
  private final double moveSpeed = 40;

  public static void main(String[] args) {
    SwingUtilities.invokeLater(Editor::new);
  }

  public Editor() {
    setTitle("Graphical Level Editor");
    setSize(1200, 800);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    // Level Selector
    JPanel topPanel = new JPanel();
    levelSelector = new JComboBox<>(new String[]{"New Level", "Level_1"});
    levelSelector.addActionListener(e -> loadLevel((String) levelSelector.getSelectedItem()));
    topPanel.add(new JLabel("Select Level: "));
    topPanel.add(levelSelector);
    add(topPanel, BorderLayout.NORTH);

    // Drawing Panel
    drawingPanel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderLevel(g);
      }
    };
    drawingPanel.setBackground(Color.WHITE);
    drawingPanel.setPreferredSize(new Dimension(1000, 700));
    drawingPanel.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        handleMouseClick(e);
      }
    });
    add(drawingPanel, BorderLayout.CENTER);

    // Add key listener for panning
    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
          case KeyEvent.VK_W -> offsetY += moveSpeed;
          case KeyEvent.VK_S -> offsetY -= moveSpeed;
          case KeyEvent.VK_A -> offsetX += moveSpeed;
          case KeyEvent.VK_D -> offsetX -= moveSpeed;
        }
        drawingPanel.repaint();
      }
    });

    // Control Buttons
    JPanel bottomPanel = new JPanel();
    JButton saveButton = new JButton("Save Level");
    saveButton.addActionListener(e -> saveLevel());
    JButton addPlatformButton = new JButton("Add Platform");
    addPlatformButton.addActionListener(e -> addPlatform());
    bottomPanel.add(saveButton);
    bottomPanel.add(addPlatformButton);
    add(bottomPanel, BorderLayout.SOUTH);

    setVisible(true);
    loadLevel("New Level");

    // Make the JFrame focusable for key events
    setFocusable(true);
    requestFocusInWindow();
  }

  private void loadLevel(String levelName) {
    if (levelName.equals("New Level")) {
      currentLevel = null;
      platforms.clear();
      completionZone = null;
      startPosition = null;
    } else if (levelName.equals("Level_1")) {
      currentLevel = new Level_1();
      platforms.clear();
      platforms.addAll(List.of(currentLevel.platforms));
      startPosition = currentLevel.startPos;
      completionZone = currentLevel.completeZone;
    }
    drawingPanel.repaint();
  }

  private void saveLevel() {
    StringBuilder code = new StringBuilder();
    code.append("new Platform[]{\n");

    for (Platform platform : platforms)
      code.append("    ").append(platform.toCode()).append(",\n");


    // delete last comma
    code.deleteCharAt(code.length() - 2);
    code.append("},\n");

    // start position
    if (startPosition != null)
      code.append("new Vertex(").append((int) startPosition.x).append(", ").append((int) startPosition.y).append("),\n");
    else code.append("new Vertex(0, 700),\n");


    // completion zone
    if (completionZone != null) {
      code.append("new Platform(").append((int) completionZone.pos().x).append(", ").append((int) completionZone.pos().y).append(", ").append((int) completionZone.width()).append(", ").append((int) completionZone.height()).append(")\n");
    } else {
      code.append("new Platform(0, 0, 0, 0)\n");  // default
    }
  }

  private void addPlatform() {
    String input = JOptionPane.showInputDialog(this, "Enter Platform (x, y, width, height):");
    if (input != null) {
      String[] parts = input.split(",");
      if (parts.length == 4) {
        try {
          double x = Double.parseDouble(parts[0].trim());
          double y = Double.parseDouble(parts[1].trim());
          double width = Double.parseDouble(parts[2].trim());
          double height = Double.parseDouble(parts[3].trim());
          platforms.add(new Platform((int) x, (int) y, width, height));
          drawingPanel.repaint();
        } catch (NumberFormatException e) {
          JOptionPane.showMessageDialog(this, "Invalid input! Please enter valid numbers.");
        }
      } else {
        JOptionPane.showMessageDialog(this, "Invalid format! Use: x, y, width, height");
      }
    }
  }

  private void handleMouseClick(MouseEvent e) {
    String[] options = {"Set Start Position", "Set Completion Zone", "Delete Platform"};
    int choice = JOptionPane.showOptionDialog(this, "Choose Action", "Mouse Action", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

    if (choice == 0) { // Set Start Position
      startPosition = new Vertex(e.getX() - offsetX, e.getY() - offsetY);
    } else if (choice == 1) { // Set Completion Zone
      String input = JOptionPane.showInputDialog(this, "Enter Completion Zone Width, Height:");
      if (input != null) {
        String[] parts = input.split(",");
        if (parts.length == 2) {
          try {
            double width = Double.parseDouble(parts[0].trim());
            double height = Double.parseDouble(parts[1].trim());
            completionZone = new Platform((int) (e.getX() - offsetX), (int) (e.getY() - offsetY), width, height);
          } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid dimensions!");
          }
        }
      }
    } else if (choice == 2) { // Delete Platform
      platforms.removeIf(p -> p.touches((int) (e.getX() - offsetX), (int) (e.getY() - offsetY)));
    }
    drawingPanel.repaint();
  }

  private void renderLevel(Graphics g) {
    g.setColor(Color.BLACK);
    for (Platform p : platforms) {
      g.fillRect((int) (p.pos().x + offsetX), (int) (p.pos().y + offsetY), (int) p.width(), (int) p.height());
    }

    if (startPosition != null) {
      g.setColor(Color.BLUE);
      g.fillOval((int) (startPosition.x + offsetX), (int) (startPosition.y + offsetY), 10, 10);
    }

    if (completionZone != null) {
      g.setColor(Color.RED);
      g.drawRect((int) (completionZone.pos().x + offsetX), (int) (completionZone.pos().y + offsetY), (int) completionZone.width(), (int) completionZone.height());
    }
  }
}