package io.yom.ontmodel;

import static org.junit.Assert.*;
import io.yom.metamodel.MetaClassTest;
import io.yom.metamodel.MetaIndividualTest;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class OntModuleTest {

	@Test
	public void test() {
		OntObjectModule m = getTestOntModule();
		assertTrue("id", m.getClassList().get(0).getId().equals(OntObjectTest.newClassId));
		assertTrue("id", m.getIndividualList().get(0).getId().equals(OntObjectTest.newIndividualId));

	}
	
	

	public static OntObjectModule getTestOntModule() {
		OntObject c = OntObjectTest.getTestOntClassObject();
		OntObject i = OntObjectTest.getTestOntIndividualObject();
		OntObjectModule m = new OntObjectModule();
		m.addOntObjectToList(OntObjectType.CLASS, c);
		m.addOntObjectToList(OntObjectType.INDIVIDUAL, i);
		return m;
	}
}
