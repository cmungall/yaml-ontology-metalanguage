package io.yom.metamodel;

import java.util.List;


public class Template {
	final private String templateString;
	final private List<Variable> variableList;
	
	public Template(String templateString, List<Variable> variableList) {
		super();
		this.templateString = templateString;
		this.variableList = variableList;
	}
	
	/**
	 * @return the templateString
	 */
	public String getTemplateString() {
		return templateString;
	}
	/**
	 * @return the variableList
	 */
	public List<Variable> getVariableList() {
		return variableList;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Template [templateString=" + templateString + ", variableList="
				+ variableList + "]";
	}
	
	
	
}