package diffieHellman;

import java.util.ArrayList;
import NodeInfos.HeaderNode;
import ciphers.SecretKeyPacket;
import utils.Consts;

/**
 * Klasa b�d�ca zbiorem narz�dzi u�ywanych podczas szyfrowania DH AES
 *
 */
public class diffieUtils {
	
//	/**
//	 * Dzieli 
//	 */
//	static ArrayList<byte[]> divide(ArrayList<byte[]> headerList) {
//		ArrayList<byte[]> newHeaderList = new ArrayList<byte[]>();
//		for(int index=0;index<headerList.size();index++)
//		{
//		newHeaderList=splitter(newHeaderList,headerList.get(index));
//		}
//		
//		return newHeaderList;
//	}
//	private static ArrayList<byte[]> splitter(ArrayList<byte[]> newHeaderList,
//			byte[] bs) {
//		int sizeOfSecondPart=Consts.RSA_BLOCKSIZE-Consts.RSA_ENCRYPTIONBYTELIMIT;
//		byte[] firstPart = new byte[117];
//		byte[] secondPart=new byte[sizeOfSecondPart];
//		for(int index=0;index<Consts.RSA_ENCRYPTIONBYTELIMIT;index++)
//		{
//			firstPart[index]=bs[index];
//		}
//		for(int index=0;index<sizeOfSecondPart;index++)
//		{
//		secondPart[index]=bs[Consts.RSA_ENCRYPTIONBYTELIMIT+index];	
//		}
//		newHeaderList.add(firstPart);
//		newHeaderList.add(secondPart);
//		return newHeaderList;
//	}
	
	/**
	 * Zwraca liczb� cz�ci z jakiej sk�ada si� nag��wek
	 */
	static int getSizeOfHeaderParts(ArrayList<HeaderNode> encHeader,
			int index) {
		return encHeader.get(index).getHeadParts().size();
	}

	/**
	 * Przekszta�ca nag��wek,usuwa pierwsze miejsce z listy
	 */
	public static ArrayList<byte[]> createHeaderTable(ArrayList<byte[]> headerBytes)
	 {
		 ArrayList<byte[]> headersToEncrypt = new  ArrayList<byte[]>();
		 for(int index=1;index<headerBytes.size();index++)
		 {
			 headersToEncrypt.add(headerBytes.get(index));
		 }
		return headersToEncrypt;
	 }
	
	/**
	 * Tworzy list� kluczy do szyfrowania nag��wka
	 */
	 public static ArrayList<SecretKeyPacket> getKeysToEncryptHeaders(ArrayList<SecretKeyPacket> listOfKeys,ArrayList<String> useableHeaderParts)
	 {
		 ArrayList<SecretKeyPacket> keysToEncrypt =  new ArrayList<SecretKeyPacket>();
		// int ind=0;
		 for(int ind = 0; ind<useableHeaderParts.size()-1;ind++)
		 {
		 for(int index=0;index<listOfKeys.size();index++)
		 {	
			 if(listOfKeys.get(index).getKeyOwner().getNodeName().equals(useableHeaderParts.get(ind)))
			 {
				 keysToEncrypt.add(listOfKeys.get(index));
			 }
		 }
		 }
		 return keysToEncrypt;
	 }
	 
		/**
		 * Tworzy list� kluczy do szyfrowania nag��wka
		 */
	 public static ArrayList<SecretKeyPacket> getKeysToEncryptText(ArrayList<SecretKeyPacket> listOfKeys,ArrayList<String> useableHeaderParts)
	 {
		 ArrayList<SecretKeyPacket> keysToEncrypt =  new ArrayList<SecretKeyPacket>();
		// int ind=0;
		 for(int ind = 0; ind<useableHeaderParts.size();ind++)
		 {
		 for(int index=0;index<listOfKeys.size();index++)
		 {	
			 if(listOfKeys.get(index).getKeyOwner().getNodeName().equals(useableHeaderParts.get(ind)))
			 {
				 keysToEncrypt.add(listOfKeys.get(index));
			 }
		 }
		 }
		 return keysToEncrypt;
	 }

}
