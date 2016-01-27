package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import NodeInfos.HeaderNode;
import ciphers.PublicKeyPacket;
import utils.Consts;

/**
 * Klasa bêd¹ca reprezentacj¹ wiadomoœci do wys³ania/odebrania 
 *
 */
public class Message implements Serializable   {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2178981908696606796L;
	
	private String type;			// flaga mówi¹ca o tym jakiego typu jest wiadomoœc
	private String header;			// naglowek niezaszyfrowany
	private String text;			//tresc wiadomosci niezaszyfrowana
	private PublicKeyPacket pubKeyPacket;	//pakiet klucza publicznego
	//private byte[] encryptedHeader;			//zaszyfrowany naglowek podzielony na czesci i w postaci bajtów
	private ArrayList<byte[]> encryptedText; //zaszyfrowana tresc wiadomosci
	private ArrayList<HeaderNode> headerr;		//tablica reprezentuj¹ca czêœci nag³ówka odpowiednio zaszyfrowane b¹dŸ nie
	private ArrayList<Long>rsaGenerationTime; //czasy generacji klucza - przekazywane s¹ dalej w wiadomosci
	private ArrayList<Long> rsaEncryptDecryptTime; //czasy szyfr/deszyfr
	private ArrayList<Long>	aesGenerationTime;  //czasy generacji i szyfr/deszyfr DH AES
	
	public Message()
	{
	}
	/**
	 * Zwraca tablicê z czasami szyfr/deszyfr RSA
	 */
	public ArrayList<Long> getRsaEncryptDecryptTime()
	{
		return rsaEncryptDecryptTime;
	}
	/**
	 * Zwraca tablicê z czasami generacji oraz szyfr/deszyfr DH AES
	 */
	public ArrayList<Long> getAesGenerationTime()
	{
		return aesGenerationTime;
	}
	/**
	 * Zwraca czasy generowania kluczy RSA
	 */
	public ArrayList<Long> getRsaGenerationTime()
	{
		return rsaGenerationTime;
	}
	/**
	 * Tworzy wiadomoœæ nieszyfrowan¹ 
	 * 
	 */
	public void createMessage(String header,String text)
	{
		type=Consts.MESSAGE;
		this.header = header;
		this.text = text;
	}
	/**
	 * Tworzy wiadomoœc przenosz¹c¹ klucz publiczny RSA
	 */
	public void createCipherKeyRSA(PublicKeyPacket pubKeyPacket)
	{
		this.pubKeyPacket = pubKeyPacket;
		type=Consts.CIPHERKEYRSA;
	}
	/**
	 * Tworzy wiadomoœc przenosz¹c¹ klucz publiczny DH
	 */
	public void createCipherKeyDiffieHelman(PublicKeyPacket pubKeyPacket)
	{
		this.pubKeyPacket = pubKeyPacket;
		type=Consts.CIPHERKEYDIFFIEHELMAN;
	}
	/**
	 *	Tworzy wiadomosc przenosz¹c¹ klucz publiczny DH bêd¹c¹ odpowiedzi¹ na wiadomoœc z kluczem publicznym DH 
	 */
	public void createCipherKeyDiffieHelmanResponse(PublicKeyPacket pubKeyPacket, long l)
	{
		aesGenerationTime = new ArrayList<Long>();
		aesGenerationTime.add(l);
		this.pubKeyPacket = pubKeyPacket;
		type=Consts.CIPHERKEYDIFFIEHELMANRESPONSE;
	}
//	/**
//	 * Tworzy wiadomoœæ przenosz¹c¹ klucz publiczny ELGAMAL
//	 */
//	public void createCipherKeyElGamal(PublicKeyPacket pubKeyPacket)
//	{
//		this.pubKeyPacket = pubKeyPacket;
//		type=Consts.CIPHERKEYELGAMAL;
//	}
	/**
	 * 
	 * metoda tworz¹ca zaszyfrowan¹ wiadomoœæ RSA
	 */
	public void createCipheredMessageRSA(ArrayList<HeaderNode> headerr, ArrayList<byte[]> encryptedText,ArrayList<Long>rsaGenerationTime, ArrayList<Long> rsaEncryptDecryptTime)
	{
		type=Consts.CIPHEREDMESSAGERSA;
		this.encryptedText = encryptedText;
		this.headerr = headerr;
		this.rsaGenerationTime = rsaGenerationTime;
		this.rsaEncryptDecryptTime=rsaEncryptDecryptTime;
	}
	/**
	 * metoda tworz¹ca wiadomosc zaszyfrowan¹ AES
	 */
	public void createCipheredMessageDiffieHellman(ArrayList<HeaderNode> headerr, ArrayList<byte[]> encryptedText,ArrayList<Long>aesGenTime)
	{
		type=Consts.CIPHEREDMESSAGEDIFFIEHELLMAN;
		this.encryptedText = encryptedText;
		this.headerr = headerr;
		this.aesGenerationTime = aesGenTime;
	}
//	public void createCipheredMessageElGamal(ArrayList<HeaderNode> headerr, ArrayList<byte[]> encryptedText,String encryptedTEXT)
//	{
//		type=Consts.CIPHEREDMESSAGEELGAMAL;
//		this.encryptedText = encryptedText;
//		this.headerr = headerr;
//	}
	/**
	 * metoda tworz¹ca niezaszyfrowan¹ wiadomoœæ
	 */
	public void createMessage(ArrayList<HeaderNode> decryptedHeader, String text) 
	{
		this.headerr=decryptedHeader;
		this.text=text;
		
	}
	/**
	 * metoda zwracaj¹ca zaszyfrowane czesci naglowka
	 * @return
	 */
	public ArrayList<HeaderNode> getEncHeader()
	{
		return headerr;
	}
	/**
	 * Zwraca niezaszyfrowan¹ wiadomosc
	 */
	public String getText()
	{
		return text;
	}
	/**
	 * Zwraca niezaszyfrowany naglowek
	 * 
	 */
	public String getHeader()
	{
		return header;
	}
	/**
	 * zwraca typ wiadomosci
	 */
	public String getType()
	{
		return type;
	}
//	public byte[] getEncryptedHeader()
//	{
//		return encryptedHeader;
//	}
	/**
	 * zwraca zaszyfrowan¹ treœæ wiadomoœci
	 * 
	 */
	public ArrayList<byte[]> getEncryptedText()
	{
		return encryptedText;
	}
	/**
	 * ustawia niezaszyfrowany naglowek
	 */
	public void setHeader(String newHeader)
	{
		this.header=newHeader;
	}
	/**
	 * zwraca pakiet publicznego klucza
	 */
	public PublicKeyPacket getPublicKeyPacket()
	{
		return pubKeyPacket;
	}

}
