package io.yom.metamodel;

import io.yom.ontmodel.Module;

import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public abstract class MetaObject extends Module {
	
	private String id;
	private Template nameTemplate;
	private Map<String, Variable> variableMap;
	
	@Override
	public boolean isMetaObject() {
		return true;
	}

	/**
	 * @return the metaClassName
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param metaClassName the metaClassName to set
	 */
	public void setId(String metaClassName) {
		this.id = metaClassName;
	}
	/**
	 * @return the nameTemplate
	 */
	public Template getNameTemplate() {
		return nameTemplate;
	}
	/**
	 * @param nameTemplate the nameTemplate to set
	 */
	public void setNameTemplate(Template nameTemplate) {
		this.nameTemplate = nameTemplate;
	}
	/**
	 * @return the variableMap
	 */
	public Map<String, Variable> getVariableMap() {
		return variableMap;
	}
	/**
	 * @param variableMap the variableMap to set
	 */
	public void setVariableMap(Map<String, Variable> variableMap) {
		this.variableMap = variableMap;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MetaObject [metaClassName=" + id + ", nameTemplate="
				+ nameTemplate + ", variableMap=" + variableMap + "]";
	}

}
