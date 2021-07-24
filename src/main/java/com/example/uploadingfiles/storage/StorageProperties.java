package com.example.uploadingfiles.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

// singleton object. // tag should be unque
@ConfigurationProperties("storagecheck")
public class StorageProperties {

	/**
	 * Folder location for storing files
	 */
	private String location = "uploaded-images";

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
