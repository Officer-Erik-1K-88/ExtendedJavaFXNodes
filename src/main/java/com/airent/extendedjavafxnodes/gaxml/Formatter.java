package com.airent.extendedjavafxnodes.gaxml;

import com.airent.extendedjavafxnodes.gaxml.themes.Theme;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.airent.extendedjavafxnodes.gaxml.XMLProcessor.onClick;

public class Formatter {
    private final List<Node> textList = new ArrayList<>();
    private final Attributes baseFormat = new Attributes((org.w3c.dom.Node) null);
    private final Theme theme;

    public Formatter(Theme theme) {
        this(null, theme);
    }

    public Formatter(Attributes baseFormat, Theme theme) {
        if (baseFormat != null) {
            this.baseFormat.putAll(baseFormat);
        }
        this.theme = theme;
    }

    public List<Node> getTextList() {
        return textList;
    }

    public void clear() {
        textList.clear();
    }

    public boolean isEmpty() {
        return textList.isEmpty();
    }

    public Attributes getBaseFormat() {
        return baseFormat;
    }

    @NotNull
    public Attributes getBaseFormat(Attributes addedFormat) {
        Attributes format = new Attributes(baseFormat);
        format.putAll(addedFormat);
        return format;
    }

    public Theme getTheme() {
        return theme;
    }

    public void add(List<Node> nodes) {
        textList.addAll(nodes);
    }

    public void add(String message, @NotNull Attributes format) {
        textList.add(format(message, format));
    }

    public void addBreak(@NotNull Attributes format) {
        textList.add(formatBreak(format));
    }

    public void addLine(@NotNull Attributes format) {
        textList.add(formatLine(format));
    }

    public Pane formatBreak(@NotNull Attributes format) {
        return applyBlockFormat(List.of(format(" ", format)), format);
    }

    public Node formatLine(@NotNull Attributes format) {
        format = getBaseFormat(format);
        double height = 4;
        if (format.containsKey("size")) {
            height = Double.parseDouble(format.get("size"));
        }
        if (!format.containsKey("height")) {
            format.put("height", "40");
        }
        double ph = Double.parseDouble(format.get("height"));
        if (height > ph) {
            height = ph;
        }
        double y = (ph/2)-(height/2);//ph/(3.5/1.5);//(height+ph)/2.5;
        Rectangle rect = new Rectangle(668, height);
        rect.setTranslateY(y);
        
        applyFormat(rect, format);
        return applyBlockFormat(List.of(rect), format);
    }

    @NotNull
    public Node format(String message, @NotNull Attributes format) {
        format = getBaseFormat(format);
        Node newNode;
        // font
        double fontSize = 12.0;
        String fontType = null;
        if (format.containsKey("size")) {
            fontSize = Double.parseDouble(format.get("size"));
        }
        if (format.containsKey("font-type")) {
            fontType = format.get("font-type");
        }
        Text tNode = new Text(message);
        if (fontType == null) {
            tNode.setFont(new javafx.scene.text.Font(fontSize));
        } else {
            tNode.setFont(new Font(fontType, fontSize));
        }
        tNode.setWrappingWidth(668);
        applyFormat(tNode, format);
        if (!format.containsKey("href")) {
            newNode = tNode;
        } else {
            if (!format.containsKey("color")) {
                tNode.setFill(theme.getTertiary());
            }
            newNode = getHyperlink(tNode, format);
        }
        return newNode;
    }

