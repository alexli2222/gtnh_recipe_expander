package com.al232.models;

import java.util.LinkedHashMap;
import java.util.Map;

public class RecipeNode {
    public String itemName;
    public double quantity;
    public Map<String, RecipeNode> children;
    public boolean isBaseMaterial;

    public RecipeNode(String itemName, double quantity, boolean isBaseMaterial) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.children = new LinkedHashMap<>();
        this.isBaseMaterial = isBaseMaterial;
    }
} 