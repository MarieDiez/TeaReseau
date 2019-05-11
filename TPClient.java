import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter; // Window Event
import java.awt.event.WindowEvent; // Window Event

import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.BufferedReader;

/**
 * @author Alain BOUJU
 *
 */
public class TPClient extends Frame {

	byte[] etat = new byte[2 * 10 * 10];
	int team;
	int x;
	int y;
	int port = 2000;
	Socket socket = null;
	InputStream in;
	DataOutputStream out;
	TPPanel tpPanel;
	TPCanvas tpCanvas;
	Timer timer;
	static final int LONGUEUR = 650;
	static final int HAUTEUR = 710;

	/** Constructeur */
	public TPClient(int number, int team, int x, int y) {
		setLayout(new BorderLayout());
		tpPanel = new TPPanel(this);
		add("North", tpPanel);
		tpCanvas = new TPCanvas(this.etat);
		add("Center", tpCanvas);
		

		timer = new Timer();
		timer.schedule(new MyTimerTask(), 500, 500);

	}

	/** Action vers droit */
	public synchronized void droit() {
		System.out.println("Droit");
		tpCanvas.repaint();

	}

	/** Action vers gauche */
	public synchronized void gauche() {
		System.out.println("Gauche");

		tpCanvas.repaint();

	}

	/** Action vers gauche */
	public synchronized void haut() {
		System.out.println("Haut");

		tpCanvas.repaint();

	}

	/** Action vers bas */
	public synchronized void bas() {
		System.out.println("Bas");

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
		System.out.println("args :" + args[0]);
		if (args.length != 4) {
			System.out.println("Usage : java TPClient number color positionX positionY ");
			System.exit(0);
		}
		try {
			
			int numero = Integer.parseInt(args[0]);
			int team = Integer.parseInt(args[1]);
			int posX = Integer.parseInt(args[2]);
			int posY = Integer.parseInt(args[3]);
			
			TPClient tPClient = new TPClient(numero,team,posX,posY);
			tPClient.minit(numero,team,posX,posY);

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
			System.out.println("refresh");
			refresh();
		}
	}

}
