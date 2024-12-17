package com.airent.extendedjavafxnodes.gaxml.story;

public class Chapter extends StoryPart<Segment> {
    private final String name;

    public Chapter(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
