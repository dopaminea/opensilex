//**********************************************************************************************
//                                       PropertiesFileManager.java 
//
// Author(s): Arnaud Charleroy, Morgane Vidal
// PHIS-SILEX version 1.0
// Copyright © - INRA - 2016
// Creation date: august 2016
// Contact:arnaud.charleroy@inra.fr, anne.tireau@inra.fr, pascal.neveu@inra.fr,
//         morgane.vidal@inra.fr
// Last modification date:  January, 2017
// Subject: Read properties file
//***********************************************************************************************
package opensilex.service;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.Properties;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.opensilex.nosql.mongodb.MongoDBConfig;
import org.opensilex.sparql.SPARQLConfig;
import org.opensilex.sparql.rdf4j.RDF4JConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

/**
 * Gère les méthodes appelant tous types de propriétés à partir de fichier de
 * configuration Les fichiers sont disponible dans /src/main/resources par
 * défaut dans un projet Maven
 *
 * @date 05/2016
 * @author Arnaud Charleroy
 */
public class PropertiesFileManager {

    final static Logger LOGGER = LoggerFactory.getLogger(PropertiesFileManager.class.getName());
    private static RDF4JConfig rdf4jConfig;
    private static SPARQLConfig sparqlConfig;
    private static MongoDBConfig mongoConfig;
    private static String storageBasePath;
    private static String publicURI;

