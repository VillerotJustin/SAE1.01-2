
import java.util.*;

/**
 * Created by zulupero on 24/09/2021.
 * Updated by Villerot Justin and Nathan on  09/11/2021.
 */
public class Jeton {
    public static final String RESET = "\u001B[0m";
    public static final String TWHITE = "\u001B[37m";
    public static final String RBACKGROUND = "\u001B[41m";
    public static final String BBACKGROUND = "\u001B[44m";
    static final Scanner input = new Scanner(System.in);
    private static String[] state; //tableau valeur
    static final int NCASES = 21;
    static final int NLIGNES = 6; 
    static final String[] COULEURS = {"B", "R"};
    static final Random rand = new Random(); // pour permetre de generer des nombres aléatoires
    private static int scoreBleus = 0; //variables scores
    private static int scoreRouges = 0;
    public static final double RCERCLE = 15;
    private static double[][] coordonee = new double[NCASES][2];
    public static final String PARTOUT = " partout !";

    static boolean estOui(char reponse) {
        return "yYoO".indexOf(reponse) != -1;
    }
    
    public static void main(String[] args) {

        boolean newDeal;
        //deplacement des score pour pouvoir y acceder depuis tout le code

        String text = "Entre le numéro de la case" +
                " ou vous voulez posez le jeton :";
        do {
            //----------inititalisation et création des variables-------------
            initJeu();
            int level = 0;
            char reponse;

            //------------------------Question debut de partie----------------

            /* version terminal
            System.out.println("Jouer seul ? ");
            reponse = input.next().charAt(0);
            boolean single = estOui(reponse);
            isSingle();
            */

            // version StdDraw
            boolean single = isSingle();


            /* Version terminal
            if (single){
                System.out.println("Entrer le niveau de l'IA (0, 1 ou 2) : ");
                level = input.nextInt();
            }
             */
            if (single){
                level = iaLevel();
            }


            initJeuSTDDraw();
            //------------------------Déroulement de la manche----------------

            afficheJeu();
            afficheJeuStdDraw();

            for ( int val = 1 ; val <= (NCASES-1)/2 ; val++) {
                System.out.println();
                tourJoueur(val, text, 0);
                //fin tour joueur 1
                afficheJeu();
                afficheJeuStdDraw();

                if (single) {
                    touria(level, val);
                } else {
                    tourJoueur(val, text, 1);
                }
                afficheJeu();
                afficheJeuStdDraw();
            }


            int sumB = sommeVoisins(COULEURS[0]);
            int sumR = sommeVoisins(COULEURS[1]);

            /* version terminal -> score(sumB, sumR);
            System.out.println("Nouvelle Manche ? ");
            reponse = input.next().charAt(0);
            newDeal = estOui(reponse);
             */

            newDeal = scoreStdDraw(sumB, sumR);

        } while (newDeal);
        System.out.println("Bye Bye !");
        System.exit(0);
        afficheJeu();

    }

    /**
     * Initialise le jeu avec un double/triple underscore à chaque case, signifiant 'case vide'
     */
    public static void initJeu() {
        state = new String[NCASES]; //initialise le tableau dans lequel sera stocker les valeurs
        for (int i = 0 ; i < NCASES ; i++ ){
            state[i] = "";
        }
    }

    /**
     * Affiche le plateau de jeu en mode texte
     */
    public static void afficheJeu(){
        int idcase = 0;
        String vide = "                    ";
        String valeur;
        String cmptL; //initialistation de la variable compteur
        System.out.println();
        System.out.print("----------------------------------------");
        System.out.println("------");
        System.out.println();
        for(int i = 1; i <= NLIGNES; i++){
            cmptL = " " + idDebutLigne(i) + "\t";
            //création de la chaine de character qui affiche le numéro de début de ligne.
            System.out.print(cmptL + ": "); //affichage du compteur
            System.out.print(vide.substring(0, 18-(i*3)));
            for(int j = 1; j <= i; j++) {
                if(state[idcase].equals("")){
                    System.out.print(" ___ ");
                }
                else {
                    valeur = state[idcase] + "  ";
                    if ( (valeur.substring(0, 1)).equals(COULEURS[0]) ){
                        System.out.print( " "+BBACKGROUND+TWHITE
                                +valeur.substring(0, 3)+RESET+" " );
                    }
                    else {
                        System.out.print( " "
                                + RBACKGROUND
                                + TWHITE+valeur.substring(0, 3)+RESET+" " );
                    }
                }
                idcase++;
            }
            System.out.println();
            System.out.println();
        }
        
    }

