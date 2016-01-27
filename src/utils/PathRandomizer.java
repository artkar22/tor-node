package utils;

import java.util.Random;

import javax.swing.JComboBox;

import NodeInfos.Route;
import NodeInfos.RouteTable;
/**
 * 
 * klasa odpowiadaj¹ca za losowanie œcie¿ki routingowej ze zbioru sciezek
 *
 */
public class PathRandomizer {
	

	private RouteTable availableRoutes;
	private Route randomRoute;
	private RouteTable routes;
	private JComboBox<String> comboBx;
/**
 * Konstruktor PathRandomizer
 * @param routes
 * @param comboBx
 */
	public PathRandomizer(RouteTable routes,JComboBox<String> comboBx)
	{
		availableRoutes=new RouteTable();
		this.routes=routes;
		this.comboBx = comboBx;
	}
	/**
	 * Metoda która losuje scie¿kê routingow¹ z dostêpnych œcie¿ek
	 * 
	 */
	public Route generatePath()
	{
		for(int ind=0;ind<routes.getRoutes().size();ind++)
		   {
				int x=routes.getRoutes().get(ind).getRoute().size();
				String temp =routes.getRoutes().get(ind).getRoute().get(x-1).getNodeName();
			   if(comboBx.getSelectedItem().toString().equals(temp))//sprawdzenie czy wybrany ziomek z comboboxa ma ustalony routing.tu bêdzie wybieraæ drogê
			   {
				   availableRoutes.getRoutes().add(routes.getRoutes().get(ind));
			   }
		   }
		   Random generator = new Random();
		   randomRoute=availableRoutes.getRoutes().get(generator.nextInt(availableRoutes.getRoutes().size()));
		return randomRoute;
	}
}