    /**
     * Lit le fichier de configuration et retourne un objet Proprietes
     *
     * @param fileName nom du fichier à lire
     * @return null | Properties
     */
    public static Properties parseFile(String fileName) {
        InputStream inputStream = null;
        final Properties props = new Properties();
        //property is in /src/main/resources By default in maven project

        final String filePath = "/" + fileName + ".properties";

        try {
            inputStream = PropertiesFileManager.class.getResourceAsStream(filePath);
            props.load(inputStream);
        } catch (IOException | NullPointerException ex) {
            LOGGER.error(ex.getMessage(), ex);
            // Si les paramètres ne sont pas récupérés le web service propage une exception INTERNAL_SERVER_ERROR
            throw new WebApplicationException(Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error : Cannot find " + fileName + " configuration file for the wanted database\n" + ex.getMessage()).build());
         } finally { 
            if (inputStream != null) { 
                try { 
                    inputStream.close(); 
                } catch (IOException ex) { 
                    LOGGER.error(ex.getMessage(), ex); 
                } 
            } 
        } 
        return props;
    }

    /**
     * Read yaml file and return a Map.
     * @param fileName
     * @return null | Map<String,Object>
     */
    public static Map<String,Object> parseYAMLConfigFile(String fileName) {
        InputStream inputStream = null;
        
        final String filePath = "/" + fileName + ".yml";
        Yaml yaml = new Yaml();
        
        inputStream = PropertiesFileManager.class.getResourceAsStream(filePath);
        Map<String, Object> ymalMap = yaml.load(inputStream);
         if (inputStream != null) { 
            try { 
                inputStream.close(); 
            } catch (IOException ex) { 
                LOGGER.error(ex.getMessage(), ex); 
            } 
        } 
            
        return ymalMap;
    }
    
    /**
     * Write yaml file and from Map.
     * @param mapObject
     * @param yamlFilePath
     * @return boolean 
     */
    public static boolean writeYAMLFile(Map<String,Object> mapObject, String  yamlFilePath) {
        boolean resultStatus = false;
        Yaml yaml = new Yaml();
        FileWriter writer;
        try {
            writer = new FileWriter(yamlFilePath);
            
            yaml.dump(mapObject, writer);
            LOGGER.debug(yaml.dump(mapObject));
            resultStatus = true;
        } catch (IOException ex) {
           LOGGER.error("Can't write file", ex);
        }
        return resultStatus;
    }
        
    /**
     * Parses a binary public key.
     * @param configurationFileName
     * @return the key parsed
     */
    public static RSAPublicKey parseBinaryPublicKey(String configurationFileName) {
        RSAPublicKey generatedRSAPublicKey = null;
        DataInputStream dataInputStream = null;
        try {
            URL resource = PropertiesFileManager.class.getResource("/" + configurationFileName + ".der");
            File publicKeyFile = new File(resource.getPath());
            dataInputStream = new DataInputStream(new FileInputStream(publicKeyFile));
            byte[] keyBytes = new byte[(int) publicKeyFile.length()];
            dataInputStream.readFully(keyBytes);
            dataInputStream.close();

            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            generatedRSAPublicKey = (RSAPublicKey) kf.generatePublic(spec);

        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            LOGGER.error(ex.getMessage(), ex);
            // Si les paramètres ne sont pas récupérés le web service propage une exception INTERNAL_SERVER_ERROR
            throw new WebApplicationException(Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Can't process JWT Token" + ex.getMessage()).build());
        } finally { 
            if (dataInputStream != null) { 
                try { 
                    dataInputStream.close(); 
                } catch (IOException ex) { 
                    LOGGER.error(ex.getMessage(), ex); 
                } 
            } 
        } 
        return generatedRSAPublicKey;
    }

    /**
     * Phis service configuration
     */
    private static PhisWsConfig phisConfig;
    
    /**
     * Setter for all configuration needed by phis
     * 
     * @param pgConfig PostGreSQL config
     * @param phisConfig Phis service configuration
     * @param coreConfig Application core config
     * @param mongoConfig MongoDB configuration
     * @param rdf4jConfig RDF4J configuration
     */
    public static void setOpensilexConfigs(
        PhisWsConfig phisConfig,
        RDF4JConfig rdf4jConfig,
        SPARQLConfig sparqlConfig,
        MongoDBConfig mongoConfig,
        String storageBasePath,
        String publicURI
    ) {
        PropertiesFileManager.phisConfig = phisConfig;
        PropertiesFileManager.rdf4jConfig = rdf4jConfig;
        PropertiesFileManager.sparqlConfig = sparqlConfig;
        PropertiesFileManager.mongoConfig = mongoConfig;
        PropertiesFileManager.storageBasePath = storageBasePath;
        PropertiesFileManager.publicURI = publicURI;
    }
    
    /**
     * This method used to read configuration from file
     * It as been updated to use new YAML config system in modularity
     * So now this method map old properties to new ones
     *
     * @param fileName CoreConfig section
     * @param prop property name
     * @return null | property value
     */
    public static String getConfigFileProperty(String fileName, String prop) {
        String value = null;
        
        switch (fileName) {
            case "service":
                value = getServiceProperty(prop);
                break;
                
            case "sesame_rdf_config":
                value = getRDF4JProperty(prop);
                break;
                
            case "mongodb_nosql_config":
                value = getMongoProperty(prop);
                break;                
                
            default:
                break;
        }
        
        return value;
    }

    /**
     * Map old properties which where con
     * @param prop
     * @return 
     */
    private static String getServiceProperty(String prop) {
        String value = null;
        
        switch (prop) {
            case "sessionTime":
                value = phisConfig.sessionTime();
                break;
            case "waitingFileTime":
                value = phisConfig.waitingFileTime();
                break;                
            case "uploadFileServerDirectory":
                value = storageBasePath;
                break;
            case "defaultLanguage":
                value = "en";
                break;
            case "gnpisPublicKeyFileName":
                value = phisConfig.gnpisPublicKeyFileName();
                break;   
            case "phisPublicKeyFileName":
                value = phisConfig.phisPublicKeyFileName();
                break;   
            case "pageSizeMax":
                value = phisConfig.pageSizeMax();
                break;  
            default:
                break;
        }
        
        return value;
    }

    public static String getPublicURI() {
        return publicURI;
    }

    private static String getRDF4JProperty(String prop) {
        String value = null;
        
        switch (prop) {
            case "sesameServer":
                value = rdf4jConfig.serverURI();
                break;
            case "repositoryID":
                value = rdf4jConfig.repository();
                break;                
            case "infrastructure":
                value = phisConfig.infrastructure();
                break;
            case "baseURI":
                value = sparqlConfig.baseURI();
                break;
            case "vocabularyContext":
                value = phisConfig.vocabulary();
                break;
            default:
                break;
        }
        
        return value;
    }

    private static String getMongoProperty(String prop) {
         String value = null;
        
        switch (prop) {
            case "host":
                value = mongoConfig.host();
                break;
            case "port":
                value = "" + mongoConfig.port();
                break;
            case "user":
                value = mongoConfig.username();
                break;
            case "password":
                value = mongoConfig.password();
                break;
            case "authdb":
                value = mongoConfig.authDB();
                break;
            case "db":
                value = mongoConfig.database();
                break;                     
            case "documents":
                value = phisConfig.documentsCollection();
                break;
            case "provenance":
                value = phisConfig.provenanceCollection();
                break;
            case "data":
                value = phisConfig.dataCollection();
                break;
            case "images":
                value = phisConfig.imagesCollection();
                break;
            default:
                break;
        }
        
        return value;
    }
}
