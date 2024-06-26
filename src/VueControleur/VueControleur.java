package VueControleur;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;


import modele.*;


/** Cette classe a deux fonctions :
 *  (1) Vue : proposer une représentation graphique de l'application (cases graphiques, etc.)
 *  (2) Controleur : écouter les évènements clavier et déclencher le traitement adapté sur le modèle (flèches direction Pacman, etc.))
 *
 */
public class VueControleur extends JFrame implements Observer {
    private Jeu jeu; // référence sur une classe de modèle : permet d'accéder aux données du modèle pour le rafraichissement, permet de communiquer les actions clavier (ou souris)

    private int sizeX; // taille de la grille affichée
    private int sizeY;

    // icones affichées dans la grille
    private ImageIcon icoHero;
    private ImageIcon icoVide;
    private ImageIcon icoMur;
    private ImageIcon icoBloc;

    private ImageIcon[] icoNiveau;
    private ImageIcon icoGlace;
    private ImageIcon icoPortail;
    private ImageIcon icoTarget;

    private JLabel[][] tabJLabel; // cases graphique (au moment du rafraichissement, chaque case va être associée à une icône, suivant ce qui est présent dans le modèle)


    public VueControleur(Jeu _jeu) {
        sizeX = _jeu.SIZE_X;
        sizeY = _jeu.SIZE_Y;
        jeu = _jeu;

        chargerLesIcones();
        placerLesComposantsGraphiques();
        ajouterEcouteurClavier();

        jeu.addObserver(this);

        mettreAJourAffichage();

        // Centrer la fenêtre
        setLocationRelativeTo(null);
    }
    private ImageIcon resizeImage(File file) throws IOException {
        BufferedImage img = ImageIO.read(file);
        Image resizedImage = img.getScaledInstance(img.getWidth() * 2, img.getHeight() * 2, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }
    private void ajouterEcouteurClavier() {
        addKeyListener(new KeyAdapter() { // new KeyAdapter() { ... } est une instance de classe anonyme, il s'agit d'un objet qui correspond au controleur dans MVC
            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {  // on regarde quelle touche a été pressée

                    case KeyEvent.VK_LEFT : jeu.deplacerHeros(Direction.gauche); break;
                    case KeyEvent.VK_RIGHT : jeu.deplacerHeros(Direction.droite); break;
                    case KeyEvent.VK_DOWN : jeu.deplacerHeros(Direction.bas); break;
                    case KeyEvent.VK_UP : jeu.deplacerHeros(Direction.haut); break;


                }
            }
        });
    }


    private void chargerLesIcones() {
        try {
            icoHero = resizeImage(new File("Images/hero.png"));
            icoVide = resizeImage(new File("Images/Vide.png"));
            icoMur = resizeImage(new File("Images/Mur.png"));
            icoBloc = resizeImage(new File("Images/shell.png"));
            icoPortail = resizeImage(new File("Images/Portail.png"));
            icoTarget = resizeImage(new File("Images/Target.png"));
            icoGlace = resizeImage(new File("Images/glace.png"));
            icoNiveau = new ImageIcon[8];
            for(int i=1;i<6;i++){
                System.out.println("Images/chiffres/"+i+".png");
                icoNiveau[i-1] = resizeImage(new File("Images/chiffres/"+i+".png"));
            }
            icoNiveau[5] = resizeImage(new File("Images/lettres/n.png"));
            icoNiveau[6] = resizeImage(new File("Images/lettres/i.png"));
            icoNiveau[7] = resizeImage(new File("Images/lettres/v.png"));




        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ImageIcon chargerIcone(String urlIcone) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(new File(urlIcone));
        } catch (IOException ex) {
            Logger.getLogger(VueControleur.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return new ImageIcon(image);
    }

    private void placerLesComposantsGraphiques() {
        setTitle("Sokoban");
        setSize(775, 435);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // permet de terminer l'application à la fermeture de la fenêtre

        JComponent grilleJLabels = new JPanel(new GridLayout(sizeY, sizeX)); // grilleJLabels va contenir les cases graphiques et les positionner sous la forme d'une grille

        tabJLabel = new JLabel[sizeX][sizeY];

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                JLabel jlab = new JLabel();
                tabJLabel[x][y] = jlab; // on conserve les cases graphiques dans tabJLabel pour avoir un accès pratique à celles-ci (voir mettreAJourAffichage() )
                grilleJLabels.add(jlab);
            }
        }
        add(grilleJLabels);
    }

    
    /**
     * Il y a une grille du côté du modèle ( jeu.getGrille() ) et une grille du côté de la vue (tabJLabel)
     */
    private void mettreAJourAffichage() {

        int indiceNiv=5;
        int nivAff=1;
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {

                Case c = jeu.getGrille()[x][y];
                if (c instanceof Portail) {
                    tabJLabel[x][y].setIcon(icoPortail);
                }

                if (c != null) {

                    Entite e = c.getEntite();

                    if (e!= null) {
                        if (c.getEntite() instanceof Heros) {
                            tabJLabel[x][y].setIcon(icoHero);
                        } else if (c.getEntite() instanceof Bloc) {
                            tabJLabel[x][y].setIcon(icoBloc);
                        }
                    } else {
                        if (jeu.getGrille()[x][y] instanceof Mur) {
                            tabJLabel[x][y].setIcon(icoMur);
                        } else if (jeu.getGrille()[x][y] instanceof Vide) {

                            tabJLabel[x][y].setIcon(icoVide);
                        } else if (jeu.getGrille()[x][y] instanceof Target) {
                            tabJLabel[x][y].setIcon(icoTarget);
                        } else if (jeu.getGrille()[x][y] instanceof Glace) {
                            tabJLabel[x][y].setIcon(icoGlace);
                        } else if (jeu.getGrille()[x][y] instanceof  Menu) {
                            indiceNiv=indiceNiv%(icoNiveau.length);
                            tabJLabel[x][y].setIcon(icoNiveau[indiceNiv]);

                            System.out.println(indiceNiv);
                            if (indiceNiv==0){
                                if(nivAff<5){
                                    tabJLabel[x][y].setIcon(icoNiveau[nivAff-1]);
                                    nivAff++;
                                    indiceNiv=4;
                                }
                            }
                            indiceNiv++;

                        }
                    }



                }

            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        mettreAJourAffichage();
        /*

        // récupérer le processus graphique pour rafraichir
        // (normalement, à l'inverse, a l'appel du modèle depuis le contrôleur, utiliser un autre processus, voir classe Executor)


        SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        mettreAJourAffichage();
                    }
                }); 
        */

    }
}
