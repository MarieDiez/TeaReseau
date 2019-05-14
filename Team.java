import java.awt.Color;

public enum Team {
	noir(Color.black),
	bleu(Color.blue),
	rouge(Color.red),
	vert(Color.green),
	jaune(Color.yellow);
	
	private Color couleur;
	
	Team(Color col){
		this.couleur = col;
	}

	public Color getCouleur() {
		return couleur;
	}
}
