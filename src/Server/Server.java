package Server;

import java.net.*;
import java.util.ArrayList;
import java.io.*;
import javax.swing.JTextArea;
import ciphers.PrivateKeyPacket;
import ciphers.PublicKeyPacket;
import ciphers.SecretKeyPacket;
import Client.Message;
import NodeInfos.NodeInfo;
/**
 * Klasa odpowiedzialna za odbieranie wiadomosci do serwera oraz tworzenie i wysylanie odpowiedzi na nie jesli to konieczne
 *
 */
public class Server {

	private Object object;
	private JTextArea textArea;
	private ArrayList<NodeInfo> nodes;
	private ArrayList<PublicKeyPacket> publicRSAKeys;
	private PrivateKeyPacket myPrivateRSAKey;

//	private ArrayList<PublicKeyPacket> publicElGamalKeys;
//	private PrivateKeyPacket myPrivateElGamalKey;
	private ArrayList<SecretKeyPacket> secretKeyPackets;
	private PublicKeyPacket myDiffiePublicKeyPacket;
	private PrivateKeyPacket myDiffiePrivateKeyPacket;
	private ArrayList<Long>rsaGenerationTime;
	private ArrayList<Long>aesGenerationTime;
	

/**
 *  Konstruktor klasy Server - serwer przyjmuje jako argument port routera, pole tekstowe aby mogl wypisywac komunikaty, listê wszystkich wezlow, klucze routera oraz listy kluczy innych routerow oraz czasy generacji
 * @param port
 * @param textArea
 * @param nodes
 * @param publicRSAKeys
 * @param myPrivateRSAKey
 * @param myDiffiePrivateKeyPacket
 * @param secretKeyPackets
 * @param myDiffiePublicKeyPacket
 * @param rsaGenerationTime
 * @param aesGenerationTime
 * @throws IOException
 */
	public Server(int port,JTextArea textArea,ArrayList<NodeInfo> nodes,ArrayList<PublicKeyPacket> publicRSAKeys,PrivateKeyPacket myPrivateRSAKey,PrivateKeyPacket myDiffiePrivateKeyPacket,ArrayList<SecretKeyPacket> secretKeyPackets, PublicKeyPacket myDiffiePublicKeyPacket,ArrayList<Long>rsaGenerationTime,ArrayList<Long>aesGenerationTime) throws IOException 
		{   
			ServerSocket ss = new ServerSocket(port);
			this.aesGenerationTime=aesGenerationTime;
			this.rsaGenerationTime=rsaGenerationTime;
			this.myDiffiePublicKeyPacket = myDiffiePublicKeyPacket;
			this.myPrivateRSAKey=myPrivateRSAKey;
			this.publicRSAKeys=publicRSAKeys;
			this.secretKeyPackets=secretKeyPackets;
//  		this.myPrivateElGamalKey=myPrivateElGamalKey;
//    		this.publicElGamalKeys=publicElGamalKeys;
			this.myDiffiePrivateKeyPacket=myDiffiePrivateKeyPacket;
			this.textArea=textArea;
			this.nodes=nodes;
			while(true) {
				new ServerThread(ss.accept());
				}
			}
	/**
	 *	Klasa bêd¹ca w¹tkiem serwera, tu w³asnie odbywa siê odbiór i obs³uga wiadomoœci
	 * 
	 */
  private class ServerThread extends Thread {
    private final Socket socket;
    public ServerThread(Socket socket) {
      this.socket = socket;
      start();
    }
    public void run() {
      try {
        ObjectOutputStream out = new ObjectOutputStream(
          socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(
          socket.getInputStream());
        while(true) {
         object= in.readObject();		//odebranie wiadomosci
         Message x = (Message)object;
         MessageHandler handler = new MessageHandler(x,textArea,nodes,publicRSAKeys,myPrivateRSAKey,myDiffiePrivateKeyPacket, secretKeyPackets,myDiffiePublicKeyPacket,rsaGenerationTime,aesGenerationTime);
         handler.handleMessage();				///obsluga odebranej wiadomosci
          out.writeObject(new String("test"));
          out.flush();
          out.reset();
        }
      } catch(Throwable t) {
        System.out.println("Caught " + t + " - closing thread");
      }
    }
  }
}