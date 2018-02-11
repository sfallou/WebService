package proxy;
import java.util.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("Room")
public class Proxy {
    private static List<Message> messages;
    public Proxy() {}
   
    /**
     * Permet de s'inscrire à un salon de discussion en faisant appel à méthode suscribe du proxyRPC.
     * Verb : GET
     * Endpoint : http://@ip_serveur_proxy:8080/Messagerie/Chat/Room/join/nomSalon/pseudo
     * @param nomSalon Le salon de discussion que l'utilisateur souhaite rejoindre
     * @param pseudo Le pseudonyme de l'utilisateur
     * @return object Response avec un body = { "[INFO] : "+pseudo+" joined the chatroom "+ room; }
     */
    @GET
	@Path("join/{room}/{pseudo}")
	public Response suscribe(
			@PathParam("room") String room,
			@PathParam("pseudo") String pseudo) {
    			Boolean response = ProxyRPC.subscribe(pseudo, room);
    			String result;
    			if(response) {
    				 result = "[INFO] : "+pseudo+" joined the chatroom "+ room;
    			}
    			else {
    				 result = "[INFO] Failed";
    			}
    			
    			return Response.status(200)
        				.entity(result )
        				.build();
    	}
    
    /**
     * Permet de récupérer la liste des message d'un salon donné en faisant appel à méthode getMessages du proxyRPC.
     * Verb : GET
     * Endpoint : http://@ip_serveur_proxy:8080/Messagerie/Chat/Room/getMessages/nomSalon/lastMsg
     * @param nomSalon Le salon de discussion que l'utilisateur souhaite rejoindre
     * @param lastMessage  Le numéro du dernier message que l'on a reçu 
     * @return object Response avec un body = { "[INFO] : "+pseudo+" joined the chatroom "+ room; }
     */
    @GET
    @Path("getMessages/{room}/{lastMsg}")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Message> getLastMessages(
    		@PathParam("room") String room,
    		@PathParam("lastMsg") int lastMsg
    		) {
    		return ProxyRPC.getMessages(lastMsg, room);
    }
    
    /**
     * Permet de poster un message dans un salon de discussion en faisant appel à la méthode postMessage du ProxyRPC
     * Verb : POST
     * Endpoint : http://@ip_serveur_proxy:8080/Messagerie/Chat/Room/messages/post
     * @param message Le message que l'on désire poster sous forme json comme par exemple: {"content": "Hello World", "id": 0, "poster": "Abdou", "room": "DUT1" }
     * @param room Le salon de discussion dans lequel on veut poster le message
     * @return object Response avec un body = {  "Posted : SUCCESS" ou  "Posted : FAILED" } 
     */
    @POST
    @Path("message/post")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(Message msg) {
    		String result = "Test";
    		Boolean response = ProxyRPC.postMessage(msg,msg.getRoom());
    		
    		if(response != null && response == true) {
    			result = "Posted : SUCCESS";
    		}
    		else {
    			result = "Posted : FAILED";
    		}
    		return Response.status(200)
    				.entity(result)
    				.build();
    }
   
    /**
     * Permet d'obtenir la liste des salons de discussion disponibles en faisant appel à la méthode getRooms du ProxyRPC
     * Verb : GET
     * Endpoint : http://@ip_serveur_proxy:8080/Messagerie/Chat/Room/getRooms
     * @return Un tableau de chaînes de caractères dont chacune représente le nom d'un salon de discussion
     */
    @GET
    @Path("getRooms")
    @Produces(MediaType.APPLICATION_JSON)
    public String[] getRooms() {
    		return ProxyRPC.getRooms();
    }
}