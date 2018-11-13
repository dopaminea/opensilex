//******************************************************************************
//                           ResultEvent.java
// SILEX-PHIS
// Copyright © INRA 2017
// Creation date: 13 nov. 2018
// Contact: andreas.garcia@inra.fr, anne.tireau@inra.fr, pascal.neveu@inra.fr
//******************************************************************************
package phis2ws.service.view.brapi.results;

import java.util.ArrayList;
import phis2ws.service.view.brapi.Pagination;
import phis2ws.service.view.manager.Result;
import phis2ws.service.view.model.phis.Event;

/**
 * A class which represents the result part in the response form, 
 * adapted to the events
 * @author Andréas Garcia <andreas.garcia@inra.fr>
 */
public class ResultEvent extends Result<Event> {
    /**
     * Constructor which calls the mother-class constructor 
     * in the case of a list with only 1 element
     * @param events 
     */
    public ResultEvent(ArrayList<Event> events) {
        super(events);
    }
    
    /**
     * Contructor which calls the mother-class constructor 
     * in the case of a list with several elements
     * @param events
     * @param pagination
     * @param paginate 
     */
    public ResultEvent(ArrayList<Event> events
            , Pagination pagination, boolean paginate) {
        super(events, pagination, paginate);
    }
}
