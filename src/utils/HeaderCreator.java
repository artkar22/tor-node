package utils;

import java.util.ArrayList;

import NodeInfos.Route;
/**
 * Klasa zarz¹dzaj¹ca nag³ówkiem
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
	 * Tworzy nag³owki dla danej œcie¿ki
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
	 * Dzieli nag³ówek na czêœci 
	 * 
	 */
	public String[] splitHeader(String head)
	{
		String[] paths=head.split("/");
		
		return paths;
	}
	/**
	 * Tworzy nag³ówek z listy nastepnych "hopów" 
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
