package com.portalmedia.embarc.parser.dpx;

import java.io.Serializable;
import java.util.LinkedList;

import com.fasterxml.jackson.annotation.JsonCreator;
/**
 * DPX Metadata Sections and Titles
 * 
 * @author PortalMedia
 * @since   2019-05-01
 **/
public enum DPXSection  implements Serializable{
	FILE_INFORMATION_HEADER("File Information"),
	IMAGE_INFORMATION_HEADER("Image Information"),
	IMAGE_SOURCE_INFORMATION_HEADER("Image Source Information"),
	MOTION_PICTURE_FILM_INFORMATION_HEADER("Motion Picture Film Information"),
	TELEVISION_INFORMATION_HEADER("Television Information"),
	USER_DEFINED_DATA("User Defined Information");
	
	private String displayName;
	
	@JsonCreator
	private DPXSection(String displayName)
	{
		this.displayName = displayName;
	}
	public String getDisplayName() {
		return displayName;
	}
	public LinkedList<DPXColumn> getColumns()
	{
		LinkedList<DPXColumn> toReturn = new LinkedList<DPXColumn>();
		for(DPXColumn column : DPXColumn.values())
		{
			if(column.getSection() == this) {
				toReturn.add(column);
			}
		}
		return toReturn;
	}
}
