package io.yom.ontmodel;

import static org.junit.Assert.*;
import io.yom.metamodel.MetaClassTest;
import io.yom.metamodel.MetaIndividualTest;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class OntObjectTest {

	public static String newClassId = "FOO:new";
	public static String newIndividualId = "FOO:new-individual";

	@Test
	public void test() {
		OntObject ontObject = getTestOntClassObject();
		assertTrue("id", ontObject.getId().equals(newClassId));
	}
	
	public static OntObject getTestOntClassObject() {
		OntObject obj = new OntObject(OntObjectType.CLASS);
		obj.setId(newClassId);
		obj.setLabel("this will be overwritten");
		Map<String, Object> m = new HashMap<>();
		m.put(MetaClassTest.slotA, "FOO:1");
		m.put(MetaClassTest.slotB, "FOO:2");
		obj.setPropertyValueMap(m);
		return obj;
	}

	public static OntObject getTestOntIndividualObject() {
		OntObject obj = new OntObject(OntObjectType.INDIVIDUAL);
		obj.setId(newIndividualId);
		obj.setLabel("this will be overwritten");
		Map<String, Object> m = new HashMap<>();
		m.put(MetaIndividualTest.slotA, "FOO:1");
		m.put(MetaIndividualTest.slotB, "FOO:2");
		obj.setPropertyValueMap(m);
		return obj;
	}
}
