package modele;

public class Portail extends Case {
    private Portail portailAssocie;

    public Portail(Jeu _jeu) {
        super(_jeu);
    }

    public void setPortailAssocie(Portail portailAssocie) {
        this.portailAssocie = portailAssocie;
    }

    public Portail getPortailAssocie() {
        return portailAssocie;
    }
    @Override
    public boolean peutEtreParcouru() {
        return true; // Les portails peuvent être parcourus
    }
}
