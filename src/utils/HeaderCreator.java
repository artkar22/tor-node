package utils;

import java.util.ArrayList;

import NodeInfos.Route;
/**
 * Klasa zarz�dzaj�ca nag��wkiem
 *
 */
public class HeaderCreator {

	private String header="";
	private Route randomRoute;
	/**
	 * Konstruktor domyslny
	 */
	public HeaderCreator(){};
/**
 * Konstruktor
 */
	public HeaderCreator(Route randomRoute)
	{
		this.randomRoute=randomRoute;
		createHeader();
	}
	/**
	 * Tworzy nag�owki dla danej �cie�ki
	 */
	public void createHeader()
	{
		header="";
		for(int index=0;index<randomRoute.getRoute().size();index++)
		{
			header=header+"/"+randomRoute.getRoute().get(index).getNodeName();
		}
	};
	/**
	 * zwraca naglowek
	 */
	public String getHeader()
	{
		return header;
	}
	/**
	 * Dzieli nag��wek na cz�ci 
	 * 
	 */
	public String[] splitHeader(String head)
	{
		String[] paths=head.split("/");
		
		return paths;
	}
	/**
	 * Tworzy nag��wek z listy nastepnych "hop�w" 
	 */
	public void createHeader(String[] hops)
	{
		header="";
		for(int index=1;index<hops.length;index++)
		{
			header=header+"/"+hops[index];
		}
	}
	/**
	 * Zmienia typ danych
	 */
	public static ArrayList<String> splitter(String[] headerParts)
	{
		 ArrayList<String> useableHeaderParts = new ArrayList<String>();
		 for(int index=1;index<headerParts.length;index++)
		   {
			   useableHeaderParts.add(headerParts[index]);
		   }
		 return useableHeaderParts;
	}
}
