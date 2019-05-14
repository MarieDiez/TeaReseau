import java.io.Serializable;

/**
 * @author Johan Guerrero & Marie Diez
 *
 */
public class Joueur implements Serializable{
	
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
	public byte getPosX() {
		return posX;
	}


	public void setPosX(byte posX) {
		this.posX = posX;
	}


	public byte getPosY() {
		return posY;
	}


	public void setPosY(byte posY) {
		this.posY = posY;
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
	
}
