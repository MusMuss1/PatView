package sample;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
        primaryStage.setTitle("PatView");
        primaryStage.getIcons().add(new Image("logo.png"));
        //primaryStage.setScene(new Scene(root, 1280, 720));
        root.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        primaryStage.setScene(new Scene(root));
        primaryStage.sizeToScene();


        primaryStage.show();
        primaryStage.setMinHeight(primaryStage.getHeight());
        primaryStage.setMinWidth(primaryStage.getWidth());
    }


    @FXML

    public static void main(String[] args) {
        launch(args);
    }
}
