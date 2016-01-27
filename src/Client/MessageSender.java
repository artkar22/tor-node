package Client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.thoughtworks.xstream.XStream;
/**
 * Klasa odpowiedzialna za wyslanie wiadomosci
 *
 */
public class MessageSender {
	private Socket socket;
	private int clientPort;
	private ObjectOutputStream out;
	/**
	 * Konstruktor klasy odpowiedzialnej za wyslanie wiadomosci
	 */
	public MessageSender(Socket socket,int clientPort)
	{
		this.socket=socket;
		this.clientPort=clientPort;
	}
/**
 * Metoda za pomoc¹ której wysylamy wiadomoœæ	
 */
	public void WSocket(Message message){	
		   try{
			
		     socket = new Socket(InetAddress.getLocalHost().getHostAddress(), clientPort);
		   //  out =xstream.createObjectOutputStream(socket.getOutputStream());
		     out = new ObjectOutputStream(socket.getOutputStream());
		     out.writeObject(message);
		    
		    // out.writeObject(xml);
//		    OutputStreamWriter osw =new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
//		        osw.write(xml);
		   } catch (UnknownHostException e) {
		     System.out.println("Unknown host: kq6py");
		     System.exit(1);
		   } catch  (IOException e) {
			   e.printStackTrace();
		     System.out.println("No I/O WSOCKET 176 LINIA");
		     System.exit(1);
		   }
		}
}