    /**
     * Place un jeton sur le plateau, si possible.
     * @param couleur couleur du jeton : COULEURS[0] ou COULEURS[1]
     * @param val valeur faciale du jeton
     * @param pos position (indice) de l'emplacement où placer le jeton
     * @return true si le jeton a pu être posé, false sinon.
     */
    public static boolean jouer(String couleur, int val, int pos){
        if (pos < 0 || pos > NCASES-1) {
            System.out.println("error : invalid position number");
            return false;
        }
        if ( !(state[pos].equals("")) ){
            System.out.println("case deja occuper entrer un autre valeur");
            return false;
        }
        else {
            String temp =  couleur + val;
            state[pos] = temp;
            return true;
        }
    }


    /**
     * joue la tour de l'ia
     * @param level niveau de l'ia
     * @param val valeur du jeton
     */
    public static void touria(int level, int val) {
        int idCaseJouee;
        boolean verif;
        do {//---------------------Un joueur----------------------
            switch (level) {
                case 1: idCaseJouee = iaRouge1(); break;
                case 2: idCaseJouee = iaRouge2(); break;
                default: idCaseJouee = iaRouge(); break;
            }
            verif = jouer(COULEURS[1], val, idCaseJouee);
        }
        while (!verif);
        //fin tour iA / Joueur 2
    }


    /**
     * execute le tour du joueur
     * @param val valeur du jeton
     * @param text texte a afficher dans le terminal
     * @param joueur joueur qui joue 0 -> joueur 1 et 1 -> joueur 2
     */
    public static void tourJoueur(int val, String text, int joueur) {
        int idCaseJouee;
        boolean verif;
        System.out.println(text + " (joueur" + joueur + ")");
        do {//---------------------Deux joueur--------------------
            idCaseJouee = actionJoueur();
            verif = jouer(COULEURS[joueur], val, idCaseJouee);
        }
        while (!verif);

    }

    /**
     * Retourne l'indice de la case débutant la ligne idLigne
     * @param idLigne indice de la ligne. La première ligne est la ligne #0.
     * @return l'indice de la case la plus à gauche de la ligne
     */
    public static int idDebutLigne(int idLigne){
        int idDebutLigne = 0;
        for ( int i = 1 ; i < idLigne ; i ++){
            idDebutLigne += i;
        }
        return idDebutLigne;
    }

    /**
     * Retourne l'indice de la case terminant la ligne idLigne
     * @param idLigne indice de la ligne. La première ligne est la ligne #0.
     * @return l'indice de la case la plus à droite de la ligne
     */
    public static int idFinLigne(int idLigne){
        int idFinLigne = 0;
        for (int i = 1 ; i <= idLigne ; i++){
            idFinLigne += i;
        }
        return idFinLigne - 1;
    }

    /**
     * Renvoie la position du jeton manquant
     * @return l'indice de la case non occupée
     */
    public static int getIdVide(){
        int idVide = 0;
        for (int i = 0 ; i < NCASES ; i++){
            if (state[i].equals(""))
                idVide = i;
        }
        return idVide;
    }

