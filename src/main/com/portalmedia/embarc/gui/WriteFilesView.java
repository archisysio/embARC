package com.portalmedia.embarc.gui;

import java.io.IOException;
import java.util.HashSet;

import com.portalmedia.embarc.gui.model.DatabaseSummary;
import com.portalmedia.embarc.parser.dpx.DPXSection;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Editor pane view when no files are selected. Displays rule set check boxes
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class WriteFilesView extends AnchorPane {

	@FXML
	private AnchorPane writeFilesSummaryPane;
	DPXSection section;
	CheckBox smptcCB;
	CheckBox fsrCB;
	CheckBox frCB;
	CheckBox foCB;

	public WriteFilesView() {
		final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("WriteFilesView.fxml"));
		fxmlLoader.setController(this);
		fxmlLoader.setRoot(this);
		try {
			fxmlLoader.load();
		} catch (final IOException exception) {
			throw new RuntimeException(exception);
		}
		ControllerMediator.getInstance().registerWriteFilesView(this);
		smptcCB = new CheckBox();
		fsrCB = new CheckBox();
		frCB = new CheckBox();
		foCB = new CheckBox();
		resetValidationRuleIndicators();
	}

	public void resetValidationRuleIndicators() {
		final HashSet<ValidationRuleSetEnum> selectedRules = ControllerMediator.getInstance().getCurrentRuleSets();

		if (selectedRules.contains(ValidationRuleSetEnum.SMPTE_C)) {
			smptcCB.setSelected(true);
		} else {
			smptcCB.setSelected(false);
		}

		if (selectedRules.contains(ValidationRuleSetEnum.FADGI_SR)) {
			fsrCB.setSelected(true);
		} else {
			fsrCB.setSelected(false);
		}

		if (selectedRules.contains(ValidationRuleSetEnum.FADGI_R)) {
			frCB.setSelected(true);
		} else {
			frCB.setSelected(false);
		}

		if (selectedRules.contains(ValidationRuleSetEnum.FADGI_O)) {
			foCB.setSelected(true);
		} else {
			foCB.setSelected(false);
		}

	}

	public void setMessage(DPXSection section) {
		final VBox header = new VBox();
		header.getStyleClass().add("editor-header-box");
		header.setPrefWidth(70);
		AnchorPane.setTopAnchor(header, 0.00);
		AnchorPane.setLeftAnchor(header, 0.00);
		AnchorPane.setRightAnchor(header, 0.00);
		final Label l = new Label("File Summary \rSelect one or more files to begin editing");
		l.prefHeight(25);
		l.getStyleClass().add("editor-header");
		header.getChildren().add(l);

		writeFilesSummaryPane.getChildren().add(header);
		final GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(75, 25, 25, 25));

		writeFilesSummaryPane.getChildren().add(grid);

		final Label totalFilesLabel = new Label("Total Files:");
		final FontAwesomeIconView icon0 = new FontAwesomeIconView(FontAwesomeIcon.FILE_IMAGE_ALT);
		totalFilesLabel.setGraphic(icon0);
		grid.add(totalFilesLabel, 1, 0);

		final Label totalFilesText = new Label(String.valueOf(DatabaseSummary.getFileCount()));
		grid.add(totalFilesText, 2, 0);

		final EventHandler<ActionEvent> cbEventHandler = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (event.getSource() instanceof CheckBox) {
					final CheckBox cb = (CheckBox) event.getSource();
					final String id = cb.getId();
					if ("SMPTE_C".equals(id)) {
						ControllerMediator.getInstance().toggleRuleSet(ValidationRuleSetEnum.SMPTE_C);
					} else if ("FADGI_SR".equals(id)) {
						ControllerMediator.getInstance().toggleRuleSet(ValidationRuleSetEnum.FADGI_SR);
					} else if ("FADGI_R".equals(id)) {
						ControllerMediator.getInstance().toggleRuleSet(ValidationRuleSetEnum.FADGI_R);
					} else if ("FADGI_O".equals(id)) {
						ControllerMediator.getInstance().toggleRuleSet(ValidationRuleSetEnum.FADGI_O);
					}
				}
			}
		};

		smptcCB.setId("SMPTE_C");
		smptcCB.setOnAction(cbEventHandler);
		grid.add(smptcCB, 0, 1);

		final Label smptc = new Label("Files with SMPTE-C Violations:");
		final FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
		icon.setStyleClass("smpte-c-warning");
		smptc.setGraphic(icon);

		grid.add(smptc, 1, 1);
		final int violations = DatabaseSummary.getErrorCount(ValidationRuleSetEnum.SMPTE_C);
		final Label smptcText = new Label(String.valueOf(violations));
		grid.add(smptcText, 2, 1);

		fsrCB.setId("FADGI_SR");
		fsrCB.setOnAction(cbEventHandler);
		grid.add(fsrCB, 0, 2);

		final Label fsr = new Label("Files with FADGI-SR Violations:");
		final FontAwesomeIconView icon1 = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
		icon1.setStyleClass("fadgi-sr-warning");
		fsr.setGraphic(icon1);
		grid.add(fsr, 1, 2);

		final Label fsrText = new Label(String.valueOf(DatabaseSummary.getErrorCount(ValidationRuleSetEnum.FADGI_SR)));
		grid.add(fsrText, 2, 2);

		frCB.setId("FADGI_R");
		frCB.setOnAction(cbEventHandler);
		grid.add(frCB, 0, 3);

		final Label fr = new Label("Files with FADGI-R Violations:");
		final FontAwesomeIconView icon2 = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
		icon2.setStyleClass("fadgi-r-warning");
		fr.setGraphic(icon2);
		grid.add(fr, 1, 3);

		final Label frText = new Label(String.valueOf(DatabaseSummary.getErrorCount(ValidationRuleSetEnum.FADGI_R)));
		grid.add(frText, 2, 3);

		foCB.setId("FADGI_O");
		foCB.setOnAction(cbEventHandler);
		grid.add(foCB, 0, 4);

		final Label fo = new Label("Files with FADGI-O Violations:");
		final FontAwesomeIconView icon3 = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
		icon3.setStyleClass("fadgi-o-warning");
		fo.setGraphic(icon3);
		grid.add(fo, 1, 4);

		final Label foText = new Label(String.valueOf(DatabaseSummary.getErrorCount(ValidationRuleSetEnum.FADGI_O)));
		grid.add(foText, 2, 4);
	}

}
