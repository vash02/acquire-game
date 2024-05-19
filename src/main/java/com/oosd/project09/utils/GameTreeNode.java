package com.oosd.project09.utils;

import GameObjects.State;

import java.util.ArrayList;
import java.util.List;

public class GameTreeNode {
    private State value;
    private List<GameTreeNode> children;
    private GameTreeNode parent;

    public GameTreeNode(State value) {
        this.value = value;
        this.children = new ArrayList<>();
        this.parent = null;
    }

    // Constructor to create a node with both action and value
    public GameTreeNode(State value, List<GameTreeNode> children) {
        this.value = value;
        this.children = children;
        this.parent = null;
    }

    public void setChildren(List<GameTreeNode> children) {
        for (GameTreeNode child : children) {
            addChild(child);
        }
    }

    // Add a method to add a child node
    public void addChild(GameTreeNode child) {
        children.add(child);
        child.setParent(this);
    }

    // Getter for the value
    public State getValue() {
        return value;
    }

    public List<GameTreeNode> getChildren() {
        return children;
    }

    // Getter and setter for parent
    public GameTreeNode getParent() {
        return parent;
    }

    public void setParent(GameTreeNode parent) {
        this.parent = parent;
    }

    // Getters, setters, and other methods as needed
}
