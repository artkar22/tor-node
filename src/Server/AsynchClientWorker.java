package Server;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channels;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
/**
 * NOT-USED
 *
 */
public class AsynchClientWorker implements Runnable {

	private AsynchronousServerSocketChannel client;
	private ObjectInputStream ois = null;
	  //Constructor
	  public AsynchClientWorker(AsynchronousServerSocketChannel client, String serverId) {
	    this.client = client;
	  }
	  
	  public void run(){
	    InputStream in = null;
	    while(true){
	    Future<AsynchronousSocketChannel> serverFuture = client.accept();
	    try{
	    	final AsynchronousSocketChannel clientSocket = serverFuture.get();
	      in = Channels.newInputStream(clientSocket);
	        ois = new ObjectInputStream(in);
	    } catch (IOException | InterruptedException | ExecutionException e) {
	      System.out.println("in or out failed");
	      e.printStackTrace();
	      System.exit(-1);
	    }

	   
	      try{
	    	   Object obj = ois.readObject();///tu jest b³¹d- Ÿle dopasowuje typ
	    	   obj.toString();

		    	  }catch (IOException e) {		  
		    		  serverFuture.isDone();
		    		  continue;
	       } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	  }
	  } 
}
