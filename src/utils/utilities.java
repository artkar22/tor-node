package utils;

import java.util.ArrayList;
/**
 * Klasa zawieraj¹ca narzêdzia
 *
 */
public class utilities {
	
	/**
	 * Metoda przeksztalcaj¹ca podzielony na czesci naglowek z typu String na byte
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
 * £¹czy dwie tablice bajtów
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
	 * Dzieli tablicê bajtów na czêœci y- granica pdzia³u
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
	 * Metoda ³¹cz¹ca nag³ówek zaszyfrowany
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