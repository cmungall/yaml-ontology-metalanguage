package io.yom.metamodel;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class MetaIndividualTest extends MetaObjectTest {

	@Test
	public void test() {
		
		MetaIndividual mc = getTestMetaIndividual();
		assertTrue("name does not match", mc.getId().equals("test-individual"));
		
	}

	public static MetaIndividual getTestMetaIndividual() {
		List<Variable> variableList = new ArrayList<>();
		variableList.add(new Variable(slotA));
		variableList.add(new Variable(slotB));
		MetaIndividual mc = new MetaIndividual();
		mc.setId("test-individual");
		
		mc.setNameTemplate(new Template("%s of %s", variableList));
		mc.addTypeTemplate(new Template("%s and 'part of' some %s", variableList));
		
		return mc;
	}
	
}
