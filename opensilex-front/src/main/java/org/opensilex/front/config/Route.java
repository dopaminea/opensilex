/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensilex.front.config;

import java.util.List;
import org.opensilex.config.ConfigDescription;

public interface Route {

    @ConfigDescription("Path of the route")
    public String path();

    @ConfigDescription("Vue component identifier")
    public String component();

    @ConfigDescription("Route required credentials list")
    public List<String> credentials();
}
