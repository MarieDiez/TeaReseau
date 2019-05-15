import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter; // Window Event
import java.awt.event.WindowEvent; // Window Event

import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Johan Guerrero & Marie Diez
 * 
 */
@SuppressWarnings("serial")
public class TPClient extends Frame {

	int port = 2000;
	Socket socket = null;

	static Joueur joueur;
	TPPanel tpPanel;
	TPCanvas tpCanvas;
	Timer timer;
	static final int LONGUEUR = 650; //
	static final int HAUTEUR = 710;

	/** Constructeur */
	// number -> id
	public TPClient(byte id, byte team, byte x, byte y) {
		setLayout(new BorderLayout());
		tpPanel = new TPPanel(this);
		add("North", tpPanel);
		tpCanvas = new TPCanvas();
		add("Center", tpCanvas);
		// - - - -
		TPClient.joueur = new Joueur(id, x, y, Team.getTeamById(team));
		try {
			this.socket = new Socket("localhost", this.port);
			Thread threadClient = new Thread(new ThreadClient(socket, tpCanvas));
			threadClient.start();
		} catch (IOException e) {
			System.err.println("Pas de Serveur: " + e.getLocalizedMessage());
			System.exit(-1);
			// e.printStackTrace();

		}

	}

	/** Action vers droit */
	public synchronized void droit() {
		if (TPClient.joueur.isVivant()) {
			if ((TPClient.joueur.getPosX() + 1) < 10) {
				TPClient.joueur.setPosX(TPClient.joueur.getPosX() + 1);
				//System.out.println("envoie 'Droit' au serveur");
				tpCanvas.repaint();
			}
		}

	}

	/** Action vers gauche */
	public synchronized void gauche() {
		if (TPClient.joueur.isVivant()) {
			if ((TPClient.joueur.getPosX() - 1) >= 0) {
				TPClient.joueur.setPosX(TPClient.joueur.getPosX() - 1);
				//System.out.println("envoie 'Gauche' au serveur");
				tpCanvas.repaint();
			}
		}

	}

	/** Action vers gauche */
	public synchronized void haut() {
		if (TPClient.joueur.isVivant()) {
			if ((TPClient.joueur.getPosY() - 1) >= 0) {
				TPClient.joueur.setPosY(TPClient.joueur.getPosY() - 1);
				//System.out.println("envoie 'Haut' au serveur");
				tpCanvas.repaint();
			}
		}

	}

	/** Action vers bas */
	public synchronized void bas() {
		if (TPClient.joueur.isVivant()) {
			if ((TPClient.joueur.getPosY() + 1) < 10) {
				TPClient.joueur.setPosY(TPClient.joueur.getPosY() + 1);
				//System.out.println("envoie 'Bas' au serveur");
				tpCanvas.repaint();
			}

		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 4) {
			System.err.println("Usage : java TPClient identifiant id_equipe posX posY ");
			System.exit(0);
		}
		try {

			byte numero = (byte) Integer.parseInt(args[0]);
			byte team = (byte) Integer.parseInt(args[1]);
			byte posX = (byte) Integer.parseInt(args[2]);
			byte posY = (byte) Integer.parseInt(args[3]);
			posX--;
			posY--;

			TPClient tPClient = new TPClient(numero, team, posX, posY);

			// Pour fermeture
			tPClient.setResizable(false);
			tPClient.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});

			// Create Panel back forward
			tPClient.pack();
			tPClient.setSize(LONGUEUR, HAUTEUR);
			tPClient.setVisible(true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

}

/**
 * Un threadClient est créer pour chaque client
 * @author marie & johan
 *
 */
class ThreadClient implements Runnable {

	private ObjectInputStream objInput;
	private ObjectOutputStream objOutput;
	private TPCanvas canvas;

	/**
	 * 
	 * @param socket
	 * @param canvas
	 */
	public ThreadClient(Socket socket, TPCanvas canvas) {
		this.canvas = canvas;
		try {
			this.objOutput = new ObjectOutputStream(socket.getOutputStream());
			this.objInput = new ObjectInputStream(socket.getInputStream());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		boolean fini = false;
		
		// initialisation joueur avec id compatible
		try {
			this.objOutput.writeUnshared(TPClient.joueur);
			TPClient.joueur = (Joueur) objInput.readUnshared();
		} catch (ClassNotFoundException | IOException e1) {
			e1.printStackTrace();
		}
		if (TPClient.joueur.getId() == -1) {
			System.err.println("L'identifiant demandé est déjà prit, choisissez en un autre.");
			fini = true;
			System.exit(0);
		}
		
		// boucle d'envoie du joueur avec reception de la liste de joueurs du serveur
		while (!fini) {
			try {
				Thread.sleep(120);
				
				// on envoie le joueur au serveur, qui l'ajoutera a la liste des joueurs
				this.objOutput.writeUnshared(TPClient.joueur);
				
				// reception de la liste de joueurs
				canvas.joueurs = new ArrayList<Joueur>();
				int size = objInput.readInt();
				for (int i = 0; i < size; i++) {
					Joueur j = (Joueur) objInput.readUnshared();
					
					// on actualise les coordonnées du joueur, si celle ci n ont pu être 
					// réaliser par le serveur.
					TPClient.joueur.update(j);
					canvas.joueurs.add(j);
				}
				canvas.repaint();

			} catch (ClassNotFoundException | IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
