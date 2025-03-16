package com.al232.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicScrollBarUI;

import com.al232.AppConstants;
import com.al232.RecipeManager;
import com.al232.models.RecipeState;

public class GUIManager {
    private final RecipeManager recipeManager;
    private JFrame frame;
    private JTextArea outputArea;
    private JTextField inputField;
    private final StringBuilder history;

    public GUIManager(RecipeManager recipeManager) {
        this.recipeManager = recipeManager;
        this.history = new StringBuilder();
    }

    public void createAndShowGUI() {
        frame = new JFrame("GTNH Recipe Expander");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setUndecorated(true);
        frame.setBackground(AppConstants.DARK_PURPLE);
        
        JPanel mainPanel = createMainPanel();
        setupDragSupport(mainPanel);
        
        frame.getContentPane().add(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Initial output
        appendToOutput("GTNH Recipe Expander");
        appendToOutput("------------------------");
        appendToOutput("\nEnter an item name and optional quantity (e.g., 'hv_motor 5')");
        
        inputField.requestFocusInWindow();
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(AppConstants.DARK_PURPLE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, AppConstants.DARK_PURPLE,
                    getWidth(), getHeight(), AppConstants.BACKGROUND_BLACK
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), AppConstants.CORNER_RADIUS, AppConstants.CORNER_RADIUS);
                g2.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setOpaque(false);

        mainPanel.add(createTitlePanel(), BorderLayout.NORTH);
        mainPanel.add(createScrollPane(), BorderLayout.CENTER);
        mainPanel.add(createInputField(), BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        
        JPanel titleTextPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(AppConstants.TITLE_FONT);
                g2.setColor(AppConstants.TEXT_WHITE);
                int textHeight = g2.getFontMetrics().getHeight();
                int textY = (getHeight() - textHeight) / 2 + g2.getFontMetrics().getAscent();
                g2.drawString("GTNH Recipe Expander", 10, textY);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, 40);
            }
        };
        titleTextPanel.setOpaque(false);

        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        closePanel.setOpaque(false);
        
        JButton closeButton = new JButton("×") {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };
        closeButton.setForeground(AppConstants.TEXT_WHITE);
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 22));
        closeButton.setBorder(null);
        closeButton.setFocusPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setPreferredSize(new Dimension(30, 30));
        closeButton.addActionListener(e -> System.exit(0));
        closePanel.add(closeButton);

        titlePanel.add(titleTextPanel, BorderLayout.CENTER);
        titlePanel.add(closePanel, BorderLayout.EAST);
        
        return titlePanel;
    }

    private JScrollPane createScrollPane() {
        outputArea = new JTextArea() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(30, 0, 40, 200),
                    getWidth(), getHeight(), new Color(15, 0, 25, 200)
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), AppConstants.CORNER_RADIUS, AppConstants.CORNER_RADIUS);
                super.paintComponent(g);
            }
        };
        outputArea.setEditable(false);
        outputArea.setOpaque(false);
        outputArea.setForeground(AppConstants.TEXT_WHITE);
        outputArea.setFont(AppConstants.CONTENT_FONT);
        outputArea.setMargin(new Insets(15, 15, 15, 15));
        outputArea.setBorder(null);
        outputArea.setCaretColor(AppConstants.TEXT_WHITE);

        JScrollPane scrollPane = new JScrollPane(outputArea) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(25, 0, 35, 200),
                    getWidth(), getHeight(), new Color(10, 0, 20, 200)
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), AppConstants.CORNER_RADIUS, AppConstants.CORNER_RADIUS);
            }
        };
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        setupScrollBar(scrollPane);
        setupSmoothScrolling(scrollPane);
        
        return scrollPane;
    }

    private void setupScrollBar(JScrollPane scrollPane) {
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setPreferredSize(new Dimension(AppConstants.SCROLLBAR_WIDTH, 0));
        verticalScrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(60, 0, 80);
                this.trackColor = new Color(30, 0, 40);
            }
            
            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                // Don't paint anything for the track
            }
            
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.width == 0 || thumbBounds.height == 0) {
                    return;
                }
                
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(AppConstants.DARKER_PURPLE);
                g2.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);
                
                GradientPaint gradient = new GradientPaint(
                    thumbBounds.x, thumbBounds.y, new Color(80, 20, 100),
                    thumbBounds.x + thumbBounds.width, thumbBounds.y, new Color(60, 0, 80)
                );
                g2.setPaint(gradient);
                
                g2.fillRoundRect(
                    thumbBounds.x + 2,
                    thumbBounds.y + 2,
                    thumbBounds.width - 4,
                    thumbBounds.height - 4,
                    8, 8
                );
                
                g2.dispose();
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });
        
        verticalScrollBar.setOpaque(false);
        verticalScrollBar.setBackground(new Color(0, 0, 0, 0));
    }

    private void setupSmoothScrolling(JScrollPane scrollPane) {
        scrollPane.getVerticalScrollBar().setUnitIncrement(8);
        scrollPane.getVerticalScrollBar().putClientProperty("JScrollBar.smoothScrolling", true);
        
        final double[] scrollVelocity = {0.0};
        final Timer[] activeTimer = {null};
        
        outputArea.addMouseWheelListener(e -> {
            JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
            double direction = e.getWheelRotation();
            double acceleration = 5.0;
            
            scrollVelocity[0] += direction * acceleration;
            
            double maxVelocity = 20.0;
            scrollVelocity[0] = Math.max(-maxVelocity, Math.min(maxVelocity, scrollVelocity[0]));
            
            if (activeTimer[0] == null || !activeTimer[0].isRunning()) {
                activeTimer[0] = new Timer(4, null);
                activeTimer[0].addActionListener(evt -> {
                    if (Math.abs(scrollVelocity[0]) < 0.1) {
                        scrollVelocity[0] = 0;
                        activeTimer[0].stop();
                        return;
                    }
                    
                    int currentValue = scrollBar.getValue();
                    int newValue = currentValue + (int)scrollVelocity[0];
                    
                    scrollVelocity[0] *= 0.92;
                    
                    newValue = Math.max(0, Math.min(newValue, 
                        scrollBar.getMaximum() - scrollBar.getVisibleAmount()));
                    
                    if (newValue != currentValue) {
                        scrollBar.setValue(newValue);
                    }
                });
                activeTimer[0].start();
            }
        });
    }

    private JTextField createInputField() {
        inputField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(AppConstants.DARKER_PURPLE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(40, 0, 50, 255),
                    getWidth(), getHeight(), new Color(25, 0, 35, 255)
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), AppConstants.CORNER_RADIUS, AppConstants.CORNER_RADIUS);
                g2.dispose();
                
                setOpaque(false);
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                // Don't paint the default border
            }
        };
        inputField.setOpaque(false);
        inputField.setBackground(new Color(0, 0, 0, 0));
        inputField.setForeground(AppConstants.TEXT_WHITE);
        inputField.setCaretColor(AppConstants.TEXT_WHITE);
        inputField.setFont(AppConstants.INPUT_FONT);
        inputField.setMargin(new Insets(10, 15, 10, 15));
        inputField.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        inputField.addActionListener(e -> processInput(inputField.getText()));
        
        return inputField;
    }

    private void setupDragSupport(JPanel mainPanel) {
        MouseAdapter dragListener = new MouseAdapter() {
            private Point dragStart = null;
            
            @Override
            public void mousePressed(MouseEvent e) {
                dragStart = e.getPoint();
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragStart != null) {
                    Point location = frame.getLocation();
                    frame.setLocation(
                        location.x + e.getX() - dragStart.x,
                        location.y + e.getY() - dragStart.y
                    );
                }
            }
        };
        mainPanel.addMouseListener(dragListener);
        mainPanel.addMouseMotionListener(dragListener);
    }

    private void processInput(String input) {
        try {
            String[] parts = input.trim().toLowerCase().split("\\s+");
            String query = parts[0];
            int quantity = 1;

            if (parts.length > 1) {
                try {
                    quantity = Integer.parseInt(parts[1]);
                    if (quantity < 1) {
                        appendToOutput("Invalid quantity. Please enter a positive integer.");
                        appendToOutput("\nEnter an item to expand its recipe (or 'exit' to quit):");
                        return;
                    }
                } catch (NumberFormatException e) {
                    appendToOutput("Invalid quantity. Using default quantity of 1.");
                }
            }

            appendToOutput("\n> " + input);
            inputField.setText("");

            if (query.equalsIgnoreCase("exit")) {
                System.exit(0);
            }

            if (recipeManager.isWaitingForStepBack()) {
                if (query.equals("y")) {
                    stepBack();
                } else {
                    recipeManager.setWaitingForStepBack(false);
                    appendToOutput("\nEnter an item to expand its recipe (or 'exit' to quit):");
                }
                return;
            }

            recipeManager.setCurrentQuery(query);
            recipeManager.clearRecipeStack();
            recipeManager.expandRecipeStepByStep(query, quantity);
            
            if (recipeManager.getRecipeStack().isEmpty() && !recipeManager.itemExists(query)) {
                appendToOutput("Unknown item: " + query);
                appendToOutput("\nEnter an item to expand its recipe (or 'exit' to quit):");
                return;
            }

            displayCurrentState();
        } catch (Exception e) {
            appendToOutput("Error processing recipe: " + e.getMessage());
            appendToOutput("\nEnter an item to expand its recipe (or 'exit' to quit):");
        }
    }

    private void stepBack() {
        if (!recipeManager.getRecipeStack().isEmpty()) {
            recipeManager.getRecipeStack().pop();
            if (!recipeManager.getRecipeStack().isEmpty()) {
                displayCurrentState();
            } else {
                recipeManager.setWaitingForStepBack(false);
                appendToOutput("\nNo more steps to go back.");
                appendToOutput("\nEnter an item to expand its recipe (or 'exit' to quit):");
            }
        } else {
            recipeManager.setWaitingForStepBack(false);
            appendToOutput("\nNo more steps to go back.");
            appendToOutput("\nEnter an item to expand its recipe (or 'exit' to quit):");
        }
    }

    private void displayCurrentState() {
        if (recipeManager.getRecipeStack().isEmpty()) {
            appendToOutput("No recipe found.");
            appendToOutput("\nEnter an item to expand its recipe (or 'exit' to quit):");
            return;
        }

        RecipeState current = recipeManager.getRecipeStack().peek();
        appendToOutput("\nMaterials needed:");
        
        for (Map.Entry<String, Double> entry : current.recipe.entrySet()) {
            long roundedAmount = (long) Math.ceil(entry.getValue());
            appendToOutput("  - " + entry.getKey() + ": " + roundedAmount);
        }

        if (recipeManager.getRecipeStack().size() > 1) {
            appendToOutput("\nWould you like to step back to intermediate components? (y/n)");
            recipeManager.setWaitingForStepBack(true);
        } else {
            appendToOutput("\nThere are no more steps to go back.");
            appendToOutput("\nEnter an item to expand its recipe (or 'exit' to quit):");
            recipeManager.setWaitingForStepBack(false);
        }
    }

    private void appendToOutput(String text) {
        if (text.startsWith("Materials needed:")) {
            history.append("\n");
        }
        
        if (text.startsWith("  - ")) {
            text = "  • " + text.substring(4);
        }
        
        history.append(text).append("\n");
        
        if (text.equals("Would you like to step back to intermediate components? (y/n)")) {
            history.append("\n");
        }
        
        outputArea.setText(history.toString());
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }
} 