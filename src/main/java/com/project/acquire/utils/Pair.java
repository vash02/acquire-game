package com.project.acquire.utils;

import java.util.List;

public class Pair {
    private String first;
    private List<Integer> second;

    public Pair(String first, List<Integer> second) {
        this.first = first;
        this.second = second;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public List<Integer> getSecond() {
        return second;
    }

    public void setSecond(List<Integer> second) {
        this.second = second;
    }
}
