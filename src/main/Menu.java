package main;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.security.KeyPair;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;

import com.thoughtworks.xstream.XStream;

import ciphers.CipherCreator;
import ciphers.PrivateKeyPacket;
import ciphers.PublicKeyPacket;
import ciphers.SecretKeyPacket;
import diffieHellman.CipherDiffie;
import diffieHellman.DiffieHellmanModule;
import utils.Consts;
import utils.HeaderCreator;
import utils.MessagePrompt;
import utils.PathRandomizer;
import Client.Message;
import Client.MessageSender;
import ElGamal.CipherElGamal;
import NodeInfos.NodeInfo;
import NodeInfos.Route;
import NodeInfos.RouteTable;
import Parser.ConfigParser;
import RSA.CipherRSA;
import Server.AsynchClientWorker;
import Server.Server;
/**
 * Klasa odpowiadaj¹ca za menu i obsluge przyciskow
 * 
 *
 */
public class Menu extends JFrame implements ActionListener {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3765855657840052689L;
	/**wszystkie wêz³y sieci**/
	private ArrayList<NodeInfo> nodes;
	/**moj wezel**/
	private NodeInfo myNode;
	/**tablica routingu, dostepne sciezki**/
	private RouteTable routes;
	/**klucz prywatny RSA tego routera**/
	private PrivateKeyPacket privRSAKeyPacket;
	/**klucz publiczny RSA tego routera**/
	private PublicKeyPacket pubRSAKeyPacket; 
	/**klucze publiczne RSA innych routerów **/
	private ArrayList<PublicKeyPacket> publicRSAKeys;
	
//	private PrivateKeyPacket privElGamalKeyPacket;/*klucz prywatny ELGamal tego routera
//	private PublicKeyPacket pubElGamalKeyPacket; //klucz publiczny ElGamal tego routera
//	private ArrayList<PublicKeyPacket> publicElGamalKeys; //klucze publiczne ElGamal innych routerów 
	 /**klucz publiczny Diffie-Hellman AES tego routera**/
	private PublicKeyPacket myDiffiePublicKeyPacket;
	/**klucz prywatny Diffie-Hellman AES tego routera**/
	private PrivateKeyPacket myDiffiePrivateKeyPacket;
	/**lista sekretnych kluczy uzywanych w DH - AES**/
	volatile private ArrayList<SecretKeyPacket> secretKeyPackets; 
	/**czas generacji klucza RSA, kazdy kolejny router na sciezce dodaje tu swoj pomiar**/
	private ArrayList<Long> rsaGenerationTime; 
	/**czas szyfrowania/deszyfrowania, kazdy kolejny router na sciezce dodaje swoj pomiar**/
	private ArrayList<Long> rsaEncryptDecryptTime;
	/**pomiar czas generowania kluczy i szyfr/deszyfr w DH AES - kazdy kolejny router dodaje swoj pomiar**/
	private ArrayList<Long> aesGenerationTime; 
	/**Scroll do przewijania w menu**/
	private JScrollPane scroll;
	
	private AsynchronousServerSocketChannel server;
	private JTextArea textArea;
	private Socket socket;
	Socket sock; //do szyfrów
	/**przycisk za wysylanie wiadomosci szyfrowanej RSA**/
	private JButton clientRSAButton;
	/**przycisk odpowiadajacy za wysylanie wiadomosci szyfrowanej AES**/
	private JButton clientDiffieHellmanButton;
	/**przycisk odpowiadaj¹cy za wyslanie klucza publicznego RSA**/
	private JButton keygenRSAButton;	
	/**przycisk odpowiadajacy za wysylanie klucza publicznego DH AES**/
	private JButton diffieHellmanButton;
	private JTextField textField;
	/**info routera na indeksie 0 a s¹siedzi routera na 1**/
	private ArrayList<ArrayList<String>> serverInfo; 
	/**przechowuje mapê sieci**/
	private ArrayList<String> networkInfo;
	/**port na który wys³aæ wiadomoœæ**/
	private int clientPort; 
	private String num="2";
	/**œcie¿ka do pliku z konfigiem routera**/
	private String configPath ="config"+'/'+"node"+num+".xml"; 
	/**œcie¿ka do pliku z ca³¹ map¹ sieci**/
	final static private String NETPATH = "config"+'/'+"network.xml"; 
	private JComboBox<String> comboBx;
/**
 * Konstruktor menu - przyjmuje jako argument identyfikator routera 
 * 
 */
	public Menu(String arg0)
	
