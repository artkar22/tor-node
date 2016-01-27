package NodeInfos;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Przechowuje informacje o wêŸle sieci
 *
 */
public class NodeInfo implements Serializable {
	private static final long serialVersionUID = 3L;
	private String nodeName;
	private String port;
	private ArrayList<NodeInfo> neighbours;
	/**
	 * Konstruktor domyslny
	 */
	public NodeInfo()
	{}
	/**
	 * Konstruktor
	 */
	public NodeInfo(String nodeName, String port)
	{
		this.nodeName=nodeName;
		this.port=port;
		neighbours = new ArrayList<NodeInfo>();
	}
	/**
	 * Ustawia s¹siadów wêz³a- z kim mo¿e siê komunikowaæ
	 */
	public void setNeighbours(ArrayList<String> neighbours)
	{
		for(int x=0;x<neighbours.size();x=x+2)
		{
			NodeInfo node = new NodeInfo(neighbours.get(x+1),neighbours.get(x));
			this.neighbours.add(node);
		}
	}
	
	/**
	 * Zwraca nazwê wêz³a
	 */
	public String getNodeName() {
		return nodeName;
	}
/**
 * zwraca port na którym jest dostepny wêze³
 */
	public String getPort() {
		return port;
	}
	/**
	 * zwraca port na którym jest dostepny wêze³ jako liczbe calkowit¹
	 */
	public int getIntPort() {
		return Integer.parseInt(port);
	}
}
