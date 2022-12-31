package com.samknows.server;

import com.samknows.utility.XMLUtility;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;

/**
 * This method is intended to load and prepare the server with the config files
 * The config files may be needed for server configuration or app configuration
 * Ex: We have request xml and xsd - if they are not evaluated and loaded the server should not start
 */
public class ServerConfig {

    public static ServerConfig serverConfig;
    private final Hashtable<String, Node> xmlRequestsMap;

    //Configuration properties loaded from metrics.properties file
    //This Property will be used to configure the server and no need to expose now
    private static Properties serverProperties;
    private static boolean serverConfigured = false;

    private ServerConfig(Hashtable<String, Node> xmlRequestsMap){
        LOGGER.log(Level.INFO, "ServerConfig Object Instantiated");
        this.xmlRequestsMap = xmlRequestsMap;
    }


    static Logger LOGGER = LogManager.getLogger(ServerConfig.class.getName());
    private static ServerConfig setupServer(){
        try{
            //loading server properties
            if(serverProperties == null || serverProperties.isEmpty()){
                try(InputStream propertyFile = new FileInputStream("src/main/resources/metrics.properties")){
                    LOGGER.log(Level.INFO, "ServerProperties is Empty, Loading properties from property file");
                    serverProperties = new Properties();
                    serverProperties.load(propertyFile);
                }catch (IOException io){
                    LOGGER.log(Level.FATAL, "Exception occurred while loading the properties file");
                    return null;
                }
            }

            //Validate the xml

            File xsdFile = new File(serverProperties.getProperty(ServerConfigProperties.FILTER_REQUESTS_SCHEMA.getProperty()));
            File xmlFile = new File(serverProperties.getProperty(ServerConfigProperties.FILTER_REQUESTS_XML.getProperty()));

            LOGGER.log(Level.INFO, "Setting up the Request XML");

            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(xsdFile);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xmlFile));

            LOGGER.log(Level.INFO, "Request Schema and Request XML file validated. Now Loading into ServerConfig Object");

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document xmlDoc = db.parse(xmlFile);


            //Further configurations shall be added from here or when grows to large scale shall be moved to different classes
            //Different types of Exceptions shall be captured in the catch block

            Hashtable<String, Node> xmlMap = XMLUtility.getRequestPathMap(xmlDoc);
            if(xmlMap == null || xmlMap.isEmpty()){
                LOGGER.log(Level.FATAL, "xmlMap is Empty or null, Server Configuration stopped");
                return null;
            }

            serverConfig = new ServerConfig(xmlMap);

            serverConfigured = true;
            return serverConfig;
        }catch (Exception ex){ //Generalising the XML Exception for now
            LOGGER.log(Level.FATAL, "Exception occurred while loading and configuring the Request XML for Server Configuration ", ex);
            return null;
        }
    }

    public static ServerConfig getInstance(){
        if(serverConfig == null){
            LOGGER.log(Level.INFO, "Setting up server");
            setupServer();
        }
        return serverConfig;
    }

    //instance method that shall be used for custom server configuration
    public static ServerConfig getInstance(Properties properties){
        if(serverConfig == null){
            LOGGER.log(Level.INFO, "Setting up server with custom properties");
            serverProperties = properties;
            setupServer();
        }
        return serverConfig;
    }

    public Hashtable<String, Node> getXmlRequestsMap() {
        return xmlRequestsMap;
    }

    public static boolean isServerConfigured() {
        return serverConfigured;
    }
}
