package sample;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
        primaryStage.setTitle("PatView");
        primaryStage.setScene(new Scene(root, 1280, 720));


        primaryStage.show();
    }

    @FXML

    public static void main(String[] args) {
        launch(args);
    }
}
