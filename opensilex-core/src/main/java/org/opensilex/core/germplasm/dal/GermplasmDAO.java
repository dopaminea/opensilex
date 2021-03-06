//******************************************************************************
//                          GermplasmGetDTO.java
// OpenSILEX - Licence AGPL V3.0 - https://www.gnu.org/licenses/agpl-3.0.en.html
// Copyright © INRA 2019
// Contact: alice.boizet@inra.fr, anne.tireau@inra.fr, pascal.neveu@inra.fr
//******************************************************************************
package org.opensilex.core.germplasm.dal;

import java.net.URI;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import static org.apache.jena.arq.querybuilder.AbstractQueryBuilder.makeVar;
import org.apache.jena.arq.querybuilder.AskBuilder;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.vocabulary.RDFS;
import org.opensilex.core.ontology.Oeso;
import org.opensilex.sparql.deserializer.SPARQLDeserializers;
import org.opensilex.sparql.exceptions.SPARQLException;
import org.opensilex.sparql.model.SPARQLResourceModel;
import org.opensilex.sparql.service.SPARQLQueryHelper;
import org.opensilex.sparql.service.SPARQLService;
import org.opensilex.sparql.utils.Ontology;
import org.opensilex.utils.OrderBy;
import org.opensilex.utils.ListWithPagination;

/**
 * Germplasm DAO
 * @author Alice Boizet
 */
public class GermplasmDAO {
    
    protected final SPARQLService sparql;
    
    public GermplasmDAO(SPARQLService sparql) {
        this.sparql = sparql;
    }
    
    public GermplasmModel create(GermplasmModel instance) throws Exception {
        sparql.create(instance);
        return instance;
    }
    
    public void create(List<GermplasmModel> instances) throws Exception {
        sparql.create(instances);
    }

    public boolean germplasmLabelExists(String label) throws Exception {
        return sparql.existsByUniquePropertyValue(
                GermplasmModel.class,
                RDFS.label,
                label
        );
    }

    public GermplasmModel create(
            URI uri, 
            String label, 
            URI rdfType, 
            URI fromSpecies, 
            URI fromVariety, 
            URI fromAccession
    ) throws Exception {
        GermplasmModel germplasm = new GermplasmModel();
        germplasm.setUri(uri);
        germplasm.setLabel(label);
        germplasm.setType(rdfType);        
        
        if (fromAccession != null) {
            GermplasmModel accession = new GermplasmModel();
            accession.setUri(fromAccession);
            germplasm.setAccession(accession);
        }

        if (fromVariety != null) {
            GermplasmModel variety = new GermplasmModel();
            variety.setUri(fromVariety);        
            germplasm.setVariety(variety);
        }
        
        if (fromSpecies != null) {
            GermplasmModel species = new GermplasmModel();
            species.setUri(fromSpecies);
            germplasm.setSpecies(species);             
        }           
      
        sparql.create(germplasm);
        
        return germplasm;
    }  
    
    public GermplasmModel get(URI uri) throws Exception {
        return sparql.getByURI(GermplasmModel.class, uri, null);
    }
    
    public ListWithPagination<GermplasmModel> search(
            URI uri,
            URI rdfType,
            String label,
            URI species,
            URI variety,
            URI accession, 
            List<OrderBy> orderByList, 
            Integer page, 
            Integer pageSize) throws Exception {

        return sparql.searchWithPagination(
                GermplasmModel.class,
                null,
                (SelectBuilder select) -> {
                    appendUriRegexFilter(select, uri);
                    appendRdfTypeFilter(select, rdfType);
                    appendRegexLabelFilter(select, label);
                    appendSpeciesFilter(select, species);
                    appendVarietyFilter(select, variety);
                    appendAccessionFilter(select, accession);
                },
                orderByList,
                page,
                pageSize
        );
    }

    private void appendUriRegexFilter(SelectBuilder select, URI uri) {
        if (uri != null) {
            Var uriVar = makeVar(SPARQLResourceModel.URI_FIELD);
            Expr strUriExpr = SPARQLQueryHelper.getExprFactory().str(uriVar);
            select.addFilter(SPARQLQueryHelper.regexFilter(strUriExpr, uri.toString(), null));
        }
    }
    private void appendRdfTypeFilter(SelectBuilder select, URI rdfType) throws Exception {
        if (rdfType != null) {
            select.addFilter(SPARQLQueryHelper.eq(GermplasmModel.TYPE_FIELD, rdfType));
        }
    }

    private void appendRegexLabelFilter(SelectBuilder select, String label) {
        if (!StringUtils.isEmpty(label)) {
            select.addFilter(SPARQLQueryHelper.regexFilter(GermplasmModel.LABEL_VAR, label));
        }
    }

    private void appendSpeciesFilter(SelectBuilder select, URI species) throws Exception {
        if (species != null) {
            select.addFilter(SPARQLQueryHelper.eq(GermplasmModel.SPECIES_URI_SPARQL_VAR.toString(), NodeFactory.createURI(SPARQLDeserializers.getExpandedURI(species.toString()))));
        }
    }

    private void appendVarietyFilter(SelectBuilder select, URI variety) throws Exception {
        if (variety != null) {
            select.addFilter(SPARQLQueryHelper.eq(GermplasmModel.VARIETY_URI_SPARQL_VAR, NodeFactory.createURI(SPARQLDeserializers.getExpandedURI(variety.toString()))));
        }
    }

    private void appendAccessionFilter(SelectBuilder select, URI accession) throws Exception {
        if (accession != null) {
            select.addFilter(SPARQLQueryHelper.eq(GermplasmModel.ACCESSION_URI_SPARQL_VAR, NodeFactory.createURI(SPARQLDeserializers.getExpandedURI(accession.toString()))));
        }
    }

    public boolean isGermplasmType(URI rdfType) throws SPARQLException {
        return sparql.executeAskQuery(new AskBuilder()
                .addWhere(SPARQLDeserializers.nodeURI(rdfType), Ontology.subClassAny, Oeso.Germplasm)
        );
    }
    
    public boolean isPlantMaterialLot(URI rdfType) throws SPARQLException {
        return sparql.executeAskQuery(new AskBuilder()
                .addWhere(SPARQLDeserializers.nodeURI(rdfType), Ontology.subClassAny, Oeso.PlantMaterialLot)
        );
    }

    public void delete(URI uri) throws Exception {
        try {
            sparql.startTransaction();
            // if 
            // Delete existing user profile group relations
            //sparql.deleteByObjectRelation(GroupUserProfileModel.class, GroupUserProfileModel.PROFILE_FIELD, instanceURI);
            // Delete user
            //sparql.delete(ProfileModel.class, instanceURI);
            sparql.commitTransaction();
        } catch (Exception ex) {
            sparql.rollbackTransaction();
            throw ex;
        }
    }
}