    /**
     * fait la somme des poids des voisins de couleur col
     * (6 voisins au maximum)
     *
     * @param col couleur des voisins considérés
     * @return somme des poids
     */
    public static int sommeVoisins(String col){
        int idVide = getIdVide();
        int sommeVoisins = 0;
        int idligne = numligne(idVide);
        if (idVide == 0){
            sommeVoisins += verifCouleur(col, state[1]);
            sommeVoisins += verifCouleur(col, state[2]);
        } //haut
        else if (idVide == 15){
            sommeVoisins += verifCouleur(col, state[10]);
            sommeVoisins += verifCouleur(col, state[16]);
        } // bas droite
        else if (idVide == 20){
            sommeVoisins += verifCouleur(col, state[14]);
            sommeVoisins += verifCouleur(col, state[19]);
        } // bas gauche
        else if (idVide == idFinLigne(idligne)){
            sommeVoisins += verifCouleur(col, state[idVide-1]);
            sommeVoisins += verifCouleur(col, state[idVide-idligne]);
            sommeVoisins += verifCouleur(col, state[idVide+idligne]);
            sommeVoisins += verifCouleur(col, state[idVide+idligne+1]);
        }//case a droite
        else if (idVide == 1 || idVide == 3|| idVide ==6 || idVide == 10){
            sommeVoisins += verifCouleur(col, state[idVide+1]);
            sommeVoisins += verifCouleur(col, state[idVide-idligne+1]);
            sommeVoisins += verifCouleur(col, state[idVide+idligne]);
            sommeVoisins += verifCouleur(col, state[idVide+idligne+1]);
        }//case a gauche
        else if (idVide > 15 && idVide < 20){
            sommeVoisins += verifCouleur(col, state[idVide+1]);
            sommeVoisins += verifCouleur(col, state[idVide-1]);
            sommeVoisins += verifCouleur(col, state[idVide-idligne]);
            sommeVoisins += verifCouleur(col, state[idVide-idligne+1]);
        }//case bas
        else{
            sommeVoisins += verifCouleur(col, state[idVide+1]);
            sommeVoisins += verifCouleur(col, state[idVide-1]);
            sommeVoisins += verifCouleur(col, state[idVide-idligne]);
            sommeVoisins += verifCouleur(col, state[idVide-idligne+1]);
            sommeVoisins += verifCouleur(col, state[idVide+idligne]);
            sommeVoisins += verifCouleur(col, state[idVide-idligne+1]);

        } // centre
        return sommeVoisins;
    }


    /**
     * fait la somme du poid autour d'une case donnée
     * @param col couleur a verifier
     * @param idVide case donnée
     * @return rend la somme des poids
     */
    public static int sommeVoisinsVides(String col, int idVide){
        int sommeVoisins = 0;
        int idligne = numligne(idVide);
        if (idVide == 0){
            sommeVoisins += verifCouleur(col, state[1]);
            sommeVoisins += verifCouleur(col, state[2]);
        } //haut
        else if (idVide == 15){
            sommeVoisins += verifCouleur(col, state[10]);
            sommeVoisins += verifCouleur(col, state[16]);
        } // bas droite
        else if (idVide == 20){
            sommeVoisins += verifCouleur(col, state[14]);
            sommeVoisins += verifCouleur(col, state[19]);
        } // bas gauche
        else if (idVide == idFinLigne(idligne)){
            sommeVoisins += verifCouleur(col, state[idVide-1]);
            sommeVoisins += verifCouleur(col, state[idVide-idligne]);
            sommeVoisins += verifCouleur(col, state[idVide+idligne]);
            sommeVoisins += verifCouleur(col, state[idVide+idligne+1]);
        }//case a droite
        else if (idVide == 1 || idVide == 3|| idVide ==6 || idVide == 10){
            sommeVoisins += verifCouleur(col, state[idVide+1]);
            sommeVoisins += verifCouleur(col, state[idVide-idligne+1]);
            sommeVoisins += verifCouleur(col, state[idVide+idligne]);
            sommeVoisins += verifCouleur(col, state[idVide+idligne+1]);
        }//case a gauche
        else if (idVide > 15 && idVide < 20){
            sommeVoisins += verifCouleur(col, state[idVide+1]);
            sommeVoisins += verifCouleur(col, state[idVide-1]);
            sommeVoisins += verifCouleur(col, state[idVide-idligne]);
            sommeVoisins += verifCouleur(col, state[idVide-idligne+1]);
        }//case bas
        else{
            sommeVoisins += verifCouleur(col, state[idVide+1]);
            sommeVoisins += verifCouleur(col, state[idVide-1]);
            sommeVoisins += verifCouleur(col, state[idVide-idligne]);
            sommeVoisins += verifCouleur(col, state[idVide-idligne+1]);
            sommeVoisins += verifCouleur(col, state[idVide+idligne]);
            sommeVoisins += verifCouleur(col, state[idVide-idligne+1]);

        } // centre
        return sommeVoisins;
    }


