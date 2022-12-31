package com.samknows.server;

import com.samknows.utility.XMLUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ServerConfigTest {

    @Mock
    private ServerConfig serverConfig;
    private Hashtable<String, Node> xmlMap;
    @BeforeEach
    public void setup() {
        serverConfig = mock(ServerConfig.class);
    }

    @Test
    public void testServerConfig_Success() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document xmlRequests = db.parse(new File("src/main/resources/requests.xml"));
        this.xmlMap = XMLUtility.getRequestPathMap(xmlRequests);

        when(serverConfig.getXmlRequestsMap()).thenReturn(this.xmlMap);
        Hashtable<String, Node> serverRequests = ServerConfig.getInstance().getXmlRequestsMap();
        Set<String> requestKeys = this.xmlMap.keySet();
        for (String request: requestKeys) {
            assertTrue(this.xmlMap.get(request).isEqualNode(serverRequests.get(request)));
        }
        assertTrue(ServerConfig.isServerConfigured());
        System.out.println("Success Test Successfully completed");
    }

    @Test
    public void testServerConfig_Failure() {
        try{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document xmlRequests = db.parse(new File("src/main/resources/requests.xml"));
            this.xmlMap = XMLUtility.getRequestPathMap(xmlRequests);

            Properties properties = new Properties();
            properties.setProperty(ServerConfigProperties.FILTER_REQUESTS_SCHEMA.getProperty(), "src/test/resources/schemas/requests.xsd");
            properties.setProperty(ServerConfigProperties.FILTER_REQUESTS_XML.getProperty(), "src/test/resources/requests.xml");

            when(serverConfig.getXmlRequestsMap()).thenReturn(this.xmlMap);
            Hashtable<String, Node> serverRequests = ServerConfig.getInstance(properties).getXmlRequestsMap();
            Set<String> requestKeys = this.xmlMap.keySet();
            for (String request: requestKeys) {
                assertTrue(this.xmlMap.get(request).isEqualNode(serverRequests.get(request)));
            }

        }catch (Exception ex){
            System.out.println("Exception: "+ex);
            assertFalse(ServerConfig.isServerConfigured());
            System.out.println("Failure Test Successfully completed");
        }
    }
}