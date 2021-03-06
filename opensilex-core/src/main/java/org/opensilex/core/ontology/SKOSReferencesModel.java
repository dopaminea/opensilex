//******************************************************************************
// OpenSILEX - Licence AGPL V3.0 - https://www.gnu.org/licenses/agpl-3.0.en.html
// Copyright © INRA 2019
// Contact: vincent.migot@inra.fr, morgane.vidal@inra.fr,anne.tireau@inra.fr, pascal.neveu@inra.fr
//******************************************************************************
package org.opensilex.core.ontology;

import java.net.URI;
import java.util.List;
import org.apache.jena.vocabulary.SKOS;
import org.opensilex.sparql.annotations.SPARQLProperty;
import org.opensilex.sparql.model.SPARQLResourceModel;

/**
 * Ontology reference model.
 *
 * @author Morgane Vidal
 */
public abstract class SKOSReferencesModel extends SPARQLResourceModel {

    @SPARQLProperty(
            ontology = SKOS.class,
            property = "exactMatch"
    )
    private List<URI> exactMatch;

    @SPARQLProperty(
            ontology = SKOS.class,
            property = "closeMatch"
    )
    private List<URI> closeMatch;

    @SPARQLProperty(
            ontology = SKOS.class,
            property = "broader"
    )
    private List<URI> broader;

    @SPARQLProperty(
            ontology = SKOS.class,
            property = "narrower"
    )
    private List<URI> narrower;

    public List<URI> getExactMatch() {
        return exactMatch;
    }

    public void setExactMatch(List<URI> exactMatch) {
        this.exactMatch = exactMatch;
    }

    public List<URI> getCloseMatch() {
        return closeMatch;
    }

    public void setCloseMatch(List<URI> closeMatch) {
        this.closeMatch = closeMatch;
    }

    public List<URI> getBroader() {
        return broader;
    }

    public void setBroader(List<URI> broader) {
        this.broader = broader;
    }

    public List<URI> getNarrower() {
        return narrower;
    }

    public void setNarrower(List<URI> narrower) {
        this.narrower = narrower;
    }
    
        
    public void setSkosReferencesNewModel(SKOSReferencesDTO dto) {
        this.setNarrower(dto.getNarrower());
        this.setBroader(dto.getBroader());
        this.setCloseMatch(dto.getCloseMatch());
        this.setExactMatch(dto.getExactMatch()); 
    }
}

