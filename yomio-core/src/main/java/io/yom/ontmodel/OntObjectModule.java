package io.yom.ontmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A module is a collection of OntObjects, plus other metadata
 * 
 * @author cjm
 *
 */
public class OntObjectModule extends Module {
	
	private Map<OntObjectType,List<OntObject>> ontObjectListMap = new HashMap<>();
	
	
	public OntObjectModule() {
		super();
	}
	
	
	@Override
	public boolean isMetaObject() {
		return false;
	}


	/**
	 * @return the ontObjectListMap
	 */
	public Map<OntObjectType, List<OntObject>> getOntObjectListMap() {
		return ontObjectListMap;
	}
	/**
	 * @param ontObjectListMap the ontObjectListMap to set
	 */
	public void setOntObjectListMap(
			Map<OntObjectType, List<OntObject>> ontObjectListMap) {
		this.ontObjectListMap = ontObjectListMap;
	}
	

	
	public List getOntObjectList(OntObjectType objectType) {
		if (ontObjectListMap.containsKey(objectType)) {
			return new ArrayList<>(ontObjectListMap.get(objectType));
		}
		else {
			return new ArrayList<>();
		}
	}
	
	public void addOntObjectToList(OntObjectType ontObjectType, OntObject ontObject) {
		if (!ontObjectListMap.containsKey(ontObjectType)) {
			ontObjectListMap.put(ontObjectType, new ArrayList<>());
		}
		ontObjectListMap.get(ontObjectType).add(ontObject);
	}

	public void setOntObjectList(OntObjectType ontObjectType,
			List<OntObject> ontObjectList) {
		ontObjectListMap.put(ontObjectType, new ArrayList<>(ontObjectList));
	}

	/**
	 * @return all classes declared in module
	 */
	public List<OntObject> getClassList() {
		return getOntObjectList(OntObjectType.CLASS);
	}
	
	/**
	 * @return all individuals declared in module
	 */
	public List<OntObject> getIndividualList() {
		return getOntObjectList(OntObjectType.INDIVIDUAL);
	}

	/**
	 * @return all objectPropertys declared in module
	 */
	public List<OntObject> getObjectPropertyList() {
		return getOntObjectList(OntObjectType.OBJECT_PROPERTY);
	}
	
	/**
	 * @return all anotationPropertys declared in module
	 */
	public List<OntObject> getAnnotationPropertyList() {
		return getOntObjectList(OntObjectType.ANNOTATION_PROPERTY);
	}
	
	/**
	 * @return all axioms declared in module
	 */
	public List<OntObject> getAxiomList() {
		return getOntObjectList(OntObjectType.AXIOM);
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OntObjectModule [ontObjectListMap=" + ontObjectListMap
				+ ", getId()=" + getId() + "]";
	}


}
