package io.yom.ontmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Project {
	private String id;
	private List<String> rootModuleIds;

	private Map<String, Module> moduleMap = new HashMap<>();
	
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
	 * @return the rootModules
	 */
	public List<String> getRootModuleIds() {
		return rootModuleIds;
	}
	/**
	 * @param rootModules the rootModules to set
	 */
	public void setRootModuleIds(List<String> rootModules) {
		this.rootModuleIds = rootModules;
	}
	
	/**
	 * @param moduleId
	 */
	public void addRootModule(String moduleId) {
		if (rootModuleIds == null)
			rootModuleIds = new ArrayList<>();
		rootModuleIds.add(moduleId);		
	}
	
	/**
	 * Indexes an ontology modules
	 * 
	 * @param m
	 */
	public void addRootModule(OntObjectModule m) {
		String id = m.getId();
		addRootModule(id);
		moduleMap.put(id, m);		
	}

	public void addModule(Module m) {
		String id = m.getId();
		moduleMap.put(id, m);		
	}

	public Module getModule(String moduleId) {
		return moduleMap.get(moduleId);
	}
	public OntObjectModule getOntObjectModule(String moduleId) {
		return (OntObjectModule) getModule(moduleId);
	}
	
	/**
	 * @return
	 */
	public List<OntObjectModule> getRootModuleList() {
		return getRootModuleIds().stream().map(mid -> getOntObjectModule(mid))
				.collect(Collectors.toList());
	}


}