	{
		comboBx = new JComboBox<String>();
		ConfigParser netParser = new ConfigParser(NETPATH);
		networkInfo = netParser.Parse().get(0);
		for(int x = 1; x<networkInfo.size();x=x+2){
		comboBx.addItem(networkInfo.get(x));
		}
		nodes = netParser.parseNodes(networkInfo);	
		configPath ="config"+'/'+"node"+arg0+".xml";
		ConfigParser parser = new ConfigParser(configPath);
		serverInfo = parser.Parse();
		myNode = new NodeInfo(serverInfo.get(0).get(1),serverInfo.get(0).get(0));
		myNode.setNeighbours(serverInfo.get(1));
		clientPort = myNode.getIntPort(); //domyœlnie loopback
		routes=new RouteTable(serverInfo.get(2),nodes);
		this.setTitle(myNode.getNodeName());
		this.setBounds(0, 0, 1024, 768);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel jp = new JPanel();
		jp.setLayout(new GridLayout(2,5));
		keygenRSAButton = new JButton("Send RSA key");
		diffieHellmanButton = new JButton("Send Diffie-Hellman AES key");
		clientRSAButton=new JButton("Send RSA Message");
		clientDiffieHellmanButton=new JButton("Send Diffie-AES message");
		
		//serverButton.addActionListener(this);
		clientRSAButton.addActionListener(this);
		clientDiffieHellmanButton.addActionListener(this);
		diffieHellmanButton.addActionListener(this);
		keygenRSAButton.addActionListener(this);
		textArea = new JTextArea();
		textField = new JTextField();
		
		scroll = new JScrollPane(textArea);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		JPanel jp2 = new JPanel();
	
		jp2.setLayout(new BorderLayout());
		jp.add(keygenRSAButton);
		jp.add(clientRSAButton);
		jp.add(comboBx);
		jp.add(diffieHellmanButton);
		jp.add(clientDiffieHellmanButton);
		jp2.add(jp,BorderLayout.NORTH);
		//jp2.add(textArea,BorderLayout.CENTER);
		jp2.add(scroll,BorderLayout.CENTER);
		jp2.add(textField,BorderLayout.SOUTH);
		this.add(jp2);
		this.setVisible(true);
		this.repaint();
		rsaGenerationTime =new ArrayList<Long>();
		rsaEncryptDecryptTime = new ArrayList<Long>();
		aesGenerationTime = new ArrayList<Long>();
		publicRSAKeys = new ArrayList<PublicKeyPacket>();
		//publicElGamalKeys = new ArrayList<PublicKeyPacket>();
		secretKeyPackets = new ArrayList<SecretKeyPacket>();
		try {
			GenerateRSAKey();  //generowanie kluczy RSA
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
		
		generateDHkeys(); //generowanie klucza publicznego i prywatnego DH
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){ ///tutaj uruchamiany jest serwer routera
	    	  
			@Override
			protected Void doInBackground() throws Exception {
				new Server(myNode.getIntPort(),textArea,nodes,publicRSAKeys,privRSAKeyPacket,myDiffiePrivateKeyPacket,secretKeyPackets,myDiffiePublicKeyPacket,rsaGenerationTime,aesGenerationTime);
				return null;
			}
		};
		worker.execute();
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Obs³uga zdarzen zwiazanych z przyciskami
	 */
	public void actionPerformed(ActionEvent event){ //tu obs³uga przycisków
		   
		Object source = event.getSource();
		   
		   if(source == keygenRSAButton) //generowanie i rozsylanie klucza RSA
		   {
			  
			   keyGenRSAButtonExecutor();
			   MessagePrompt mp = new MessagePrompt("Klucz wys³any");
			   keygenRSAButton.setEnabled(false);
			   mp.setVisible(true);
		   	}
		   else if (source == diffieHellmanButton) //wyslanie klucza DH aby pozniej uzyskac sekretny klucz
		   {
			   diffieHellman();
		   } 
		   else if(source == clientRSAButton&&textField.getText()!="") //wysy³anie wiadomoœci RSA
		   {
			sendRSAMessage();   
		   }
		    
		   else if(source == clientDiffieHellmanButton&&textField.getText()!="") //wysy³anie wiadomoœæi Diffie-Hellman
		   {
			   sendDiffieMessage();
		   }
	}
			
	/**
	 * Wysylanie klucza DH
	 */
	private void diffieHellman() 
	{
		negotiationExecutor(nodes,myDiffiePublicKeyPacket);
		
	}
	/**
	 * Generowanie klucza publicznego i prywatnego Diffie-Hellman
	 */
	private void generateDHkeys() 
	{ 	
		 long first = System.nanoTime();
				KeyPair myDiffieKeyPair = DiffieHellmanModule.genDHKeyPair();
		        PublicKey myDiffiePublicKey = myDiffieKeyPair.getPublic();
		       PrivateKey myDiffiePrivateKey = myDiffieKeyPair.getPrivate();
		       myDiffiePublicKeyPacket = new PublicKeyPacket(myNode,myDiffiePublicKey,"DH");
		       myDiffiePrivateKeyPacket = new PrivateKeyPacket(myNode,myDiffiePrivateKey,"DH");
		       long second = System.nanoTime();
		       aesGenerationTime.add(second -first);
	}
	/**
	 * Wys³anie wiadomoœci szyfrowanej DH AES
	 */
	private void sendDiffieMessage()
		{
			ArrayList<Long> optimizedTime = calculateAesTime();
			PathRandomizer random = new PathRandomizer(routes,comboBx);
		   Route randomRoute = random.generatePath();
		   HeaderCreator headcreator = new HeaderCreator(randomRoute);
		   String header=headcreator.getHeader();
		   clientPort=randomRoute.getRoute().get(0).getIntPort();
		   String[] headerParts = header.split("/");
		   ArrayList<String> useableHeaderParts = HeaderCreator.splitter(headerParts);
		   checkforNull(secretKeyPackets);
		   //lista kluczy
		   if(secretKeyPackets.size()>=useableHeaderParts.size())
		   {
		   ArrayList<SecretKeyPacket> listOfKeys = getListOfDiffieKeys(useableHeaderParts);
		   
		 SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){
		    	  
		            @Override
		            protected Void doInBackground() throws Exception {
		            	
		            	 MessageSender sender = new MessageSender(socket, clientPort);
		            	 Message message = new Message();
		            	 message.createMessage(header, textField.getText());
		            	 Message cipheredMessage=CipherDiffie.encrypt(message,listOfKeys,optimizedTime);
		            	 sender.WSocket(cipheredMessage);
		   		         textField.setText(new String(""));
					return null;
		            }

		            @Override
		            protected void done() {
		                try {
		                } catch (Exception ex) {
		                    ex.printStackTrace();
		                }
		            }

		       };
		       worker.execute();    
		}
		   else
		   {
			  MessagePrompt x = new MessagePrompt("niewystarczaj¹ca iloœæ kluczy, poproœ o klucze jeszcze raz");
			  x.setVisible(true);
		   }
	}
	
