package com.al232.models;

import java.util.Map;

public class RecipeState {
    public final String itemName;
    public final Map<String, Double> recipe;
    public final Map<String, String> sources; // Maps item to its immediate source

    public RecipeState(String itemName, Map<String, Double> recipe, Map<String, String> sources) {
        this.itemName = itemName;
        this.recipe = recipe;
        this.sources = sources;
    }
} 