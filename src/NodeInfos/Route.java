package NodeInfos;

import java.util.ArrayList;

/**
 * Reprezentuje �cie�ke routingu
 *
 */

public class Route {
	
	private ArrayList<NodeInfo> route;
	
	public Route(){
		route= new ArrayList<NodeInfo>();
		
	};
	/**
	 * Dodaje w�ze� do �cie�ki
	 */
	public void add(NodeInfo node)
	{
		route.add(node);
	}
	/**
	 * Zwraca �cie�ke
	 */
	public ArrayList<NodeInfo> getRoute()
	{
		return route;
	};
	
}
