package com.portalmedia.embarc.gui.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.parser.dpx.DPXSection;
import com.portalmedia.embarc.validation.IValidationRule;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

import javafx.collections.ObservableList;

/**
 * Collects and reports violations based on the tab sections of the tableview.
 * Stores a list of sections that have a violation
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class TabSummary {
	private static TabSummary tabSummary;

	public static void append(FileInformationViewModel fivm, HashSet<ValidationRuleSetEnum> ruleSets) {
		for (final DPXColumn column : DPXColumn.values()) {
			if (tabSummary.hasSectionViolation(column.getSection())) {
				continue;
			}
			String value = fivm.getProp(column);
			if (value == null) {
				value = "";
			}

			final HashMap<ValidationRuleSetEnum, List<IValidationRule>> ruleViolations = fivm.getRuleViolations(column);
			if (ruleViolations != null && !ruleViolations.isEmpty()) {
				for (final ValidationRuleSetEnum rule : ruleSets) {
					if (ruleViolations.containsKey(rule)) {
						tabSummary.addSectionViolation(column.getSection());
					}
				}
			}
		}
	}

	// Static methods for creating the summary
	public static TabSummary create(ObservableList<FileInformationViewModel> files,
			HashSet<ValidationRuleSetEnum> ruleSets) {
		final TabSummary ts = new TabSummary();

		if (files == null || files.size() == 0) {
			return ts;
		}

		for (final FileInformationViewModel fivm : files) {
			for (final DPXColumn column : DPXColumn.values()) {
				if (ts.hasSectionViolation(column.getSection())) {
					continue;
				}
				String value = fivm.getProp(column);
				if (value == null) {
					value = "";
				}

				final HashMap<ValidationRuleSetEnum, List<IValidationRule>> ruleViolations = fivm
						.getRuleViolations(column);
				if (ruleViolations != null && !ruleViolations.isEmpty()) {
					for (final ValidationRuleSetEnum rule : ruleSets) {
						if (ruleViolations.containsKey(rule)) {
							ts.addSectionViolation(column.getSection());
						}
					}
				}
			}
		}
		return ts;
	}

	public static TabSummary getTabSummary() {
		return tabSummary;
	}

	public static void start() {
		tabSummary = new TabSummary();
	}

	public HashSet<DPXSection> sectionViolations;

	public TabSummary() {
		sectionViolations = new HashSet<>();
	}

	public void addSectionViolation(DPXSection section) {
		sectionViolations.add(section);
	}

	public boolean hasSectionViolation(DPXSection section) {
		return sectionViolations.contains(section);
	}

	public int sectionViolationCount() {
		return sectionViolations.size();
	}

}
