package Server;

import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;

import javax.crypto.SecretKey;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import ciphers.PrivateKeyPacket;
import ciphers.PublicKeyPacket;
import ciphers.SecretKeyPacket;
import diffieHellman.CipherDiffie;
import diffieHellman.DiffieHellmanModule;
import utils.Consts;
import utils.HeaderCreator;
import utils.utilities;
import Client.Message;
import Client.MessageSender;
import ElGamal.CipherElGamal;
import NodeInfos.HeaderNode;
import NodeInfos.NodeInfo;
import RSA.CipherRSA;
/**
 *Klasa odpowiedzialna za obs³ugê odebranej wiadomoœci 
 *
 */
public class MessageHandler {

	/**wiadomoœæ któr¹ obslugujemy**/
	private Message message; 
	private JTextArea textArea;
	/**
	 * nastepny odbiorca wiadomosci
	 */
	private byte[] nextHop;
	private String newHeader;
	private ArrayList<NodeInfo> nodes;
	private int clientPort;
	private Socket socket;
	private ArrayList<PublicKeyPacket> publicRSAKeys;
	private PrivateKeyPacket myPrivateRSAKey;
//	private ArrayList<PublicKeyPacket> publicElGamalKeys;
//	private PrivateKeyPacket myPrivateElGamalKey;
	private PrivateKeyPacket myPrivateDiffieHellman;
	private ArrayList<SecretKeyPacket> secretKeyPackets;
	private PublicKeyPacket myDiffiePublicKeyPacket;
	private ArrayList<Long>rsaGenerationTime;
	ArrayList<Long>aesGenerationTime;
	/**
	 * Konstruktor klasy obslugujacej odbior wiadomosci
	 * @param message
	 * @param textArea
	 * @param nodes
	 * @param publicRSAKeys
	 * @param myPrivateRSAKey
	 * @param myPrivateDiffieHellman
	 * @param secretKeyPackets
	 * @param myDiffiePublicKeyPacket
	 * @param rsaGenerationTime
	 * @param aesGenerationTime
	 */
	public MessageHandler(Message message,JTextArea textArea,ArrayList<NodeInfo> nodes,ArrayList<PublicKeyPacket> publicRSAKeys,PrivateKeyPacket myPrivateRSAKey,PrivateKeyPacket myPrivateDiffieHellman,ArrayList<SecretKeyPacket> secretKeyPackets, PublicKeyPacket myDiffiePublicKeyPacket,ArrayList<Long>rsaGenerationTime,ArrayList<Long>aesGenerationTime)
	{
		this.aesGenerationTime=aesGenerationTime;
		this.rsaGenerationTime=rsaGenerationTime;
		this.myDiffiePublicKeyPacket=myDiffiePublicKeyPacket;
		this.myPrivateRSAKey=myPrivateRSAKey;
		this.publicRSAKeys=publicRSAKeys;
		this.myPrivateDiffieHellman=myPrivateDiffieHellman;
//		this.myPrivateElGamalKey=myPrivateElGamalKey;
//		this.publicElGamalKeys=publicElGamalKeys;
		this.message=message;
		this.nodes=nodes;
		this.textArea=textArea;
		this.secretKeyPackets=secretKeyPackets;
	}
	/**
	 * Metoda obslugujaca wiadomoœæ. Wykonuje odpowiednie metody w zale¿noœci od typu odebranej wiadomoœci. 
	 */
	public void handleMessage()
	{
		if(message.getType().equals(Consts.MESSAGE))
		{
			executeMessage();
		}
		else if(message.getType().equals(Consts.CIPHERKEYRSA))
		{
			executeCipherKeyRSA();
		}
		else if(message.getType().equals(Consts.CIPHERKEYDIFFIEHELMAN))
		{
			executeCipherKeyDiffieHelman();
		}
		else if(message.getType().equals(Consts.CIPHERKEYDIFFIEHELMANRESPONSE))
		{
			executeCipherKeyDiffieHelmanResponse();
		}
//		else if(message.getType().equals(Consts.CIPHERKEYELGAMAL))
//		{
//			executeCipherKeyELGAMAL();
//		}
		else if(message.getType().equals(Consts.CIPHEREDMESSAGEDIFFIEHELLMAN))
		{
			executeCipheredMessageDiffieHellman();
		}
		else if(message.getType().equals(Consts.CIPHEREDMESSAGERSA))
		{
			executeCipheredMessageRSA();
		}
//		else if(message.getType().equals(Consts.CIPHEREDMESSAGEELGAMAL))
//		{
//			executeCipheredMessageELGAMAL();
//		}
			
	}
	
