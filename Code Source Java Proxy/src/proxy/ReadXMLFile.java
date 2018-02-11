package proxy;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;




public class ReadXMLFile {
	//public static String Salon = "";
	
	// Cette fonction permet de recuperer la liste des tous les salons
		public static  String[] MesSalon() {
		String Salon = "";
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 	
        try {
        final DocumentBuilder builder = factory.newDocumentBuilder();
	    final Document document= builder.parse(ReadXMLFile.class.getResourceAsStream("../config.xml"));
	    final Element racine = document.getDocumentElement();
	    final NodeList racineNoeuds = racine.getChildNodes();
	    final int nbRacineNoeuds = racineNoeuds.getLength();
	    for (int i = 0; i<nbRacineNoeuds; i++) {
	        if(racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
	            final Element server = (Element) racineNoeuds.item(i);
	            //if (server.getAttribute("nom").equalsIgnoreCase(LeSalon))
	            	Salon =Salon+ server.getAttribute("nom")+":";
	        		}				
	    		} 
        }
       
        catch (final ParserConfigurationException e) {
            e.printStackTrace(); }
        catch (final SAXException e) {
            e.printStackTrace(); }
        catch (final IOException e) {
            e.printStackTrace(); }
        String[] Salle = Salon.split(":");
        return Salle;
	}
	
	// Cette fonction permet de recuperer le port et l'addresse IP du salon demande
	public static  String MonServer(String LeSalon) {
		String infoServer = "";
         // Etape 1 : recuperation d'une instance de la classe "DocumentBuilderFactory"
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 	
        try {
        	//Etape 2 : creation d'un parseur
            final DocumentBuilder builder = factory.newDocumentBuilder();
            //Etape 3 : creation d'un Document
	    final Document document= builder.parse(new File("../config.xml"));
	    //Etape 4 : recuperation de l'Element racine
	    final Element racine = document.getDocumentElement();
	    //Etape 5 : recuperation des servers
	    final NodeList racineNoeuds = racine.getChildNodes();
	  //Etape 5 : recuperation des attributs
	    final int nbRacineNoeuds = racineNoeuds.getLength();
	    for (int i = 0; i<nbRacineNoeuds; i++) {
	        if(racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
	            final Element server = (Element) racineNoeuds.item(i);
	            if (server.getAttribute("nom").equalsIgnoreCase(LeSalon))
	            		infoServer =infoServer+ server.getAttribute("ip")+":"+ server.getAttribute("port");
	        		}				
	    		}
        }
        catch (final ParserConfigurationException e) {
            e.printStackTrace(); }
        catch (final SAXException e) {
            e.printStackTrace(); }
        catch (final IOException e) {
            e.printStackTrace(); }
         
        return infoServer;
	}
	

	/*public static void main(String[] args) {
        //String LeSalon = "DIC2";
		//String LeServeur = MonServer(LeSalon);
		//System.out.println(LeServeur);
        String[] Salle = MesSalon();
        System.out.println(Salle.length);

	}
*/
}
