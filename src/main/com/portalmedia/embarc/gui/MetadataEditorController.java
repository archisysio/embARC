package com.portalmedia.embarc.gui;

import java.net.URL;
import java.util.ResourceBundle;

import com.portalmedia.embarc.parser.dpx.DPXSection;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;

/**
 * Editor pane controller
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
@SuppressWarnings({ "unchecked" })
public class MetadataEditorController implements Initializable {

	@FXML
	private AnchorPane metadataEditorContainer;
	@FXML
	private AnchorPane editorContentSwapPane;
	@FXML
	private AnchorPane editorPane;
	@FXML
	private Tab editableTab;
	@FXML
	private Tab nonEditableTab;
	@FXML
	private ListView<Label> editableListView;
	@FXML
	private ListView<Label> nonEditableListView;
	@FXML
	private Button writeFilesButton;
	EditorForm pane1;
	GeneralForm pane2;

	@SuppressWarnings("rawtypes")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ControllerMediator.getInstance().registerMetadataEditorController(this);
		ControllerMediator.getInstance().isEditingProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue o, Object ov, Object nv) {
				if (pane1 == null) {
					return;
				} else {
					writeFilesButton.setDisable((boolean) nv);
				}
			}
		});

		writeFilesButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				ControllerMediator.getInstance().showWriteFilesDialog();
			}
		});

		writeFilesButton.setGraphicTextGap(20);

	}

	public void refreshValidation() {
		if (pane1 != null) {
			pane1.refreshValidation();
		}
	}

	public void resetEditControl(DPXSection section) {
		editorContentSwapPane.getChildren().removeAll(editorContentSwapPane.getChildren());
		setWriteControl(section);
	}

	public void setEditControl(DPXSection section) {
		pane1 = new EditorForm();
		if (pane2 != null) {
			pane2.setVisible(false);
		}
		pane1.setVisible(true);
		if (editorContentSwapPane.getChildren().size() > 0) {
			editorContentSwapPane.getChildren().removeAll(editorContentSwapPane.getChildren());
		}
		pane1.setSection(section, false);
		pane1.setTitle(section.getDisplayName());
		pane1.setMaxWidth(Double.MAX_VALUE);
		AnchorPane.setTopAnchor(pane1, 0.0);
		AnchorPane.setLeftAnchor(pane1, 0.0);
		AnchorPane.setRightAnchor(pane1, 0.0);
		AnchorPane.setBottomAnchor(pane1, 0.0);

		editorContentSwapPane.getChildren().setAll(pane1);
	}

	public void setGeneralControl() {
		pane2 = new GeneralForm();
		pane2.setVisible(true);
		if (pane1 != null) {
			pane1.setVisible(false);
		}
		if (editorContentSwapPane.getChildren().size() > 0) {
			editorContentSwapPane.getChildren().removeAll(editorContentSwapPane.getChildren());
		}
		pane2 = new GeneralForm();
		pane2.setTitle("General Information");
		pane2.setMaxWidth(Double.MAX_VALUE);
		AnchorPane.setTopAnchor(pane2, 0.0);
		AnchorPane.setLeftAnchor(pane2, 0.0);
		AnchorPane.setRightAnchor(pane2, 0.0);
		AnchorPane.setBottomAnchor(pane2, 0.0);

		editorContentSwapPane.getChildren().setAll(pane2);
	}

	public void setWriteControl(DPXSection section) {
		final WriteFilesView pane1 = new WriteFilesView();
		pane1.setMaxWidth(Double.MAX_VALUE);
		pane1.setMessage(section);
		AnchorPane.setTopAnchor(pane1, 0.0);
		AnchorPane.setLeftAnchor(pane1, 0.0);
		AnchorPane.setRightAnchor(pane1, 0.0);
		AnchorPane.setBottomAnchor(pane1, 0.0);

		editorContentSwapPane.getChildren().setAll(pane1);

	}
}