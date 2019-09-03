package com.portalmedia.embarc.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.controlsfx.control.textfield.CustomTextField;

import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.validation.DPXColumnValidationRules;
import com.portalmedia.embarc.validation.IValidationRule;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

/**
 * Sets, determines, and reports metadata rule violations
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class ValidationChangeListener implements javafx.beans.value.ChangeListener<String> {

	private CustomTextField textField;
	DPXColumn column;

	public ValidationChangeListener(CustomTextField textField, DPXColumn column) {
		this.textField = textField;
		this.column = column;
	}

	public ValidationChangeListener(TextArea textArea, DPXColumn column) {
		this.column = column;
	}

	@Override
	public void changed(ObservableValue<? extends String> ov, String oldValue, String newValue) {
		if (newValue == null) {
			return;
		}

		final HashMap<ValidationRuleSetEnum, List<IValidationRule>> validationRules = DPXColumnValidationRules
				.getInstance().getRules(column);
		final HashSet<ValidationRuleSetEnum> activeRuleSets = ControllerMediator.getInstance().getCurrentRuleSets();
		if (validationRules.size() > 0) {
			final Set<ValidationRuleSetEnum> violatedRules = new HashSet<>();
			for (final ValidationRuleSetEnum ruleSet : validationRules.keySet()) {
				if (!activeRuleSets.contains(ruleSet)) {
					continue;
				}
				for (final IValidationRule rule : validationRules.get(ruleSet)) {
					if (!rule.isValid(newValue)) {
						violatedRules.add(ruleSet);
						break;
					}
				}
			}
			setInvalidRuleSets(violatedRules);
		}
	}

	public void setInvalidRuleSets(Set<ValidationRuleSetEnum> invalidRuleSet) {
		final ValidationWarningIcons icons = new ValidationWarningIcons();
		icons.setInvalidRuleSets(invalidRuleSet, column);
		AnchorPane.setBottomAnchor(icons, 0.0);
		AnchorPane.setTopAnchor(icons, 0.0);
		if (textField != null) {
			textField.setRight(icons);
		}
	}
}
