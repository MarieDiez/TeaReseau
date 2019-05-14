
/**
 * @author Johan Guerrero & Marie Diez
 *
 */
public class Joueur {

	private byte posX;
	private byte posY;
	private Team team;
	private boolean vivant;
	
	public Joueur(byte posX, byte posY) {
		super();
		this.posX = posX;
		this.posY = posY;
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
