import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ClientJavaREST {
	
	/****************** GET ROOMS *****************/
	public static ArrayList getRooms(String myUrl) {
		try {

			URL url = new URL(myUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
			ArrayList resultats = new ArrayList();
			String output;
			String rooms = "";
			//System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				rooms += output;
			}
			
			conn.disconnect();
			String[]res = rooms.split(",|\"|]|\\[");
			for(int i=0;i<res.length;i++) {
				if (res[i].trim().length() > 0)
					resultats.add(res[i]);
			}
			return resultats;

		  } catch (MalformedURLException e) {

			e.printStackTrace();

		  } catch (IOException e) {

			e.printStackTrace();

		  }
		return null;
	}
	/******************* JOIN **********************/
	public static String join(String myUrl) {
		try {

			URL url = new URL(myUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

			String output;
			String res = "";
			//System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				res += output;
			}
			
			conn.disconnect();
			
			return res;

		  } catch (MalformedURLException e) {

			e.printStackTrace();

		  } catch (IOException e) {

			e.printStackTrace();

		  }
		return null;
	}
	
	/****************** GET Messages *****************/
	public static ArrayList<JSONObject> getMessages(String myUrl) {
		try {

			URL url = new URL(myUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
			ArrayList resultats = new ArrayList();
			String output;
			String msgs = "";
			//System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				msgs += output;
			}
			
			conn.disconnect();
			String[]res = msgs.split("]|\\[|},|}");
			JSONParser parser = new JSONParser();
			for(int i=0;i<res.length;i++) {
				if(res[i].trim().length() > 0) {
					try {
						JSONObject json = (JSONObject) parser.parse((String) (res[i]+"}"));
						resultats.add(json);	
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
			}
			return resultats;
			
			//return res;

		  } catch (MalformedURLException e) {

			e.printStackTrace();

		  } catch (IOException e) {

			e.printStackTrace();

		  }
		return null;
	}
	
	/*************** POST MESSAGE ****************/
	public static String postMessage(String myURL, String content, String poster, String room) {
		  try {
			URL url = new URL(myURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			String input = "{\n    \"content\": \""+content+"\",\n    \"id\": 0,\n    \"poster\": \""+poster+"\",\n    \"room\": \""+room+"\"\n}";
			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
			String output;
			String res="";
			while ((output = br.readLine()) != null) {
				res += output;
				//System.out.println(output);
			}

			conn.disconnect();
			
			return res;

		  } catch (MalformedURLException e) {

			e.printStackTrace();

		  } catch (IOException e) {

			e.printStackTrace();

		 }
		  return null;

		}

	
	/**************** FIN *******************/
	
	
	public static void main(String[] args) {
		
		/*ArrayList rooms = getRooms("http://192.168.0.13:8080/Messagerie/Chat/Room/getRooms");
		System.out.println(rooms);
		*/
		
		/*String resultat = join("http://192.168.0.13:8080/Messagerie/Chat/Room/join/DUT1/sfallou");
		System.out.println(resultat);
		*/
		
		
		/*ArrayList resultat = getMessages("http://192.168.0.13:8080/Messagerie/Chat/Room/getMessages/DUT1/0");
		JSONObject jsonObject = (JSONObject) resultat.get(0);
		System.out.println(jsonObject.get("id"));
		*/
		/*
		String resultat = postMessage("http://192.168.0.13:8080/Messagerie/Chat/Room/message/post","Ok OK","Simon","DUT1");
		System.out.println(resultat);
		*/
		
	}

}