package NodeInfos;

import java.util.ArrayList;

/**
 * Reprezentuje œcie¿ke routingu
 *
 */

public class Route {
	
	private ArrayList<NodeInfo> route;
	
	public Route(){
		route= new ArrayList<NodeInfo>();
		
	};
	/**
	 * Dodaje wêze³ do œcie¿ki
	 */
	public void add(NodeInfo node)
	{
		route.add(node);
	}
	/**
	 * Zwraca œcie¿ke
	 */
	public ArrayList<NodeInfo> getRoute()
	{
		return route;
	};
	
}
