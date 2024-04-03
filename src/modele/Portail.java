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
    public boolean entrerSurLaCase(Entite e,Direction d) {

        //Case c = e.getCase();
        //if (c !=null) {
        //    c.quitterLaCase();
        //}
       portailAssocie.setEntite(e);
        e.avancerDirectionChoisie(d);
        return true;
    }
}
