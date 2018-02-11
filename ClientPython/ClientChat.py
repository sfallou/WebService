# -*- coding: utf-8 -*-

try:
    import Tkinter as Tkinter
    import tkMessageBox as messagebox
    import Queue as Queue
except ImportError:
    import tkinter as Tkinter
    import tkinter.messagebox as messagebox
    import queue as Queue
import time
import threading
import random
import requests
import style as css

#proxyRoot = "http://192.168.0.14:8080/Messagerie/Chat/Room/"
SALON = ""
PROXY = ""

class GuiPart:
    def __init__(self, master, queue, endCommand):
        self.queue = queue
        self.proxyRoot = ""
        self.salons = ""
        # Set up the GUI

        ###################### Login Section #######################################
        self.frame_Login = Tkinter.LabelFrame(master,text="Connexion au Server Proxy", fg=css.fgColor, font=css.titreFont, bg="white",bd=10,labelanchor="n")
        self.frame_Login.grid(padx=5,pady=5,row=0,column=0)
        
        self.labelIP = Tkinter.Label(self.frame_Login,text="Adresse IP Proxy ", fg="#03224C", font=css.fontSimple, bg='white')
        self.labelIP.grid(padx=2, row=0 ,column=0)
        
        self.EntryIP = Tkinter.Entry(self.frame_Login, fg="#03224C", font=css.fontSimple, bg='white', width=20)
        self.EntryIP.grid(padx=2, row=1 ,column=0)

        self.labelPort = Tkinter.Label(self.frame_Login,text="Port Proxy ", fg="#03224C", font=css.fontSimple, bg='white')
        self.labelPort.grid(padx=2, row=0 ,column=1)
        
        self.EntryPort = Tkinter.Entry(self.frame_Login, fg="#03224C", font=css.fontSimple, bg='white', width=10)
        self.EntryPort.grid(padx=2, row=1 ,column=1)

        self.boutonLogin = Tkinter.Button(self.frame_Login,text='Se connecter',bd=1,width=10, relief=Tkinter.RAISED, overrelief=Tkinter.RIDGE, bg=css.buttonColor,command=self.connexion)
        self.boutonLogin.grid(padx=2, row=1 ,column=2)
             

        ###################### First Section #######################################
        self.frame_0 = Tkinter.LabelFrame(master,text="Connexion à un salon", fg=css.fgColor, font=css.titreFont, bg="white",bd=10,labelanchor="n")
        self.frame_0.grid(padx=5,pady=5,row=1,column=0)
        
        self.labelPseudo = Tkinter.Label(self.frame_0,text="Pseudo :", fg="#03224C", font=css.fontSimple, bg='white')
        self.labelPseudo.grid(padx=2, row=0 ,column=0)
        
        self.EntryPseudo = Tkinter.Entry(self.frame_0, fg="#03224C", font=css.fontSimple, bg='white', width=20)
        self.EntryPseudo.grid(padx=2, row=0 ,column=1)

        self.boutonJoin = Tkinter.Button(self.frame_0,text='Joindre',bd=1,width=10, relief=Tkinter.RAISED, overrelief=Tkinter.RIDGE, bg=css.buttonColor,command=self.join)
        self.boutonJoin.grid(padx=2, row=0 ,column=3)

        ###################### Second Section #######################################
        self.frame_1 = Tkinter.LabelFrame(master,text="Posted Messages", fg=css.fgColor, font=css.titreFont, bg="white",bd=10,labelanchor="n")
        self.frame_1.grid(padx=5,pady=5,row=2,column=0)

        self.zoneText = Tkinter.Text(self.frame_1,width=80,height=20, bg="white",foreground='#03224C', font=('Helvetica',12,'bold'))
        self.zoneText.grid(padx=5,pady=5,row=0,column=0)

        self.scrollbar = Tkinter.Scrollbar(self.frame_1, command=self.zoneText.yview)
        self.scrollbar.grid(row=0,column=1,sticky="nsew")
        self.zoneText['yscrollcommand'] = self.scrollbar.set

        ###################### Third Section #######################################
        self.frame_2 = Tkinter.LabelFrame(master,text="Post Messages", fg=css.fgColor, font=css.titreFont, bg="white",bd=10,labelanchor="n")
        self.frame_2.grid(padx=5,pady=5,row=3,column=0)

        self.EntryMessage = Tkinter.Entry(self.frame_2, fg="#03224C", font=css.fontSimple, bg='white', width=70)
        self.EntryMessage.grid(padx=2, row=0 ,column=0)
        
        self.boutonSend = Tkinter.Button(self.frame_2,text='Envoyer',bd=1,width=10, relief=Tkinter.RAISED, overrelief=Tkinter.RIDGE, bg=css.buttonColor,command=self.post_message)
        self.boutonSend.grid(padx=2, row=0 ,column=1)
        

        self.frame_3 = Tkinter.LabelFrame(master,text="", fg=css.fgColor, font=css.titreFont, bg="white",bd=1,labelanchor="n")
        self.frame_3.grid(padx=5,pady=5,row=4,column=0)
        
        self.boutonClear = Tkinter.Button(self.frame_3,text="Clear",bd=1, relief=Tkinter.RAISED, overrelief=Tkinter.RIDGE, bg=css.buttonColor,command=self.clean_interface)
        self.boutonClear.grid(padx=2, row=0 ,column=1)

        ###################### Last Section #######################################
        self.ButtonQuit = Tkinter.Button(master, text='Quitter', command=endCommand)
        self.ButtonQuit.grid(padx=2, row=5 ,column=0)
        master.protocol("WM_DELETE_WINDOW", endCommand)

        ###################### Disabling some sections ############################
        self.disable_fenetre(self.frame_0)
        self.disable_fenetre(self.frame_1)
        self.disable_fenetre(self.frame_2)

    def connexion(self):
        # Get the rooms
        try:
            ip = self.EntryIP.get()
            port = self.EntryPort.get()
            if ip  != "" and port != "" :
                proxyIP = ip
                proxyPort = port
                self.proxyRoot = "http://"+proxyIP+":"+proxyPort+"/Messagerie/Chat/Room/"
                #print(self.proxyRoot)
        except Exception as error:
            print('Erreur de saisie: ' + repr(error))
        
        try:
            self.salons = eval(requests.get(self.proxyRoot+"getRooms").text)
            self.choixSalon = Tkinter.StringVar(self.frame_0)
            self.choixSalon.set("Choisir un salon")
            self.EntryChoixSalon = Tkinter.OptionMenu(self.frame_0, self.choixSalon,*self.salons)
            self.EntryChoixSalon.config(width=20)
            self.EntryChoixSalon.grid(padx=2,row=0,column=2)
            self.disable_fenetre(self.frame_Login)
            self.enable_fenetre(self.frame_0)

        except:   
            messagebox.showerror("Aucun salon de discussion !", " Veuillez vérifier votre connexion")
    
        """      
        -----------------------------------------------
        Fonction qui permet d'afficher les messages
        -----------------------------------------------
        """
    def processIncoming(self):
        while self.queue.qsize():
            try:
                msg = self.queue.get(0)
                self.zoneText.insert(Tkinter.INSERT,str(msg)+"\n")
            except Queue.Empty:
                pass

        """
        -----------------------------------------------
        Fonction qui permet de joindre un salon
        -----------------------------------------------
        """
    def join(self):
        global SALON, PROXY
        try:
            pseudo = self.EntryPseudo.get()
            salon = self.choixSalon.get()
            print('pseudo : ' + pseudo)
            print('salon : ' + salon)
            if salon  != "Choisir un salon" and pseudo  != "" :
                self.pseudo = pseudo
                self.salon = salon
                SALON = salon
                PROXY = self.proxyRoot
                result = requests.get(self.proxyRoot+"join/"+self.salon+"/"+self.pseudo)
                print(result.text)
                # On désactive la fenetre de connexion et on active les deux autres
                if result.text == "[INFO] : "+self.pseudo+" joined the chatroom "+self.salon:
                    self.disable_fenetre(self.frame_0)
                    self.enable_fenetre(self.frame_1)
                    self.enable_fenetre(self.frame_2)
        except Exception as error:
            print('Erreur de saisie: ' + repr(error))


        """
        -----------------------------------------------
        Fonction qui permet de poster un message
        -----------------------------------------------
        """
    def post_message(self):
        try:
            msg = self.EntryMessage.get()
            if msg  != "":
                url = self.proxyRoot+"message/post"
                payload = "{\n    \"content\": \""+msg+"\",\n    \"id\": 0,\n    \"poster\": \""+self.pseudo+"\",\n    \"room\": \""+self.salon+"\"\n}"
                headers = {
                    'content-type': "application/json",
                    'cache-control': "no-cache",
                    'postman-token': "2107eef0-f695-68ce-a2bc-977a51e9b45e"
                }
                response = requests.request("POST", url, data=payload, headers=headers)
                print(response.text)
                self.EntryMessage.delete(0,Tkinter.END)
                
        except Exception as error:
            print('Erreur de saisie: ' + repr(error))


        """      
        -----------------------------------------------
        Fonction qui permet de désactiver des widgets
        -----------------------------------------------
        """
    def disable_fenetre(self,widget,state='disabled'):
        try:
            widget.configure(state=state)
        except Tkinter.TclError:
            pass
        for child in widget.winfo_children():
            self.disable_fenetre(child,state=state)

        """
        -----------------------------------------------
        Fonction qui permet de désactiver des widgets
        -----------------------------------------------
        """

    def enable_fenetre(self,widget,state='normal'):
        try:
            widget.configure(state=state)
        except Tkinter.TclError:
            pass
        for child in widget.winfo_children():
            self.disable_fenetre(child,state=state)

        """
        -----------------------------------------------
        Fonction qui permet d'effacer du text
        -----------------------------------------------
        """
    def clean_interface(self):
        self.zoneText.delete('0.0',Tkinter.END)





