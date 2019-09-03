package com.portalmedia.embarc.gui;

import java.util.HashMap;
import java.util.HashSet;

import com.portalmedia.embarc.gui.model.FileInformationViewModel;
import com.portalmedia.embarc.gui.model.SelectedFilesSummary;
import com.portalmedia.embarc.parser.dpx.DPXColumn;
import com.portalmedia.embarc.parser.dpx.DPXSection;
import com.portalmedia.embarc.validation.ValidationRuleSetEnum;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

/**
 * Facilitates communication between UI components controllers
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public class ControllerMediator implements IMediateControllers {

	private MainViewController mainViewController;
	private CenterPaneController centerPaneController;
	private MetadataEditorController metadataEditorController;
	private WorkingSummaryController workingSummaryController;
	private EditorForm editorForm;
	private WriteFilesView writeFilesView;
	private final HashSet<ValidationRuleSetEnum> validationRulesSelected = new HashSet<>();
	private final BooleanProperty isEditing = new SimpleBooleanProperty();

	private static class ControllerMediatorHolder {
		private static final ControllerMediator INSTANCE = new ControllerMediator();
	}

	private ControllerMediator() {
		validationRulesSelected.add(ValidationRuleSetEnum.SMPTE_C);
	}

	public static ControllerMediator getInstance() {
		return ControllerMediatorHolder.INSTANCE;
	}

	public void createImageChecksumReport() {
		mainViewController.createImageReport();
	}

	public void createReport() {
		mainViewController.createReport();
	}

	@Override
	public void deleteSelectedFiles() {
		centerPaneController.deleteSelectedFiles();
	}

	@Override
	public void deselectAllFiles() {
		centerPaneController.deselectAllFiles();
	}

	@Override
	public int getCurrentFileCount() {
		return centerPaneController.getTableSize();
	}

	public HashSet<ValidationRuleSetEnum> getCurrentRuleSets() {
		return validationRulesSelected;
	}

	public int getEditedFieldsCount() {
		return editorForm.getEditedFieldsCount();
	}

	public ObservableList<FileInformationViewModel> getSelectedFileList() {
		return centerPaneController.getSelectedFiles();
	}

	public SelectedFilesSummary getSelectedFilesSummary() {
		return centerPaneController.getSelectedFilesSummary();
	}

	public TableView<FileInformationViewModel> getTable() {
		return centerPaneController.getTable();
	}

	public BooleanProperty isEditingProperty() {
		return isEditing;
	}

	public void notifyColumnsSet() {
	}

	@Override
	public void refetchFileList() {
		mainViewController.refetchFileList();
		centerPaneController.setSelectedRuleSets(getCurrentRuleSets());
		centerPaneController.setTabWarnings();
		centerPaneController.refreshEditor(true);
	}

	@Override
	public void registerCenterPaneController(CenterPaneController controller) {
		centerPaneController = controller;
	}

	@Override
	public void registerEditorForm(EditorForm controller) {
		editorForm = controller;
	}

	@Override
	public void registerMainViewController(MainViewController controller) {
		mainViewController = controller;
	}

	@Override
	public void registerMetadataEditorController(MetadataEditorController controller) {
		metadataEditorController = controller;
	}

	@Override
	public void registerWorkingSummaryController(WorkingSummaryController controller) {
		workingSummaryController = controller;
	}

	@Override
	public void registerWriteFilesView(WriteFilesView controller) {
		writeFilesView = controller;
	}

	public void resetEditor(DPXSection section) {
		metadataEditorController.resetEditControl(section);
	}

	public void resetValidationRuleIndicators() {
		writeFilesView.resetValidationRuleIndicators();
	}

	@Override
	public void selectAllFiles() {
		centerPaneController.selectAllFiles();
	}

	public void setEditedFieldsCount(int count) {
		editorForm.setEditedFieldsCount(count);
	}

	public void setEditor(DPXSection section) {
		metadataEditorController.setEditControl(section);
	}

	@Override
	public void setFileList() {
		centerPaneController.setFiles();
		workingSummaryController.setFiles();
	}

	@Override
	public void setFiles() {
		workingSummaryController.setFiles();
	}

	public void setGeneralEditor() {
		metadataEditorController.setGeneralControl();
	}

	@Override
	public void setSelectedFileList(ObservableList<FileInformationViewModel> list) {
		workingSummaryController.setSelectedFileList(list);
	}

	public void showAboutModal() {
		mainViewController.showAboutModal();
	}

	public void showColumnVisibilityDialogue() {
		centerPaneController.showColumnVisibilityDialogue();
	}

	public void showRuleSetsDialogue() {
		mainViewController.showRuleSetsDialog();
	}

	public void showWriteFilesDialog() {
		mainViewController.showWriteFilesDialog();
	}

	public int toggleErrorsOnlyFilter(Boolean isChecked) {
		if (isChecked) {
			final int numWithViolations = centerPaneController.FilterByViolations();
			return numWithViolations;
		} else {
			centerPaneController.ClearFilterByViolations();
			return 0;
		}
	}

	public void toggleRuleSet(ValidationRuleSetEnum ruleSet) {
		if (validationRulesSelected.contains(ruleSet)) {
			validationRulesSelected.remove(ruleSet);
		} else {
			validationRulesSelected.add(ruleSet);
		}

		centerPaneController.setSelectedRuleSets(validationRulesSelected);
		centerPaneController.refreshValidation();
		metadataEditorController.refreshValidation();
		workingSummaryController.resetErrorCount();
		System.gc();
	}

	public void updateChangedValues(HashMap<DPXColumn, String> changedValues) {
		centerPaneController.updateChangedValues(changedValues);
	}

	public void uploadFiles() {
		mainViewController.uploadFiles();
	}

	public void uploadFilesFromDirectory() {
		mainViewController.uploadFilesFromDirectory();
	}
}
