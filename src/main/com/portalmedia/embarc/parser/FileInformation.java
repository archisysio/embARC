package com.portalmedia.embarc.parser;

import java.io.Serializable;

public class FileInformation<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name, type, hash, path, uuid;
	
	private T fileData;
	
	public FileInformation() {
		super();
	}
	
	public FileInformation(String name, String type, String hash, String path, String uuid) {
		super();
		this.name = name; 
		this.type = type;
		this.hash = hash;
		this.path = path;
		this.uuid = uuid;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return this.type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getHash() {
		return this.hash;
	}
	
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	public String getUUID() {
		return this.uuid;
	}

	public void setUUID(String uuid) {
		this.uuid = uuid;
	}

	public T getFileData() {
		return fileData;
	}

	public void setFileData(T data) {
		this.fileData = data;
	}

}
