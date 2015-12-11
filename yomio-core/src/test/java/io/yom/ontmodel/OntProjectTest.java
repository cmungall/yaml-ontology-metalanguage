package io.yom.ontmodel;

import static org.junit.Assert.*;
import io.yom.metamodel.MetaClassTest;
import io.yom.metamodel.MetaIndividualTest;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class OntProjectTest {

	@Test
	public void test() {
		Project p = getTestProject();
		OntObjectModule m = p.getRootModuleList().get(0);
		assertTrue("id", m.getClassList().get(0).getId().equals(OntObjectTest.newClassId));
		assertTrue("id", m.getIndividualList().get(0).getId().equals(OntObjectTest.newIndividualId));

	}
	
	

	public static Project getTestProject() {
		OntObjectModule m = OntModuleTest.getTestOntModule();
		Project p = new Project();
		p.setId("test-project");
		p.addRootModule(m);
		return p;
	}
}
