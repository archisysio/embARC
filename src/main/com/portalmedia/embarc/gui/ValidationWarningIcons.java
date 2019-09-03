package com.portalmedia.embarc.gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.validation.DPXColumnValidationRules;
import com.portalmedia.embarc.validation.IValidationRule;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

/**
 * Displays rule set violation warning icons on invalid data fields
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class ValidationWarningIcons extends AnchorPane {
	@FXML
	private HBox warningIcons;

	public ValidationWarningIcons() {
		final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ValidationWarningIcons.fxml"));
		fxmlLoader.setController(this);
		fxmlLoader.setRoot(this);
		try {
			fxmlLoader.load();
		} catch (final IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	public void setInvalidRuleSets(Set<ValidationRuleSetEnum> invalidRuleSet) {
		// TODO: if column isn't available when this is called (see
		// centerPaneController) there will be no tooltip
		if (invalidRuleSet == null) {
			return;
		}
		for (final ValidationRuleSetEnum rule : invalidRuleSet) {
			if (rule == ValidationRuleSetEnum.FADGI_O) {
				final FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
				icon.setStyleClass("fadgi-o-warning");
				warningIcons.getChildren().add(icon);
			} else if (rule == ValidationRuleSetEnum.FADGI_R) {
				final FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
				icon.setStyleClass("fadgi-r-warning");
				warningIcons.getChildren().add(icon);
			} else if (rule == ValidationRuleSetEnum.FADGI_SR) {
				final FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
				icon.setStyleClass("fadgi-sr-warning");
				warningIcons.getChildren().add(icon);
			} else if (rule == ValidationRuleSetEnum.SMPTE_C) {
				final FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
				icon.setStyleClass("smpte-c-warning");
				warningIcons.getChildren().add(icon);
			}
		}
	}

	public void setInvalidRuleSets(Set<ValidationRuleSetEnum> invalidRuleSet, DPXColumn fieldName) {

		final HashMap<ValidationRuleSetEnum, List<IValidationRule>> validationRuleSet = DPXColumnValidationRules
				.getInstance().getRuleSet(fieldName);

		String ruleText = "No rule set";

		for (final ValidationRuleSetEnum rule : invalidRuleSet) {
			if (rule == ValidationRuleSetEnum.FADGI_O) {
				for (final ValidationRuleSetEnum key : validationRuleSet.keySet()) {
					if (key == rule) {
						for (final IValidationRule vRule : validationRuleSet.get(key)) {
							ruleText = vRule.getRule();
						}
					}
				}
				final FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
				icon.setStyleClass("fadgi-o-warning");
				final Tooltip tt = new Tooltip(ruleText);
				tt.setStyle("-fx-text-fill: white; -fx-font-size: 12px");
				Tooltip.install(icon, tt);
				warningIcons.getChildren().add(icon);
			} else if (rule == ValidationRuleSetEnum.FADGI_R) {
				for (final ValidationRuleSetEnum key : validationRuleSet.keySet()) {
					if (key == rule) {
						for (final IValidationRule vRule : validationRuleSet.get(key)) {
							ruleText = vRule.getRule();
						}
					}
				}
				final FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
				icon.setStyleClass("fadgi-r-warning");
				final Tooltip tt = new Tooltip(ruleText);
				tt.setStyle("-fx-text-fill: white;");
				Tooltip.install(icon, tt);
				warningIcons.getChildren().add(icon);
			} else if (rule == ValidationRuleSetEnum.FADGI_SR) {
				for (final ValidationRuleSetEnum key : validationRuleSet.keySet()) {
					if (key == rule) {
						for (final IValidationRule vRule : validationRuleSet.get(key)) {
							ruleText = vRule.getRule();
						}
					}
				}
				final FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
				icon.setStyleClass("fadgi-sr-warning");
				final Tooltip tt = new Tooltip(ruleText);
				tt.setStyle("-fx-text-fill: white;");
				Tooltip.install(icon, tt);
				warningIcons.getChildren().add(icon);
			} else if (rule == ValidationRuleSetEnum.SMPTE_C) {
				for (final ValidationRuleSetEnum key : validationRuleSet.keySet()) {
					if (key == rule) {
						for (final IValidationRule vRule : validationRuleSet.get(key)) {
							ruleText = vRule.getRule();
						}
					}
				}
				final FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
				icon.setStyleClass("smpte-c-warning");
				final Tooltip tt = new Tooltip(ruleText);
				tt.setStyle("-fx-text-fill: white;");
				Tooltip.install(icon, tt);
				warningIcons.getChildren().add(icon);
			}
		}
	}
}
