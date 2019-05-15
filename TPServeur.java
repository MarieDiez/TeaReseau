import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

	@SuppressWarnings("resource")
	public static void main(String[] args) {

		System.out.println("Serveur Lancé");
		initGrille();

		ServerSocket server = null;
		try {
			server = new ServerSocket(2000);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// pour chaque client on créer un thread pour le gérer.
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
	
	/**
	 * initialisation de la grille à false, aucun joueur n'est encore présent.
	 */
	private static void initGrille() {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				TPServeur.grille[i][j] = false;
			}
		}

	}

}

/**
 * Chaque client a un threadServeur pour le gérer.
 * @author marie & johan
 *
 */
class ServeurClientThread implements Runnable {

	private Socket client;
	private ObjectOutputStream objOutput;
	private ObjectInputStream objInput;
	private Joueur joueur;

	/**
	 * 
	 * @param client
	 */
	public ServeurClientThread(Socket client) {
		this.client = client;
		try {
			this.objOutput = new ObjectOutputStream(this.client.getOutputStream());
			this.objInput = new ObjectInputStream(this.client.getInputStream());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		boolean fini = false;
		Joueur j = null;

		// initialisation du joueur
		try {
			j = (Joueur) this.objInput.readObject();
			this.joueur = joueurOkInit(j);
			this.objOutput.writeObject(this.joueur);
		} catch (ClassNotFoundException | IOException e1) {
			e1.printStackTrace();
		}
		if (this.joueur.getId() == -1) {
			fini = true;
		} else {
			// on ajoute le joueur a la liste des joueurs.
			TPServeur.joueurs.add(this.joueur);
		}

		/* boucle de lecture du joueur et d envoie de la liste des joueurs au client*/
		while (!fini) {
			try {
				byte keep_x = this.joueur.getPosX();
				byte keep_y = this.joueur.getPosY();

				// écoute du client
				this.joueur.update((Joueur) this.objInput.readUnshared());
				joueurOk(this.joueur, keep_x, keep_y);

				//testes de capture de joueurs
				etatVerticle(this.joueur);
				etatHorizontal(this.joueur);

				// réponse au client
				this.objOutput.writeInt(TPServeur.joueurs.size());
				for (int i = 0; i < TPServeur.joueurs.size(); i++) {
					this.objOutput.writeUnshared(TPServeur.joueurs.get(i));
				}

			} catch (IOException e) {
				// e.printStackTrace();
				System.out.println("Le client " + this.joueur.toString() + " est clot");
				TPServeur.joueurs.remove(this.joueur);
				TPServeur.grille[this.joueur.getPosX()][this.joueur.getPosY()] = false;
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

	/**
	 * Verifie si un joueur est encerclé horizontalement
	 * @param j
	 */
	private void etatHorizontal(Joueur j) {
		
		// si le joueur n'est pas sur les extremités
		if (j.getPosX() != 0 && j.getPosX() != 9) {

			// si 2 joueurs l'encadre
			if (TPServeur.grille[j.getPosX() - 1][j.getPosY()] && TPServeur.grille[j.getPosX() + 1][j.getPosY()]) {

				Joueur advDroite = advPos((byte) (j.getPosX() + 1), j.getPosY());
				Joueur advGauche = advPos((byte) (j.getPosX() - 1), j.getPosY());

				// et sont entre eux de la meme equipe mais differente de celle du joueur
				if ((advDroite.getTeam() == advGauche.getTeam()) && advDroite.getTeam() != j.getTeam()) {
					j.setVivant(false);
					j.setTeam(Team.noir);
				}
			}
		}
	}

	/**
	 * Verifie si un joueur est encerclé verticalement
	 * @param j
	 */
	private void etatVerticle(Joueur j) {
		
		// si le joueur n'est pas sur les extremités
		if (j.getPosY() != 0 && j.getPosY() != 9) {

			// si 2 joueurs l'encadre
			if (TPServeur.grille[j.getPosX()][j.getPosY() + 1] && TPServeur.grille[j.getPosX()][j.getPosY() - 1]) {

				Joueur advSup = advPos(j.getPosX(), (byte) (j.getPosY() + 1));
				Joueur advInf = advPos(j.getPosX(), (byte) (j.getPosY() - 1));

				// et sont entre eux de la meme equipe mais differente de celle du joueur
				if ((advSup.getTeam() == advInf.getTeam()) && advSup.getTeam() != j.getTeam()) {
					j.setVivant(false);
					j.setTeam(Team.noir);
				}
			}
		}
	}

	/**
	 * Donne le joueur de la liste de joueurs a la position demander
	 * @param posX
	 * @param posY
	 * @return
	 */
	private Joueur advPos(byte posX, byte posY) {
		for (int i = 0; i < TPServeur.joueurs.size(); i++) {
			if (TPServeur.joueurs.get(i).getPosX() == posX && TPServeur.joueurs.get(i).getPosY() == posY) {
				return TPServeur.joueurs.get(i);
			}
		}
		return null;
	}

	/**
	 * Verifie si la case est libre, sinon le joueur ne bouge pas
	 * @param joueur
	 * @param x
	 * @param y
	 */
	private void joueurOk(Joueur joueur, byte x, byte y) {
		if (TPServeur.grille[joueur.getPosX()][joueur.getPosY()]) {
			joueur.setPosX(x);
			joueur.setPosY(y);
		} else {
			TPServeur.grille[x][y] = false;
			TPServeur.grille[joueur.getPosX()][joueur.getPosY()] = true;
		}
	}

	/**
	 * initialisation du joueur, on controle l'id, si il est deja utilisé, 
	 * le client doit en donner un autre.
	 * Si le joueur veut commencer sur une case incomatible (joueur deja present ou hors champs),
	 * une case aleatoire lui est attribuée.
	 * 
	 * @param joueur
	 * @return
	 */
	private Joueur joueurOkInit(Joueur joueur) {
		for (Joueur j : TPServeur.joueurs) {
			if (j.getId() == joueur.getId()) {
				return new Joueur((byte) -1, (byte) -1, (byte) -1, joueur.getTeam());
			}
		}
		
		// test si la position demandé est dans un carré 10x10 et que la place est libre sinon, la position est choisit aléatoirement
		while ( joueur.getPosX() > 9 || joueur.getPosX() < 0 || joueur.getPosY() > 9 || joueur.getPosY() < 0 || TPServeur.grille[joueur.getPosX()][joueur.getPosY()]) {
			joueur.setPosX((int) (Math.random() * 10));
			joueur.setPosY((int) (Math.random() * 10));
		}
		TPServeur.grille[joueur.getPosX()][joueur.getPosY()] = true;
		return new Joueur(joueur.getId(), joueur.getPosX(), joueur.getPosY(), joueur.getTeam());

	}

}