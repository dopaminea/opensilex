//******************************************************************************
//                          TripletsResourceService.java
// SILEX-PHIS
// Copyright © INRA 2018
// Creation date: 7 March 2018
// Contact: morgane.vidal@inra.fr, anne.tireau@inra.fr, pascal.neveu@inra.fr
//******************************************************************************
package opensilex.service.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.sql.Timestamp;
import java.util.ArrayList;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import opensilex.service.PropertiesFileManager;
import opensilex.service.dao.TripletDAO;
import opensilex.service.documentation.DocumentationAnnotation;
import opensilex.service.resource.dto.TripletDTO;
import opensilex.service.resource.validation.interfaces.Required;
import opensilex.service.utils.POSTResultsReturn;
import opensilex.service.view.brapi.form.AbstractResultForm;
import opensilex.service.view.brapi.form.ResponseFormPOST;
import org.opensilex.security.authentication.ApiProtected;
import org.opensilex.sparql.service.SPARQLService;

/**
 * RDF Triplet resource service.
 * For the moment, the following keys have been implemented:
 * "s"
 * "p"
 * "o"
 * "o_type" = "literal" or "uri" ("bnode" is not implemented yet)
 * "o_lang" = "en-US" for example
 * @author Morgane Vidal <morgane.vidal@inra.fr>
 */
@Api("/triplets")
@Path("/triplets")
public class TripletsResourceService extends ResourceService {

    @Inject
    public TripletsResourceService(SPARQLService sparql) {
        this.sparql = sparql;
    }

    private final SPARQLService sparql;

    /**
     * Inserts triplets.
     * @param triplets triplets list to save. 
     * Triplets is a list of sets of triplets linked to each other. 
     * If the URI of the subject is unknown, the "s" field must be equals to "?".
     * The post service will generate an URI for the subject. 
     * @example of list : 
     * [
     *    {
     *        "s": "?",
     *        "p": "rdf:type",
     *        "o": "http://www.opensilex.org/vocabulary/oeso#Experiment",
     *        "o_type": "uri",
     *        "g": "http://www.phenome-fppn.fr/phis_field/DIA2017-2"
     *    },
     *    {
     *      "s": "?",
     *      "p": "http://www.opensilex.org/vocabulary/oeso#hasDocument",
     *      "o": "http://www.phenome-fppn.fr/phis_field/documents/documente597f57ba71d421a86277d830f4b9885",
     *      "o_type": "uri" ,
     *      "g": "http://www.phenome-fppn.fr/phis_field/DIA2017-2"
     *    }
     * ]
     * @param context
     * @return
     */
    @POST
    @ApiOperation(value = "Post triplets",
            notes = "Register new triplets in the triplestore")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Triplets saved", response = ResponseFormPOST.class),
        @ApiResponse(code = 400, message = DocumentationAnnotation.BAD_USER_INFORMATION),
        @ApiResponse(code = 401, message = DocumentationAnnotation.USER_NOT_AUTHORIZED),
        @ApiResponse(code = 500, message = DocumentationAnnotation.ERROR_SEND_DATA)
    })
    @ApiProtected
    public Response postTriplets(
            @ApiParam(value = DocumentationAnnotation.TRIPLET_POST_DATA_DEFINITION, required = true) @Required @Valid ArrayList<ArrayList<TripletDTO>> triplets,
            @Context HttpServletRequest context) throws Exception {
        //SILEX:warning
        // blank nodes are not implemented yet
        //\SILEX:warning

        AbstractResultForm postResponse = null;

        //If there are at least one list of triplets
        if (triplets != null && !triplets.isEmpty()) {
            TripletDAO tripletDao = new TripletDAO(sparql);
            if (context.getRemoteAddr() != null) {
                tripletDao.remoteUserAdress = context.getRemoteAddr();
            }
            tripletDao.user = userSession.getUser();

            String graphUri = PropertiesFileManager.getConfigFileProperty("sesame_rdf_config", "baseURI")
                    + Long.toString(new Timestamp(System.currentTimeMillis()).getTime());

            POSTResultsReturn insertResult = tripletDao.checkAndInsert(triplets, graphUri);

            //triplets inserted
            if (insertResult.getHttpStatus().equals(Response.Status.CREATED)) {
                postResponse = new ResponseFormPOST(insertResult.statusList);
                postResponse.getMetadata().setDatafiles(insertResult.getCreatedResources());
            } else if (insertResult.getHttpStatus().equals(Response.Status.BAD_REQUEST)
                    || insertResult.getHttpStatus().equals(Response.Status.OK)
                    || insertResult.getHttpStatus().equals(Response.Status.INTERNAL_SERVER_ERROR)) {
                postResponse = new ResponseFormPOST(insertResult.statusList);
            }
            return Response.status(insertResult.getHttpStatus()).entity(postResponse).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).entity(new ResponseFormPOST()).build();
        }
    }
}