		/**
		 * Czysci listê sekretnych kluczy ze œmieci
		 * @param secretKeyPack
		 */
	private void checkforNull(ArrayList<SecretKeyPacket> secretKeyPack) {
		secretKeyPack.removeAll(Collections.singleton(null));
		
	}
	/**
	 * Obliczenie czasu trwania AES
	 * @return
	 */
	private ArrayList<Long> calculateAesTime() {
		ArrayList<Long> aestime = new ArrayList<Long>();
		long time = aesGenerationTime.get(0);
		if(aesGenerationTime.size()>1)
		{
		long max = aesGenerationTime.get(1);
		for(int index = 2;index<aesGenerationTime.size();index++)
		{
			if(max<aesGenerationTime.get(index))
			{
				max=aesGenerationTime.get(index);
			}
		}
		aestime.add(time+max);
		return aestime;
		}
		else
		{
		aestime.add(time);
		return aestime;
		}
		
	}
/**
 * Wysylanie wiadomoœci szyfrowanej RSA
 */
	private void sendRSAMessage() {
			PathRandomizer random = new PathRandomizer(routes,comboBx);
		   Route randomRoute = random.generatePath();
		   HeaderCreator headcreator = new HeaderCreator(randomRoute);
		   String header=headcreator.getHeader();
		   clientPort=randomRoute.getRoute().get(0).getIntPort();

		   String[] headerParts = header.split("/");
		   ArrayList<String> useableHeaderParts = HeaderCreator.splitter(headerParts);
		   
		   //lista kluczy
		 ArrayList<PublicKeyPacket> listOfKeys = getListOfRSAKeys(useableHeaderParts);
		   
		 SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){
		    	  
		            @Override
		            protected Void doInBackground() throws Exception {
		            	
		            	 MessageSender sender = new MessageSender(socket, clientPort);
		            	 Message message = new Message();
		            	 message.createMessage(header, textField.getText());
		            	 Message cipheredMessage=CipherRSA.encrypt(message,listOfKeys,rsaGenerationTime,rsaEncryptDecryptTime);
		            	 sender.WSocket(cipheredMessage);
		   		         textField.setText(new String(""));
					return null;
		            }

		            @Override
		            protected void done() {
		                try {
		                } catch (Exception ex) {
		                    ex.printStackTrace();
		                }
		            }

		       };
		       worker.execute();    
		
	}
	
