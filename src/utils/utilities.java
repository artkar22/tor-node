package utils;

import java.util.ArrayList;
/**
 * Klasa zawieraj�ca narz�dzia
 *
 */
public class utilities {
	
	/**
	 * Metoda przeksztalcaj�ca podzielony na czesci naglowek z typu String na byte
	 * @param useableHeaderParts
	 */
	public static ArrayList<byte[]> headerPartsToBytes(ArrayList<String> useableHeaderParts) {
		ArrayList<byte[]> useableHeaderPartsBytes = new ArrayList<byte[]>();
		for(int index=0;index<useableHeaderParts.size();index++)
		{
			useableHeaderPartsBytes.add(useableHeaderParts.get(index).getBytes());
		}
		return useableHeaderPartsBytes;
	}
/**
 * ��czy dwie tablice bajt�w
 */
	static public byte[] merge(byte[] x, byte[] y )
	{
		int size = x.length+y.length;
		byte[] addition= new byte[size];
		int index;
		for(index=0;index<x.length;index++)
		{
			
				addition[index]=x[index];
		}
		for(int indexX=0;indexX<y.length;indexX++)
		{
			
			addition[index]=y[indexX];
			index++;
		}
		return addition;
	}
	/**
	 * Dzieli tablic� bajt�w na cz�ci y- granica pdzia�u
	 */
	static public ArrayList<byte[]> split(byte[] x, int y)
	{
		ArrayList<byte[]> blocks = new ArrayList<byte[]>();
		byte[] temp = new byte[y];
		int mark=0;
		while(mark<x.length)
		{
		for(int i=0;i<y;i++)
		{
			temp[i]=x[i];
			mark++;
		}
		blocks.add(temp);
		}
		return blocks;
	}
	/**
	 * Metoda ��cz�ca nag��wek zaszyfrowany
	 */
	static public byte[] mergeAll(ArrayList<byte[]> x)
	{
		byte[] result=x.get(x.size()-1);
		for(int index=1;index<x.size();index++)
		{
			result =merge(result,x.get(x.size()-1-index));
		}
	return result;
	}
	
	
}