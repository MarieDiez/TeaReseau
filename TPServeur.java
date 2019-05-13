import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Johan Guerrero & Marie Diez
 *
 */
public class TPServeur {

	public static void main(String[] args) {
		System.out.println("Serveur Lancé");
		
		ServerSocket server = null;
		try {
			server = new ServerSocket(2000);
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (true) {
			try {
				Socket socket = server.accept();
				Thread thr = new Thread(new ServeurClientThread(socket));
				thr.start();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}

}

class ServeurClientThread implements Runnable{

	private Socket client;
	private DataInputStream inputClient;
	private byte id;
	private byte team;
	private byte x;
	private byte y;
	
	
	public ServeurClientThread(Socket client) {
		this.client = client;
		try {
			this.inputClient = new DataInputStream(this.client.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {

		initialisationClient();
		
		while (true) {
			int c;
			String msg = "";
			
			try {
				while ((c=inputClient.read()) != -1)
				{
					msg += (char)c;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (!msg.equals("")) {
				System.out.println(msg);
			}
			
		}
	}

	private void initialisationClient() {
		byte [] reponse = null;
		byte para;
		
		try {
			int i = 0;
			while ((para=(byte)inputClient.read()) != -1)
			{
				reponse[i] = para;
				i++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.id = reponse[0];
		this.team = reponse[1];
		this.x = reponse[2];
		this.y = reponse[3];
	}
	
}
