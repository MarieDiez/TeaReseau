import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author Johan Guerrero & Marie Diez
 *
 */
public class TPServeur {

	public static boolean[][] grille = new boolean[10][10];
	public static ArrayList<Joueur> joueurs = new ArrayList<Joueur>();

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

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				TPServeur.grille[i][j] = false;
			}
		}

	}

}

class ServeurClientThread implements Runnable {

	private Socket client;
	private ObjectOutputStream objOutput;
	private ObjectInputStream objInput;
	private Joueur joueur;

	public ServeurClientThread(Socket client) {
		this.client = client;
		try {
			this.objOutput = new ObjectOutputStream(this.client.getOutputStream());
			this.objInput = new ObjectInputStream(this.client.getInputStream());	
			//
			//this.joueur = initialisationClient();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		boolean fini = false;
		while (!fini) {
			try {
				// écoute du client
				this.joueur = (Joueur) this.objInput.readObject();
				
				//TODO test de positionnement + ajout dans l'array
				
				// réponse au client
				this.objOutput.writeObject(TPServeur.joueurs);
			} catch (IOException e) {
				//e.printStackTrace();
				System.out.println("Le client "+this.joueur.toString()+" est clot");
				fini = true;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		try {
			this.client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Joueur initialisationClient() {
		byte[] reponse = new byte[4];
		Joueur joueur = null;
		try {
			joueur = (Joueur) this.objInput.readObject();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte id = reponse[0];
		Team team = Team.getTeamById(reponse[1]);
		byte x = reponse[2];
		byte y = reponse[3];

		// TODO verification dispo case : accepte par def
		Joueur ceJoueur = new Joueur(id, x, y, team);
		TPServeur.grille[x][y] = true;
		TPServeur.joueurs.add(ceJoueur);

		return ceJoueur;
	}

}