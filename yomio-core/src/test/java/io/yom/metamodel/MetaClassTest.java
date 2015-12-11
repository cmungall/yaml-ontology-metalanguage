package io.yom.metamodel;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class MetaClassTest extends MetaObjectTest {

	@Test
	public void test() {
		
		MetaClass mc = getTestMetaClass();
		assertTrue("name does not match", mc.getId().equals("test"));
		
	}

	public static MetaClass getTestMetaClass() {
		List<Variable> variableList = new ArrayList<>();
		variableList.add(new Variable(slotA));
		variableList.add(new Variable(slotB));
		MetaClass mc = new MetaClass();
		mc.setId("test");
		
		mc.setNameTemplate(new Template("%s of %s", variableList));
		mc.setEquivalentToTemplate(new Template("%s and 'part of' some %s", variableList));
		mc.setTextDefinitionTemplate(new Template("Something that is both an %s and an %s", variableList));
		
		// add reciprocals using a GCI
		variableList.add(new Variable(slotB));
		variableList.add(new Variable(slotA));
		mc.addGeneralClassAxiom(new Template("(%s and 'part of' some %s) SubClassOf: (%s and 'has part' some %s)", 
				variableList));
		return mc;
	}
	
	
}
