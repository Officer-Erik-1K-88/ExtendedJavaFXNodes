package com.airent.extendedjavafxnodes.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.WritableValue;
import javafx.css.CssMetaData;
import javafx.css.FontCssMetaData;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.StyleableStringProperty;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.PaintConverter;
import javafx.css.converter.SizeConverter;
import javafx.css.converter.StringConverter;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;

import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FilePicker extends Control {
    private final ListView<Label> fileDisplay;
    private final Button trigger;

    private final DirectoryChooser directoryChooser;
    private final FileChooser fileChooser;
    private final ArrayList<File> files;

    public FilePicker() {
        this(null, null, false, false);
    }

    public FilePicker(String text, String title, boolean isDirectoryPicker, boolean isMultiselect) {
        super();
        setDirectoryPicker(isDirectoryPicker);
        setMultiselect(isMultiselect);
        files = new ArrayList<>();
        directoryChooser = new DirectoryChooser();
        fileChooser = new FileChooser();
        fileDisplay = new ListView<>();
        trigger = new Button();
        getChildren().add(fileDisplay);
        getChildren().add(trigger);
        trigger.setOnAction(event -> {
            if (isMultiselect()) {
                showMulti();
            } else {
                show();
            }
        });

        if (text == null || text.isBlank()) {
            updateTriggerTextToDefault();
        } else {
            setText(text);
        }
        if (title == null || title.isBlank()) {
            updateTitleToDefault();
        } else {
            setTitle(title);
        }
    }

    private List<File> unmodifiableFiles;

    public List<File> getFiles() {
        List<File> unf = unmodifiableFiles;
        if (unf == null) {
            unf = Collections.unmodifiableList(files);
            unmodifiableFiles = unf;
        }
        return unf;
    }

    ListView<Label> getFileDisplay() {
        return fileDisplay;
    }

    Button getTrigger() {
        return trigger;
    }

    private void setFiles(@NotNull List<File> fileList) {
        if (!fileList.isEmpty()) {
            clear();
            for (File file : fileList) {
                if (file != null && !files.contains(file)) {
                    fileDisplay.getItems().add(createFileLabel(file));
                    files.add(file);
                }
            }
        }
    }

    private void setFile(File file) {
        if (file != null) {
            clear();
            fileDisplay.getItems().add(createFileLabel(file));
            files.add(file);
        }
    }

    private @NotNull Label createFileLabel(@NotNull File file) {
        Label label = new Label(file.getAbsolutePath());
        // TODO: Add label formatting
        return label;
    }

    public final void clear() {
        fileDisplay.getItems().clear();
        files.clear();
    }

    public final String getTitle() {
        if (isDirectoryPicker()) {
            return directoryChooser.getTitle();
        }
        return fileChooser.getTitle();
    }

    public final void setTitle(String title) {
        if (isDirectoryPicker()) {
            directoryChooser.setTitle(title);
        }
        fileChooser.setTitle(title);
    }

    private boolean checkText(@NotNull String text) {
        return text.equals("Choose File") ||
                text.equals("Choose Directory") ||
                text.equals("Choose Files") ||
                text.equals("Choose Directories");
    }

    public final void updateTriggerTextToDefault() {
        String text = "Choosing ";
        if (isDirectoryPicker()) {
            text += "Directory";
        } else {
            if (isMultiselect()) {
                text += "Files";
            } else {
                text += "File";
            }
        }
        setText(text);
    }

    public final void updateTitleToDefault() {
        String text = "Choose ";
        if (isDirectoryPicker()) {
            if (isMultiselect()) {
                text += "Directories";
            } else {
                text += "Directory";
            }
        } else {
            if (isMultiselect()) {
                text += "Files";
            } else {
                text += "File";
            }
        }
        setTitle(text);
    }

    public final void show() {
        File file;
        if (isDirectoryPicker()) {
            file = directoryChooser.showDialog(this.getScene().getWindow());
        } else {
            file = fileChooser.showOpenDialog(this.getScene().getWindow());
        }
        setFile(file);
    }

    public final void showMulti() {
        List<File> fileList;
        if (isDirectoryPicker()) {
            fileList = new ArrayList<>();
            String lastDirTitle = directoryChooser.getTitle();
            directoryChooser.setTitle("(In Multiselect Mode, will continue to open this window till cancel is selected.) "+lastDirTitle);
            File file;
            do {
                file = directoryChooser.showDialog(this.getScene().getWindow());
                if (file != null) {
                    fileList.add(file);
                }
            } while (file != null);
            directoryChooser.setTitle(lastDirTitle);
        } else {
            fileList = fileChooser.showOpenMultipleDialog(this.getScene().getWindow());
        }
        setFiles(fileList);
    }

    /* ------------------------------------------------------------------ */
    // Skin

    @Override
    protected Skin<?> createDefaultSkin() {
        return new FilePickerSkin(this);
    }
    
    /* *************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

    private DoubleProperty spacing;
    public final DoubleProperty spacingProperty() {
        if (spacing == null) {
            spacing = new StyleableDoubleProperty() {
                @Override
                public void invalidated() {
                    requestLayout();
                }

                @Override
                public Object getBean() {
                    return FilePicker.this;
                }

                @Override
                public String getName() {
                    return "spacing";
                }

                @Override
                public CssMetaData<FilePicker, Number> getCssMetaData() {
                    return StyleableProperties.SPACING;
                }
            };
        }
        return spacing;
    }
    public double getSpacing() {
        if (spacing == null) return 1;
        return spacing.get();
    }
    public void setSpacing(double spacing) {
        spacingProperty().set(spacing);
    }

    private final BooleanProperty directoryPicker = new SimpleBooleanProperty(this, "directoryPicker", false);

    public final BooleanProperty directoryPickerProperty() {
        return directoryPicker;
    }

    public final boolean isDirectoryPicker() {
        return directoryPicker.get();
    }

    public final void setDirectoryPicker(boolean directoryPicker) {
        this.directoryPicker.set(directoryPicker);
    }

    private final BooleanProperty multiselect = new SimpleBooleanProperty(this, "multiselect", false);

    public final BooleanProperty multiselectProperty() {
        return multiselect;
    }

    public final boolean isMultiselect() {
        return multiselect.get();
    }

    public final void setMultiselect(boolean multiselect) {
        this.multiselect.set(multiselect);
    }

    /**
     * The text to display in the file picker button. The text may be null.
     *
     * @return the text to display in the file picker button
     */
    public final StringProperty textProperty() {
        return trigger.textProperty();
    }
    public final void setText(String value) {
        trigger.setText(value);
    }
    public final String getText() {
        return trigger.getText();
    }

    /**
     * Specifies the behavior for lines of text <em>when text is multiline</em>.
     * This setting only affects multiple lines of text relative to the text bounds.
     *
     * @return the alignment of lines of text within this labeled
     * @defaultValue {@code TextAlignment.LEFT}
     */
    public final ObjectProperty<TextAlignment> textAlignmentProperty() {
        return trigger.textAlignmentProperty();
    }
    public final void setTextAlignment(TextAlignment value) {
        trigger.setTextAlignment(value);
    }
    public final TextAlignment getTextAlignment() {
        return trigger.getTextAlignment();
    }

    /**
     * Specifies the behavior to use if the text of the {@code FilePicker}
     * exceeds the available space for rendering the text.
     *
     * @return the overrun behavior if the text exceeds the available space
     * @defaultValue {@code OverrunStyle.ELLIPSIS}
     */
    public final ObjectProperty<OverrunStyle> textOverrunProperty() {
        if (textOverrun == null) {
            textOverrun = new StyleableObjectProperty<OverrunStyle>(OverrunStyle.ELLIPSIS) {

                @Override
                public CssMetaData<FilePicker,OverrunStyle> getCssMetaData() {
                    return StyleableProperties.TEXT_OVERRUN;
                }

                @Override
                public Object getBean() {
                    return FilePicker.this;
                }

                @Override
                public String getName() {
                    return "textOverrun";
                }

                @Override
                protected void invalidated() {
                    super.invalidated();
                    trigger.setTextOverrun(get());
                    fileDisplay.getItems().forEach(label -> label.setTextOverrun(get()));
                }
            };
        }
        return textOverrun;
    }
    private ObjectProperty<OverrunStyle> textOverrun;
    public final void setTextOverrun(OverrunStyle value) {
        textOverrunProperty().setValue(value);
    }
    public final OverrunStyle getTextOverrun() {
        return textOverrun == null ? OverrunStyle.ELLIPSIS : textOverrun.getValue();
    }

    private static final String DEFAULT_ELLIPSIS_STRING = "...";
    /**
     * Specifies the string to display for the ellipsis when text is truncated.
     *
     * <table>
     *   <caption>Ellipsis Table</caption>
     *   <tr><th scope="col" colspan=2>Examples</th></tr>
     *   <tr class="altColor"><th scope="row">"..." </th>        <td>Default value for most locales</td>
     *   <tr class="rowColor"><th scope="row">" . . . " </th>    <td></td>
     *   <tr class="altColor"><th scope="row">" [...] " </th>    <td></td>
     *   <tr class="rowColor"><th scope="row">"&#92;u2026" </th> <td>The Unicode ellipsis character '&hellip;'</td>
     *   <tr class="altColor"><th scope="row">"" </th>           <td>No ellipsis, just display the truncated string</td>
     * </table>
     *
     * Note that not all fonts support all Unicode characters.
     *
     * @return the ellipsis property on the string to display for the ellipsis
     * when text is truncated
     * @see <A href="http://en.wikipedia.org/wiki/Ellipsis#Computer_representations">Wikipedia:ellipsis</A>
     * @defaultValue {@code "..."}
     * @since JavaFX 2.2
     */
    public final StringProperty ellipsisStringProperty() {
        if (ellipsisString == null) {
            ellipsisString = new StyleableStringProperty(DEFAULT_ELLIPSIS_STRING) {
                @Override public Object getBean() {
                    return FilePicker.this;
                }

                @Override public String getName() {
                    return "ellipsisString";
                }

                @Override public CssMetaData<FilePicker,String> getCssMetaData() {
                    return StyleableProperties.ELLIPSIS_STRING;
                }

                @Override
                protected void invalidated() {
                    super.invalidated();
                    trigger.setEllipsisString(get());
                    fileDisplay.getItems().forEach(label -> label.setEllipsisString(get()));
                }
            };
        }
        return ellipsisString;
    }
    private StringProperty ellipsisString;
    public final void setEllipsisString(String value) {
        ellipsisStringProperty().set((value == null) ? "" : value);
    }
    public final String getEllipsisString() {
        return ellipsisString == null ? DEFAULT_ELLIPSIS_STRING : ellipsisString.get();
    }


    /**
     * If a run of text exceeds the width of the FilePicker, then this variable
     * indicates whether the text should wrap onto another line.
     *
     * @return the wrap property if a run of text exceeds the width of the FilePicker
     * @defaultValue {@code false}
     */
    public final BooleanProperty wrapTextProperty() {
        if (wrapText == null) {
            wrapText = new StyleableBooleanProperty() {

                @Override
                public CssMetaData<FilePicker,Boolean> getCssMetaData() {
                    return StyleableProperties.WRAP_TEXT;
                }

                @Override
                public Object getBean() {
                    return FilePicker.this;
                }

                @Override
                public String getName() {
                    return "wrapText";
                }

                @Override
                protected void invalidated() {
                    super.invalidated();
                    trigger.setWrapText(get());
                    fileDisplay.getItems().forEach(label -> label.setWrapText(get()));
                }
            };
        }
        return wrapText;
    }
    private BooleanProperty wrapText;
    public final void setWrapText(boolean value) {
        wrapTextProperty().setValue(value);
    }
    public final boolean isWrapText() {
        return wrapText == null ? false : wrapText.getValue();
    }

    /**
     * If wrapText is true, then contentBias will be HORIZONTAL, otherwise it is null.
     * @return orientation of width/height dependency or null if there is none
     */
    @Override public Orientation getContentBias() {
        return isWrapText()? Orientation.HORIZONTAL : null;
    }

    /**
     * The default font to use for text in the FilePicker. If the Label's text is
     * rich text then this font may or may not be used depending on the font
     * information embedded in the rich text, but in any case where a default
     * font is required, this font will be used.
     *
     * @return the default font to use for text in this labeled
     * @defaultValue {@link Font#getDefault()}
     */
    public final ObjectProperty<Font> fontProperty() {

        if (font == null) {
            font = new StyleableObjectProperty<Font>(Font.getDefault()) {

                private boolean fontSetByCss = false;

                @Override
                public void applyStyle(StyleOrigin newOrigin, Font value) {

                    //
                    // RT-20727 - if CSS is setting the font, then make sure invalidate doesn't call NodeHelper.reapplyCSS
                    //
                    try {
                        // super.applyStyle calls set which might throw if value is bound.
                        // Have to make sure fontSetByCss is reset.
                        fontSetByCss = true;
                        super.applyStyle(newOrigin, value);
                    } catch(Exception e) {
                        throw e;
                    } finally {
                        fontSetByCss = false;
                    }
                }

                @Override
                public void set(Font value) {
                    final Font oldValue = get();
                    if (!Objects.equals(value, oldValue)) {
                        super.set(value);
                    }

                }

                @Override
                protected void invalidated() {
                    trigger.setFont(get());
                    fileDisplay.getItems().forEach(label -> label.setFont(get()));
                }

                @Override
                public CssMetaData<FilePicker,Font> getCssMetaData() {
                    return StyleableProperties.FONT;
                }

                @Override
                public Object getBean() {
                    return FilePicker.this;
                }

                @Override
                public String getName() {
                    return "font";
                }
            };
        }
        return font;
    }
    private ObjectProperty<Font> font;
    public final void setFont(Font value) {
        fontProperty().setValue(value);
    }
    public final Font getFont() {
        return font == null ? Font.getDefault() : font.getValue();
    }


    /**
     * The {@link Paint} used to fill the text.
     *
     * @defaultValue {@code Color.BLACK}
     */
    private ObjectProperty<Paint> textFill; // TODO for now change this

    public final void setTextFill(Paint value) {
        textFillProperty().set(value);
    }

    public final Paint getTextFill() {
        return textFill == null ? Color.BLACK : textFill.get();
    }

    public final ObjectProperty<Paint> textFillProperty() {
        if (textFill == null) {
            textFill = new StyleableObjectProperty<Paint>(Color.BLACK) {

                @Override
                public CssMetaData<FilePicker,Paint> getCssMetaData() {
                    return StyleableProperties.TEXT_FILL;
                }

                @Override
                public Object getBean() {
                    return FilePicker.this;
                }

                @Override
                public String getName() {
                    return "textFill";
                }

                @Override
                protected void invalidated() {
                    super.invalidated();
                    trigger.setTextFill(get());
                    fileDisplay.getItems().forEach(label -> label.setTextFill(get()));
                }
            };
        }
        return textFill;
    }

    /* *************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/

    /**
     * Returns the initial alignment state of this control, for use
     * by the JavaFX CSS engine to correctly set its initial value. This method
     * is overridden to use Pos.CENTER_LEFT initially.
     *
     * @return the initial alignment state of this control
     * @since 9
     */
    protected Pos getInitialAlignment() {
        return Pos.CENTER_LEFT;
    }

    private static class StyleableProperties {
        private static final FontCssMetaData<FilePicker> FONT =
            new FontCssMetaData<>("-fx-font", Font.getDefault()) {

            @Override
            public boolean isSettable(@NotNull FilePicker n) {
                return n.font == null || !n.font.isBound();
            }

            @Override
            public StyleableProperty<Font> getStyleableProperty(@NotNull FilePicker n) {
                return (StyleableProperty<Font>)(WritableValue<Font>)n.fontProperty();
            }
        };

        private static final CssMetaData<FilePicker,Paint> TEXT_FILL =
                new CssMetaData<>("-fx-text-fill",
                PaintConverter.getInstance(), Color.BLACK) {

            @Override
            public boolean isSettable(@NotNull FilePicker n) {
                return n.textFill == null || !n.textFill.isBound();
            }

            @Override
            public StyleableProperty<Paint> getStyleableProperty(@NotNull FilePicker n) {
                return (StyleableProperty<Paint>)(WritableValue<Paint>)n.textFillProperty();
            }
        };

        private static final CssMetaData<FilePicker,OverrunStyle> TEXT_OVERRUN =
                new CssMetaData<>("-fx-text-overrun",
                new EnumConverter<>(OverrunStyle.class),
                OverrunStyle.ELLIPSIS) {

            @Override
            public boolean isSettable(@NotNull FilePicker n) {
                return n.textOverrun == null || !n.textOverrun.isBound();
            }

            @Override
            public StyleableProperty<OverrunStyle> getStyleableProperty(@NotNull FilePicker n) {
                return (StyleableProperty<OverrunStyle>)(WritableValue<OverrunStyle>)n.textOverrunProperty();
            }
        };

        private static final CssMetaData<FilePicker,String> ELLIPSIS_STRING =
                new CssMetaData<>("-fx-ellipsis-string",
                StringConverter.getInstance(), DEFAULT_ELLIPSIS_STRING) {

            @Override public boolean isSettable(@NotNull FilePicker n) {
                return n.ellipsisString == null || !n.ellipsisString.isBound();
            }

            @Override public StyleableProperty<String> getStyleableProperty(@NotNull FilePicker n) {
                return (StyleableProperty<String>)n.ellipsisStringProperty();
            }
        };

        private static final CssMetaData<FilePicker,Boolean> WRAP_TEXT =
                new CssMetaData<>("-fx-wrap-text",
                BooleanConverter.getInstance(), false) {

            @Override
            public boolean isSettable(@NotNull FilePicker n) {
                return n.wrapText == null || !n.wrapText.isBound();
            }

            @Override
            public StyleableProperty<Boolean> getStyleableProperty(@NotNull FilePicker n) {
                return (StyleableProperty<Boolean>)n.wrapTextProperty();
            }
        };

        private static final CssMetaData<FilePicker,Number> SPACING =
                new CssMetaData<>("-fx-spacing",
                        SizeConverter.getInstance(), 1d) {

                    @Override
                    public boolean isSettable(FilePicker node) {
                        return node.spacing == null || !node.spacing.isBound();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(FilePicker node) {
                        return (StyleableProperty<Number>)node.spacingProperty();
                    }
                };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                new ArrayList<>(Control.getClassCssMetaData());
            Collections.addAll(
                    styleables,
                    FONT,
                    TEXT_FILL,
                    TEXT_OVERRUN,
                    ELLIPSIS_STRING,
                    WRAP_TEXT,
                    SPACING
            );
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }
}
