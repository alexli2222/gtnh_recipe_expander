package com.al232;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import org.yaml.snakeyaml.Yaml;

import com.al232.models.RecipeState;

public class RecipeManager {
    private final Map<String, Object> references;
    private final Map<String, Map<String, Object>> items;
    private Stack<RecipeState> recipeStack;
    private String currentQuery;
    private boolean waitingForStepBack;

    public RecipeManager() {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = this.getClass().getClassLoader()
                .getResourceAsStream("references.yml");
            if (inputStream == null) {
                throw new RuntimeException("Could not find references.yml");
            }
            references = yaml.load(inputStream);
            items = (Map<String, Map<String, Object>>) references.get("items");
            recipeStack = new Stack<>();
            waitingForStepBack = false;
        } catch (Exception e) {
            throw new RuntimeException("Error loading references.yml: " + e.getMessage(), e);
        }
    }

    public boolean isWaitingForStepBack() {
        return waitingForStepBack;
    }

    public void setWaitingForStepBack(boolean waiting) {
        waitingForStepBack = waiting;
    }

    public Stack<RecipeState> getRecipeStack() {
        return recipeStack;
    }

    public void expandRecipeStepByStep(String itemName, int quantity) {
        // Get the immediate recipe first
        Map<String, Double> currentRecipe = getImmediateRecipe(itemName);
        if (currentRecipe.isEmpty()) {
            if (isBaseMaterial(itemName)) {
                // If it's a base material, add it as its own recipe
                Map<String, Double> baseRecipe = new LinkedHashMap<>();
                baseRecipe.put(itemName, (double) quantity);
                recipeStack.push(new RecipeState(itemName, baseRecipe, new HashMap<>()));
            }
            return;
        }

        // Create a list to store all recipe states
        Stack<RecipeState> allStates = new Stack<>();
        
        // Start with the immediate recipe multiplied by quantity
        Map<String, Double> scaledRecipe = new LinkedHashMap<>();
        Map<String, String> sources = new HashMap<>();
        for (Map.Entry<String, Double> entry : currentRecipe.entrySet()) {
            scaledRecipe.put(entry.getKey(), Math.ceil(entry.getValue() * quantity));
            sources.put(entry.getKey(), itemName);
        }
        allStates.push(new RecipeState(itemName, scaledRecipe, new HashMap<>(sources)));

        // Keep expanding one level at a time until we reach all base materials
        currentRecipe = scaledRecipe;
        boolean hasNonBaseMaterials;
        do {
            hasNonBaseMaterials = false;
            Map<String, Double> nextLevelRecipe = new LinkedHashMap<>();
            Map<String, String> nextLevelSources = new HashMap<>();

            for (Map.Entry<String, Double> entry : currentRecipe.entrySet()) {
                String ingredient = entry.getKey();
                double amount = entry.getValue();

                if (isBaseMaterial(ingredient)) {
                    nextLevelRecipe.merge(ingredient, amount, Double::sum);
                } else {
                    Map<String, Double> subRecipe = getImmediateRecipe(ingredient);
                    if (!subRecipe.isEmpty()) {
                        hasNonBaseMaterials = true;
                        for (Map.Entry<String, Double> subEntry : subRecipe.entrySet()) {
                            double newAmount = Math.ceil(subEntry.getValue() * amount);
                            nextLevelRecipe.merge(subEntry.getKey(), newAmount, Double::sum);
                            nextLevelSources.put(subEntry.getKey(), ingredient);
                        }
                    } else if (isBaseMaterial(ingredient)) {
                        nextLevelRecipe.merge(ingredient, amount, Double::sum);
                    }
                }
            }

            if (hasNonBaseMaterials) {
                allStates.push(new RecipeState(itemName, nextLevelRecipe, nextLevelSources));
                currentRecipe = nextLevelRecipe;
            }
        } while (hasNonBaseMaterials);

        // Now push to the main stack in the correct order
        while (!allStates.isEmpty()) {
            recipeStack.push(allStates.pop());
        }

        // Reverse the stack so base materials are first
        Stack<RecipeState> tempStack = new Stack<>();
        while (!recipeStack.isEmpty()) {
            tempStack.push(recipeStack.pop());
        }
        recipeStack = tempStack;
    }

    public boolean itemExists(String itemName) {
        return items != null && items.containsKey(itemName);
    }

    public boolean isBaseMaterial(String itemName) {
        if (items == null || !items.containsKey(itemName)) return false;
        Map<String, Object> item = items.get(itemName);
        return "base_material".equals(item.get("type"));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Double> getImmediateRecipe(String itemName) {
        try {
            if (items != null && items.containsKey(itemName)) {
                Map<String, Object> item = items.get(itemName);
                if (item.containsKey("recipe")) {
                    return convertToDoubleMap((Map<String, Object>) item.get("recipe"));
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting immediate recipe: " + e.getMessage());
        }
        return new LinkedHashMap<>();
    }

    private Map<String, Double> convertToDoubleMap(Map<String, Object> recipe) {
        Map<String, Double> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : recipe.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Number) {
                result.put(entry.getKey(), ((Number) value).doubleValue());
            } else if (value instanceof String) {
                result.put(entry.getKey(), Double.parseDouble((String) value));
            }
        }
        return result;
    }

    public void clearRecipeStack() {
        recipeStack.clear();
    }

    public String getCurrentQuery() {
        return currentQuery;
    }

    public void setCurrentQuery(String query) {
        this.currentQuery = query;
    }
} 