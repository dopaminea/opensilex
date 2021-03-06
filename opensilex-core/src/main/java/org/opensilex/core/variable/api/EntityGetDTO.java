/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensilex.core.variable.api;

import java.net.URI;
import org.opensilex.core.variable.dal.EntityModel;


/**
 *
 * @author vidalmor
 */
public class EntityGetDTO {

    private URI uri;

    private String label;

    private String comment;

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public static EntityGetDTO fromModel(EntityModel model) {
        EntityGetDTO dto = new EntityGetDTO();

        dto.setUri(model.getUri());
        dto.setLabel(model.getLabel());
        dto.setComment(model.getComment());
        
        return dto;
    }
}
