package test;

import com.airent.extendedjavafxnodes.control.Alert;
import com.airent.extendedjavafxnodes.control.FilePicker;
import com.airent.extendedjavafxnodes.control.tutorial.TutorialContent;
import com.airent.extendedjavafxnodes.control.tutorial.TutorialPopup;
import com.airent.extendedjavafxnodes.gaxml.themes.Light;
import com.airent.extendedjavafxnodes.shape.Arrow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);

        if (scene.getRoot() instanceof VBox vBox) {
            FilePicker filePicker = new FilePicker();
            TutorialContent tutorialContent = new TutorialContent("Test");
            tutorialContent.addSlide("File Picker", null, HelloApplication.class.getResource("Page1.xml"), new Light(), filePicker);

            Button displayTut = new Button("Display Tutorial");
            tutorialContent.addSlide("Display Button", null, HelloApplication.class.getResource("Page1.xml"), new Light(), displayTut);

            displayTut.setOnAction(event -> tutorialContent.show());

            vBox.getChildren().addAll(filePicker, displayTut);
        }

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}