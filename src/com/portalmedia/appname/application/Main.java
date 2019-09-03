package com.portalmedia.appname.application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

//Test
public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Parent a = FXMLLoader.load(getClass()
                    .getResource("/com/portalmedia/embarc/gui/MainGridPane.fxml"));
			Scene scene1 = new Scene(a,1200,800);
			scene1.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene1);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