//		
	private ArrayList<PublicKeyPacket> getListOfRSAKeys(ArrayList<String> useableHeaderParts) 
	{
		ArrayList<PublicKeyPacket> listOfKeys = new ArrayList<PublicKeyPacket>();
		for(int ind=0;ind<publicRSAKeys.size();ind++)
		   {
			   for(int index=0;index<useableHeaderParts.size();index++)
			   {
				   if(publicRSAKeys.get(ind).getKeyOwner().getNodeName().equals(useableHeaderParts.get(index)))
				   {
					   listOfKeys.add(publicRSAKeys.get(ind));
				   }
			   }
		   }
		return listOfKeys;
	}
	private ArrayList<SecretKeyPacket> getListOfDiffieKeys(ArrayList<String> useableHeaderParts) 
	{
		ArrayList<SecretKeyPacket> listOfKeys = new ArrayList<SecretKeyPacket>();
		for(int ind=0;ind<secretKeyPackets.size();ind++)
		   {
			   for(int index=0;index<useableHeaderParts.size();index++)
			   {
				   if(secretKeyPackets.get(ind).getKeyOwner().getNodeName().equals(useableHeaderParts.get(index)))
				   {
					   listOfKeys.add(secretKeyPackets.get(ind));
				   }
			   }
		   }
		return listOfKeys;
	}
	
	private void GenerateRSAKey() throws NoSuchProviderException
	{
		CipherCreator creator = new CipherCreator(myNode);
		///////////tutaj start odmierzania
		long firstMeasureOfTime = System.nanoTime();
		   creator.CipherCreate(Consts.RSA);
		long secondMeasureOfTime = System.nanoTime();
		long result = secondMeasureOfTime - firstMeasureOfTime;
		rsaGenerationTime.add(result);
		////////// tutaj koniec odmierzania
		   privRSAKeyPacket=creator.getPrivPacket();  
		 pubRSAKeyPacket=creator.getPublicPacket();
	}
	
		  private void keyGenRSAButtonExecutor()
		  {
			 Message message = new Message();
			 message.createCipherKeyRSA(pubRSAKeyPacket);
			 for(int index=0;index<nodes.size();index++)
			 {
				 if(nodes.get(index).getIntPort()!=myNode.getIntPort())
				 {
					 int clientPort = nodes.get(index).getIntPort();
					  SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){

		                    @Override
		                    protected Void doInBackground() throws Exception {
		                    	 MessageSender sender = new MessageSender(new Socket(), clientPort);
		           		      sender.WSocket(message);
		                    	 return null;
		                    }
		 
		                    @Override
		                    protected void done() {
		                        try {
		                        } catch (Exception ex) {
		                            ex.printStackTrace();
		                        }
		                    }
		 
		               };
		               worker.execute(); 
		               
				 }
			 }
		  }
		  
		  /**
		   * Tworzenie i wyslanie wiadomosci z kluczem DH
		   */
		  private void negotiationExecutor(ArrayList<NodeInfo> nodes,PublicKeyPacket diffieHellman)
		  {
			 Message message = new Message();
			 message.createCipherKeyDiffieHelman(diffieHellman);
			 for(int index=0;index<nodes.size();index++)
			 {
				 if(nodes.get(index).getIntPort()!=myNode.getIntPort())
				 {
					 int clientPort = nodes.get(index).getIntPort();
					  SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){

		                    @Override
		                    protected Void doInBackground() throws Exception {
		                    	 MessageSender sender = new MessageSender(new Socket(), clientPort);
		           		      sender.WSocket(message);
		                  	 return null;
		                    }
		 
		                    @Override
		                    protected void done() {
		                        try {
		                        } catch (Exception ex) {
		                            ex.printStackTrace();
		                        }
		                    }
		 
		               };
		               worker.execute(); 
		               
				 }
			 }
		  }
		  					  
	
//	public void listenSocket(){ //odbieranie
//		  try{
//			  server = AsynchronousServerSocketChannel.open();
//				    //String host = "localhost";
//				    InetSocketAddress sAddr = new InetSocketAddress("192.168.2.101", myNode.getIntPort());
//				    server.bind(sAddr);
//		    MessagePrompt prompt = new MessagePrompt("Serwer nas³uchuje!");
//	        prompt.setLocationRelativeTo(this);
//	        prompt.setVisible(true);
//		  } catch (IOException e) {
//			  
//			  MessagePrompt prompt = new MessagePrompt("Serwer nie mo¿e tu s³uchaæ!");
//		        prompt.setLocationRelativeTo(this);
//		        prompt.setVisible(true);
//		    System.out.println("Could not listen on port"+myNode.getIntPort());
//		   // System.exit(-1);
//		  }
//			    AsynchClientWorker   w = new AsynchClientWorker(server/*, textArea,networkInfo,pubKeys,secKeys,privateDiffieKey,publicDiffieKey*/,myNode.getNodeName());
//			      Thread t = new Thread(w);
//			      t.start();
//		}

