package com.portalmedia.embarc.parser.dpx;

import java.util.LinkedList;
import java.util.List;

import com.portalmedia.embarc.gui.model.MetadataColumnViewModel;
import com.portalmedia.embarc.validation.DPXColumnValidationRules;

/**
 * Singleton to manage a list of DPX column objects needed for the TableView
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class DPXMetadataColumnViewModelList {

	private static DPXMetadataColumnViewModelList instance = null;

	public static DPXMetadataColumnViewModelList getInstance() {
		if (instance == null) {
			instance = new DPXMetadataColumnViewModelList();
		}

		return instance;
	}

	private List<MetadataColumnViewModel> columns;

	private DPXMetadataColumnViewModelList() {
		buildDPXColumns();
	}

	private void buildDPXColumns() {
		columns = new LinkedList<>();
		final DPXColumnValidationRules rules = DPXColumnValidationRules.getInstance();
		for (final DPXColumn c : DPXColumn.values()) {

			final MetadataColumnViewModel mcvm = new MetadataColumnViewModel();

			mcvm.setLength(c.getLength());

			mcvm.setHasSubsection(c.hasSubsection());

			mcvm.setValidationRules(rules.getRuleSet(c));

			mcvm.setColumn(c);

			columns.add(mcvm);

		}

	}

	public List<MetadataColumnViewModel> getColumns() {
		return columns;
	}

}
