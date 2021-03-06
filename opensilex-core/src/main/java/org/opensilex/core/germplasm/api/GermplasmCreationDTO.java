//******************************************************************************
//                          GermplasmGetDTO.java
// OpenSILEX - Licence AGPL V3.0 - https://www.gnu.org/licenses/agpl-3.0.en.html
// Copyright © INRA 2019
// Contact: alice.boizet@inra.fr, anne.tireau@inra.fr, pascal.neveu@inra.fr
//******************************************************************************
package org.opensilex.core.germplasm.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.net.URI;
import javax.validation.constraints.NotNull;
import org.opensilex.core.germplasm.dal.GermplasmModel;
import org.opensilex.server.rest.validation.Required;
import org.opensilex.server.rest.validation.ValidURI;

/**
 * DTO representing JSON for posting germplasm
 * @author Alice Boizet
 */
@ApiModel
class GermplasmCreationDTO extends GermplasmGetDTO{
    @ValidURI
    @ApiModelProperty(value = "Germplasm URI", example = "http://opensilex.dev/opensilex/id/plantMaterialLot#SL_001")
    @Override
    public URI getUri() {
        return uri;
    }
    
    @NotNull
    @ApiModelProperty(value = "rdfType URI", example = "http://www.opensilex.org/vocabulary/oeso#SeedLot")
    @Override
    public URI getRdfType() {
        return rdfType;
    }
    
    @Required
    @ApiModelProperty(value = "Germplasm label", example = "SL_001", required = true)
    @Override
    public String getLabel() {
        return label;
    }
    
    @ValidURI
    @ApiModelProperty(value = "species URI", example = "http://opensilex.dev/opensilex/id/species#zeamays")
    @Override
    public URI getFromSpecies() {
        return fromSpecies;
    }
    
    @ValidURI
    @ApiModelProperty(value = "variety URI", example = "http://opensilex.dev/opensilex/id/variety#B73")
    @Override
    public URI getFromVariety() {
        return fromVariety;
    }
    
    @ValidURI
    @ApiModelProperty(value = "accession URI", example = "http://opensilex.dev/opensilex/id/accession#B73_INRA")
    @Override
    public URI getFromAccession() {
        return fromAccession;
    }
    
    public GermplasmModel newModel() {
        GermplasmModel model = new GermplasmModel();
        
        if (uri != null) {
            model.setUri(uri);
        }
        if (label != null) {
            model.setLabel(label);
        }
        if (rdfType != null) {
            model.setType(rdfType);
        }
        
        if (fromSpecies != null) {
            GermplasmModel species = new GermplasmModel();
            species.setUri(fromSpecies);
            model.setSpecies(species);
        }
        if (fromVariety != null) {
            GermplasmModel variety = new GermplasmModel();
            variety.setUri(fromVariety);
            model.setVariety(variety);
        }
        if (fromAccession != null) {
            GermplasmModel accession = new GermplasmModel();
            accession.setUri(fromAccession);
            model.setAccession(accession);
        }
        
                
        return model;
    }   

}
