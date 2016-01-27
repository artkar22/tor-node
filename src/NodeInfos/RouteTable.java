package NodeInfos;

import java.util.ArrayList;
/**
 * Reprezentuje Tablicê routingu - dostêpne œcie¿ki aby dostrzeæ do celu
 * @author Inni
 *
 */
public class RouteTable {
 
	private ArrayList<String> routes;
	private ArrayList<Route> routeTable;
	private ArrayList<NodeInfo> nodes;
	/**
	 * kontruktor domyslny
	 */
	public RouteTable()
	{
		routeTable=new ArrayList<Route>();
	}
	/**
	 * Konstruktor
	 */
	public RouteTable(ArrayList<String> routes,ArrayList<NodeInfo> nodes)
	{
	 this.routes = routes;
	 this.nodes = nodes;
	 routeTable=new ArrayList<Route>();
	 createRouteTable();
	}
	/**
	 * Tworzy tablicê routingow¹
	 */
	private void createRouteTable()
	{
		for(int x = 1;x<routes.size();x++)
		{
		Route path = new Route();
		String[] pathParts = routes.get(x).split("->");
		for(int index=0;index<pathParts.length;index++)
		{
			for(int in=0;in<nodes.size();in++)
			{
				if(nodes.get(in).getNodeName().equals(pathParts[index]))
				{
					NodeInfo partOfTheRoute = new NodeInfo(pathParts[index],nodes.get(in).getPort());
					path.add(partOfTheRoute);
				}
			}
			
			
		}
		routeTable.add(path);
		x++;}
	}
	/**
	 * Zwraca tablicê routingow¹
	 */
	public ArrayList<Route> getRoutes()
	{
		return routeTable;
	}

}
