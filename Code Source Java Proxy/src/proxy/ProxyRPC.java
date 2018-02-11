package proxy;

import java.util.*;
import org.apache.xmlrpc.*;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

// javadoc -d documentation -classpath "org.apache.commons.codec_1.6.0.v201305230611.jar;xmlrpc-2.0.jar;." Proxy.java

// javac Message.java

// javac -cp "org.apache.commons.codec_1.6.0.v201305230611.jar;xmlrpc-2.0.jar;." Proxy.java
// java -cp "org.apache.commons.codec_1.6.0.v201305230611.jar;xmlrpc-2.0.jar;." Proxy

// jar -cvfm Proxy.jar manifestProxy Proxy.class Message.class org.apache.commons.codec_1.6.0.v201305230611.jar xmlrpc-2.0.jar
// java -jar Proxy.jar

public class ProxyRPC {
	/**
     * Permet de s'inscrire à un salon de discussion
     * @param pseudo Le pseudonyme de l'utilisateur
     * @param room Le salon de discussion que l'utilisateur souhaite rejoindre
     * @return Un booléen indiquant si l'inscription a réussi ou non 
     */
	public static Boolean subscribe(String pseudo, String room) {
		Boolean result = false;
		String  service = "subscribe";
		try {
			Vector params = new Vector();
			params.addElement(new String(pseudo));
			Object res = Redirect(room, service, params);
			result = ((Boolean) res);
		}
		catch (Exception exception) {
			System.err.println("Proxy Subscribe : " + exception.toString()); exception.printStackTrace();
		}
		return result;
	}

	/**
     * Permet de se désinscrire d'un salon de discussion
     * @param pseudo Le pseudonyme de l'utilisateur
     * @param room Le salon de discussion que l'utilisateur souhaite quitter
     * @return Un booléen indiquant si la désinscription a réussi ou non 
     */
	public static Boolean unsubscribe(String pseudo, String room) {
		Boolean result = false;
		String  service = "unsubscribe";
		try {
			Vector params = new Vector();
			params.addElement(new String(pseudo));
			Object res = Redirect(room, service, params);
			result = ((Boolean) res);
		}
		catch (Exception exception) {
			System.err.println("Proxy Unubscribe : " + exception.toString()); exception.printStackTrace();
		}
		return result;
	}

	/**
     * Permet de poster un message dans un salon de discussion
     * @param message Le message que l'on désire poster
     * @param room Le salon de discussion dans lequel on veut poster le message
     * @return Un booléen indiquant si l'envoi du message au salon a réussi ou non 
     */
	public static Boolean postMessage(Message message, String room) {
		
		Boolean result = false;
		String  service = "postMessage";
		try {
			Vector params = new Vector();
			params.addElement(message.getPoster());
			params.addElement(message.getContent());
			params.addElement(message.getRoom());
			Object res = Redirect(room, service, params);
			result = ((Boolean) res);
		}
		catch (Exception exception) {
			System.err.println("Proxy GetMessages : " + exception.toString()); exception.printStackTrace();
		}
		return result;
		
		
	}

	/**
     * Permet d'obtenir la liste des nouveaux messages postés dans un salon de discussion à partir d'un certain numéro
     * @param numLastMsg Le numéro du dernier message que l'on a reçu
     * @param room Le salon de discussion dont on veut obtenir les messages
     * @return Une collection (de type ArrayList) de messages postérieurs à celui dont le numéro est fourni en argument 
     */
	public static ArrayList<Message> getMessages(int numLastMsg, String room) {
		ArrayList<Message> result = new ArrayList<>();
		String  service = "getMessages";
		try {
			Vector params = new Vector();
			params.addElement(numLastMsg);

			XmlRpcClient server = new XmlRpcClient("http://" + getAddress(room) + "/RPC2");
			String msg = (String) server.execute("server" + "." + service, params);
			String tabMsg[] = msg.split(":");
			String m[];
			if (!msg.equals("")) {
				for (int i=0; i<tabMsg.length; i++) {
					m = tabMsg[i].split(";");
					if (m.length == 4) {
						result.add(new Message(Integer.parseInt(m[0]), m[1], m[2], m[3]));
					}
				}
			}
		}
		catch (Exception exception) {
			System.err.println("Proxy GetMessages : " + exception.toString()); exception.printStackTrace();
		}
		return result;
	}

	/**
     * Permet au proxy d'obtenir l'adresse de la machine serveur qui héberge un salon de discussion donné en 
     * consultant son fichier de configuration
     * @param room Le nom du salon de discussion
     * @return Une chaîne de caractères représentant l'adresse du serveur 
     */
	public static String getAddress(String room) {
		String infoServer = "";
        // Etape 1 : recuperation d'une instance de la classe "DocumentBuilderFactory"
       final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 	
       try {
       	//Etape 2 : creation d'un parseur
           final DocumentBuilder builder = factory.newDocumentBuilder();
           //Etape 3 : creation d'un Document
	    final Document document= builder.parse(ProxyRPC.class.getResourceAsStream("../config.xml"));
	    //Etape 4 : recuperation de l'Element racine
	    final Element racine = document.getDocumentElement();
	    //Etape 5 : recuperation des servers
	    final NodeList racineNoeuds = racine.getChildNodes();
	  //Etape 5 : recuperation des attributs
	    final int nbRacineNoeuds = racineNoeuds.getLength();
	    for (int i = 0; i<nbRacineNoeuds; i++) {
	        if(racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
	            final Element server = (Element) racineNoeuds.item(i);
	            if (server.getAttribute("nom").equalsIgnoreCase(room))
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

	/**
     * Permet d'obtenir la liste des salons de discussion disponibles (hébergés chacun sur une machine différente)
     * en consultant son fichier de configuration
     * @return Un tableau de chaînes de caractères dont chacune représente le nom d'un salon de discussion
     */
	public static String[] getRooms() {
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
	
	/**
     * Permet au proxy de rediriger une requête vers le serveur qui héberge le salon de discussion de l'utilisateur
     * en utilisant le protocle XML-RPC
     * @param room Le nom du salon de discussion
     * @param service La méthode devant être executée sur l'objet serveur distant
     * @param params Le vecteur contenant la liste des paramètres
     * @return Un objet représentant le résultat de l'exécution de la requête
     */
	private static Object Redirect(String room, String service, Vector params) {
		Object result = null;
		try {
			XmlRpcClient server = new XmlRpcClient("http://" + getAddress(room) + "/RPC2");
			result = server.execute("server" + "." + service, params);
		}
		catch (Exception exception) {
			System.err.println("Proxy Redirect : " + exception.toString()); exception.printStackTrace();
		}
		return result;
	}
}
