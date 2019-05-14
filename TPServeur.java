import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Johan Guerrero & Marie Diez
 *
 */
public class TPServeur {

	public static boolean[][] grille = new boolean[10][10];
	
	public static void main(String[] args) {		
		
		System.out.println("Serveur Lancé");
		initGrille();
		
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

	private static void initGrille() {
		
		for (int i = 0 ; i < 10 ; i++) {
			for (int j = 0 ; j < 10 ; j++) {
				TPServeur.grille[i][j] = false;
			}
		}
		
	}

}

class ServeurClientThread implements Runnable{

	private Socket client;
	private DataInputStream inputClient;
	private OutputStream outputClient;
	private byte id;
	private byte team;
	private byte x;
	private byte y;
	
	public ServeurClientThread(Socket client) {
		this.client = client;
		try {
			this.inputClient = new DataInputStream(this.client.getInputStream());
			this.outputClient = this.client.getOutputStream();
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
		byte [] reponse = new byte[4];
		byte para;
		
		try {
			for (int i = 0 ; i < 4 ; i++) {
				para=(byte)inputClient.read();
				reponse[i] = para;
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.id = reponse[0];
		this.team = reponse[1];
		this.x = reponse[2];
		this.y = reponse[3];
		
		//TODO verification dispo case : accepte par def
		TPServeur.grille[this.x][this.y] = true;
		try {
			this.outputClient.write(this.x);
			this.outputClient.write(this.y);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
