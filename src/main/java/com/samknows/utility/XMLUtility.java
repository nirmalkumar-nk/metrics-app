package com.samknows.utility;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Hashtable;

/**
 * A utility class for XML Operations
 */

public class XMLUtility {
    static Logger LOGGER = LogManager.getLogger(XMLUtility.class.getName());
    public static Hashtable getRequestPathMap(Document xmlDoc){
       try {
           Hashtable<String, Node> xmlMap = new Hashtable<>();
           NodeList childNodes = xmlDoc.getDocumentElement().getElementsByTagName("url");
           for(int i=0; i<childNodes.getLength(); i++){
               Node node = childNodes.item(i);
               xmlMap.put(node.getAttributes().getNamedItem("path").getNodeValue()
                       +"_"
                       +node.getAttributes().getNamedItem("method").getNodeValue(),
                       node);
           }
           LOGGER.log(Level.INFO, "XML Request Mapping done successfully");
           return xmlMap;
       }catch (Exception ex){
           LOGGER.log(Level.FATAL, "XML Request Mapping failed with an exception {0}", ex);
           return null;
       }
    }
}
