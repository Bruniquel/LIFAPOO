package modele;

public class Target extends Case {
    public Target(Jeu _jeu) {
        super(_jeu);
    }

    @Override
    public boolean peutEtreParcouru() {
        return true; // La case cible peut être parcourue
    }
}
