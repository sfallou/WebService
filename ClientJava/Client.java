/*
 * javac  -cp "json-simple-1.1.jar:."  ClientJavaREST.java
 * javac -cp "json-simple-1.1.jar:." Client.java 
 * java -cp "json-simple-1.1.jar:." Client
 */

import javax.swing.*;

import org.json.simple.JSONObject;

import java.awt.*;
import java.lang.*;
import java.awt.event.*;
import java.rmi.*;
import java.rmi.Naming;
import java.util.*;
import java.util.Timer;



public class Client extends JFrame implements ActionListener{
	private static String proxyRoot;
	private static String myPseudo;
	private static String myRoom;
	private static long compteur = 0;
	public JPanel panel;
	public JButton buttonValider, buttonQuitter;
	public JTextField pseudo;
	public JLabel labelPseudo;
	public JLabel labelCombo;
	public JComboBox combo;

	public JFrame window = new JFrame("Salon de discussion");
    public JTextArea txtOutput = new JTextArea();
    public JTextField txtMessage = new JTextField();
    public JButton btnSend = new JButton("Envoyer");
    public JButton btnDeco = new JButton("Deconnexion");

	public Client(String titre){
		this.setTitle(titre);
		this.setSize(400,200);
		panel = new JPanel();
		Container contenu = this.getContentPane();
		GridBagLayout g = new GridBagLayout();
		contenu.setLayout(g);
		contenu.add(panel);
		
		buttonValider = new JButton("Valider");
		buttonQuitter = new JButton("Quitter");
		buttonValider.addActionListener(this);
		buttonQuitter.addActionListener(this);

		
		labelPseudo = new JLabel("Pseudo : ");
		pseudo = new JTextField(10);
		labelCombo = new JLabel("Choisir Salon : ");
		ArrayList list = ClientJavaREST.getRooms(proxyRoot+"getRooms");
		String[] lesSalons = (String[]) list.toArray(new String[list.size()]);
		combo = new JComboBox(lesSalons);
		JLabel espace = new JLabel(" ");
		JLabel espace1 = new JLabel(" ");

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.VERTICAL;
		/*---------------------------------------*/
		c.gridx = 1; c.gridy = 0; c.gridwidth = 2;
		contenu.add(labelPseudo, c);
		/*---------------------------------------*/
		c.gridx = 4; c.gridy = 0; c.gridwidth = 2;
		contenu.add(pseudo, c);
		/*---------------------------------------*/
			//Espace
		/*---------------------------------------*/
		c.gridx = 1; c.gridy = 1; c.gridwidth = 2;
		contenu.add(espace, c);
		c.gridx = 4; c.gridy = 1; c.gridwidth = 4;
		contenu.add(espace, c);
		/*---------------------------------------*/
			//Combo
		/*---------------------------------------*/
		c.gridx = 1; c.gridy = 2; c.gridwidth = 2;
		contenu.add(labelCombo, c);
		/*---------------------------------------*/
		c.gridx = 4; c.gridy = 2; c.gridwidth = 4;
		contenu.add(combo, c);
		/*---------------------------------------*/
			//Espace
		/*---------------------------------------*/
		c.gridx = 0; c.gridy = 3; c.gridwidth = 2;
		contenu.add(espace1, c);
		c.gridx =4; c.gridy = 3; c.gridwidth = 2;
		contenu.add(espace1, c);
		/*---------------------------------------*/

			//Buttons
		/*---------------------------------------*/
		c.gridx = 3; c.gridy = 4; c.gridwidth = 2;
		contenu.add(buttonValider, c);
		/*---------------------------------------*/
		c.gridx = 5; c.gridy = 4; c.gridwidth = 2;
		contenu.add(buttonQuitter, c);
		/*---------------------------------------*/

		//this.pack();
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	}

	/*Interface d'accueil aprés avoir donné le pseudo et le salon*/
	