class ThreadedClient:
    def __init__(self, master):
        self.master = master
        # Create the queue
        self.queue = Queue.Queue()

        # Set up the GUI part
        self.gui = GuiPart(master, self.queue, self.endApplication)

        # Set up the thread to do asynchronous I/O
        self.running = 1
        self.thread1 = threading.Thread(target=self.workerThread1)
        self.thread1.start()

        # Start the periodic call in the GUI to check if the queue contains
        # anything
        self.periodicCall()

    def periodicCall(self):
        """
        Check every 100 ms if there is something new in the queue.
        """
        self.gui.processIncoming()
        if not self.running:
            import sys
            sys.exit(1)
        self.master.after(100, self.periodicCall)

    def workerThread1(self):
        compt = 0
        while self.running:
            time.sleep(0.5)
            try:
                data = eval(requests.get(PROXY+"getMessages/"+SALON+"/"+str(compt)).text)
                if len(data) != 0:
                    for msg in data:
                        if int(msg["id"]) > int(compt):
                            id = msg["id"]
                            poster = msg["poster"]
                            content = msg["content"]
                            line = "\n< "+poster+ "> : "+ content+"\n"
                    compt = id
                    self.queue.put(line)
            except:
                pass

    def endApplication(self):
        self.running = 0


##############################################################################

if __name__ == '__main__':
    root = Tkinter.Tk()
    root.configure(bg=css.bgColor)
    root.title("Discussion instantanée")
    client = ThreadedClient(root)
    root.mainloop()