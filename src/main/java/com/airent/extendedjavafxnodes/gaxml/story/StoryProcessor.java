package com.airent.extendedjavafxnodes.gaxml.story;

import com.airent.extendedjavafxnodes.gaxml.Attributes;
import com.airent.extendedjavafxnodes.gaxml.XMLProcessor;
import com.airent.extendedjavafxnodes.utils.Pair;
import javafx.scene.Node;
import org.jetbrains.annotations.NotNull;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class StoryProcessor {
    public final static StoryProcessor processor = new StoryProcessor("com/airent/clickergame/pages/story", "story");
    private final static StoryPart<Act> acts = new StoryPart<Act>() {
        @Override
        public String getName() {
            return "Acts";
        }
    };

    public static List<Node> load(@NotNull String path, Map<String, String> format) {
        String[] splitPath = path.split(":");
        if (splitPath.length < 3) {
            throw new IllegalArgumentException("The provided path doesn't have at least 3 parts.");
        }
        Act act = acts.get(splitPath[0]);
        Chapter chapter = act.get(splitPath[1]);
        Segment segment = chapter.get(splitPath[2]);
        return segment.load(new Attributes(format));
    }

    public final Path storyPath;
    public final String level;

    private StoryProcessor(String path, String level) {
        Path storyPath1;
        URL resource = this.getClass().getClassLoader().getResource(path);
        if (resource == null) {
            storyPath1 = Paths.get(path);
        } else {
            try {
                storyPath1 = Paths.get(resource.toURI());
            } catch (URISyntaxException e) {
                storyPath1 = Paths.get(resource.getPath());
            }
        }
        storyPath = storyPath1;
        this.level = level;
    }

    public Path getStoryPath() {
        return storyPath;
    }

    public void loadStory() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(storyPath)) {
            Act act = null;
            Chapter chapter = null;
            if (level.equals("act") || level.equals("chapter")) {
                act = acts.getLast();
            }
            if (level.equals("chapter")) {
                chapter = act.getLast();
            }
            for (Path entry : stream) {
                File entryFile = entry.toFile();
                if (level.equals("story")) {
                    if (entryFile.isFile()) {
                        continue;
                    }
                    acts.add(new Act(entryFile.getName()));
                    new StoryProcessor(entry.toString(), "act").loadStory();
                } else if (level.equals("act")) {
                    if (entryFile.isFile()) {
                        continue;
                    }
                    act.add(new Chapter(entryFile.getName()));
                    new StoryProcessor(entry.toString(), "chapter").loadStory();
                } else if (level.equals("chapter")) {
                    if (entryFile.isDirectory() || !entryFile.getName().endsWith("xml")) {
                        continue;
                    }
                    //System.out.println(entryFile.getPath());
                    chapter.add(new Segment(entryFile.getName().split("\\.")[0], new XMLProcessor(entryFile.getPath())));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveStory() {
        acts.forEach(act -> {
            File actFile = new File(storyPath.toString()+File.separator+act.getName());
            if (!actFile.exists()) {
                actFile.mkdir();
            }
            act.forEach(chapter -> {
                File chapterFile = new File(actFile.getPath()+File.separator+chapter.getName());
                if (!chapterFile.exists()) {
                    chapterFile.mkdir();
                }
                chapter.forEach(segment -> {
                    if (segment.getGaxml().getFile() == null) {
                        File segmentFile = new File(chapterFile.getPath()+File.separator+segment.getName()+".xml");
                        StringBuilder text = new StringBuilder();
                        if (!segmentFile.exists()) {
                            try {
                                segmentFile.createNewFile();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        try {
                            segment.getGaxml().setFile(segmentFile);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    try {
                        segment.getGaxml().update();
                    } catch (TransformerException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
        });
    }
}
