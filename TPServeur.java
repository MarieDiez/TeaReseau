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
		Joueur j = null;
		
		try {
			j = (Joueur) this.objInput.readObject();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		this.joueur = joueurOkInit(j);
		TPServeur.joueurs.add(this.joueur);
		
		while (!fini) {
			try {
				// réponse au client
				
				
				this.objOutput.writeObject(TPServeur.joueurs.size());
				for (int i = 0 ; i < TPServeur.joueurs.size() ; i ++) {
					this.objOutput.writeObject(TPServeur.joueurs.get(i));
				}
				
				byte keep_x = this.joueur.getPosX();
				byte keep_y = this.joueur.getPosY();
				
				// écoute du client
				this.joueur.update((Joueur) this.objInput.readObject());
				
				//TODO test de positionnement + ajout dans l'array
				joueurOk(this.joueur,keep_x,keep_y);
				
				
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

	private void joueurOk(Joueur joueur, byte x , byte y) {
		
		if (TPServeur.grille[joueur.getPosX()][joueur.getPosY()]) {
			joueur.setPosX(x);
			joueur.setPosY(y);
		}
	}

	private Joueur joueurOkInit(Joueur joueur) {
		
		while (TPServeur.grille[joueur.getPosX()][joueur.getPosY()]) {
			joueur.setPosX((int)(Math.random()*10));
			joueur.setPosY((int)(Math.random()*10));		
		}
		TPServeur.grille[joueur.getPosX()][joueur.getPosY()] = true;
		return new Joueur(joueur.getId(),joueur.getPosX(),joueur.getPosY(),joueur.getTeam());
	}

}