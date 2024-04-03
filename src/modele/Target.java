package modele;

public class Target extends Case {
    Jeu jeu;
    public Target(Jeu _jeu) {
        super(_jeu);
        jeu=_jeu;
    }

    @Override
    public boolean entrerSurLaCase(Entite e,Direction d) {

        //Case c = e.getCase();
        //if (c !=null) {
        //    c.quitterLaCase();
        //}
        if (e instanceof Bloc) {
            jeu.passerAuNiveauSuivant();
        }
        setEntite(e);
        return true;
    }
    @Override
    public boolean peutEtreParcouru() {
        return true; // La case cible peut être parcourue
    }
}
