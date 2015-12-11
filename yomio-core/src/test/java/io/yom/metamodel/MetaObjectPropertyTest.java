package io.yom.metamodel;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class MetaObjectPropertyTest extends MetaObjectTest {

	@Test
	public void test() {
		
		MetaObjectProperty mc = getTestMetaObjectProperty();
		assertTrue("name does not match", mc.getId().equals("test-object-property"));
		// TODO: increase coverage of test
		
	}

	public static MetaObjectProperty getTestMetaObjectProperty() {
		List<Variable> variableList = new ArrayList<>();
		variableList.add(new Variable(slotA));
		variableList.add(new Variable(slotB));
		MetaObjectProperty mc = new MetaObjectProperty();
		mc.setId("test-object-property");
		
		mc.setNameTemplate(new Template("%s", variableList));
		mc.addSubPropertyOfTemplate(new Template("%s", variableList));
		
		return mc;
	}
	
}
