package io.yom.metamodel;

import static org.junit.Assert.*;

import org.junit.Test;

public class MetaObjectTest {

	@Test
	public void test() {
		MetaObject mc = MetaClassTest.getTestMetaClass();
		assertTrue("name does not match", mc.getMetaClassName().equals("test"));
	}
	
}
