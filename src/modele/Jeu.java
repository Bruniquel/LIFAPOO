/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele;


import java.awt.Point;
import java.util.HashMap;
import java.util.Observable;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;


public class Jeu extends Observable {

    public static final int SIZE_X = 20;
    public static final int SIZE_Y = 10;

    private int niveauActuel = 0; // Niveau actuel du jeu

    private Heros heros;

    private HashMap<Case, Point> map = new  HashMap<Case, Point>(); // permet de récupérer la position d'une case à partir de sa référence
    private Case[][] grilleEntites = new Case[SIZE_X][SIZE_Y]; // permet de récupérer une case à partir de ses coordonnées

    private Vector<Portail> tabPortails;
    private Point targetPosition;
    private File[] tabLevels;

    public Jeu() {

        //initialisationNiveau1();
        tabPortails=new Vector<Portail>();
        initLevelsFromFiles();
        initNiveau(tabLevels[0]);
    }


    
    public Case[][] getGrille() {
        return grilleEntites;
    }
    
    public Heros getHeros() {
        return heros;
    }

    public void deplacerHeros(Direction d) {
        heros.avancerDirectionChoisie(d);
        setChanged();
        notifyObservers();
    }

    private void initLevelsFromFiles(){
        File folder = new File("Data");
        if (folder.isDirectory())
        {
            tabLevels = folder.listFiles();
        }
    }

    // Méthode pour passer au niveau suivant
    public void passerAuNiveauSuivant() {
        niveauActuel++;
        if (niveauActuel==tabLevels.length){
            afficherMessage("Vous avez atteint le dernier niveau !");
        } else {
            initNiveau(tabLevels[niveauActuel]);
        }

    }

    private void linkPortails() {
        if (tabPortails.size() % 2 !=0) {
            System.out.println("/!\\ ATTENTION : il y a un nombre impair de portails dans le niveau. Ils sont donc désactivés /!\\ ");
            return;
        }
        for (int i=0;i<tabPortails.size();i+=2) {
            tabPortails.elementAt(i).setPortailAssocie(tabPortails.elementAt(i+1));
            tabPortails.elementAt(i+1).setPortailAssocie(tabPortails.elementAt(i));
        }

    }
    private void initialisationNiveau1() {
        // Murs extérieurs horizontaux
        for (int x = 0; x < 20; x++) {
            addCase(new Mur(this), x, 0);
            addCase(new Mur(this), x, 9);
        }

        // Murs extérieurs verticaux
        for (int y = 1; y < 9; y++) {
            addCase(new Mur(this), 0, y);
            addCase(new Mur(this), 19, y);
        }

        // Cases vides
        for (int x = 1; x < 19; x++) {
            for (int y = 1; y < 9; y++) {
                addCase(new Vide(this), x, y);
            }
        }

        // Position du héros
        heros = new Heros(this, grilleEntites[4][4]);

        // Position du bloc
        Bloc b = new Bloc(this, grilleEntites[6][6]);

        // Position de la cible
        targetPosition = new Point(10, 5);
        addCase(new Target(this), targetPosition.x, targetPosition.y);
        addCase(new Glace(this), 5, 5);
        addCase(new Glace(this), 5, 6);

        // Position des portails
        Portail portail1 = new Portail(this);
        Portail portail2 = new Portail(this);
        addCase(portail1, 3, 5);
        addCase(portail2, 15, 5);
        portail1.setPortailAssocie(portail2);
        portail2.setPortailAssocie(portail1);
    }

    private void initialisationNiveau2() {

        for (int x = 0; x < 20; x++) {
            addCase(new Mur(this), x, 0);
            addCase(new Mur(this), x, 9);

        }

        // Murs extérieurs verticaux
        for (int y = 1; y < 9; y++) {
            addCase(new Mur(this), 0, y);
            addCase(new Mur(this), 19, y);

        }

        // Cases vides
        for (int x = 1; x < 19; x++) {
            for (int y = 1; y < 9; y++) {
                addCase(new Vide(this), x, y);
                addCase(new Mur(this), x, 4);
            }
        }


        // Position du bloc
        Bloc b = new Bloc(this, grilleEntites[6][6]);

        // Position de la cible
        targetPosition = new Point(6, 1);
        addCase(new Target(this), targetPosition.x, targetPosition.y);
        Portail portail1 = new Portail(this);
        Portail portail2 = new Portail(this);
        addCase(portail1, 6, 5);
        addCase(portail2, 1, 2);
        portail1.setPortailAssocie(portail2);
        portail2.setPortailAssocie(portail1);
    }

    private void initNiveau(File file) {
        try {
            Scanner myReader = new Scanner(file);
            int y=0;
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                for (int x=0;x<20;x++) {
                    switch(data.charAt(x)) {
                        case 'M':
                            addCase(new Mur(this), x, y);
                            break;
                        case 'H':
                            addCase(new Vide(this), x, y);
                            heros = new Heros(this, grilleEntites[x][y]);
                            break;
                        case 'B':
                            addCase(new Vide(this), x, y);
                            Bloc b = new Bloc(this, grilleEntites[x][y]);
                            break;
                        case 'T':
                            targetPosition = new Point(x, y);
                            addCase(new Target(this), targetPosition.x, targetPosition.y);
                            break;
                        case 'G':
                            addCase(new Glace(this), x, y);
                            break;
                        case 'P':
                            tabPortails.add(new Portail(this));
                            addCase(tabPortails.lastElement(),x,y);
                            break;
                        default:
                            addCase(new Vide(this), x, y);
                            break;
                    }

                }
                y++;
            }
            linkPortails();
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private void addCase(Case e, int x, int y) {
        grilleEntites[x][y] = e;
        map.put(e, new Point(x, y));
    }
    

    
    /** Si le déplacement de l'entité est autorisé (pas de mur ou autre entité), il est réalisé
     * Sinon, rien n'est fait.
     */
    public boolean deplacerEntite(Entite e, Direction d) {
        boolean retour = true;
        
        Point pCourant = map.get(e.getCase());
        
        Point pCible = calculerPointCible(pCourant, d);

        if (contenuDansGrille(pCible)) {
            Entite eCible = caseALaPosition(pCible).getEntite();
            if (eCible != null) {
                eCible.pousser(d);
            }

            // si la case est libérée
            if (caseALaPosition(pCible).peutEtreParcouru()) {
                e.getCase().quitterLaCase();
                caseALaPosition(pCible).entrerSurLaCase(e,d);
            } else {
                retour = false;
            }

        } else {
            retour = false;
        }

        return retour;
    }
    private void afficherMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    
    private Point calculerPointCible(Point pCourant, Direction d) {
        Point pCible = null;
        
        switch(d) {
            case haut: pCible = new Point(pCourant.x, pCourant.y - 1); break;
            case bas : pCible = new Point(pCourant.x, pCourant.y + 1); break;
            case gauche : pCible = new Point(pCourant.x - 1, pCourant.y); break;
            case droite : pCible = new Point(pCourant.x + 1, pCourant.y); break;     
            
        }
        
        return pCible;
    }
    

    
    /** Indique si p est contenu dans la grille
     */
    private boolean contenuDansGrille(Point p) {
        return p.x >= 0 && p.x < SIZE_X && p.y >= 0 && p.y < SIZE_Y;
    }
    
    private Case caseALaPosition(Point p) {
        Case retour = null;
        
        if (contenuDansGrille(p)) {
            retour = grilleEntites[p.x][p.y];
        }
        
        return retour;
    }

}

