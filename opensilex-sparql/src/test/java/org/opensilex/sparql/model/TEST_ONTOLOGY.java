//******************************************************************************
// OpenSILEX - Licence AGPL V3.0 - https://www.gnu.org/licenses/agpl-3.0.en.html
// Copyright © INRA 2019
// Contact: vincent.migot@inra.fr, anne.tireau@inra.fr, pascal.neveu@inra.fr
//******************************************************************************
package org.opensilex.sparql.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.opensilex.sparql.utils.Ontology;

/**
 *
 * @author vincent
 */
public class TEST_ONTOLOGY {

    public static final Path FILE_PATH = Paths.get("ontologies", "test.owl");
    public static final Lang FILE_FORMAT = RDFLanguages.RDFXML;

    public static final String NAMESPACE = "http://test.opensilex.org/";

    public static final Path DATA_FILE_PATH = Paths.get("ontologies", "test_data.ttl");
    public static final Lang DATA_FILE_FORMAT = RDFLanguages.TURTLE;

    public static final Path SHACL_FAIL_FILE_PATH = Paths.get("ontologies", "test_shacl_fail.ttl");
    public static final Lang SHACL_FAIL_FILE_FORMAT = RDFLanguages.TURTLE;

    public static final String DATA_NAMESPACE = NAMESPACE + "data/";

    public static final Resource A = Ontology.resource(NAMESPACE, "A");
    public static final Resource B = Ontology.resource(NAMESPACE, "B");
    public static final Resource C = Ontology.resource(NAMESPACE, "C");
    public static final Resource Fail = Ontology.resource(NAMESPACE, "Fail");

    public static final Property hasRelationToB = Ontology.property(NAMESPACE, "hasRelationToB");

    public static final Property hasString = Ontology.property(NAMESPACE, "hasString");
    public static final Property hasInt = Ontology.property(NAMESPACE, "hasInt");
    public static final Property hasLong = Ontology.property(NAMESPACE, "hasLong");
    public static final Property hasBoolean = Ontology.property(NAMESPACE, "hasBoolean");
    public static final Property hasFloat = Ontology.property(NAMESPACE, "hasFloat");
    public static final Property hasDouble = Ontology.property(NAMESPACE, "hasDouble");
    public static final Property hasChar = Ontology.property(NAMESPACE, "hasChar");
    public static final Property hasShort = Ontology.property(NAMESPACE, "hasShort");
    public static final Property hasByte = Ontology.property(NAMESPACE, "hasByte");
    public static final Property hasDate = Ontology.property(NAMESPACE, "hasDate");
    public static final Property hasDateTime = Ontology.property(NAMESPACE, "hasDateTime");
    public static final Property hasStringList = Ontology.property(NAMESPACE, "hasStringList");

    public static final Property hasLabel = Ontology.property(NAMESPACE, "hasLabel");
}
