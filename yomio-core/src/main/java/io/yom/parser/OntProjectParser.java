package io.yom.parser;

import io.yom.ontmodel.Module;
import io.yom.ontmodel.OntObjectModule;
import io.yom.ontmodel.Project;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OntProjectParser {
	
	/**
	 * Logger.
	 */
	private static final Logger logger =
			LoggerFactory.getLogger(OntObjectParser.class);


	private String baseDirectory;
	MetaObjectParser metaObjectParser = new MetaObjectParser();
	OntObjectParser ontObjectParser = new OntObjectParser();
	
	

	public Project parseOntProject(Map yamlObj) throws UnknownTagException, IOException {
		Project ontProject = new Project();

		for (Object key : yamlObj.keySet()) {
			Object v = yamlObj.get(key);

			if (key.equals("id")) {
				ontProject.setId((String)v);
			}
			else if (key.equals("@context")) {
				// TODO
			}
			else if (key.equals("rootModules")) {
				ontProject.setRootModuleIds((List)v);
			}
			else {
				throw new UnknownTagException(key);
			}
		}
		resolveAll(ontProject);
		return ontProject;
	}

	private void resolveAll(Project ontProject) throws IOException, UnknownTagException {
		Stack<String> mids = new Stack<String>();
		ontProject.getRootModuleIds().stream().forEach(mid -> mids.add(mid));
		while (mids.size() > 0) {
			String mid = mids.pop();
			logger.info("Resolving: "+mid);
			Module m = resolveModule(mid, ontProject);
			if (m.getRequiredModuleIds() != null) {
				mids.addAll(
						m.getRequiredModuleIds().stream().filter(rmid -> !mids.contains(rmid)).collect(Collectors.toList())
						);
			}
		}
	}

	private Module resolveModule(String moduleId, Project ontProject) throws IOException, UnknownTagException {
		String path = resolveModulePath(moduleId);
		Object yamlObj = ParserUtils.parseFile(new File(path));
		Module mod;
		if (yamlObj instanceof Map) {
			Map m = (Map)yamlObj;
			boolean isMeta = false;
			if (m.containsKey("pattern_name")) {
				isMeta = true;
			}
			
			if (isMeta) {
				mod = metaObjectParser.translateYAMLToMetaClass(m);
			}
			else {
				mod = ontObjectParser.translateYAMLToOntModule(m);
				
			}
		}
		else {
			mod = null;
			logger.error("FAIL: "+moduleId+" OBJ:"+yamlObj);
		}
		if (mod.getId() == null) {
			logger.warn("No id declared: "+moduleId);
			mod.setId(moduleId);
		}
		if (!mod.getId().equals(moduleId)) {
			logger.error("Module external reference does not match id declared in module: "+moduleId+" != " + mod.getId());
		}
		ontProject.addModule(mod);
		return mod;
	}

	private String resolveModulePath(String moduleId) {
		return getBaseDirectory() + "/" + moduleId + ".yaml";
	}

	/**
	 * @return the baseDirectory
	 */
	public String getBaseDirectory() {
		return baseDirectory;
	}

	/**
	 * @param baseDirectory the baseDirectory to set
	 */
	public void setBaseDirectory(String baseDirectory) {
		this.baseDirectory = baseDirectory;
	}



}
