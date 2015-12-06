package io.yom.metamodel;

import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class MetaObject {
	
	private String metaClassName;
	private Template nameTemplate;
	private Map<String, Variable> variableMap;
	
	/**
	 * @return the metaClassName
	 */
	public String getMetaClassName() {
		return metaClassName;
	}
	/**
	 * @param metaClassName the metaClassName to set
	 */
	public void setMetaClassName(String metaClassName) {
		this.metaClassName = metaClassName;
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
		return "MetaObject [metaClassName=" + metaClassName + ", nameTemplate="
				+ nameTemplate + ", variableMap=" + variableMap + "]";
	}

}
