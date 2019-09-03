package com.portalmedia.embarc.gui;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.controlsfx.control.textfield.CustomTextField;

import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * Large text input area for editable ASCII fields
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class ASCIIField extends AnchorPane implements IEditorField {
	@FXML
	private HBox ASCIIHBox;
	@FXML
	private CustomTextField editorTextField;
	@FXML
	private Label editorTextFieldLabel;
	@FXML
	private FontAwesomeIconView popoutIcon;

	DPXColumn column;
	HashSet<ValidationRuleSetEnum> validationRuleSetEnum;
	private String originalValue;

	public ASCIIField() {
		final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ASCIIField.fxml"));
		fxmlLoader.setController(this);
		fxmlLoader.setRoot(this);
		try {
			fxmlLoader.load();
		} catch (final IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.portalmedia.embarc.gui.IEditorField#clearValidation()
	 */
	@Override
	public void clearValidation() {
		editorTextField.setRight(null);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.portalmedia.embarc.gui.IEditorField#getColumn()
	 */
	@Override
	public DPXColumn getColumn() {
		return column;
	}

	@Override
	public String getValue() {
		return editorTextField.getText();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.portalmedia.embarc.gui.IEditorField#resetValueChanged()
	 */
	@Override
	public void resetValueChanged() {
		originalValue = null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.portalmedia.embarc.gui.IEditorField#setColumn(DPXColumn)
	 */
	@Override
	public void setColumn(DPXColumn column) {
		if (this.column == null) {
			editorTextField.textProperty()
					.addListener(new MaxLengthChangeListener(editorTextField, column.getLength()));
			editorTextField.textProperty().addListener(new ValidationChangeListener(editorTextField, column));
		}
		this.column = column;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.portalmedia.embarc.gui.IEditorField#setEditable(boolean)
	 */
	@Override
	public void setEditable(boolean editable) {
		editorTextField.setEditable(editable);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.portalmedia.embarc.gui.IEditorField#setInvalidRuleSets(java.util.Set)
	 */
	@Override
	public void setInvalidRuleSets(Set<ValidationRuleSetEnum> invalidRuleSet) {
		final ValidationWarningIcons icons = new ValidationWarningIcons();
		icons.setInvalidRuleSets(invalidRuleSet, getColumn());
		AnchorPane.setBottomAnchor(icons, 0.0);
		AnchorPane.setTopAnchor(icons, 0.0);
		editorTextField.setRight(icons);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.portalmedia.embarc.gui.IEditorField#setLabel(java.lang.String)
	 */
	@Override
	public void setLabel(String text) {
		editorTextFieldLabel.setText(text);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.portalmedia.embarc.gui.IEditorField#setLabel(java.lang.String)
	 */
	@Override
	public void setLabel(String labelText, String helpText) {
		final Tooltip tt = new Tooltip(labelText + "\n\n" + helpText);
		tt.setStyle("-fx-text-fill: white; -fx-font-size: 12px");
		tt.setPrefWidth(500);
		tt.setWrapText(true);
		tt.setAutoHide(false);
		editorTextFieldLabel.setText(labelText);
		editorTextFieldLabel.setTooltip(tt);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.portalmedia.embarc.gui.IEditorField#setPopoutIcon()
	 */
	@Override
	public void setPopoutIcon() {
		popoutIcon.setVisible(true);
		popoutIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				final Alert alert = new Alert(AlertType.NONE);
				alert.setTitle("Edit " + editorTextFieldLabel.getText());
				alert.setHeaderText(null);
				alert.setContentText(null);

				final ButtonType[] buttonList = new ButtonType[1];
				if (column.getEdiable()) {
					buttonList[0] = ButtonType.APPLY;
				} else {
					buttonList[0] = ButtonType.CLOSE;
				}
				alert.getButtonTypes().setAll(buttonList);

				final Label label = new Label(editorTextFieldLabel.getText());

				final TextArea textArea = new TextArea(editorTextField.getText());
				textArea.setEditable(true);
				textArea.setWrapText(true);

				textArea.setMaxWidth(Double.MAX_VALUE);
				textArea.setMaxHeight(Double.MAX_VALUE);
				GridPane.setVgrow(textArea, Priority.ALWAYS);
				GridPane.setHgrow(textArea, Priority.ALWAYS);

				final GridPane expContent = new GridPane();
				expContent.setMaxWidth(Double.MAX_VALUE);
				expContent.add(label, 0, 0);
				expContent.add(textArea, 0, 1);
				alert.getDialogPane().setContent(expContent);

				alert.showAndWait();
				if (alert.getResult() == ButtonType.APPLY) {
					final String value = textArea.getText();
					editorTextField.setText(value);
					alert.close();
				} else if (alert.getResult() == ButtonType.CLOSE) {
					alert.close();
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.portalmedia.embarc.gui.IEditorField#setValue(java.lang.String)
	 */
	@Override
	public void setValue(String value) {
		editorTextField.setText(value);
		if (originalValue == null) {
			originalValue = editorTextField.getText();
		}
	}

	public StringProperty textProperty() {
		return editorTextField.textProperty();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.portalmedia.embarc.gui.IEditorField#valueChanged()
	 */
	@Override
	public boolean valueChanged() {
		if (originalValue == null) {
			return false;
		}
		//System.out.println("Orig: " + originalValue.trim() + " New: " + editorTextField.getText().trim())
		if (originalValue.trim().equals(editorTextField.getText().trim())) {
			return false;
		}

		return true;
	}

}
