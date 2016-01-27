package ciphers;

import java.security.PrivateKey;
import NodeInfos.NodeInfo;
/**
 * Klasa b�d�ca pozwalaj�ca na obslug� i przechowywanie klucza prywatnego
 *
 */
public class PrivateKeyPacket {
private PrivateKey privKey;
private NodeInfo myNode;
private String type;
/**
 * Konstruktor
 */
	public PrivateKeyPacket(NodeInfo myNode,PrivateKey privKey,String type)
	{
		this.myNode=myNode;
		this.privKey=privKey;
		this.type = type;
	}
	/**
	 * zwraca klucz prywatny
	 */
	public PrivateKey getPrivKey(){
		return privKey;
	}
	/**
	 * zwraca typ szyfrowania
	 */
	public String getType(){
		return type;
	}
	/**
	 * zwraca wlasciciela klucza
	 */
	public NodeInfo getKeyOwner()
	{
		return myNode;
	}
}
