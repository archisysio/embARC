package com.portalmedia.embarc.gui;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.portalmedia.embarc.gui.helper.DPXFileListHelper;
import com.portalmedia.embarc.gui.model.FileInformationViewModel;
import com.portalmedia.embarc.parser.FileFormat;
import com.portalmedia.embarc.parser.FileFormatDetection;
import com.portalmedia.embarc.report.DPXReportService;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Controls main UI, handles file drags, various modals, file processing
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class MainViewController implements Initializable {

	@FXML
	private AnchorPane mainViewPane;
	@FXML
	private Pane addFilesOverlayPane;
	@FXML
	private GridPane createReportGridPane;
	@FXML
	private Button selectReportPathButton;
	@FXML
	private Label writeReportPath;
	@FXML
	private GridPane outputDirectoryGridPane;
	@FXML
	private Button chooseOutputDirButton;
	@FXML
	private Label writeFilesPath;
	@FXML
	private Button writeFilesButton;
	@FXML
	private Button closeAboutButton;
	@FXML
	private Button aboutButton;
	@FXML
	private Button licenseButton;
	@FXML
	private Pane aboutPane;
	@FXML
	private Pane licensePane;
	@FXML
	private Hyperlink fadgiLink;
	
	List<String> DPXFileList = new ArrayList<>();
	List<String> notDPXFileList = new ArrayList<>();
	List<String> DPXFailures = new ArrayList<>();
	int totalItemsCount = 0;
	boolean greyDragOver = false;

	public static int countFiles(File directory) {
		int count = 0;
		final File[] files = directory.listFiles();
		if (files != null) {
			for (final File file : files) {
				if (file.isFile()) {
					count++;
				}
				if (file.isDirectory()) {
					count += countFiles(file);
				}
			}
		}
		return count;
	}

	private void checkFileType(String f) {
		if (FileFormatDetection.getFileFormat(f) != FileFormat.OTHER) {
			DPXFileList.add(f);
		} else {
			notDPXFileList.add(f);
		}
	}

	public void createImageReport() {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select a file");
		fileChooser.setInitialFileName(
				"ImageDataChecksumReport_" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + ".csv");
		final File file = fileChooser.showSaveDialog(mainViewPane.getScene().getWindow());
		try {
			if (file != null) {
				DPXReportService.WriteImageHashCsv(ControllerMediator.getInstance().getTable(), file.getAbsolutePath());
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private Task<Void> createProcessFilesTask(List<File> files) {
		final Task<Void> task = new Task<Void>() {
			@Override
			public Void call() {
				System.currentTimeMillis();
				for (final File f : files) {
					getDirectoryContents(f.getAbsolutePath());
				}
				int count = 0;
				final int totalDPXFiles = DPXFileList.size();

				int increment = 1;

				if (totalDPXFiles > 10000) {
					increment = 50;
				} else if (totalDPXFiles > 5000) {
					increment = 25;
				} else if (totalDPXFiles > 1000) {
					increment = 10;
				}

				final int totalFiles = totalDPXFiles + notDPXFileList.size();
				double processed = 0;
				try {
					for (final String f : DPXFileList) {
						// TODO: allow user to cancel file upload?
						if (isCancelled()) {
							break;
						}
						// TODO: better success/error reporting instead of just returning t/f
						final boolean success = DPXFileListHelper.addFileToDatabase(f);
						count++;
						if (success) {

						} else {
							// This error is most likely a duplicate file path
							System.out.println("Error while adding DPX file to DB.");
							DPXFailures.add(f);
						}
						if (count == totalFiles || count % increment == 0) {
							processed = (count * 100) / totalFiles;
							updateProgress(processed, 100);
						}
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
				for (final String f : notDPXFileList) {
					count++;
					processed = (count * 100) / totalFiles;
					updateProgress(processed, 100);
				}
				Runtime.getRuntime().gc();
				System.gc();
				return null;
			}
		};
		return task;
	}

	public void createReport() {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select a file");
		fileChooser.setInitialFileName(
				"DPXValidationReport_" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + ".csv");
		final File file = fileChooser.showSaveDialog(mainViewPane.getScene().getWindow());
		try {
			if (file != null) {
				DPXReportService.WriteValidationCsv(ControllerMediator.getInstance().getTable(),
						file.getAbsolutePath());
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private List<CheckBox> createRuleSetCheckboxes() {
		final List<CheckBox> cbList = new ArrayList<>();
		final HashSet<ValidationRuleSetEnum> currentRules = ControllerMediator.getInstance().getCurrentRuleSets();

		// SMPTE-C
		final CheckBox smpteCheckbox = new CheckBox("SMPTE-C");

		final FontAwesomeIconView icon2 = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
		icon2.setStyleClass("smpte-c-warning");

		smpteCheckbox.setGraphic(icon2);
		if (currentRules.contains(ValidationRuleSetEnum.SMPTE_C)) {
			smpteCheckbox.setSelected(true);
		} else {
			smpteCheckbox.setSelected(false);
		}
		smpteCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> obs, Boolean ov, Boolean nv) {
				ControllerMediator.getInstance().toggleRuleSet(ValidationRuleSetEnum.SMPTE_C);
				ControllerMediator.getInstance().resetValidationRuleIndicators();
			}
		});
		cbList.add(smpteCheckbox);

		// FADGI-SR
		final CheckBox fadgiSRCheckbox = new CheckBox("FADGI-SR");
		final FontAwesomeIconView icon3 = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
		icon3.setStyleClass("fadgi-sr-warning");
		fadgiSRCheckbox.setGraphic(icon3);
		if (currentRules.contains(ValidationRuleSetEnum.FADGI_SR)) {
			fadgiSRCheckbox.setSelected(true);
		} else {
			fadgiSRCheckbox.setSelected(false);
		}
		fadgiSRCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> obs, Boolean ov, Boolean nv) {
				ControllerMediator.getInstance().toggleRuleSet(ValidationRuleSetEnum.FADGI_SR);
				ControllerMediator.getInstance().resetValidationRuleIndicators();
			}
		});
		cbList.add(fadgiSRCheckbox);

		// FADGI-R
		final CheckBox fadgiRCheckbox = new CheckBox("FADGI-R");
		final FontAwesomeIconView icon4 = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
		icon4.setStyleClass("fadgi-r-warning");
		fadgiRCheckbox.setGraphic(icon4);
		if (currentRules.contains(ValidationRuleSetEnum.FADGI_R)) {
			fadgiRCheckbox.setSelected(true);
		} else {
			fadgiRCheckbox.setSelected(false);
		}
		fadgiRCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> obs, Boolean ov, Boolean nv) {
				ControllerMediator.getInstance().toggleRuleSet(ValidationRuleSetEnum.FADGI_R);
				ControllerMediator.getInstance().resetValidationRuleIndicators();
			}
		});
		cbList.add(fadgiRCheckbox);

		// FADGI-O
		final CheckBox fadgiOCheckbox = new CheckBox("FADGI-O");
		final FontAwesomeIconView icon5 = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
		icon5.setStyleClass("fadgi-o-warning");
		fadgiOCheckbox.setGraphic(icon5);
		if (currentRules.contains(ValidationRuleSetEnum.FADGI_O)) {
			fadgiOCheckbox.setSelected(true);
		} else {
			fadgiOCheckbox.setSelected(false);
		}
		fadgiOCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> obs, Boolean ov, Boolean nv) {
				ControllerMediator.getInstance().toggleRuleSet(ValidationRuleSetEnum.FADGI_O);
				ControllerMediator.getInstance().resetValidationRuleIndicators();
			}
		});
		cbList.add(fadgiOCheckbox);

		return cbList;
	}

	public void deleteSelectedFiles() {
		final ObservableList<FileInformationViewModel> toDelete = ControllerMediator.getInstance()
				.getSelectedFileList();

		DPXFileListHelper.deleteSelectedRows(toDelete);
		final List<FileInformationViewModel> newList = new LinkedList<>();
		for (final FileInformationViewModel m : toDelete) {
			newList.add(m);
		}
		for (final FileInformationViewModel m : newList) {
			DPXFileListHelper.deleteFileFromDB(m.getId());
		}
	}

	private void getDirectoryContents(String dir) {
		final Path folder = Paths.get(dir);
		if (!Files.isDirectory(folder)) {
			checkFileType(folder.toAbsolutePath().toString());
		} else {
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
				for (final Path filePath : stream) {
					if (Files.isHidden(filePath)) {
						continue;
					}
					if (Files.isDirectory(filePath)) {
						getDirectoryContents(filePath.toAbsolutePath().toString());
					} else {
						checkFileType(filePath.toAbsolutePath().toString());
					}

				}
			} catch (final IOException ex) {
				// An I/O problem has occurred
			}
		}
	}

	@FXML
	private void handleOnDragDropped(DragEvent event) {
		if (ControllerMediator.getInstance().isEditingProperty().get()) {
			final String info = "Cannot add files to workspace while editing.";
			final Alert alert = new Alert(AlertType.WARNING, info, ButtonType.OK);
			alert.show();
			if (alert.getResult() == ButtonType.OK) {
				alert.close();
			}
			addFilesOverlayPane.setStyle("-fx-background-color: transparent;");
			addFilesOverlayPane.setMouseTransparent(true);
			greyDragOver = false;
			event.consume();
		} else {
			final Dragboard db = event.getDragboard();
			if (db.hasFiles()) {
				final List<File> files = db.getFiles();
				if (files.size() > 0) {
					if (files.get(0).isDirectory()) {
						int count = 0;
						for (final File file : files) {
							count += countFiles(file);
						}
						totalItemsCount = count;
					} else {
						totalItemsCount = files.size();
					}
				}
				processFiles(files, event);
			}
		}
	}

	@FXML
	private void handleOnDragEntered(DragEvent event) {
		addFilesOverlayPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
		addFilesOverlayPane.setMouseTransparent(false);
		greyDragOver = true;
		event.consume();
	}

	@FXML
	private void handleOnDragExited(DragEvent event) {
		event.consume();
		addFilesOverlayPane.setStyle("-fx-background-color: transparent");
		addFilesOverlayPane.setMouseTransparent(true);
		greyDragOver = false;
	}

	@FXML
	private void handleOnDragOver(DragEvent event) {
		event.acceptTransferModes(TransferMode.ANY);
		event.consume();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ControllerMediator.getInstance().registerMainViewController(this);
		addFilesOverlayPane.setMouseTransparent(true);

		// safety check for app grey out bug
		addFilesOverlayPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent t) {
				if (greyDragOver) {
					addFilesOverlayPane.setStyle("-fx-background-color: transparent");
					addFilesOverlayPane.setMouseTransparent(true);
					greyDragOver = false;
				}
			}
		});
	}

	private void openUrl(URI uri) {
		try {
			Desktop.getDesktop().browse(uri);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void processFiles(List<File> files, DragEvent event) {

		final Task<Void> task = createProcessFilesTask(files);
		final ProgressDialog progressDialog = new ProgressDialog();
		final Label filesToProcess = new Label("Total items found: " + totalItemsCount);
		final FontAwesomeIconView question = new FontAwesomeIconView(FontAwesomeIcon.QUESTION_CIRCLE);
		filesToProcess.setTooltip(new Tooltip("This count includes all folders and hidden files"));
		filesToProcess.setGraphic(question);
		progressDialog.setCountLabel(filesToProcess);
		progressDialog.activateProgressBar(task);
		progressDialog.getDialogStage().show();

		progressDialog.getDialogStage().setOnCloseRequest(e -> {
			if (task.isRunning()) {
				task.cancel();
				if (DPXFileList.size() > 0) {
					DPXFileList.removeAll(DPXFileList);
					notDPXFileList.removeAll(notDPXFileList);
					DPXFailures.removeAll(DPXFailures);
					ControllerMediator.getInstance().refetchFileList();
				}
			}
		});

		new Thread(task).start();

		task.setOnSucceeded(e -> {

			// remove DPX failures from DPXFileList for reporting purposes
			if (DPXFailures.size() > 0) {
				for (final String f : DPXFailures) {
					DPXFileList.remove(f);
				}
			}

			// create file processing report labels for ProgressDialog
			final List<Label> labels = new ArrayList<>();
			final int total = DPXFileList.size() + DPXFailures.size() + notDPXFileList.size();
			labels.add(new Label("DPX Successes: " + DPXFileList.size()));
			labels.add(new Label("DPX Failures: " + DPXFailures.size()));
			labels.add(new Label("Non-DPX Ignored: " + notDPXFileList.size()));
			final Label totalProcessed = new Label("Total Files Processed: " + total);
			labels.add(totalProcessed);
			progressDialog.showLabels(labels);
			progressDialog.showCloseButton();

			addFilesOverlayPane.setStyle("-fx-background-color: transparent;");
			addFilesOverlayPane.setMouseTransparent(true);
			greyDragOver = false;

			// fetch the new file list if any DPX files were added
			if (DPXFileList.size() > 0) {
				DPXFileList.removeAll(DPXFileList);
				notDPXFileList.removeAll(notDPXFileList);
				DPXFailures.removeAll(DPXFailures);
				ControllerMediator.getInstance().refetchFileList();
				System.gc();
			}

			if (event != null) {
				event.consume();
			}
		});
		task.setOnFailed(e -> {
			// remove DPX failures from DPXFileList for reporting purposes
			if (DPXFailures.size() > 0) {
				for (final String f : DPXFailures) {
					DPXFileList.remove(f);
				}
			}

			// create file processing report labels for ProgressDialog
			final List<Label> labels = new ArrayList<>();
			final int total = DPXFileList.size() + DPXFailures.size() + notDPXFileList.size();
			labels.add(new Label("DPX Successes: " + DPXFileList.size()));
			labels.add(new Label("DPX Failures: " + DPXFailures.size()));
			labels.add(new Label("Non-DPX Ignored: " + notDPXFileList.size()));
			final Label totalProcessed = new Label("Total Files Processed: " + total);
			labels.add(totalProcessed);
			progressDialog.showLabels(labels);
			progressDialog.showCloseButton();

			// fetch the new file list if any DPX files were added
			if (DPXFileList.size() > 0) {
				DPXFileList.removeAll(DPXFileList);
				notDPXFileList.removeAll(notDPXFileList);
				DPXFailures.removeAll(DPXFailures);
				ControllerMediator.getInstance().refetchFileList();
				System.gc();
			}

			if (event != null) {
				event.consume();
			}
		});
	}

	public void refetchFileList() {
		ControllerMediator.getInstance().setFileList();
	}

	public void showAboutModal() {
		final Stage aboutStage = new Stage();
		aboutStage.initOwner(Main.getPrimaryStage());
		aboutStage.setHeight(450);
		aboutStage.setWidth(670);
		aboutStage.initStyle(StageStyle.UTILITY);
		aboutStage.setResizable(false);
		aboutStage.setTitle("About embARC");

		final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AboutModal.fxml"));
		fxmlLoader.setController(this);

		try {
			final Scene scene = new Scene(fxmlLoader.load());
			aboutStage.setScene(scene);
			aboutStage.show();
			fadgiLink.setBorder(Border.EMPTY);
			fadgiLink.setPadding(new Insets(4, 0, 4, 0));
			fadgiLink.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent t) {
					URI uri;
					try {
						uri = new URI(fadgiLink.getText());
						openUrl(uri);
					} catch (final URISyntaxException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (final IOException exception) {
			throw new RuntimeException(exception);
		}

		closeAboutButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				aboutStage.close();
			}
		});

		aboutButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				aboutPane.setVisible(true);
				aboutPane.setPrefHeight(350);
				licensePane.setVisible(false);
				licensePane.setPrefHeight(0);
			}
		});

		licenseButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				aboutPane.setVisible(false);
				aboutPane.setPrefHeight(0);
				licensePane.setVisible(true);
				licensePane.setPrefHeight(350);
			}
		});
	}

	public void showRuleSetsDialog() {
		final Alert ruleSetsDialog = new Alert(AlertType.INFORMATION, "", ButtonType.OK);
		ruleSetsDialog.setResizable(true);
		ruleSetsDialog.setGraphic(null);
		ruleSetsDialog.setTitle("Rule Sets");
		ruleSetsDialog.setHeaderText(null);
		ruleSetsDialog.setContentText("Toggle rule sets");
		final List<CheckBox> checkBoxes = createRuleSetCheckboxes();
		if (checkBoxes.size() > 0) {
			final GridPane grid = new GridPane();
			grid.setHgap(20);
			grid.setVgap(20);
			grid.setPadding(new Insets(20, 50, 20, 20));
			int rowIndex = 0;
			for (final CheckBox cb : checkBoxes) {
				final Text sectionHead = new Text(cb.getId());
				sectionHead.setStyle("fx-font-weight: bold");
				grid.add(sectionHead, 0, rowIndex);
				grid.add(cb, 1, rowIndex);
				rowIndex++;
			}
			ruleSetsDialog.getDialogPane().setContent(grid);
		} else {
			ruleSetsDialog.setContentText("No files in workspace.");
		}
		ruleSetsDialog.initOwner(mainViewPane.getScene().getWindow());
		ruleSetsDialog.showAndWait();
		if (ruleSetsDialog.getResult() == ButtonType.OK) {
			ruleSetsDialog.close();
		}
	}

	public void showWriteFilesDialog() {
		final ChoiceDialog<ButtonData> dialog = new ChoiceDialog<>();
		final FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.DOWNLOAD);
		icon.setStyleClass("write-files-icon");
		dialog.setGraphic(icon);
		dialog.getDialogPane().setPrefSize(525, 320);
		dialog.setTitle("Write Files");
		dialog.setHeaderText("Write Files to Disk");
		dialog.initOwner(mainViewPane.getScene().getWindow());

		// Set the button types.
		final ButtonType loginButtonType = new ButtonType("Close", ButtonData.CANCEL_CLOSE);
		final ButtonType writeFilesButtonType = new ButtonType("Write Files", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().setAll(loginButtonType, writeFilesButtonType);

		dialog.setResultConverter((ButtonType type) -> {
			final ButtonBar.ButtonData data = type == null ? null : type.getButtonData();
			return data;
		});

		// Create the username and password labels and fields.
		final GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 10, 10, 10));

		final CheckBox writeEditedCb = new CheckBox("Write only edited files");
		writeEditedCb.setSelected(true);

		final CheckBox reportCb = new CheckBox("Create an image data checksum report");
		reportCb.setSelected(true);

		final CheckBox saveAsCb = new CheckBox("Save Files to a separate directory");

		final Label reportPath = new Label(System.getProperty("user.home") + "/" + "ImageDataChecksumComparisonReport_"
				+ new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + ".csv");
		final Button selectReportPathButton = new Button();
		selectReportPathButton.setText("Save Checksums To...");
		selectReportPathButton.setPrefWidth(125);

		selectReportPathButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				final FileChooser fileChooser = new FileChooser();
				fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
				fileChooser.setInitialFileName(
						"ChecksumReport" + "_" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + ".csv");
				fileChooser.setTitle("Select a file");
				final File file = fileChooser.showSaveDialog(null);
				if (file != null) {
					reportPath.setText(file.getAbsolutePath());
				}
			}
		});

		final Label writeFilesPath = new Label(System.getProperty("user.home"));
		final Button chooseOutputDirButton = new Button();
		chooseOutputDirButton.setText("Save Files To...");
		chooseOutputDirButton.setPrefWidth(125);
		chooseOutputDirButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				final DirectoryChooser dirChooser = new DirectoryChooser();
				dirChooser.setInitialDirectory(new File(System.getProperty("user.home")));
				dirChooser.setTitle("Select a directory");
				final File dir = dirChooser.showDialog(null);
				if (dir != null) {
					writeFilesPath.setText(dir.getAbsolutePath());
				}
			}
		});
		selectReportPathButton.setDisable(!reportCb.isSelected());
		chooseOutputDirButton.setDisable(true);
		reportPath.setDisable(true);
		writeFilesPath.setDisable(true);
		reportCb.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				// reportCb.setSelected(!newValue);
				selectReportPathButton.setDisable(!newValue);
				reportPath.setDisable(!newValue);
			}
		});
		saveAsCb.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				// saveAsCb.setSelected(!newValue);
				chooseOutputDirButton.setDisable(!newValue);
				writeFilesPath.setDisable(!newValue);
			}
		});
		grid.add(writeEditedCb, 0, 0, 2, 1);
		grid.add(reportCb, 0, 1, 2, 1);
		grid.add(selectReportPathButton, 0, 2);
		grid.add(reportPath, 1, 2);
		grid.add(saveAsCb, 0, 3, 2, 1);
		grid.add(chooseOutputDirButton, 0, 4);
		grid.add(writeFilesPath, 1, 4);

		dialog.getDialogPane().setContent(grid);
		final Optional<ButtonData> result = dialog.showAndWait();
		if (result.isPresent()) {
			if (result.get() == ButtonData.OK_DONE) {
				final String tmpWriteFilesPath = saveAsCb.isSelected() ? writeFilesPath.getText() : "";
				final String tmpReportPath = reportCb.isSelected() ? reportPath.getText() : "";

				final WriteFilesDialog d = new WriteFilesDialog(tmpWriteFilesPath, tmpReportPath,
						writeEditedCb.isSelected());
				mainViewPane.getChildren().add(d);
			}
		}

	}

	public void uploadFiles() {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select a file");
		final List<File> files = fileChooser.showOpenMultipleDialog(mainViewPane.getScene().getWindow());

		if (files != null && !files.isEmpty()) {
			totalItemsCount = files.size();
			processFiles(files, null);
		}
	}

	public void uploadFilesFromDirectory() {
		final DirectoryChooser fileChooser = new DirectoryChooser();
		fileChooser.setTitle("Select a Directory");
		final File file = fileChooser.showDialog(mainViewPane.getScene().getWindow());

		if (file != null) {
			// count total items found
			totalItemsCount = countFiles(file);

			final List<File> files = new ArrayList<>();
			files.add(file);
			processFiles(files, null);
		}
	}
}
