package io.yom.metamodel;

public class MetaObject {
	
	private String metaClassName;
	private Template nameTemplate;
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

	
}