    /**
     * rend le numero de la ligne de la case avec pour id idCase
     * @param idCase id de la case dont on cherche la ligne
     * @return  numero de la ligne
     */
    public static int numligne(int idCase){
        for (int i=1 ; i <= NLIGNES ; i++){
            if ( idDebutLigne(i) <= idCase && idFinLigne(i) >= idCase){
                return i;
            }
        }
        System.out.println("error : can't find ligne number");
        return -1;
    }


    /**
     * verifie la couleur de la case et rend la valuer si la couleur correspond
     * @param col couleur rechercher
     * @param stateToCheck couleur + valeur de la case
     * @return rend la valeur de la case ou 0 si mauvaise couleur
     */
    public static int verifCouleur(String col, String stateToCheck){
        int valeur = 0;
        if ( stateToCheck.isEmpty() ){
            return valeur;
        }
        else if ( (stateToCheck.substring(0, 1)).equals(col) ){
            valeur += Integer.parseInt(stateToCheck.substring(1));
        }
        return valeur;
    }

    /**
     * affiche qui a gagné la manche et adapte le score
     * @param sumB somme des bleu
     * @param sumR somme des rouges
     */
    public static void score(int sumB, int sumR){
        if ( sumB < sumR){
            System.out.println("Les bleus gagnent par "+sumB+" à "+sumR);
            scoreBleus++;
        }
        else if (sumB == sumR)
            System.out.println("Égalité : "+sumB+ PARTOUT);
        else {
            System.out.println("Les rouges gagnent par "+sumR+" à "+sumB);
            scoreRouges++;
        }
        if ( scoreRouges < scoreBleus){
            System.out.println("Les bleus gagnent la partie par "
                    +scoreBleus+" manche à "+scoreRouges);
        }
        else if (scoreRouges == scoreBleus)
            System.out.println("Égalité : "+scoreRouges+ PARTOUT);
        else {
            System.out.println("Les Rouges gagnent la partie par "
                    +scoreRouges+" manche à "+scoreBleus);
        }
    }

    // ------------------------------IA----------------------------------------

    /**
     * Renvoie le prochain coup à jouer pour les rouges le premier disponible
     * Algo naïf = la première case dispo
     * @return id de la case
     */
    public static int iaRouge(){
        for (int i = 0 ; i < NCASES ; i++ ){
            if (state[i].isEmpty())
                return i;
        }
        return 0;
    }

    /** renvoy le prochain coup pour les rouges random
     * Cette ia genere une cases aléotoire parmis les cases vides
     * @return id de la case
     */
    public static int iaRouge1(){
        List<Integer> emptycase = new ArrayList<>();
        for (int i = 0 ; i < NCASES ; i++ ){
            if (state[i].isEmpty())
                emptycase.add(i);
        }
        return emptycase.get(rand.nextInt(emptycase.size()));
    }

    /**
     * rend l'id de la case vide avec la plus grande somme rouge superieur a la somme bleu
     * @return id de la case
     */
    public static int iaRouge2(){
        List<Integer> emptycase = new ArrayList<>();
        int max = 0;
        for (int i = 0; i < NCASES; i++){
            if(state[i].isEmpty())
                emptycase.add(i);
        }
        int idMax = emptycase.get(0);
        System.out.println(emptycase);
        for(int casesVides = 0; casesVides < emptycase.size(); casesVides++){
            if(sommeVoisinsVides(COULEURS[1]
                    , emptycase.get(casesVides))>sommeVoisinsVides(COULEURS[0]
                    , emptycase.get(casesVides))
                    && sommeVoisinsVides(COULEURS[1]
                    , emptycase.get(casesVides)) > max){
                max=sommeVoisinsVides(COULEURS[1], emptycase.get(casesVides));
                idMax = emptycase.get(casesVides);
            }
        }
        return idMax;

    }

