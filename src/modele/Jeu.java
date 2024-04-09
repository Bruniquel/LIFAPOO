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
        heros = new Heros(this, grilleEntites[5][2]);

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

            System.out.println("tp hero");
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

    public void passerNivMenu(Integer indice){

        initNiveau(tabLevels[indice-1]);
        niveauActuel = indice-1;


    }
    private void initNiveau(File file) {
        try {
            Scanner myReader = new Scanner(file);
            int y=0;


            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                for (int x=0;x<20;x++) {
                    switch(data.charAt(x)) {
                        case 'N':
                            addCase(new Menu(this),x,y);
                            break;
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
                        case '1':
                            targetPosition = new Point(x,y);
                            Target T1 = new Target(this);
                            addCase(T1,  targetPosition.x, targetPosition.y);
                            T1.niv = 1;
                            break;
                        case '2':
                            targetPosition = new Point(x,y);
                            Target T2 = new Target(this);
                            addCase(T2,  targetPosition.x, targetPosition.y);
                            T2.niv = 2;
                            break;
                        case '3':
                            targetPosition = new Point(x,y);
                            Target T3 = new Target(this);
                            addCase(T3,  targetPosition.x, targetPosition.y);
                            T3.niv = 3;
                            break;
                        case '4':
                            targetPosition = new Point(x,y);
                            Target T4 = new Target(this);
                            addCase(T4,  targetPosition.x, targetPosition.y);
                            T4.niv = 4;
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
    public void tpHero(){
        Point p = new Point(5,2);
        caseALaPosition(p).setEntite(heros);
        setChanged();
    }
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

