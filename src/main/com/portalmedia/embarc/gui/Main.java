package com.portalmedia.embarc.gui;

import java.io.IOException;
import java.util.Optional;

import com.portalmedia.embarc.database.DBService;
import com.portalmedia.embarc.gui.model.FileInformationViewModel;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

/**
 * Main class. Starts app, sets up layout, and creates menu bar items
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class Main extends Application {
	private static Stage primaryStage;
    private BorderPane rootLayout;
    private static DBService<FileInformationViewModel> dbService;

	@Override
	public void start(Stage stage) {
		try {
			dbService = new DBService<FileInformationViewModel>(FileInformationViewModel.class);
			primaryStage = stage;
	        primaryStage.setTitle("embARC");
	        primaryStage.getIcons().add(new Image(com.portalmedia.embarc.gui.Main.class.getResourceAsStream("embARC.png")));
	        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	            @Override
	            public void handle(WindowEvent e) {
		        	Alert alert = new Alert(AlertType.CONFIRMATION);
		        	alert.setGraphic(null);
		        	alert.setTitle("Quit embARC");
		        	alert.setHeaderText(null);
		        	alert.setContentText("Are you sure you want to quit embARC?");
		        	
		        	Optional<ButtonType> result = alert.showAndWait();
		        	if (result.get() == ButtonType.OK){
			        	System.out.println("Closing db");
						dbService = new DBService<FileInformationViewModel>(FileInformationViewModel.class);
				        dbService.dropCollection();
			        	dbService.closeDB();
		        	} else {
		        		e.consume();
		        	}
	            }
			});
	        initRootLayout();
	        showMainStage();
	        dbService.getSize();
	        dbService.dropCollection();
        	dbService.closeDB();
	        //dbService.getSize();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/com/portalmedia/embarc/gui/Root.fxml"));
            rootLayout = loader.load();

            MenuBar menuBar = createMenuBar();
			final String os = System.getProperty("os.name");
			if (os != null && os.startsWith("Mac")) {
				menuBar.useSystemMenuBarProperty().set(true);
				rootLayout.getChildren().add(menuBar);
			} else {
				rootLayout.setTop(menuBar);
	            primaryStage.setFullScreen(true);
			}
            Scene scene = new Scene(rootLayout);
            
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
           
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public void showMainStage() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/com/portalmedia/embarc/gui/MainView.fxml"));
            AnchorPane mainWindow = (AnchorPane) loader.load();
            rootLayout.setCenter(mainWindow);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

	public static void main(String[] args) {
		launch(args);
	}
	
	public MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
		
		Menu fileMenu = new Menu("File");
		

		MenuItem importFilesItem = new MenuItem("Import Files");
		importFilesItem.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	ControllerMediator.getInstance().uploadFiles();
		    }
		});
		fileMenu.getItems().add(importFilesItem);
		
		MenuItem importDirectoryItem = new MenuItem("Import Folder");
		importDirectoryItem.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	ControllerMediator.getInstance().uploadFilesFromDirectory();
		    }
		});
		fileMenu.getItems().add(importDirectoryItem);
		
		MenuItem ruleSets = new MenuItem("Rule Sets");
		ruleSets.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	ControllerMediator.getInstance().showRuleSetsDialogue();
		    }
		});

		MenuItem writeFilesItem = new MenuItem("Write Files");
		writeFilesItem.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	ControllerMediator.getInstance().showWriteFilesDialog();
		    }
		});
		fileMenu.getItems().add(writeFilesItem);
		
		MenuItem aboutItem = new MenuItem("About embARC");
		aboutItem.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	ControllerMediator.getInstance().showAboutModal();
		    }
		});
		fileMenu.getItems().add(aboutItem);

		MenuItem exitItem = new MenuItem("Exit");
		exitItem.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	primaryStage.fireEvent(
                    new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST)
                );
		    }
		});
		fileMenu.getItems().add(exitItem);
		
		
		Menu optionsMenu = new Menu("Options");
		MenuItem toggleColumns = new MenuItem("Toggle Column Visibility");
		toggleColumns.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	ControllerMediator.getInstance().showColumnVisibilityDialogue();
		    }
		});

		optionsMenu.getItems().add(ruleSets);
		optionsMenu.getItems().add(toggleColumns);
		
		Menu reportsMenu = new Menu("Reports");
		MenuItem createReport = new MenuItem("Download Rule Violations");
		createReport.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	ControllerMediator.getInstance().createReport();
		    }
		});
		MenuItem createImageChecksumReport = new MenuItem("Download Image Data Checksums");
		createImageChecksumReport.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent t) {
		    	ControllerMediator.getInstance().createImageChecksumReport();
		    }
		});
		reportsMenu.getItems().add(createReport);
		reportsMenu.getItems().add(createImageChecksumReport);
		
		menuBar.getMenus().addAll(fileMenu, optionsMenu, reportsMenu);
		return menuBar;
	}
}