    @NotNull
    private Hyperlink getHyperlink(Text tNode, @NotNull Attributes format) {
        Hyperlink hyperlink = new Hyperlink("", tNode);
        String path = format.get("href");
        onClick(hyperlink, event -> {
            if (path.startsWith("goesTo:")) {
                System.out.println("To Be Implemented Later.");
                //((StoryPage) Page.getPage("StoryPage")).display(path.replace("goesTo:",""));
            } else if (path.startsWith("http")) {
                try {
                    URI uri = new URI(path);
                    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
                    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                        try {
                            desktop.browse(uri);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException("Cannot open an unsupported external file.");
            }
        });
        return hyperlink;
    }

    public void applyFormat(Shape node, @NotNull Attributes format) {
        // misc
        if (format.containsKey("color")) {
            node.setFill(Color.web(format.get("color")));
        } else {
            node.setFill(theme.getSecondary());
        }
        if (format.containsKey("width")) {
            double width = Double.parseDouble(format.get("width"));
            node.maxWidth(width);
            node.prefWidth(width);
            if (node instanceof Text) {
                ((Text) node).setWrappingWidth(width);
            }
        }
        // outline
        if (format.containsKey("outline-color")) {
            node.setStroke(Color.web(format.get("outline-color")));
        }
        if (format.containsKey("outline-width")) {
            node.setStrokeWidth(Double.parseDouble(format.get("outline-width")));
        }
        if (format.containsKey("outline-miter")) {
            node.setStrokeMiterLimit(Double.parseDouble(format.get("outline-miter")));
        }
        if (format.containsKey("outline-dash-offset")) {
            node.setStrokeDashOffset(Double.parseDouble(format.get("outline-dash-offset")));
        }
        if (format.containsKey("outline-type")) {
            node.setStrokeType(StrokeType.valueOf(format.get("outline-type")));
        }
        if (format.containsKey("outline-line-cap")) {
            node.setStrokeLineCap(StrokeLineCap.valueOf(format.get("outline-line-cap")));
        }
        if (format.containsKey("outline-line-join")) {
            node.setStrokeLineJoin(StrokeLineJoin.valueOf(format.get("outline-line-join")));
        }
        // special
        if (format.containsKey("blend")) {
            node.setBlendMode(BlendMode.valueOf(format.get("blend")));
        }
        // CSS
        if (format.containsKey("class")) {
            String[] classes = format.get("class").split(" ");
            node.getStyleClass().addAll(classes);
        }
    }

    public void applyFormat(Text node, @NotNull Attributes format) {
        // apply shape relevant
        applyFormat((Shape) node, format);
        // misc
        if (format.containsKey("tab-size")) {
            node.setTabSize(Integer.parseInt(format.get("tab-size")));
        }
        // highlighting (selection)
        if (format.containsKey("highlight-color")) {
            node.setSelectionFill(Color.web(format.get("highlight-color")));
        }
        if (format.containsKey("highlight-start")) {
            node.setSelectionStart(Integer.parseInt(format.get("highlight-start")));
        }
        if (format.containsKey("highlight-end")) {
            node.setSelectionStart(Integer.parseInt(format.get("highlight-end")));
        }
        // special
        if (format.containsKey("strikethrough")) {
            node.setStrikethrough(Boolean.parseBoolean(format.get("strikethrough")));
        }
        if (format.containsKey("underline")) {
            node.setUnderline(true);
            String underlineType = format.get("underline");
            if (!underlineType.contains("full")) {
                String[] underline = new String[0];
                if (underlineType.contains("half")) {
                    if (underlineType.contains("start")) {
                        underline = new String[]{
                                "0", String.valueOf(node.getText().length()/2)
                        };
                    } else if (underlineType.contains("end")) {
                        underline = new String[]{
                                String.valueOf(node.getText().length()/2),
                                String.valueOf(node.getText().length())
                        };
                    }
                } else {
                    underline = format.get("underline").split(" ");
                }
                if (underline.length == 2) {
                    node.underlineShape(Integer.parseInt(underline[0]), Integer.parseInt(underline[1]));
                }
            }
        }
    }

    @NotNull
    public Pane applyBlockFormat(@NotNull List<Node> nodeList, @NotNull Attributes format) {
        return applyBlockFormat(nodeList, format, false);
    }

    @NotNull
    private Pane subductedBlockFormat(@NotNull List<Node> nodeList, @NotNull Attributes format) {
        Attributes attrs = new Attributes(format);
        attrs.remove("height");
        attrs.remove("border");
        attrs.remove("background");
        return applyBlockFormat(nodeList, format, true);
    }

    @NotNull
    private Pane applyBlockFormat(@NotNull List<Node> nodeList, @NotNull Attributes format, boolean subducted) {
        boolean canTextFlow = true;
        List<Node> nodes = new ArrayList<>();
        List<Node> tempNodes = new ArrayList<>();
        for (Node node : nodeList) {
            if (node instanceof Text || node instanceof Hyperlink) {
                tempNodes.add(node);
            } else {
                canTextFlow = false;
                if (!tempNodes.isEmpty()) {
                    nodes.add(subductedBlockFormat(tempNodes, format));
                    tempNodes.clear();
                }
                nodes.add(node);
            }
        }

        if (!tempNodes.isEmpty() && !subducted) {
            nodes.add(subductedBlockFormat(tempNodes, format));
            tempNodes.clear();
        } else {
            if (nodes.isEmpty()) {
                nodes = tempNodes;
            }
        }

        Pane pane;
        if (canTextFlow) {
            pane = new TextFlow();
        } else {
            pane = new VBox();
        }
        pane.getChildren().addAll(nodes);
        if (format.containsKey("width")) {
            double width = Double.parseDouble(format.get("width"));
            pane.setMaxWidth(width);
            pane.setPrefWidth(width);
        } else {
            pane.setMaxWidth(668);
            pane.setPrefWidth(668);
        }
        if (format.containsKey("height")) {
            double height = Double.parseDouble(format.get("height"));
            pane.setMaxHeight(height);
            pane.setPrefHeight(height);
        }
        // border
        if (format.containsKey("border")) {
            String[] borderFormat = format.get("border").split(" ");
            BorderStroke borderStroke = null;
            if (borderFormat.length == 1 && !borderFormat[0].equals("none")) {
                borderFormat = new String[] {
                        borderFormat[0], "solid", "none", theme.getSecondary().toString(), "none"
                };
            } else if (borderFormat.length == 2) {
                borderFormat = new String[] {
                        borderFormat[0], "solid", "none", borderFormat[1], "none"
                };
            } else if (borderFormat.length == 3) {
                borderFormat = new String[] {
                        borderFormat[0], borderFormat[1], "none", borderFormat[2], "none"
                };
            } else if (borderFormat.length == 4) {
                borderFormat = new String[] {
                        borderFormat[0], borderFormat[1], borderFormat[2], borderFormat[3], "none"
                };
            }
            if (borderFormat.length == 5) {
                BorderStrokeStyle strokeStyle = switch (borderFormat[1]) {
                    case "solid" -> BorderStrokeStyle.SOLID;
                    case "dotted" -> BorderStrokeStyle.DOTTED;
                    case "dashed" -> BorderStrokeStyle.DASHED;
                    case "none" -> BorderStrokeStyle.NONE;
                    case null, default -> BorderStrokeStyle.SOLID;
                };
                CornerRadii cornerRadii = getCornerRadii(borderFormat[2], CornerRadii.EMPTY);
                javafx.geometry.Insets insets = getInsets(borderFormat[4], javafx.geometry.Insets.EMPTY);
                borderStroke = new BorderStroke(Color.web(borderFormat[3]), strokeStyle, cornerRadii, new BorderWidths(Double.parseDouble(borderFormat[0])), insets);
            }
            if (borderStroke != null) {
                pane.setBorder(new Border(borderStroke));
            } else {
                pane.setBorder(Border.EMPTY);
            }
        }
        // background
        if (format.containsKey("background")) {
            String[] bgFormat = format.get("background").split(" ");
            BackgroundFill backgroundFill = null;
            if (bgFormat.length == 1 && !bgFormat[0].equals("none")) {
                bgFormat = new String[] {
                        bgFormat[0], "none", "none"
                };
            } else if (bgFormat.length == 2) {
                bgFormat = new String[] {
                        bgFormat[0], bgFormat[1], "none"
                };
            }
            if (Objects.equals(bgFormat[0], "default")) {
                bgFormat[0] = theme.getPrimary().toString();
            }
            if (bgFormat.length == 3) {
                CornerRadii cornerRadii = getCornerRadii(bgFormat[1], pane.getBorder().getStrokes().getFirst().getRadii());
                javafx.geometry.Insets insets = getInsets(bgFormat[2], pane.getBorder().getStrokes().getFirst().getInsets());
                backgroundFill = new BackgroundFill(Color.web(bgFormat[0]), cornerRadii, insets);
            }
            if (backgroundFill != null) {
                pane.setBackground(new Background(backgroundFill));
            } else {
                pane.setBackground(Background.EMPTY);
            }
        }
        return pane;
    }

    @Nullable
    private static javafx.geometry.Insets getInsets(@NotNull String insetsFormat, javafx.geometry.Insets delt) {
        javafx.geometry.Insets insets = null;
        if (!insetsFormat.equals("none")) {
            String[] ins = insetsFormat.split("/");
            if (ins.length == 2) {
                ins = new String[] {
                        ins[0], ins[1], ins[0], ins[1]
                };
            } else if (ins.length == 3) {
                ins = new String[] {
                        ins[0], ins[1], ins[2], ins[1]
                };
            }
            if (ins.length == 4) {
                insets = new javafx.geometry.Insets(Double.parseDouble(ins[0]), Double.parseDouble(ins[1]), Double.parseDouble(ins[2]), Double.parseDouble(ins[3]));
            } else if (ins.length == 1) {
                insets = new Insets(Double.parseDouble(ins[0]));
            }
        }
        if (insets == null) {
            insets = delt;
        }
        return insets;
    }

    @NotNull
    private static CornerRadii getCornerRadii(@NotNull String radiiFormat, CornerRadii delt) {
        CornerRadii cornerRadii = null;
        if (!radiiFormat.equals("none")) {
            String[] radii = radiiFormat.split("/");
            if (radii.length == 1) {
                cornerRadii = new CornerRadii(Double.parseDouble(radii[0]));
            } else if (radii.length == 2) {
                cornerRadii = new CornerRadii(Double.parseDouble(radii[0]), radii[1].equals("true"));
            }
        }
        if (cornerRadii == null) {
            cornerRadii = delt;
        }
        return cornerRadii;
    }
}