    /*
		Écire un véritable code sachant jouer.
		La ligne du return ci-dessous doit donc naturellement aussi être ré-écrite.
		Cette version ne permet que de reproduire le fonctionnement à 2 joueurs
		tout en conservant l'appel à la fonction,
		cela peut s'avérer utile lors du développement.
	*/


    // ------------------------------Affichage stdDraw-------------------------

    /**
     * Initialise l'interface STDDraw
     */
    public static void initJeuSTDDraw() {
        StdDraw.setXscale(-100, 100); // fixe l'amplitude des abscisses dans la fenêtre
        StdDraw.setYscale(-100, 100); // fixe l'amplitude des ordonnées dans la fenêtre
        StdDraw.clear(StdDraw.WHITE); //fond d'écrans en blanc
        StdDraw.setPenColor(StdDraw.BLACK);
        double yLigne= 80;//initialisation de l'ordonnées des lignes de valeurs
        double dCercle = 2 * RCERCLE;
        int idCase=0;
        String idCaseString;
        double xCaseLignePaire;
        double xCaseLigneImpaire;
        int decalage=0;
        for (int ligne = 1 ; ligne <= NLIGNES ; ligne++){   // Parcours des lignes
            if ((ligne%2)==0)             // augmente le décalage toute les ligne paires
                decalage++;
            for (int emplacement = 0 ; emplacement < ligne ; emplacement++){
                idCaseString = Integer.toString(idCase);
                xCaseLignePaire = RCERCLE+dCercle*emplacement;
                xCaseLigneImpaire = (0+dCercle*emplacement);
                if ( ( ligne % 2 ) != 0){
                    StdDraw.circle(xCaseLigneImpaire-(dCercle*decalage)
                            , yLigne
                            , RCERCLE);
                    StdDraw.text(xCaseLigneImpaire-(dCercle*decalage)
                            , yLigne
                            , idCaseString);
                    coordonee[idCase][0]= xCaseLigneImpaire-(dCercle*decalage);
                    coordonee[idCase][1]= yLigne;
                }
                else {
                    StdDraw.circle(xCaseLignePaire-(dCercle*decalage)
                            , yLigne
                            , RCERCLE);
                    StdDraw.text(xCaseLignePaire-(dCercle*decalage)
                            , yLigne
                            , idCaseString);
                    coordonee[idCase][0]= xCaseLigneImpaire-(dCercle*decalage);
                    coordonee[idCase][1]= yLigne;
                }
                idCase++;
            }
            yLigne-= dCercle+1;
        }
        /*
        Toute la partie avec le decalage est la car il était plus simple de crée
        un triangle rectangle plutot qu'une piramide un fois le triangle rectangle
        créer il ne restait plus qu'a décaler les niveaux plutot que de donner les
        bonne coordonées directement

        Il y a une différenciation entre les lignes impaires et paire car les dernieres
        ne pouvait pas etre centre comme les lignes impaires pour palier a ce probleme
        il suffit de decaler les lignes paires d'une distance éguale au rayon du cercle
        qui représente les cases
         */
    }

    /**
     * Affiche le plateau de jeu en mode graphique
     */
    public static void afficheJeuStdDraw(){
        double yLigne= 80;//initialisation de l'ordonnées des lignes de valeurs
        double dCercle = 2 * RCERCLE;
        int idCase=0;
        int decalage=0;
        for (int ligne = 1 ; ligne <= NLIGNES ; ligne++){   // Parcours des lignes
            if ((ligne%2)==0)             // augmente le décalage toute les ligne paires
                decalage++;
            for (int emplacement = 0 ; emplacement < ligne ; emplacement++){
                if (!state[idCase].isEmpty()){
                    afficheJeuStdDraw2(idCase
                            , emplacement
                            , ligne
                            , decalage
                            , yLigne);
                }
                idCase++;
            }
            yLigne-= dCercle+1;
        }

    }

