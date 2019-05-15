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

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		boolean fini = false;
		Joueur j = null;

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
			TPServeur.joueurs.add(this.joueur);
		}

		while (!fini) {
			try {
				byte keep_x = this.joueur.getPosX();
				byte keep_y = this.joueur.getPosY();

				// écoute du client
				this.joueur.update((Joueur) this.objInput.readUnshared());
				joueurOk(this.joueur, keep_x, keep_y);

				//testes de capture
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

	private void etatHorizontal(Joueur j) {
		if (j.getPosX() != 0 && j.getPosX() != 9) {

			if (TPServeur.grille[j.getPosX() - 1][j.getPosY()] && TPServeur.grille[j.getPosX() + 1][j.getPosY()]) {

				Joueur advDroite = advPos((byte) (j.getPosX() + 1), j.getPosY());
				Joueur advGauche = advPos((byte) (j.getPosX() - 1), j.getPosY());

				if ((advDroite.getTeam() == advGauche.getTeam()) && advDroite.getTeam() != j.getTeam()) {
					j.setVivant(false);
					j.setTeam(Team.noir);
				}
			}
		}
	}

	private void etatVerticle(Joueur j) {
		if (j.getPosY() != 0 && j.getPosY() != 9) {

			if (TPServeur.grille[j.getPosX()][j.getPosY() + 1] && TPServeur.grille[j.getPosX()][j.getPosY() - 1]) {

				Joueur advSup = advPos(j.getPosX(), (byte) (j.getPosY() + 1));
				Joueur advInf = advPos(j.getPosX(), (byte) (j.getPosY() - 1));

				if ((advSup.getTeam() == advInf.getTeam()) && advSup.getTeam() != j.getTeam()) {
					j.setVivant(false);
					j.setTeam(Team.noir);
				}
			}
		}
	}

	private Joueur advPos(byte posX, byte posY) {
		for (int i = 0; i < TPServeur.joueurs.size(); i++) {
			if (TPServeur.joueurs.get(i).getPosX() == posX && TPServeur.joueurs.get(i).getPosY() == posY) {
				return TPServeur.joueurs.get(i);
			}
		}
		return null;
	}

	private void joueurOk(Joueur joueur, byte x, byte y) {
		if (TPServeur.grille[joueur.getPosX()][joueur.getPosY()]) {
			joueur.setPosX(x);
			joueur.setPosY(y);
		} else {
			TPServeur.grille[x][y] = false;
			TPServeur.grille[joueur.getPosX()][joueur.getPosY()] = true;
		}
	}

	private Joueur joueurOkInit(Joueur joueur) {
		for (Joueur j : TPServeur.joueurs) {
			if (j.getId() == joueur.getId()) {
				return new Joueur((byte) -1, (byte) -1, (byte) -1, joueur.getTeam());
			}
		}
		while (TPServeur.grille[joueur.getPosX()][joueur.getPosY()]) {
			joueur.setPosX((int) (Math.random() * 10));
			joueur.setPosY((int) (Math.random() * 10));
		}
		TPServeur.grille[joueur.getPosX()][joueur.getPosY()] = true;
		return new Joueur(joueur.getId(), joueur.getPosX(), joueur.getPosY(), joueur.getTeam());

	}

}