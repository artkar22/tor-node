package ciphers;

import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import utils.Consts;
import ElGamal.CipherElGamal;
import NodeInfos.NodeInfo;
import RSA.CipherRSA;
/**
 * Tu tworzone s¹ szyfry RSA oraz ElGamal(wy³¹czone)
 *
 */
public class CipherCreator {
	
	private PrivateKey privKey;
	private PublicKey pubKey;
	private NodeInfo myNode;
	private PublicKeyPacket publicPacket;
	private PrivateKeyPacket privPacket;
	/**
	 * Konstruktor 
	 */
	public CipherCreator(NodeInfo myNode)
	{
		this.myNode = myNode;
	}
	/**
	 * Metoda tworz¹ca pakiety zawierajace szyfry
	 * @throws NoSuchProviderException
	 */
	public void CipherCreate(String typeOfAlgorithm) throws NoSuchProviderException{
	if(typeOfAlgorithm.equals(Consts.RSA))
	{
	CipherRSA rsa = new CipherRSA();
	privKey = rsa.getPrivKey();
	pubKey = rsa.getPublicKey();
	publicPacket=new PublicKeyPacket(myNode,pubKey,"RSA");
	privPacket = new PrivateKeyPacket(myNode,privKey,"RSA");
	}
	else if (typeOfAlgorithm.equals(Consts.ELGAMAL))
	{
		CipherElGamal elGamal = new CipherElGamal();
		privKey = elGamal.getPrivKey();
		pubKey = elGamal.getPublicKey();
		publicPacket=new PublicKeyPacket(myNode,pubKey,"ElGamal");
		privPacket = new PrivateKeyPacket(myNode,privKey,"ElGamal");
	}
	else{
		publicPacket=new PublicKeyPacket(myNode,null,null);
	}
}
/**
 * Zwraca pakiet z kluczem publicznym 
 */
	public PublicKeyPacket getPublicPacket() {
		return publicPacket;
	}
/**
 * Zwraca pakiet z kluczem prywatnym
 */
	public PrivateKeyPacket getPrivPacket() {
		return privPacket;
	}
}