    /**
     * Sous méthode de la méthode afficheJeuStdDraw
     * @param idCase id de la case a afficher
     * @param emplacement place dans la ligne
     * @param ligne ligne de la case
     * @param decalage décalage nécessaire pour cette ligne
     * @param yLigne ordonnée de la lignes
     */
    private static void afficheJeuStdDraw2(int idCase
            , int emplacement
            , int ligne
            , int decalage
            , double yLigne){
        double dCercle = 2 * RCERCLE;
        double xCaseLignePaire;
        double xCaseLigneImpaire;
        if ( (state[idCase].substring(0, 1)).equals(COULEURS[0]) ){
            StdDraw.setPenColor(StdDraw.BLUE);
        }
        else {
            StdDraw.setPenColor(StdDraw.RED);
        }
        xCaseLignePaire = RCERCLE+dCercle*emplacement;
        xCaseLigneImpaire = (0+dCercle*emplacement);
        if ( ( ligne % 2 ) != 0){
            StdDraw.filledCircle(xCaseLigneImpaire-(dCercle*decalage)
                    , yLigne
                    , RCERCLE);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.text(xCaseLigneImpaire-(dCercle*decalage)
                    , yLigne
                    , state[idCase].substring(1));
        }
        else {
            StdDraw.filledCircle(xCaseLignePaire-(dCercle*decalage)
                    , yLigne
                    , RCERCLE);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.text(xCaseLignePaire-(dCercle*decalage)
                    , yLigne
                    , state[idCase].substring(1));
        }

    }

    /**
     * fait apparatre une fennetre graphique qui demande a l'utilisateur de choisir entre 1 et 2
     * @return true si le joueur veux jouer seul, et false si non.
     */
    public static boolean isSingle(){
        StdDraw.setXscale(-100, 100); // fixe l'amplitude des abscisses dans la fenêtre
        StdDraw.setYscale(-100, 100); // fixe l'amplitude des ordonnées dans la fenêtre
        StdDraw.clear(StdDraw.WHITE); //fond d'écrans en blanc
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.filledRectangle(-50, -10, 50, 90);
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.filledRectangle(50, -10, 50, 90);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(0, 90, "Mode de jeux");
        StdDraw.setPenRadius(0.01);
        StdDraw.line(0, 80, 0, -100);
        StdDraw.line(-100, 80, 100, 80);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(-50, -10, "Deux joueur");
        StdDraw.text(50, -10, "Jouer contre l'IA");


        do {
            if (StdDraw.mousePressed() && StdDraw.mouseX() >= 0){
                System.out.println(true);
                StdDraw.pause(500);
                return true;
            }
            else if (StdDraw.mousePressed() && StdDraw.mouseX() < 0){
                System.out.println(false);
                StdDraw.pause(500);
                return false;
            }
        }
        while (true);
    }


    /**
     * demande le niveau de l'ia au joueur
     * @return le niveau de l'ia
     */
    public static int iaLevel(){
        StdDraw.setXscale(-100, 100); // fixe l'amplitude des abscisses dans la fenêtre
        StdDraw.setYscale(-100, 100); // fixe l'amplitude des ordonnées dans la fenêtre
        StdDraw.clear(StdDraw.WHITE); //fond d'écrans en blanc
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.filledRectangle(0, 50, 100, 30);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(0, 50, "Facile");
        StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
        StdDraw.filledRectangle(0, -10, 100, 30);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(0, -10, "Normale");
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.filledRectangle(0, -70, 100, 30);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(0, -70, "Difficile");
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(0, 90, "Difficulter IA");
        StdDraw.setPenRadius(0.01);
        StdDraw.line(-100, 80, 100, 80);
        StdDraw.line(-100, 20, 100, 20);
        StdDraw.line(-100, -40, 100, -40);
        do {
            if (StdDraw.mousePressed()
                    && StdDraw.mouseY() < 80
                    && StdDraw.mouseY() > 20){
                System.out.println(0);
                return 0;
            }
            else if (StdDraw.mousePressed()
                    && StdDraw.mouseY() < 20
                    && StdDraw.mouseY() > -40){
                System.out.println(1);
                return 1;
            }
            else if (StdDraw.mousePressed()
                    && StdDraw.mouseY() < -40){
                System.out.println(2);
                return 2;
            }
        }
        while (true);

    }

