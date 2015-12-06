package io.yom.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.yom.generator.OWLClassGeneratorTest;
import io.yom.metamodel.MetaClass;
import io.yom.metamodel.Template;
import io.yom.metamodel.Variable;
import io.yom.parser.AbstractParser;

public class MetaObjectParser extends AbstractParser {

	/**
	 * Logger.
	 */
	private static final Logger logger =
			LoggerFactory.getLogger(MetaObjectParser.class);
	
	public MetaClass translateYAMLToMetaClass(Map yamlObj) {
		MetaClass mc = new MetaClass();
		for (Object key : yamlObj.keySet()) {
			Object v = yamlObj.get(key);
			if (key.equals("vars")) {
				translateVars(mc, (Map)v);
			}
			else if (key.equals("def")) {
				Template t =  translateTemplate(mc, (Map)v);
				mc.setTextDefinitionTemplate(t);
			}
			else if (key.equals("equivalentTo")) {
				Template t =  translateTemplate(mc, (Map)v);
				mc.setEquivalentToTemplate(t);
			}
			else if (key.equals("gcis")) {
				for (Map m : (List<Map>)v) {
					Template t =  translateTemplate(mc, m);
					mc.addGeneralClassAxiom(t);
				}
			}
			else if (key.equals("name")) {
				Template t =  translateTemplate(mc, (Map)v);
				mc.setNameTemplate(t);
			}
			else if (key.equals("pattern_name")) {
				// TODO
			}
			else {
				// TODO
			}
		}
		return mc;
	}

	private Template translateTemplate(MetaClass mc, Map v) {
		List<Variable> vars = new ArrayList<>();
		List vnlist = (List)v.get("vars");
		Map<String, Variable> vmap = mc.getVariableMap();
		for (Object vn : vnlist) {
			vars.add(vmap.get((String)vn));
		}
		Template t = new Template((String) v.get("text"), vars);
		logger.info("Parsed: "+t);
		return t;
	}

	private void translateVars(MetaClass mc, Map vars) {
		Map<String, Variable> vmap = new HashMap<>();
		for (Object v : vars.keySet()) {
			Variable vbl = new Variable((String)v);
			// , vars.get(v)
			vmap.put((String)v, vbl);
			
		}
		mc.setVariableMap(vmap);
		
	}
	
}
