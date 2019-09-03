package com.portalmedia.embarc.gui;

import com.portalmedia.embarc.gui.model.FileInformationViewModel;

import javafx.collections.ObservableList;

/**
 * Interface for controller classes that interact with ControllerMediator
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2018-05-08
 */
public interface IMediateControllers {
	void deleteSelectedFiles();

	void deselectAllFiles();

	int getCurrentFileCount();

	void refetchFileList();

	void registerCenterPaneController(CenterPaneController controller);

	void registerEditorForm(EditorForm controller);

	void registerMainViewController(MainViewController controller);

	void registerMetadataEditorController(MetadataEditorController controller);

	void registerWorkingSummaryController(WorkingSummaryController controller);

	void registerWriteFilesView(WriteFilesView controller);

	void selectAllFiles();

	void setFileList();

	void setFiles();

	void setSelectedFileList(ObservableList<FileInformationViewModel> list);
}
