package Parser;
import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import NodeInfos.NodeInfo;

import java.util.ArrayList;

/**
 * Klasa odpowiadaj¹ca za odczytywanie danych z pliku konfiguracyjnego
 *
 */
public class ConfigParser {

	private File fXmlFile;
	private String documentName;
	public ConfigParser(String docName){
		documentName=docName;
	 fXmlFile = new File(docName);
	}

	/**
	 * Tworzy zbiór wszystkich routerów
	 */
public ArrayList<NodeInfo> parseNodes(ArrayList<String> networkInfo)
{
	ArrayList<NodeInfo> nodeList = new ArrayList<NodeInfo>();
	for(int x =0;x<networkInfo.size();x=x+2)
	{
		NodeInfo node = new NodeInfo(networkInfo.get(x+1),networkInfo.get(x));
		nodeList.add(node);
	}
	return nodeList;
}
/**
 * Parsowanie konfiguracji
 */
public ArrayList<ArrayList<String>> Parse(){
	ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>();
	String serverPort = "";
	String path ="";
	String id = "";
	//String[] x = new String[2];
	ArrayList<String> neighbourTable = new ArrayList<String>();
	ArrayList<String> routerTable = new ArrayList<String>(); //tablica routerów
	ArrayList<String> routingTable = new ArrayList<String>(); //tablica routingu
	try{
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	Document doc = (Document) dBuilder.parse(fXmlFile);
	((org.w3c.dom.Document) doc).getDocumentElement().normalize();
	
	NodeList nList = ((org.w3c.dom.Document) doc).getElementsByTagName("Node");
	
	for (int temp = 0; temp < nList.getLength(); temp++) {

		Node nNode = (Node) nList.item(temp);
				
		System.out.println("\nCurrent Element :" + nNode.getNodeName());
				
		if (nNode.getNodeType() == Node.ELEMENT_NODE) {

			Element eElement = (Element) nNode;

			System.out.println("Staff id : " + eElement.getAttribute("id"));
			id = eElement.getAttribute("id");
			//System.out.println("First Name : " + eElement.getElementsByTagName("serverport").item(0).getTextContent());
			serverPort = eElement.getElementsByTagName("serverport").item(0).getTextContent();
			
		/*	x[0] = serverPort;
			x[1] = id;*/
			routerTable.add(serverPort);
			routerTable.add(id);
			table.add(routerTable);
		}
	}
		/////////////Neighbours////////////
	   ///////////////////////////////////
	if(!documentName.matches("config/network.xml"))
		{NodeList neighList = ((org.w3c.dom.Document) doc).getElementsByTagName("Neighbour");
		
		for (int tempo = 0; tempo < neighList.getLength(); tempo++) {

			Node neighNode = (Node) neighList.item(tempo);
					
			//System.out.println("\nCurrent Element :" + neighNode.getNodeName());
					
			if (neighNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) neighNode;

				//System.out.println("Staff id : " + eElement.getAttribute("id"));
				id = eElement.getAttribute("id");
				//System.out.println("First Name : " + eElement.getElementsByTagName("serverport").item(0).getTextContent());
				serverPort = eElement.getElementsByTagName("serverport").item(0).getTextContent();
				neighbourTable.add(serverPort);
				neighbourTable.add(id);
				}
	}
		
	NodeList routeList = ((org.w3c.dom.Document) doc).getElementsByTagName("Route");
	
	for (int tem = 0; tem < routeList.getLength(); tem++) {

		Node routeNode = (Node) routeList.item(tem);
				
		//System.out.println("\nCurrent Element :" + neighNode.getNodeName());
				
		if (routeNode.getNodeType() == Node.ELEMENT_NODE) {

			Element eElement = (Element) routeNode;

			id = eElement.getAttribute("id");
			routingTable.add(id);
			NodeList pathList = /*((org.w3c.dom.Document) doc)*/eElement.getElementsByTagName("path");
			for(int index = 0; index<pathList.getLength();index++)
			{
			path = eElement.getElementsByTagName("path").item(index).getTextContent();
			routingTable.add(path);
			}
			//routingTable.add(id);
			}
}}
	table.add(neighbourTable);
	table.add(routingTable);		
		
	}
	catch (Exception e) {
		e.printStackTrace();
	    }
	//int foo = Integer.parseInt(serverPort);
	return table;
}



}
