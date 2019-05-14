import java.awt.Color;

public enum Team {
	noir(0, Color.black),
	bleu(1, Color.blue),
	rouge(2, Color.red),
	vert(3, Color.green),
	jaune(4, Color.yellow);
	
	private int id;
	private Color couleur;
	
	Team(int id, Color col){
		this.id = id;
		this.couleur = col;
	}

	public Color getCouleur() {
		return couleur;
	}
	
	public static Team getTeamById(int id) {
		Team returnTeam = null;
		for (Team t : Team.values()) {
			if(t.id == id) {
				returnTeam = t;
			}
		}
		return returnTeam;
	}
}