//	  private void GenerateElGamalKey()
//		{
//			CipherCreator creator = new CipherCreator(myNode);
//			   try {
//				creator.CipherCreate(Consts.ELGAMAL);
//			} catch (NoSuchProviderException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			 privElGamalKeyPacket=creator.getPrivPacket();  
//			 pubElGamalKeyPacket=creator.getPublicPacket();
//		}

	
	//private void sendElGamalMessage() {
		//
//				PathRandomizer random = new PathRandomizer(routes,comboBx);
//				   Route randomRoute = random.generatePath();
//				   HeaderCreator headcreator = new HeaderCreator(randomRoute);
//				   String header=headcreator.getHeader();
//				   clientPort=randomRoute.getRoute().get(0).getIntPort();
		//
//				   String[] headerParts = header.split("/");
//				   ArrayList<String> useableHeaderParts = HeaderCreator.splitter(headerParts);
//				   
//				   //lista kluczy
//				 ArrayList<PublicKeyPacket> listOfKeys = getListOfELGAMALKeys(useableHeaderParts);
//				   
//				 SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){
//				    	  
//				            @Override
//				            protected Void doInBackground() throws Exception {
//				            	
//				            	 MessageSender sender = new MessageSender(socket, clientPort);
//				            	 Message message = new Message();
//				            	 message.createMessage(header, textField.getText());
//				            	 Message cipheredMessage=CipherElGamal.encrypt(message,listOfKeys);
//				            	 XStream xstream = new XStream();
//				            	 String xml = xstream.toXML(cipheredMessage);
//				            	 
//				            	 sender.WSocket(cipheredMessage);
//				            	// sender.WSocket(message);
//				            	 textField.setText(new String(""));
//							return null;
//				            }
		//
//				            @Override
//				            protected void done() {
//				                try {
//				                } catch (Exception ex) {
//				                    ex.printStackTrace();
//				                }
//				            }
		//
//				       };
//				       worker.execute();    		
//				   }

//	  private void keyGenElGamalButtonExecutor()
//	  {
//		 Message message = new Message();
//		 message.createCipherKeyElGamal(pubElGamalKeyPacket);
//		 for(int index=0;index<nodes.size();index++)
//		 {
//			 if(nodes.get(index).getIntPort()!=myNode.getIntPort())
//			 {
//				 int clientPort = nodes.get(index).getIntPort();
//				  SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){
//
//	                    @Override
//	                    protected Void doInBackground() throws Exception {
//	                    	 MessageSender sender = new MessageSender(new Socket(), clientPort);
//	                    	 sender.WSocket(message);
//						return null;
//	                    }
//	 
//	                    @Override
//	                    protected void done() {
//	                        try {
//	                        } catch (Exception ex) {
//	                            ex.printStackTrace();
//	                        }
//	                    }
//	 
//	               };
//	               worker.execute(); 
//	               
//			 }
//		 }
//	  }
	
//			private ArrayList<PublicKeyPacket> getListOfELGAMALKeys(ArrayList<String> useableHeaderParts)
//			{
//				ArrayList<PublicKeyPacket> listOfKeys = new ArrayList<PublicKeyPacket>();
//				for(int ind=0;ind<publicElGamalKeys.size();ind++)
//				   {
//					   for(int index=0;index<useableHeaderParts.size();index++)
//					   {
//						   if(publicElGamalKeys.get(ind).getKeyOwner().getNodeName().equals(useableHeaderParts.get(index)))
//						   {
//							   listOfKeys.add(publicElGamalKeys.get(ind));
//						   }
//					   }
//				   }
//				return listOfKeys;
//				
//			}
		  
//	private void CipherTest ()
//	{
//		String plain = "Artek";
//		System.out.println("przed operacjami:"+plain);
//		Message plainMessage = new Message();
//		plainMessage.createMessage("head", plain);
//		Message encryptedMessage = CipherRSA.encrypt(plainMessage,pubRSAKeyPacket);
//		System.out.println("zaszyfrowane:"+encryptedMessage.getEncryptedText().toString());
//		Message decryptedMessage = CipherRSA.decrypt(encryptedMessage, privRSAKeyPacket);
//		System.out.println("odszyfrowane:"+decryptedMessage.getText());
//		
//	}
	
}