	public Client(String titre, String pseudo, String salon){

        // Assemblage des composants
       
        JPanel panel = (JPanel)this.window.getContentPane();
        JScrollPane sclPane = new JScrollPane(txtOutput);
        panel.add(sclPane, BorderLayout.CENTER);
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(this.txtMessage, BorderLayout.CENTER);
        southPanel.add(this.btnSend, BorderLayout.EAST);
        southPanel.add(this.btnDeco, BorderLayout.WEST);
        panel.add(southPanel, BorderLayout.SOUTH);

        // Gestion des évènements
        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnSend_Message(e, pseudo, salon);
            }
        });

        btnDeco.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deconnecter(e);
            }
        });


        // Initialisation des attributs
        this.txtOutput.setBackground(new Color(220,220,220));
        this.txtOutput.setEditable(false);
        this.window.setSize(500,400);
        this.window.setVisible(true);
        this.txtMessage.requestFocus();

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        //////////////// Récupère périodiquement les messages postés //////////
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override public void run() {
            		getMessage();
            }
        }, 0L, 500L);
    }
	
    public void deconnecter (ActionEvent e) {
    	Client entree = new Client("bonjour");
    	entree.setVisible(true);
    	this.window.setVisible(false);

    }
    
    public void getMessage() {
    		String line = "";
    		long id = 0;
    		ArrayList resultat = ClientJavaREST.getMessages(proxyRoot+"getMessages/"+myRoom+"/"+compteur);
		if(resultat.size() != 0) {
			for(int i=0;i < resultat.size();i++) {
				JSONObject jsonObject = (JSONObject) resultat.get(i);
				if((long) jsonObject.get("id") > compteur) {
					id = (long) jsonObject.get("id");
					line += "\t<"+jsonObject.get("poster")+"> : "+jsonObject.get("content")+ "\n\n";
				}
			}
			compteur = id;
			this.txtOutput.append(line);
		}
    		
    }
    
    public void btnSend_Message(ActionEvent e, String pseudo, String salon) {
        try{
	        	String resultat = ClientJavaREST.postMessage(proxyRoot+"message/post",this.txtMessage.getText(),pseudo,salon);
	    		System.out.println(resultat);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        this.txtMessage.setText("");
        this.txtMessage.requestFocus();
    }


	public void actionPerformed(ActionEvent e){
		if (e.getSource()==buttonValider){
			if (pseudo.getText().equals("")) {
				JOptionPane.showMessageDialog(null,"le pseudo ne peut etre vide", "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
				System.out.println("le pseudo ne peut etre vide");
			}
			else{
				myPseudo = pseudo.getText();
				myRoom = (String) combo.getSelectedItem();
				String resultat = ClientJavaREST.join(proxyRoot+"join/"+myRoom+"/"+myPseudo);
				System.out.println(resultat);
				/*on ouvre le panneau de chat*/
				Client accueil = new Client("Salon de discussion AFNETIC_CHAT",pseudo.getText(),String.valueOf(combo.getSelectedItem()));
				this.dispose();
			}

		}
		else if (e.getSource()==buttonQuitter) {
			this.dispose();
			System.exit(0);
		}
	}

	public static void main(String[] args) {
		JTextField proxyIP = new JTextField();
		JTextField proxyPort = new JTextField();
		final JComponent[] inputs = new JComponent[] {
		        new JLabel("Adresse IP du Proxy\n(e.g : 192.168.43.25)"),
		        proxyIP,
		        new JLabel("Port d'écoute du Proxy\n(e.g : 8080)"),
		        proxyPort
		};
		int result = JOptionPane.showConfirmDialog(null, inputs, "Informations Proxy", JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			if(proxyIP.getText().isEmpty() || proxyPort.getText().isEmpty()) {
				JOptionPane.showMessageDialog(null,"Pas de case vide!\nVeuillez redémarrer!", "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
				System.out.println("les cases ne peuvent etre vides");
			}
			else {
				proxyRoot = "http://"+proxyIP.getText()+":"+proxyPort.getText()+"/Messagerie/Chat/Room/";
			    System.out.println("Proxy: "  + proxyRoot);
			    Client entree = new Client("Bienvenue au salon");
				entree.setVisible(true);	
			}
		} else {
		    System.out.println("Annulé = " + result);
		}
	}

}

