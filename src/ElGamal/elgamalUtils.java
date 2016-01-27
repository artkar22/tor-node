package ElGamal;

import java.util.ArrayList;

import NodeInfos.HeaderNode;
import ciphers.PublicKeyPacket;
import utils.Consts;

public class elgamalUtils {
	
	static ArrayList<byte[]> divide(ArrayList<byte[]> headerList) {
		ArrayList<byte[]> newHeaderList = new ArrayList<byte[]>();
		for(int index=0;index<headerList.size();index++)
		{
		newHeaderList=splitter(newHeaderList,headerList.get(index));
		}
		
		return newHeaderList;
	}
	private static ArrayList<byte[]> splitter(ArrayList<byte[]> newHeaderList,
			byte[] bs) {
		int sizeOfSecondPart=Consts.ELGAMAL_BLOCKSIZE-Consts.ELGAMAL_ENCRYPTIONBYTELIMIT;
		byte[] firstPart = new byte[Consts.ELGAMAL_ENCRYPTIONBYTELIMIT];
		byte[] secondPart=new byte[sizeOfSecondPart];
		for(int index=0;index<Consts.ELGAMAL_ENCRYPTIONBYTELIMIT;index++)
		{
			firstPart[index]=bs[index];
		}
		for(int index=0;index<sizeOfSecondPart;index++)
		{
		secondPart[index]=bs[Consts.ELGAMAL_ENCRYPTIONBYTELIMIT+index];	
		}
		newHeaderList.add(firstPart);
		newHeaderList.add(secondPart);
		return newHeaderList;
	}
	
	static int getSizeOfHeaderParts(ArrayList<HeaderNode> encHeader,
			int index) {
		return encHeader.get(index).getHeadParts().size();
	}
	
	public static ArrayList<byte[]> createHeaderTable(ArrayList<byte[]> headerBytes)
	 {
		 ArrayList<byte[]> headersToEncrypt = new  ArrayList<byte[]>();
		 for(int index=1;index<headerBytes.size();index++)
		 {
			 headersToEncrypt.add(headerBytes.get(index));
		 }
		return headersToEncrypt;
	 }
	 
	 public static ArrayList<PublicKeyPacket> getKeysToEncryptHeaders(ArrayList<PublicKeyPacket> keys,ArrayList<String> useableHeaderParts)
	 {
		 ArrayList<PublicKeyPacket> keysToEncrypt =  new ArrayList<PublicKeyPacket>();
		// int ind=0;
		 for(int ind = 0; ind<useableHeaderParts.size()-1;ind++)
		 {
		 for(int index=0;index<keys.size();index++)
		 {	
			 if(keys.get(index).getKeyOwner().getNodeName().equals(useableHeaderParts.get(ind)))
			 {
				 keysToEncrypt.add(keys.get(index));
			 }
		 }
		 }
		 return keysToEncrypt;
	 }
	 
	 public static ArrayList<PublicKeyPacket> getKeysToEncryptText(ArrayList<PublicKeyPacket> keys,ArrayList<String> useableHeaderParts)
	 {
		 ArrayList<PublicKeyPacket> keysToEncrypt =  new ArrayList<PublicKeyPacket>();
		// int ind=0;
		 for(int ind = 0; ind<useableHeaderParts.size();ind++)
		 {
		 for(int index=0;index<keys.size();index++)
		 {	
			 if(keys.get(index).getKeyOwner().getNodeName().equals(useableHeaderParts.get(ind)))
			 {
				 keysToEncrypt.add(keys.get(index));
			 }
		 }
		 }
		 return keysToEncrypt;
	 }

}
