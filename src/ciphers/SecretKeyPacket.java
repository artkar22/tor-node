package ciphers;

import java.security.Key;
import java.security.PrivateKey;

import javax.crypto.SecretKey;

import NodeInfos.NodeInfo;

/**
 * Klasa bêd¹ca pozwalaj¹ca na obslugê i przechowywanie sekretnego klucza DH-AES
 *
 */
public class SecretKeyPacket {
	private SecretKey secretKey;
	private NodeInfo myNode;
	//private NodeInfo myNode;
	/**
	 * Konstruktor
	 */
	public SecretKeyPacket(SecretKey secretKey,NodeInfo myNode)
	{
		this.secretKey=secretKey;
		this.myNode = myNode;
	}
	/**
	 * Zwraca w³asciciela klucza
	 */
	public NodeInfo getKeyOwner()
	{
		return myNode;
	}
	/**
	 * Zwraca sekretny klucz
	 */
	public SecretKey getSecretKey() {
		return secretKey;
	}
}
