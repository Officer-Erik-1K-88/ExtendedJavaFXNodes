package com.airent.extendedjavafxnodes.gaxml.story;

public class Act extends StoryPart<Chapter> {
    private final String name;

    public Act(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
