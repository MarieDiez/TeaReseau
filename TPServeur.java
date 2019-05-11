import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
	private InputStream inputClient;
	
	public ServeurClientThread(Socket client) {
		this.client = client;
		try {
			this.inputClient = this.client.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		
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
	
	
	
}
