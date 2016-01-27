package NodeInfos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Reprezentacja czêœci nag³ówka
 *
 */
public class HeaderNode implements Serializable  {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7192107500382600418L;
	/**
	 * Tablica bajtów reprezentuj¹ca nag³ówek
	 */
	private ArrayList<byte[]> cipheredHeaderNodesParts;
	/**
	 * konstruktor domyslny
	 */
	public HeaderNode()
	{
		cipheredHeaderNodesParts = new ArrayList<byte[]>();
	}
	/**
	 * konstruktor przyjmujacy zaszyfrowane czesci naglowka
	 */
	public HeaderNode(ArrayList<byte[]> cipheredHeaderNodesParts)
	{
		this.cipheredHeaderNodesParts = cipheredHeaderNodesParts;
	}
	/**
	 * dodaje now¹ zaszyfrowan¹ czêœæ naglowka do listy
	 */
	public void push(byte[] part)
	{
		cipheredHeaderNodesParts.add(part);
	}
	/**
	 * zwraca naglowek
	 */
	public ArrayList<byte[]> getHeadParts()
	{
		return cipheredHeaderNodesParts;
	}
	private void readObject(
		     ObjectInputStream aInputStream
		   ) throws ClassNotFoundException, IOException {
		     //always perform the default de-serialization first
		     aInputStream.defaultReadObject();
		  }

	private void writeObject(
		      ObjectOutputStream aOutputStream
		    ) throws IOException {
		      //perform the default serialization for all non-transient, non-static fields
		      aOutputStream.defaultWriteObject();
		    }
}
