package com.portalmedia.embarc.gui;

import java.util.List;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Progress dialog modal to display process completion
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class ProgressDialog {
	private final Stage dialogStage;
	private final ProgressIndicator progressIndicator = new ProgressIndicator();
	private Label processCount;
	private final VBox vbox;
	private final GridPane grid;

	public ProgressDialog() {
		dialogStage = new Stage();
		dialogStage.initOwner(Main.getPrimaryStage());
		dialogStage.setHeight(400);
		dialogStage.setWidth(400);
		dialogStage.initStyle(StageStyle.UTILITY);
		dialogStage.setResizable(false);
		dialogStage.setTitle("Processing Files...");

		progressIndicator.setProgress(-1F);
		progressIndicator.setVisible(true);

		grid = new GridPane();
		grid.setPrefWidth(300);
		grid.setHgap(20);
		grid.setVgap(20);
		grid.setPadding(new Insets(40, 40, 40, 40));
		grid.setAlignment(Pos.CENTER);
		grid.add(progressIndicator, 0, 0);

		vbox = new VBox();
		vbox.setAlignment(Pos.TOP_CENTER);
		vbox.getChildren().add(grid);

		final Scene scene = new Scene(vbox);
		dialogStage.setScene(scene);
	}

	public void activateProgressBar(final Task<?> task) {
		progressIndicator.progressProperty().bind(task.progressProperty());
		dialogStage.show();
	}

	public void cancelProgressBar() {
		if (progressIndicator.progressProperty().isBound()) {
			progressIndicator.progressProperty().unbind();
		}
	}

	public Stage getDialogStage() {
		return dialogStage;
	}

	public void setCountLabel(Label processText) {
		processCount = processText;
		final int size = grid.getChildren().size();
		grid.add(processCount, 0, size);
	}

	public void showCloseButton() {
		final Button closeButton = new Button("Close");
		vbox.getChildren().add(closeButton);
		closeButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				dialogStage.close();
			}
		});
	}

	public void showLabels(List<Label> labels) {
		final int size = grid.getChildren().size();
		for (int i = 0; i < labels.size(); i++) {
			grid.add(labels.get(i), 0, size + i);
		}
	}
}
