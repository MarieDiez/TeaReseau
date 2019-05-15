import java.io.Serializable;

/**
 * @author Johan Guerrero & Marie Diez
 *
 */
public class Joueur implements Serializable {

	private byte id;
	private byte posX;
	private byte posY;
	private Team team;
	private boolean vivant;

	public Joueur(byte id, byte posX, byte posY, Team team) {
		this.id = id;
		this.posX = posX;
		this.posY = posY;
		this.team = team;
		this.vivant = true;
	}

	// Ascesseurs
	public byte getId() {
		return id;
	}
	
	public byte getPosX() {
		return posX;
	}

	public void setPosX(int i) {
		this.posX = (byte) i;
	}

	public byte getPosY() {
		return posY;
	}

	public void setPosY(int i) {
		this.posY = (byte) i;
	}

	public boolean isVivant() {
		return vivant;
	}

	public void setVivant(boolean vivant) {
		this.vivant = vivant;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public void update(Joueur joueur) {
		if (this.id == joueur.id) {
			this.posX = joueur.posX;
			this.posY = joueur.posY;
			this.team = joueur.team;
			this.vivant = joueur.vivant;
		} else {
			System.err.println("Mauvais joueur, le joueur N°" + this.id + " ne peu être mit à jour.");
		}
	}

	public String toString() {
		return "Joueur N°" + this.id + ", équipe " + this.team + " (" + this.posX + "," + this.posY + ")";
	}
}