	/**
	 * Metoda odpowiadajaca za obslugê wiadomosci bedacej odpowiedzi¹ na nasz¹ wczesniejsz¹ wiadomosc z kluczem DH
	 */
	private void executeCipherKeyDiffieHelmanResponse() {
		SecretKey key;
		try {
			long first = System.nanoTime();
			key = DiffieHellmanModule.agreeSecretKey(myPrivateDiffieHellman.getPrivKey(), message.getPublicKeyPacket().getPubKey(), true);
			long second = System.nanoTime();
			long result = second - first;
			aesGenerationTime.add(message.getAesGenerationTime().get(0)+result);
			SecretKeyPacket packet = new SecretKeyPacket(key,message.getPublicKeyPacket().getKeyOwner());
			if(checkForSameKey(secretKeyPackets,packet)==false)
			{
				secretKeyPackets.add(packet);
			}
			//secretKeyPackets.add(packet);
			textArea.append("DiffieKey added\n");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Metoda obslugujaca odbieranie kluczy publicznych DH - generowanie sekretnego klucza i inicjacja wys³ania swojego klucza publicznego w odpowiedzi
	 */
	private void executeCipherKeyDiffieHelman() {
	
    try {
    	if(!secretKeyPackets.isEmpty())
    	secretKeyPackets.clear();
    	long first = System.nanoTime();
		SecretKey key = DiffieHellmanModule.agreeSecretKey(myPrivateDiffieHellman.getPrivKey(), message.getPublicKeyPacket().getPubKey(), true);
		long second = System.nanoTime();
		SecretKeyPacket packet = new SecretKeyPacket(key,message.getPublicKeyPacket().getKeyOwner());
		if(checkForSameKey(secretKeyPackets,packet)==false)
		{
			secretKeyPackets.add(packet);
		}
		textArea.append("DiffieKey added\n");
		if(key!=null){
		sendDiffieKeyinResponse(message.getPublicKeyPacket().getKeyOwner().getIntPort(),second-first);
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
	}
	/**
	 * Metoda podmieniaj¹ca klucz DH je¿eli ju¿ wczesniej zosta³ dodany klucz od tego samego nadawcy wiadomoœci odebranej
	 */
	private boolean checkForSameKey(ArrayList<SecretKeyPacket> secretKeyPack,
			SecretKeyPacket packet) {
		boolean result = false;
		for(int index=0;index<secretKeyPack.size();index++)
		{
			if(secretKeyPack.get(index).getKeyOwner().getNodeName().equals(packet.getKeyOwner().getNodeName()))
			{
				secretKeyPack.remove(index);
				secretKeyPack.add(index, packet);
				result = true;
			}
		}
		return result;
	}
	/**
	 * Metoda wysylajaca DH klucz w odpowiedzi
	 */
	private void sendDiffieKeyinResponse(int port, long l) {
		 SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){
	    	  
             @Override
             protected Void doInBackground() throws Exception {
             	 MessageSender sender = new MessageSender(socket,port);
             	Message message = new Message();
             	message.createCipherKeyDiffieHelmanResponse(myDiffiePublicKeyPacket,l);
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
	/**
	 * Obsluguje wiadomosc zaszyfrowan¹ RSA
	 */
	private void executeCipheredMessageRSA() 
	{
		Message decryptedMessage = CipherRSA.decrypt(message, myPrivateRSAKey);
		decryptedMessage.getRsaGenerationTime().add(rsaGenerationTime.get(0));
		try {
			ArrayList<byte[]> messageBytes =decryptedMessage.getEncryptedText();
			for(int index=0;index<decryptedMessage.getEncryptedText().size();index++)
			{
			textArea.append("RSA Message: "+new String(messageBytes.get(index),"UTF-8"));
			textArea.append("\n");
			textArea.append("time:"+calculateTimeOfExecution(decryptedMessage.getRsaGenerationTime(),decryptedMessage.getRsaEncryptDecryptTime())+" ns");
			textArea.append("\n");
			}
			} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HeaderNode nextTarget = decryptedMessage.getEncHeader().get(0);
		byte[] nextHop = utilities.mergeAll(nextTarget.getHeadParts());
		try {
			textArea.append("RSA Next Hop: "+new String(nextHop,"UTF-8"));
			textArea.append("\n");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<HeaderNode> newHeader = new ArrayList<HeaderNode>();
		for(int index=1;index<decryptedMessage.getEncHeader().size();index++)
		{
			newHeader.add(decryptedMessage.getEncHeader().get(index));
		}
	Message messageToSend =	new Message();
	messageToSend.createCipheredMessageRSA(newHeader, decryptedMessage.getEncryptedText(),decryptedMessage.getRsaGenerationTime(),decryptedMessage.getRsaEncryptDecryptTime());
	 SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){
	  
      @Override
      protected Void doInBackground() throws Exception {
    	 String nextHopString = new String(nextHop,"UTF-8");
    	 for(int index=0;index<nodes.size();index++)
    	 {
    		 if(nodes.get(index).getNodeName().equals(nextHopString))
    		 {
    			 clientPort=nodes.get(index).getIntPort();
    		 }
    	 }
      	 MessageSender sender = new MessageSender(socket, clientPort);     
      	 sender.WSocket(messageToSend);
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
/**
 * Metoda wyznaczaj¹ca najdluzszy czas generacji RSA, niesumujemy ich gdy¿ wykonuj¹ siê jednoczeœnie wiêc przyjmujemy za czas generacji kluczy najd³uzszy czas wygenerowanie klucza. Nastepnie dodajemy do sumy szyfr/deszyfr
 */
	private String calculateTimeOfExecution(ArrayList<Long> rsaGenerationT, ArrayList<Long> arrayList) { //s³aby algorytm ale dla ma³ej ilosci routerow styka
		long max = rsaGenerationT.get(0);
		for(int index=1;index<rsaGenerationT.size();index++)
		{
			if(max<rsaGenerationT.get(index))
			{
				max=rsaGenerationT.get(index);
			}
		}
		Long suma=arrayList.get(0);
		for(int ind = 1;ind<arrayList.size();ind++)
		{
			suma=suma+arrayList.get(ind);
		}
		return String.valueOf(max+suma);
	}
	/**
	 * Metoda obslugujaca odebrania wiadomosci zaszyfrowanej AES - DH
	 */
	private void executeCipheredMessageDiffieHellman() 
	{
		long first = System.nanoTime();
		Message decryptedMessage = CipherDiffie.decrypt(message,secretKeyPackets.get(0),message.getAesGenerationTime());
		long second = System.nanoTime();
		message.getAesGenerationTime().add(second-first);
		long suma = second-first + addition(message.getAesGenerationTime());
		try {
			ArrayList<byte[]> messageBytes =decryptedMessage.getEncryptedText();
			for(int index=0;index<decryptedMessage.getEncryptedText().size();index++)
			{
			textArea.append("DH Message: "+new String(messageBytes.get(index),"UTF-8"));
			textArea.append("\n");
			textArea.append("AES time: "+suma + "ns");
			textArea.append("\n");
			}
			} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HeaderNode nextTarget = decryptedMessage.getEncHeader().get(0);
		byte[] nextHop = utilities.mergeAll(nextTarget.getHeadParts());
		try {
			textArea.append("DH Next Hop: "+new String(nextHop,"UTF-8"));
			textArea.append("\n");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<HeaderNode> newHeader = new ArrayList<HeaderNode>();
		for(int index=1;index<decryptedMessage.getEncHeader().size();index++)
		{
			newHeader.add(decryptedMessage.getEncHeader().get(index));
		}
	Message messageToSend =	new Message();
	messageToSend.createCipheredMessageDiffieHellman(newHeader, decryptedMessage.getEncryptedText(),message.getAesGenerationTime());
	 SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){
	  
      @Override
      protected Void doInBackground() throws Exception {
    	 String nextHopString = new String(nextHop,"UTF-8");
    	 for(int index=0;index<nodes.size();index++)
    	 {
    		 if(nodes.get(index).getNodeName().equals(nextHopString))
    		 {
    			 clientPort=nodes.get(index).getIntPort();
    		 }
    	 }
      	 MessageSender sender = new MessageSender(socket, clientPort);
      	 sender.WSocket(messageToSend);
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

	/**
	 * Metoda sumuj¹ca czasy generacji i szyfr/deszyfr AES	
	 * @param aesGenTime
	 * @return
	 */
	private long addition(ArrayList<Long> aesGenTime) {
		long suma = aesGenTime.get(0);
		for(int index=1;index<aesGenTime.size();index++)
		{
			suma = suma+aesGenTime.get(index);
		}
		return suma;
	}
	/**
	 * obsuguje odbior kluczy publicznych RSA - dodanie do listy kluczy
	 */
	private void executeCipherKeyRSA() {
		
		publicRSAKeys.add(message.getPublicKeyPacket());
	}
//	private void executeCipherKeyELGAMAL() {
//		
//		publicElGamalKeys.add(message.getPublicKeyPacket());
//	}
	/**
	 *  Obsulga wiadomosci niezaszyfrowanej, wyslanie do nastepnego celu
	 */
	private void executeMessage()
	{
		String header = message.getHeader();
		HeaderCreator hc = new HeaderCreator();
		String[] tableHeader = hc.splitHeader(header);
		String[] newTableHeader =new String[tableHeader.length-1];
		for(int index=1;index<tableHeader.length;index++)
		{
			newTableHeader[index-1]=tableHeader[index];
		}
		
		if(newTableHeader.length<2)
		{
			textArea.append("Message: "+message.getText());
			textArea.append("\n");
		}
		else
		{
			hc.createHeader(newTableHeader);
			newHeader=hc.getHeader();
			for(int index=0;index<nodes.size();index++)
			{
				if(nodes.get(index).getNodeName().equals(nextHop))
				{
					clientPort = nodes.get(index).getIntPort();
				}
			}
			 SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){
		    	  
                    @Override
                    protected Void doInBackground() throws Exception {
                    	 MessageSender sender = new MessageSender(socket, clientPort);
                    	
                    	 Message mess = new Message();
                    	 mess.createMessage(newHeader,message.getText());
           		      sender.WSocket(mess);
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
	
////////nie dzia³ajacy elgamal:
	
//	private void executeCipheredMessageELGAMAL() 
//	{
//		Message decryptedMessage = CipherElGamal.decrypt(message, myPrivateElGamalKey);
//		try {
//			ArrayList<byte[]> messageBytes =decryptedMessage.getEncryptedText();
//			for(int index=0;index<decryptedMessage.getEncryptedText().size();index++)
//			{
//			textArea.append(new String(messageBytes.get(index),"UTF-8"));
//			textArea.append("\n");
//			}
//			} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		HeaderNode nextTarget = decryptedMessage.getEncHeader().get(0);
//		byte[] nextHop = utilities.mergeAll(nextTarget.getHeadParts());
//		try {
//			textArea.append(new String(nextHop,"UTF-8"));
//			textArea.append("\n");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		ArrayList<HeaderNode> newHeader = new ArrayList<HeaderNode>();
//		for(int index=1;index<decryptedMessage.getEncHeader().size();index++)
//		{
//			newHeader.add(decryptedMessage.getEncHeader().get(index));
//		}
//	Message messageToSend =	new Message();
//	messageToSend.createCipheredMessageElGamal(newHeader, decryptedMessage.getEncryptedText(),"sss");
//	 SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){
//	  
//      @Override
//      protected Void doInBackground() throws Exception {
//    	 String nextHopString = new String(nextHop,"UTF-8");
//    	 for(int index=0;index<nodes.size();index++)
//    	 {
//    		 if(nodes.get(index).getNodeName().equals(nextHopString))
//    		 {
//    			 clientPort=nodes.get(index).getIntPort();
//    		 }
//    	 }
//      	 MessageSender sender = new MessageSender(socket, clientPort);
//      	 sender.WSocket(messageToSend);
//		return null;
//      }
//
//      @Override
//      protected void done() {
//          try {
//          } catch (Exception ex) {
//              ex.printStackTrace();
//          }
//      }
//
// };
// worker.execute();
//}

	
}
