package modele;

public class Glace extends Case {
    public Glace(Jeu _jeu) {
        super(_jeu);
    }

    @Override
    public boolean entrerSurLaCase(Entite e,Direction d) {

        //Case c = e.getCase();
        //if (c !=null) {
        //    c.quitterLaCase();
        //}

        setEntite(e);
        e.avancerDirectionChoisie(d);
        return true;
    }
    @Override
    public boolean peutEtreParcouru() {
        return true; // La case cible peut être parcourue
    }
}
