/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensilex.security.authentication.dal;

import org.opensilex.security.user.dal.UserModel;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import static org.apache.jena.arq.querybuilder.AbstractQueryBuilder.makeVar;
import org.apache.jena.arq.querybuilder.AskBuilder;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.Var;
import org.opensilex.security.authentication.ApiCredential;
import org.opensilex.security.authentication.ApiCredentialGroup;
import org.opensilex.security.authentication.SecurityOntology;
import org.opensilex.sparql.service.SPARQLService;
import org.opensilex.sparql.deserializer.SPARQLDeserializers;
import org.opensilex.sparql.exceptions.SPARQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author vince
 */
public final class AuthenticationDAO {

    private final static Logger LOGGER = LoggerFactory.getLogger(AuthenticationDAO.class);

    private final SPARQLService sparql;

    public AuthenticationDAO(SPARQLService sparql) {
        this.sparql = sparql;
    }

    public static String getCredentialIdFromMethod(Method method) {
        ApiCredential credentialAnnotation = method.getAnnotation(ApiCredential.class);
        return (credentialAnnotation != null && !credentialAnnotation.hide()) ? credentialAnnotation.credentialId() : null;
    }

    private static TreeMap<String, Map<String, String>> credentialsGroups;

    private static Map<String, String> credentialsGroupLabels;

    private static Set<String> credentialsIdList;

    private void buildCredentials() {
        if (credentialsGroups == null || credentialsGroupLabels == null || credentialsIdList == null) {
            credentialsGroups = new TreeMap<>();
            credentialsGroupLabels = new HashMap<>();
            credentialsIdList = new HashSet<>();
            Set<Method> methods = sparql.getOpenSilex().getMethodsAnnotatedWith(ApiCredential.class);
            methods.forEach((method) -> {
                ApiCredential apiCredential = method.getAnnotation(ApiCredential.class);
                ApiCredentialGroup apiCredentialGroup = method.getDeclaringClass().getAnnotation(ApiCredentialGroup.class);
                if (apiCredentialGroup != null && apiCredential != null && !apiCredential.hide()) {
                    String groupId = apiCredentialGroup.groupId();
                    if (!credentialsGroups.containsKey(groupId)) {
                        credentialsGroups.put(groupId, new HashMap<>());
                        credentialsGroupLabels.put(groupId, apiCredentialGroup.groupLabelKey());
                    }

                    Map<String, String> groupMap = credentialsGroups.get(groupId);

                    LOGGER.debug("Register credential: " + groupId + " - " + apiCredential.credentialId() + " (" + apiCredential.credentialLabelKey() + ")");
                    groupMap.put(apiCredential.credentialId(), apiCredential.credentialLabelKey());
                    credentialsIdList.add(apiCredential.credentialId());
                }
            });
        }
    }

    public Set<String> getCredentialsIdList() {
        buildCredentials();
        return credentialsIdList;
    }

    public Map<String, String> getCredentialsGroupLabels() {
        buildCredentials();
        return credentialsGroupLabels;
    }

    public TreeMap<String, Map<String, String>> getCredentialsGroups() {
        buildCredentials();
        return credentialsGroups;
    }

    public boolean checkUserAccess(UserModel user, String accessId) throws SPARQLException {
        Node nodeUri = SPARQLDeserializers.nodeURI(user.getUri());
        Var groupVar = makeVar("__group");
        Var profileVar = makeVar("__profile");

        AskBuilder query = sparql.getUriExistsQuery(UserModel.class, user.getUri())
                .addWhere(groupVar, SecurityOntology.hasUser, nodeUri)
                .addWhere(groupVar, SecurityOntology.hasProfile, profileVar)
                .addWhere(profileVar, SecurityOntology.hasAccess, accessId);

        return sparql.executeAskQuery(query);
    }
}
