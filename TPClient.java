import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter; // Window Event
import java.awt.event.WindowEvent; // Window Event

import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Johan Guerrero & Marie Diez
 * 
 */
public class TPClient extends Frame {

	byte[] etat = new byte[2 * 10 * 10];
	int port = 2000;
	Socket socket = null;
	InputStream in;
	// Joueur monJoueur;
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
		tpCanvas = new TPCanvas(this.etat);
		add("Center", tpCanvas);
		// - - - -
		TPClient.joueur = new Joueur(id, x, y, Team.getTeamById(team));
		try {
			this.socket = new Socket("localhost", this.port);
			Thread threadClient = new Thread(new ThreadClient(socket));
			threadClient.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// - - - -
		timer = new Timer();
		timer.schedule(new MyTimerTask(), 500, 500);
	}

	/** Action vers droit */
	public synchronized void droit() {
		TPClient.joueur.setPosX(TPClient.joueur.getPosX() + 1);
		System.out.println("envoie 'Droit' au serveur");
		tpCanvas.repaint();

	}

	/** Action vers gauche */
	public synchronized void gauche() {
		// TODO
		TPClient.joueur.setPosX(TPClient.joueur.getPosX() + 1);
		System.out.println("envoie 'Gauche' au serveur");
		tpCanvas.repaint();

	}

	/** Action vers gauche */
	public synchronized void haut() {
		// TODO
		TPClient.joueur.setPosX(TPClient.joueur.getPosX() + 1);
		System.out.println("envoie 'Haut' au serveur");

		tpCanvas.repaint();

	}

	/** Action vers bas */
	public synchronized void bas() {
		// TODO
		TPClient.joueur.setPosX(TPClient.joueur.getPosX() + 1);
		System.out.println("envoie 'Bas' au serveur");

		tpCanvas.repaint();

	}

	/** Pour rafraichir la situation */
	public synchronized void refresh() {

		tpCanvas.repaint();
	}

	/** Pour recevoir l'Etat */
	public void receiveEtat() {

	}

	/** Initialisations */
	public void minit(int number, int pteam, int px, int py) {

	}

	public String etat() {
		String result = new String();
		return result;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		args = new String[4];
		args[0] = "1";
		args[1] = "2";
		args[2] = "3";
		args[3] = "4";
		if (args.length != 4) {
			System.out.println("Usage : java TPClient number color positionX positionY ");
			System.exit(0);
		}
		try {

			byte numero = (byte) Integer.parseInt(args[0]);
			byte team = (byte) Integer.parseInt(args[1]);
			byte posX = (byte) Integer.parseInt(args[2]);
			byte posY = (byte) Integer.parseInt(args[3]);

			// OutputStream out;
			TPClient tPClient = new TPClient(numero, team, posX, posY);
			tPClient.minit(numero, team, posX, posY);

			// Pour fermeture
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

	/** Pour rafraichir */
	class MyTimerTask extends TimerTask {

		public void run() {
			// System.out.println("refresh");
			// refresh();
		}
	}

}

class ThreadClient implements Runnable {

	private ArrayList<Joueur> joueurs;

	private ObjectInputStream objInput;
	private ObjectOutputStream objOutput;

	public ThreadClient(Socket socket) {
		try {
			this.objOutput = new ObjectOutputStream(socket.getOutputStream());
			this.objInput = new ObjectInputStream(socket.getInputStream());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		Object o = null;
		while (true) {
			try {
				this.objOutput.writeObject(TPClient.joueur);
				o = objInput.readObject();
				if (o instanceof ArrayList<?>) {
					//TODO
					//if (((ArrayList<?>) o).get(0) instanceof Joueur) {
					//	System.out.println("pouet le joueur");
					//}
				}
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
	}

}
