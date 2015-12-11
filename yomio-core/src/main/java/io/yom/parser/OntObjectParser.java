package io.yom.parser;

import io.yom.ontmodel.OntObjectModule;
import io.yom.ontmodel.OntObject;
import io.yom.ontmodel.OntObjectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses/translates simple YAML representation to OntologyObjects. Here an OntObject is typically an OWLClass.
 * 
 * @author cjm
 *
 */
public class OntObjectParser extends AbstractParser {

	/**
	 * Logger.
	 */
	private static final Logger logger =
			LoggerFactory.getLogger(OntObjectParser.class);
	
	Map<String, OntObjectType> tagMap = null;
	
	private void populateTagMap() {
		if (tagMap != null)
			return;
		tagMap = new HashMap<>();
		tagMap.put("classes", OntObjectType.CLASS);
		tagMap.put("individuals", OntObjectType.INDIVIDUAL);
		tagMap.put("objectProperties", OntObjectType.OBJECT_PROPERTY);
		tagMap.put("annotationProperties", OntObjectType.ANNOTATION_PROPERTY);
		tagMap.put("axioms", OntObjectType.AXIOM);
	}
	
	/**
	 * Translates an individual Ontology Object
	 * 
	 * @param yamlObj
	 * @return ontology object
	 */
	public OntObject translateYAMLToOntObject(Map yamlObj, OntObjectType objectType) throws UnknownTagException {
		OntObject obj = new OntObject(objectType);
		for (Object key : yamlObj.keySet()) {
			Object v = yamlObj.get(key);
			
			if (key.equals("id")) {
				obj.setId((String)v);
			}
			else if (key.equals("label")) {
				obj.setLabel((String)v);
			}
			else if (key.equals("implements")) {
				//obj.setImplementsPattern((String)v);
				// TODO: need a lookup object
			}
			else {
				obj.setProperty((String) key, v);
			}
			
		}
		return obj;
	}
	
	public List<OntObject> translateYAMLToOntObjectList(List yamlObj, OntObjectType objectType) throws UnknownTagException {
		List<OntObject> objs = new ArrayList<>();
		for (Object obj : yamlObj) {
			objs.add(translateYAMLToOntObject((Map) obj, objectType));
		}
		return objs;
	}

	public OntObjectModule translateYAMLToOntModule(Map yamlObj) throws UnknownTagException {
		populateTagMap();
		OntObjectModule ontModule = new OntObjectModule();
		for (Object key : yamlObj.keySet()) {
			Object v = yamlObj.get(key);
			logger.info(" "+key+" ==> "+v);
			if (key.equals("id")) {
				ontModule.setId((String)v);
			}
			else if (key.equals("requires")) {
				ontModule.setRequiredModuleIds((List)v);
			}
			else if (tagMap.containsKey(key)) {
				OntObjectType t = tagMap.get(key);
				ontModule.setOntObjectList(t, translateYAMLToOntObjectList((List) v, t));
			}
			else {
				//obj.setProperty((String) key, v);
			}
			
		}
		return ontModule;	
	}
	
	
}
