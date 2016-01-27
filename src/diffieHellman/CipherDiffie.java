package diffieHellman;
 import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import utils.HeaderCreator;
import utils.utilities;
import ciphers.SecretKeyPacket;
import Client.Message;
import NodeInfos.HeaderNode;
import utils.Consts;
/**
 * Klasa odpowiedzialna za szyfrowanie i deszyfrowanie DH AES
 *
 */
public class CipherDiffie {
	private PrivateKey privKey;
	private PublicKey pubKey;
	
	/**
	 * Konstruktor
	 */
	public CipherDiffie()
	{	 
	}
	/**
	 * Zwraca klucz prywatny
	 */
	public PrivateKey getPrivKey()
	{return privKey;}
	/**
	 * Zwraca klucz publiczny
	 */
	public PublicKey getPublicKey()
	{return pubKey;}
	
	/**
	 * Metoda przeprowadzaj¹ca szyfrowanie AES
	 */
	 public static Message encrypt(Message message, ArrayList<SecretKeyPacket> listOfKeys, ArrayList<Long> optimizedTime) 
	 {
		    byte[] textBytes = new byte[Consts.DIFFIE_BLOCKSIZE];
		    Message encryptedMessage = null;
		    try {
		    	encryptedMessage = new Message();
		    	String[] headerParts = message.getHeader().split("/");
		    	ArrayList<String> useableHeaderParts = HeaderCreator.splitter(headerParts);
		    	ArrayList<byte[]> useableHeaderPartsBytes = utilities.headerPartsToBytes(useableHeaderParts);    	
		    	ArrayList<byte[]>headersToEncrypt =  diffieUtils.createHeaderTable(useableHeaderPartsBytes); //te headery zaszyfrowaæ//rozmiar powinien byæ ju¿ dopasowany
		    	ArrayList<SecretKeyPacket> keysToEncryptHeaders = diffieUtils.getKeysToEncryptHeaders(listOfKeys,useableHeaderParts);	//tymi kluczami szyfrowaæ //rozmiar powinien ju¿ byæ dopasowany
		    	ArrayList<SecretKeyPacket> keysToEncryptText = diffieUtils.getKeysToEncryptText(listOfKeys,useableHeaderParts);	//tymi kluczami szyfrowany text
			    	textBytes=message.getText().getBytes();
		    	long first = System.nanoTime();
				   ArrayList<HeaderNode> cipheredHeaders = encryptHeaders(keysToEncryptHeaders,headersToEncrypt);
				   ArrayList<byte[]> encryptedText = encryptText(keysToEncryptText,textBytes);
				  long second = System.nanoTime();
				  long result=second-first;
				  optimizedTime.add(result);
		      encryptedMessage.createCipheredMessageDiffieHellman(cipheredHeaders,encryptedText,optimizedTime);
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
			return encryptedMessage;
		  }
/**
 * Szyfrowanie treœci wiadomoœci	 
 */
	 private static ArrayList<byte[]> encryptText(ArrayList<SecretKeyPacket> keysToEncryptText, byte[] textBytes) {
		 	Cipher cipher;
		 	int numberOfEncryptions=keysToEncryptText.size();
		 	ArrayList<byte[]> textParts = new ArrayList<byte[]>();
		 	byte[] cipheredTextNext = textBytes;
			try {
				cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
				textParts.add(textBytes);
				for(int index=0;index<numberOfEncryptions;index++)
				{
					cipher.init(Cipher.ENCRYPT_MODE, keysToEncryptText.get(keysToEncryptText.size()-1-index).getSecretKey()); //wydaje mnie sie ¿e równa wielkoœæ wiêc mo¿e tak byæ
			    	for(int ind=0;ind<textParts.size();ind++){
					cipheredTextNext = cipher.doFinal(textParts.get(ind));
					textParts.remove(ind);
			    	textParts.add(ind, cipheredTextNext);
			    	}
//			    	if(numberOfEncryptions>1&&numberOfEncryptions!=index+1)
//					{
//						textParts = diffieUtils.divide(textParts);
//					}

				}
			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return textParts;
	 }
	/**
	 * Szyfrowanie nag³ówka wiadomoœci 
	 */
	 private static ArrayList<HeaderNode> encryptHeaders(ArrayList<SecretKeyPacket> keysToEncryptHeaders,ArrayList<byte[]>headersToEncrypt)
	 {
		 ArrayList<HeaderNode> cipheredHeaders = new ArrayList<HeaderNode>();
		 ArrayList<SecretKeyPacket>keysToEncryptCurrentHeader = new ArrayList<SecretKeyPacket>();
			for(int index=0;index<headersToEncrypt.size();index++)
			{
				int numberOfEncryptions = index+1;
				keysToEncryptCurrentHeader.add(keysToEncryptHeaders.get(index));
				cipheredHeaders.add(createNewEncryptedHeader(headersToEncrypt.get(index),keysToEncryptCurrentHeader,numberOfEncryptions));		
			}
		return cipheredHeaders;
	 }
	 
/**
 * Deszyfrowanie wiadomoœci	 
 */
	public static Message decrypt(Message message, SecretKeyPacket secretKeyPacket, ArrayList<Long> arrayList) {
		    
			Message decryptedMessage=null;
		    try {
		     ArrayList<HeaderNode>decryptedHeader= decryptHeader(message.getEncHeader(),secretKeyPacket);
		     ArrayList<byte[]>decryptedText = decryptText(message.getEncryptedText(),secretKeyPacket);
	    	 decryptedMessage=new Message();
	    	 decryptedMessage.createCipheredMessageDiffieHellman(decryptedHeader, decryptedText,arrayList); 
		    } catch (Exception ex) {
		      ex.printStackTrace();
		    }

		    return decryptedMessage;
		  }
	 /**
	  * Deszyfrowanie tekstu
	  */
	 private static ArrayList<byte[]> decryptText(ArrayList<byte[]> encryptedText, SecretKeyPacket key) {
		ArrayList<byte[]> decryptedText = new ArrayList<byte[]>();
		 try {
			  final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			  cipher.init(Cipher.DECRYPT_MODE, key.getSecretKey());
			  
					for(int ind=0;ind<encryptedText.size();ind++)
						{
						if(encryptedText.size()<2)
						{
							decryptedText.add(cipher.doFinal(encryptedText.get(ind)));
						}
						else
						{
							byte[] z = cipher.doFinal(encryptedText.get(ind));
							byte[] y = cipher.doFinal(encryptedText.get(ind+1));
							decryptedText.add(utilities.merge(z,y));
						}
							ind++;
						}
				
			  
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     return decryptedText; 

	}
	 /**
	  * Deszyfrowanie nag³ówka
	  */
	private static ArrayList<HeaderNode> decryptHeader(ArrayList<HeaderNode> encHeader,SecretKeyPacket secretKeyPacket) {
		ArrayList<HeaderNode> decryptedHeader = new ArrayList<HeaderNode>();
		  try {
			  final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			  cipher.init(Cipher.DECRYPT_MODE, secretKeyPacket.getSecretKey());
			  for(int index=0;index<encHeader.size();index++)
				{ 
				  		HeaderNode x = new HeaderNode();
					for(int ind=0;ind<diffieUtils.getSizeOfHeaderParts(encHeader, index);ind++)
						{
						if(diffieUtils.getSizeOfHeaderParts(encHeader, index)<2)
						{
							x.push(cipher.doFinal(encHeader.get(index).getHeadParts().get(ind)));
						}
						else
						{
							byte[] z = cipher.doFinal(encHeader.get(index).getHeadParts().get(ind));
							byte[] y = cipher.doFinal(encHeader.get(index).getHeadParts().get(ind+1));
							x.push(utilities.merge(z,y));
						}
							ind++;
						}
					decryptedHeader.add(x);
				}
			  
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     return decryptedHeader; 
	}
	 /**
	  * Tworzy zaszyfrowany nag³ówek z czêœci
	  */
	private static HeaderNode createNewEncryptedHeader(byte[] cipherHeaderNext,ArrayList<SecretKeyPacket> keysToEncryptCurrentHeader, int numberOfEncryptions) {
		Cipher cipher;
		ArrayList<byte[]> headerList = new ArrayList<byte[]>();
		try {
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			headerList.add(cipherHeaderNext);
			for(int index=0;index<numberOfEncryptions;index++)
			{
				cipher.init(Cipher.ENCRYPT_MODE, keysToEncryptCurrentHeader.get(keysToEncryptCurrentHeader.size()-1-index).getSecretKey()); //wydaje mnie sie ¿e równa wielkoœæ wiêc mo¿e tak byæ
		    	for(int ind=0;ind<headerList.size();ind++){
				cipherHeaderNext = cipher.doFinal(headerList.get(ind));
				headerList.remove(ind);
		    	headerList.add(ind, cipherHeaderNext);
		    	}
//		    	if(numberOfEncryptions>1&&numberOfEncryptions!=index+1)
//				{
//					headerList = diffieUtils.divide(headerList);
//				}

			}
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HeaderNode encryptedHeaderNode = new HeaderNode(headerList);
		return encryptedHeaderNode;
	}
		
	 
}
