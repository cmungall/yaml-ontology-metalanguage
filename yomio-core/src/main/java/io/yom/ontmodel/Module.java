package io.yom.ontmodel;

import java.util.List;

public abstract class Module {
	
	private String id;
	private List<String> requiredModuleIds;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	
	
	/**
	 * @return the requiredModuleIds
	 */
	public List<String> getRequiredModuleIds() {
		return requiredModuleIds;
	}


	/**
	 * @param requiredModuleIds the requiredModuleIds to set
	 */
	public void setRequiredModuleIds(List<String> requiredModuleIds) {
		this.requiredModuleIds = requiredModuleIds;
	}


	public abstract boolean isMetaObject();

}
