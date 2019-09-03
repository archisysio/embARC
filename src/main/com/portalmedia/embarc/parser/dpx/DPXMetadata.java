package com.portalmedia.embarc.parser.dpx;

import java.io.Serializable;
import java.util.LinkedHashMap;

import com.portalmedia.embarc.parser.MetadataColumn;

/**
 * Object to hold a list of DPX metadata values, name, path, etc
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class DPXMetadata implements Serializable {
	private static final long serialVersionUID = 1L;
	LinkedHashMap<DPXColumn, MetadataColumn> metaData;

	public DPXMetadata() {
		metaData = new LinkedHashMap<>();
	}

	public MetadataColumn getColumn(DPXColumn column) {
		return metaData.get(column);
	}

	public LinkedHashMap<DPXColumn, MetadataColumn> getMetadataHashMap() {
		return metaData;
	}

	public void setValue(DPXColumn column, String value) {
		final MetadataColumn mdc = metaData.get(column);
		mdc.setValue(value.getBytes());
	}

	public void setValue(MetadataColumn column) {
		metaData.put(column.getColumnDef(), column);
	}
}
