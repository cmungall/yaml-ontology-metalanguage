package io.yom.ontmodel;

import java.util.Map;
import org.yaml.snakeyaml.Yaml;

import io.yom.metamodel.MetaObject;

public class OntObject {
	
	private String id;
	private String label;
	private MetaObject implementsPattern;
	
	
	private Map<String, Object> propertyValueMap;
	
	

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
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the implementsPattern
	 */
	public MetaObject getImplementsPattern() {
		return implementsPattern;
	}

	/**
	 * @param implementsPattern the implementsPattern to set
	 */
	public void setImplementsPattern(MetaObject implementsPattern) {
		this.implementsPattern = implementsPattern;
	}

	/**
	 * @return the propertyValueMap
	 */
	public Map<String, Object> getPropertyValueMap() {
		return propertyValueMap;
	}

	/**
	 * @param propertyValueMap the propertyValueMap to set
	 */
	public void setPropertyValueMap(Map<String, Object> propertyValueMap) {
		this.propertyValueMap = propertyValueMap;
	}
	
	@Override
	public String toString() {
		Yaml yaml = new Yaml();
		return yaml.dump(this);
	}
	

}