    /**
     * Demande au joueur la case qu'il veux jouer
     * @return l'id de la case que le joueur veux jouer
     */
    private static int actionJoueur(){
        StdDraw.pause(500);
        do {
            for (int i = 0; i < NCASES; i++){
                if ((numligne(i)%2)==0){
                    if (StdDraw.mousePressed()
                            && StdDraw.mouseY() <= (coordonee[i][1]+RCERCLE)
                            && StdDraw.mouseY() >= (coordonee[i][1]-RCERCLE)
                            && StdDraw.mouseX() <= (coordonee[i][0]+RCERCLE*2)
                            && StdDraw.mouseX() >= (coordonee[i][0]-RCERCLE*2)){
                        System.out.println(i);
                        return i;
                    }
                }
                else {
                    if (StdDraw.mousePressed()
                        && StdDraw.mouseY() <= (coordonee[i][1]+RCERCLE)
                        && StdDraw.mouseY() >= (coordonee[i][1]-RCERCLE)
                        && StdDraw.mouseX() <= (coordonee[i][0]+RCERCLE)
                        && StdDraw.mouseX() >= (coordonee[i][0]-RCERCLE)){
                        System.out.println(i);
                    return i;
                    }

                }

            }
        }while (true);
    }


    /**
     * affiche le score du joueur et demande si il veux faire une autre manche
     * @param sumB somme bleu
     * @param sumR somme rouge
     */
    private static Boolean scoreStdDraw(int sumB, int sumR){
        StdDraw.setXscale(-100, 100); // fixe l'amplitude des abscisses dans la fenêtre
        StdDraw.setYscale(-100, 100); // fixe l'amplitude des ordonnées dans la fenêtre
        StdDraw.clear(StdDraw.WHITE); //fond d'écrans en blanc
        String contre =" contre ";

        //resultat de la manche
        if (sumB < sumR){
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.filledRectangle(0, 42.5, 100, 37.5);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(0, 42.5, "Les bleu on gagnés la manche avec "
            + sumB + contre + sumR);
            scoreBleus++;
        }
        else if (sumR < sumB){
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.filledRectangle(0, 42.5, 100, 37.5);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(0, 42.5, "Les Rouges on gagnés la manche avec "
                    + sumR + contre + sumB);
            scoreRouges++;
        }
        else {
            StdDraw.setPenColor(StdDraw.PINK);
            StdDraw.filledRectangle(0, 42.5, 100, 37.5);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(0, 42.5, "Égalité " + sumB + PARTOUT);
        }

        // score de la partie
        if (scoreBleus > scoreRouges){
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.filledRectangle(0, -32.5, 100, 37.5);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(0, -32.5, "Les bleu gagne la partie avec "
                    + scoreBleus + contre + scoreRouges);
        }
        else if (scoreRouges > scoreBleus){
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.filledRectangle(0, -32.5, 100, 37.5);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(0, -32.5, "Les Rouges gagne la partie avec "
                    + scoreRouges + contre + scoreBleus);
        }
        else {
            StdDraw.setPenColor(StdDraw.PINK);
            StdDraw.filledRectangle(0, -32.5, 100, 37.5);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(0, -32.5, "Egalité avec " + scoreRouges + PARTOUT);
        }

        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.filledRectangle(-50, -85, 50, 15);
        StdDraw.setPenColor(StdDraw.GREEN);
        StdDraw.filledRectangle(50, -85, 50, 15);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        StdDraw.line(-100, 80, 100, 80);
        StdDraw.line(-100, 5, 100, 5);
        StdDraw.line(-100, -70, 100, -70);
        StdDraw.line(0, -70, 0, -100);
        StdDraw.text(0, 90, "Tableau des scores");
        StdDraw.text(50, -85, "Continuer la partie ");
        StdDraw.text(-50, -85, " Arreter la partie ");

        do {
            if (StdDraw.mousePressed()
                    && StdDraw.mouseX() >= 0
                    && StdDraw.mouseY() <= -70){
                System.out.println(true);
                StdDraw.pause(500);
                return true;
            }
            else if (StdDraw.mousePressed()
                    && StdDraw.mouseX() < 0
                    && StdDraw.mouseY() <= -70){
                System.out.println(false);
                StdDraw.pause(500);
                return false;
            }
        }
        while (true);

    }


}


