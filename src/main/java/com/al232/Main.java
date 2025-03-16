package com.al232;

import javax.swing.SwingUtilities;

import com.al232.gui.GUIManager;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RecipeManager recipeManager = new RecipeManager();
            GUIManager guiManager = new GUIManager(recipeManager);
            guiManager.createAndShowGUI();
        });
    }
} 