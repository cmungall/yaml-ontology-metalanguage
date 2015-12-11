package io.yom.ontmodel;

import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.google.common.base.Preconditions;

import io.yom.metamodel.MetaObject;

/**
 * A generic representation of an ontology object (OWLClass, OWLObjectProperty, etc)
 * 
 * An OntObject is equivalent to an OWLObject plus (optionally) a set of filled slots from one or
 * more implemented Templates/MetaObjects 
 * 
 * @author cjm
 *
 */
public class OntObject {
	
	private String id;
	private final OntObjectType ontObjectType;
	private String label; // TODO: do not hardcode assumptions?
	private MetaObject implementsPattern;
	
	
	private Map<String, Object> propertyValueMap = new HashMap<>();

	public OntObject(OntObjectType ontObjectType) {
		super();
		this.ontObjectType = ontObjectType;
	}

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
	 * @return the ontObjectType
	 */
	public OntObjectType getOntObjectType() {
		return ontObjectType;
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
	
	/**
	 * @param key
	 * @param val
	 */
	public void setProperty(String key, Object val) {
		Preconditions.checkNotNull(key, "key cannot be null");
		propertyValueMap.put(key, val);
	}
	
	
	@Override
	public String toString() {
		Yaml yaml = new Yaml();
		return yaml.dump(this);
	}
	

}
