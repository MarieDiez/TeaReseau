import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 * @author Johan Guerrero & Marie Diez
 *
 */
@SuppressWarnings("serial")
public class TPCanvas extends Canvas {

	int size = 645;
	int nbPosition = 10;
	ArrayList<Joueur> joueurs;
	
	
	Color[] color = { Color.black, Color.blue, Color.red, Color.green, Color.yellow };
	// 0 black
	// 1 blue
	// 2 red
	// 3 green (bloqué blue)
	// 4 yellow (bloqué red);

	public TPCanvas() {
		this.joueurs = new ArrayList<Joueur>();
	}

	public void paint(Graphics win) {
		paintCarte(win);
		drawJoueurs(win);
	}

	public Dimension getMinimumSize() {
		return new Dimension(size, size);

	}

	public void paintCarte(Graphics win) {
		win.drawRect(0, 0, size - 1, size - 1); // Draw border
		for (int i = 1; i < 10; i++) {
			// Dessin
			win.setColor(Color.black);
			win.drawLine(i * size / nbPosition, 0, i * size / nbPosition, size);
			win.drawLine(0, i * size / nbPosition, size, i * size / nbPosition);
		}

	}

	public void drawJoueurs(Graphics win) {
		for (Joueur j : this.joueurs) {
				// System.out.println("Joueur "+etat[2*i]+ " X "+i%10+" Y "+i/10);
				//drawJoueur(win, i % 10, i / 10, etat[2 * i + 1]);
				drawJoueur(win, j);
		}
	}

	public void drawJoueur(Graphics win, Joueur joueur) {
		win.setColor(joueur.getTeam().getCouleur());
		win.fillOval((joueur.getPosX() * size / nbPosition) + 1, (joueur.getPosY() * size / nbPosition) + 1, size / nbPosition - 1,
				size / nbPosition - 1);
	}

}
