package ciphers;

import java.io.Serializable;
import java.security.PublicKey;

import NodeInfos.NodeInfo;

/**
 * Klasa b�d�ca pozwalaj�ca na obslug� i przechowywanie klucza publicznego
 *
 */
public class PublicKeyPacket implements Serializable  {
/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	
	private PublicKey pubKey;							//klucz publiczny
	private NodeInfo myNode;							//wlasciciel klucza publicznego
	private String type;								//typ klucza - czy RSA czy DH
	private final static String IDENTIFIER = "publicKeyPacket";
//	private boolean responseFlag = false;				
	
	/**
	 * Konstruktor pakietu klucza
	 * @param myNode
	 * @param pubKey
	 * @param type
	 */
	public PublicKeyPacket(NodeInfo myNode,PublicKey pubKey,String type)
	{
		this.myNode=myNode;
		this.pubKey=pubKey;
		this.type = type;
	}
	/**
	 * Metoda zwracaj�ca klucz publiczny
	 * 
	 */
	public PublicKey getPubKey(){
		return pubKey;
	}
	/**
	 * Metoda zwracaj�ca typ klucza
	 * 
	 */
	public String getType(){
		return type;
	}
	/**
	 * Zwraca w�a�ciciela klucza
	 */
	public NodeInfo getKeyOwner()
	{
		return myNode;
	}
	public String toString()
	{
		return IDENTIFIER;
	}
//	public void setFlag(boolean state)
//	{
//		responseFlag=state;
//	}
//	public boolean getFlag()
//	{
//		return responseFlag;
//	}
}
