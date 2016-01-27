package ElGamal;
 import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.util.encoders.Base64;

import utils.Consts;
import utils.HeaderCreator;
import utils.utilities;
import ciphers.PrivateKeyPacket;
import ciphers.PublicKeyPacket;
import Client.Message;
import NodeInfos.HeaderNode;
import RSA.rsaUtils;

public class CipherElGamal {
	private PrivateKey privKey;
	private PublicKey pubKey;
	public CipherElGamal()
	{
		KeyPairGenerator kpg;
		try {
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			kpg = KeyPairGenerator.getInstance("ElGamal", "BC");///"ElGamal/None/NoPadding", "BC"
			 SecureRandom random = new SecureRandom();
			//kpg.initialize(3,random);								////tutaj co???
			 kpg.initialize(512);
			 KeyPair keyPair = kpg.generateKeyPair();
			privKey = keyPair.getPrivate();
			pubKey = keyPair.getPublic();
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
		}
		 
	};
	public PrivateKey getPrivKey(){return privKey;}
	public PublicKey getPublicKey(){return pubKey;}
	
	 public static Message encrypt(Message message, ArrayList<PublicKeyPacket> keys) {
		    byte[] textBytes = new byte[Consts.ELGAMAL_BLOCKSIZE];
		    Message encryptedMessage = null;
		    try {
		    	 encryptedMessage = new Message();
		    	String[] headerParts = message.getHeader().split("/");
		    	ArrayList<String> useableHeaderParts = HeaderCreator.splitter(headerParts);
		    	ArrayList<byte[]> useableHeaderPartsBytes = utilities.headerPartsToBytes(useableHeaderParts);    	
		    	ArrayList<byte[]>headersToEncrypt =  elgamalUtils.createHeaderTable(useableHeaderPartsBytes); //te headery zaszyfrowaæ//rozmiar powinien byæ ju¿ dopasowany
		    	ArrayList<PublicKeyPacket> keysToEncryptHeaders = elgamalUtils.getKeysToEncryptHeaders(keys,useableHeaderParts);	//tymi kluczami szyfrowaæ //rozmiar powinien ju¿ byæ dopasowany
		    	ArrayList<PublicKeyPacket> keysToEncryptText = elgamalUtils.getKeysToEncryptText(keys,useableHeaderParts);	//tymi kluczami szyfrowany text
	
		    	textBytes=message.getText().getBytes();
		    	ArrayList<HeaderNode> cipheredHeaders = null; 
		    	
		    	Cipher 	cipher = Cipher.getInstance("ElGamal/None/NoPadding", "BC");
		    	cipher.init(Cipher.ENCRYPT_MODE, keysToEncryptText.get(0).getPubKey());
		    	byte[] encrtedText = cipher.doFinal(textBytes);
		    	
		    	ArrayList<byte[]> encryptedText = new ArrayList<byte[]>();
		    	String szyfr = new String(encrtedText,"UTF-8");
		    	
		    	 // ArrayList<HeaderNode> cipheredHeaders = encryptHeaders(keysToEncryptHeaders,headersToEncrypt);
				 //  ArrayList<byte[]> encryptedText = encryptText(keysToEncryptText,textBytes);
		    //  encryptedMessage.createCipheredMessageElGamal(cipheredHeaders,encryptedText,szyfr);
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
			return encryptedMessage;
		  }
	 
	 private static ArrayList<byte[]> encryptText(ArrayList<PublicKeyPacket> keysToEncryptText, byte[] textBytes) {
		 	Cipher cipher;
		 	int numberOfEncryptions=keysToEncryptText.size();
		 	ArrayList<byte[]> textParts = new ArrayList<byte[]>();
		 	byte[] cipheredTextNext = textBytes;
			try {
//				Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
				cipher = Cipher.getInstance("ElGamal/None/NoPadding", "BC");
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
						textParts = elgamalUtils.divide(textParts);
					}

				}
			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchProviderException e) {
				e.printStackTrace();
			}
			return textParts;
	 }
	 
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

	 
	 public static Message decrypt(Message message, PrivateKeyPacket key) {
		    byte[] decryptedText = null;
		    byte[]	decryptedHeader = null;
		    Message decryptedMessage=null;
		    try {
		      final Cipher cipher = Cipher.getInstance("ElGamal");

		      // decrypt the text using the private key
		      cipher.init(Cipher.DECRYPT_MODE, key.getPrivKey());
		     // decryptedHeader = cipher.doFinal(message.getEncryptedHeader());
		      //decryptedText = cipher.doFinal(message.getEncryptedText());
		      decryptedMessage=new Message();
		      decryptedMessage.createMessage(new String(decryptedHeader,"UTF-8"),new String(decryptedText,"UTF-8"));
		    } catch (Exception ex) {
		      ex.printStackTrace();
		    }

		    return decryptedMessage;
		  }

	 private static HeaderNode createNewEncryptedHeader(byte[] cipherHeaderNext,ArrayList<PublicKeyPacket> keysToEncryptCurrentHeader, int numberOfEncryptions) {
			Cipher cipher;
			ArrayList<byte[]> headerList = new ArrayList<byte[]>();
			try {
				cipher = Cipher.getInstance("ElGamal/None/NoPadding", "BC");
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
						headerList = elgamalUtils.divide(headerList);
					}

				}
			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchProviderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HeaderNode encryptedHeaderNode = new HeaderNode(headerList);
			return encryptedHeaderNode;
		}




}
