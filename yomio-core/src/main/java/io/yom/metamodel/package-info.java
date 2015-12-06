package io.yom.metamodel;

/*

YAML Ontology Datamodel

A DesignPattern is a meta-class that is instantiated by an
OWLEntity. A DesignPattern has one or more Variables, and or more
Templates, for different axiom types. Each Template takes a list of
variables as input.

When an OWLEntity instantiates a DesignPattern, it specifies the
values of the Variables in the DesignPattern. A Generator can use
these to create the OWLEntity and associated axioms.

 */
