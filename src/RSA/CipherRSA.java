package RSA;
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
import ciphers.PrivateKeyPacket;
import ciphers.PublicKeyPacket;
import Client.Message;
import NodeInfos.HeaderNode;
import utils.Consts;
/**
 * Klasa odpowiedzialna za wszelkie dzia³ania zwi¹zane z szyfrowanie i deszyfrowaniem RSA
 *
 */
public class CipherRSA {
	private PrivateKey privKey;
	private PublicKey pubKey;
	/**
	 * Konstruktor
	 */
	public CipherRSA()
	{
		KeyPairGenerator kpg;
		try {
			kpg = KeyPairGenerator.getInstance("RSA");
			 kpg.initialize(1024);
			 KeyPair keyPair = kpg.generateKeyPair();
			privKey = keyPair.getPrivate();
			pubKey = keyPair.getPublic();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		 
	};
	/**
	 * zwraca klucz prywatny
	 */
	public PrivateKey getPrivKey()
	{return privKey;}
	/**
	 * zwraca klucz publiczny
	 */
	public PublicKey getPublicKey()
	{return pubKey;}
	
	/**
	 * Metoda w której odbywa siê ca³oœæ szyfrowania 
	 */
	 public static Message encrypt(Message message, ArrayList<PublicKeyPacket> keys,ArrayList<Long>rsaGenerationTime,ArrayList<Long> rsaEncryptDecryptTime) 
	 {
		    byte[] textBytes = new byte[Consts.RSA_BLOCKSIZE];
		    Message encryptedMessage = null;
		    try {
		    	 encryptedMessage = new Message();
		    	String[] headerParts = message.getHeader().split("/");
		    	ArrayList<String> useableHeaderParts = HeaderCreator.splitter(headerParts);
		    	ArrayList<byte[]> useableHeaderPartsBytes = utilities.headerPartsToBytes(useableHeaderParts);    	
		    	ArrayList<byte[]>headersToEncrypt =  rsaUtils.createHeaderTable(useableHeaderPartsBytes); //te headery zaszyfrowaæ//rozmiar powinien byæ ju¿ dopasowany
		    	ArrayList<PublicKeyPacket> keysToEncryptHeaders = rsaUtils.getKeysToEncryptHeaders(keys,useableHeaderParts);	//tymi kluczami szyfrowaæ //rozmiar powinien ju¿ byæ dopasowany
		    	ArrayList<PublicKeyPacket> keysToEncryptText = rsaUtils.getKeysToEncryptText(keys,useableHeaderParts);	//tymi kluczami szyfrowany text
	
		    	textBytes=message.getText().getBytes();
		    	
		    Long first = System.nanoTime();	
				   ArrayList<HeaderNode> cipheredHeaders = encryptHeaders(keysToEncryptHeaders,headersToEncrypt);
				   ArrayList<byte[]> encryptedText = encryptText(keysToEncryptText,textBytes);
		     Long second = System.nanoTime();
		     rsaEncryptDecryptTime.add(second-first);
				   encryptedMessage.createCipheredMessageRSA(cipheredHeaders,encryptedText,rsaGenerationTime,rsaEncryptDecryptTime);
		    
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
			return encryptedMessage;
		  }
	 /**
	  * Szyfrowanie tresci wiadomoœci
	  */
	 private static ArrayList<byte[]> encryptText(ArrayList<PublicKeyPacket> keysToEncryptText, byte[] textBytes) {
		 	Cipher cipher;
		 	int numberOfEncryptions=keysToEncryptText.size();
		 	ArrayList<byte[]> textParts = new ArrayList<byte[]>();
		 	byte[] cipheredTextNext = textBytes;
			try {
				cipher = Cipher.getInstance("RSA");
				textParts.add(textBytes);
				for(int index=0;index<numberOfEncryptions;index++)
				{
					cipher.init(Cipher.ENCRYPT_MODE, keysToEncryptText.get(keysToEncryptText.size()-1-index).getPubKey()); //wydaje mnie sie ¿e równa wielkoœæ wiêc mo¿e tak byæ
			    	for(int ind=0;ind<textParts.size();ind++){
					cipheredTextNext = cipher.doFinal(textParts.get(ind));
					textParts.remove(ind);
			    	textParts.add(ind, cipheredTextNext);
			    	}
			    	if(numberOfEncryptions>1&&numberOfEncryptions!=index+1)
					{
						textParts = rsaUtils.divide(textParts);
					}

				}
			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return textParts;
	 }
	 /**
	  * Szyfrowanie nag³ówków
	  */
	 private static ArrayList<HeaderNode> encryptHeaders(ArrayList<PublicKeyPacket> keysToEncryptHeaders,ArrayList<byte[]>headersToEncrypt)
	 {
		 ArrayList<HeaderNode> cipheredHeaders = new ArrayList<HeaderNode>();
		 ArrayList<PublicKeyPacket>keysToEncryptCurrentHeader = new ArrayList<PublicKeyPacket>();
			for(int index=0;index<headersToEncrypt.size();index++)
			{
				int numberOfEncryptions = index+1;
				keysToEncryptCurrentHeader.add(keysToEncryptHeaders.get(index));
				cipheredHeaders.add(createNewEncryptedHeader(headersToEncrypt.get(index),keysToEncryptCurrentHeader,numberOfEncryptions));		
			}
		return cipheredHeaders;
	 }
	 
	 /**
	  * Deszyfrowanie RSA i ponowne tworzenie wiadomoœci do dalszego wysylania 
	  */
	public static Message decrypt(Message message, PrivateKeyPacket key) {
		    
			Message decryptedMessage=null;
		    try {
		    	Long first = System.nanoTime();
		     ArrayList<HeaderNode>decryptedHeader= decryptHeader(message.getEncHeader(),key);
		     ArrayList<byte[]>decryptedText = decryptText(message.getEncryptedText(),key);
	    	 Long second = System.nanoTime();
	    	 message.getRsaEncryptDecryptTime().add(second-first);
		     decryptedMessage=new Message();
	    	 decryptedMessage.createCipheredMessageRSA(decryptedHeader, decryptedText,message.getRsaGenerationTime(),message.getRsaEncryptDecryptTime()); 
		    } catch (Exception ex) {
		      ex.printStackTrace();
		    }

		    return decryptedMessage;
		  }
	 /**
	  * Metoda odpowiedzialna za deszyfrowanie tresci wiadomosci
	  */
	 private static ArrayList<byte[]> decryptText(ArrayList<byte[]> encryptedText, PrivateKeyPacket key) {
		ArrayList<byte[]> decryptedText = new ArrayList<byte[]>();
		 try {
			  final Cipher cipher = Cipher.getInstance("RSA");
			  cipher.init(Cipher.DECRYPT_MODE, key.getPrivKey());
			  
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
	  * Deszyfrowanie naglowka
	  */
	private static ArrayList<HeaderNode> decryptHeader(ArrayList<HeaderNode> encHeader,PrivateKeyPacket key) {
		ArrayList<HeaderNode> decryptedHeader = new ArrayList<HeaderNode>();
		  try {
			  final Cipher cipher = Cipher.getInstance("RSA");
			  cipher.init(Cipher.DECRYPT_MODE, key.getPrivKey());
			  for(int index=0;index<encHeader.size();index++)
				{ 
				  		HeaderNode x = new HeaderNode();
					for(int ind=0;ind<rsaUtils.getSizeOfHeaderParts(encHeader, index);ind++)
						{
						if(rsaUtils.getSizeOfHeaderParts(encHeader, index)<2)
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
	  * Tworzy nowy zaszyfrowany naglowek
	  * 
	  */
	 
	private static HeaderNode createNewEncryptedHeader(byte[] cipherHeaderNext,ArrayList<PublicKeyPacket> keysToEncryptCurrentHeader, int numberOfEncryptions) {
		Cipher cipher;
		ArrayList<byte[]> headerList = new ArrayList<byte[]>();
		try {
			cipher = Cipher.getInstance("RSA");
			headerList.add(cipherHeaderNext);
			for(int index=0;index<numberOfEncryptions;index++)
			{
				cipher.init(Cipher.ENCRYPT_MODE, keysToEncryptCurrentHeader.get(keysToEncryptCurrentHeader.size()-1-index).getPubKey()); //wydaje mnie sie ¿e równa wielkoœæ wiêc mo¿e tak byæ
		    	for(int ind=0;ind<headerList.size();ind++){
				cipherHeaderNext = cipher.doFinal(headerList.get(ind));
				headerList.remove(ind);
		    	headerList.add(ind, cipherHeaderNext);
		    	}
		    	if(numberOfEncryptions>1&&numberOfEncryptions!=index+1)
				{
					headerList = rsaUtils.divide(headerList);
				}

			}
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HeaderNode encryptedHeaderNode = new HeaderNode(headerList);
		return encryptedHeaderNode;
	}
		
	 
}
