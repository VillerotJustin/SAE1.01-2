
import java.util.*;

/**
 * Created by zulupero on 24/09/2021.
 * Updated by Villerot Justin and VALENNE Nathan on  16/11/2021.
 * Group 18
 */
public class Jeton {
    // Definition des constantes qui servent à changer l'apparence du terminal
    public static final String RESET = "\u001B[0m";
    public static final String T_WHITE = "\u001B[37m";
    public static final String R_BACKGROUND = "\u001B[41m";
    public static final String B_BACKGROUND = "\u001B[44m";

    // Attribues et constante d'origine
    static final Scanner input = new Scanner(System.in);
    private static String[] state; //tableau valeur
    static final int N_CASES = 21;
    static final int N_LIGNES = 6;
    static final String[] COULEURS = {"B", "R"};


    static final Random rand = new Random(); // pour permettre de générer des nombres aléatoires

    // Nous avons transformé ces deux variables en attributs pour pouvoir les utiliser en dehors de la méthode main
    private static int scoreBleus = 0; //variables scores
    private static int scoreRouges = 0;

    // Attribue et Constante rajouter
    public static final double R_CERCLE = 15;
    // rayon du cercle pour le tracer StdDraw
    private static final double[][] coordonnee = new double[N_CASES][2];
    // tableau contenant mes coordonnées nécessaires a l'affichage StdDraw
    public static final String PARTOUT = " partout !";
    // sonarlint demandait de créer cette constante, car cette chaine de texte était utilisé plusieurs fois
    public static final int SIZE = 100;
    // taille de la fenêtre StdDraw qui affiche les cases

    static boolean estOui(char reponse) {
        return "yYoO".indexOf(reponse) != -1;
    }
    
    public static void main(String[] args) {

        boolean newDeal;
        // déplacement des variables scores pour pouvoir y accede depuis tout le code

        // variable contenant le texte qui demande au joueur quelle case il veut jouer
        String text = "Entre le numéro de la case" +
                " ou vous voulez posez le jeton :";
        do {
            //----------initialisation et création des variables-------------
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

            // demande au joueur de choisir le niveau de l'IA s'il a choisi de jouer seul
            if (single){

                /* Version terminale

                System.out.println("Entrer le niveau de l'IA (0, 1 ou 2) : ");
                level = input.nextInt();
                
                */

                //version StdDraw
                level = iaLevel();
            }

            // crée la fenêtre StdDraw et trace les cases avec leurs numéros
            initJeuSTDDraw();


            //------------------------Déroulement de la manche----------------

            // affiche l'état du jeu dans le terminal
            afficheJeu();
            // affiche l'état des jeux dans la fenêtre StdDraw
            afficheJeuStdDraw();

            /* Cette boucle représente les différentes manches d'une partie
               la valeur val représente la valeur des jetons
               Le nombre de tout correspond au (nombre de cases - 1)/2 pour la case vide
               le tout diviser par deux car deux cases sont jouées par tour
            */
            for (int val = 1; val <= (N_CASES -1)/2 ; val++) {
                System.out.println();

                tourJoueur(val, text, 0);
                //fin tour joueur 1

                // affiche l'état du jeu dans le terminal
                afficheJeu();
                // affiche l'état du jeu dans la fenêtre StdDraw
                afficheJeuStdDraw();


                // Si le joueur a décidé de jouer seul la case choisi par le joueur 2
                // sinon la case sera decider de la meme manière que pour le joueur 1
                if (single) {
                    touria(level, val);
                } 
                else {
                    tourJoueur(val, text, 1);
                }

                // affiche l'état du jeu dans le terminal
                afficheJeu();
                // affiche l'état du jeu dans la fenêtre StdDraw
                afficheJeuStdDraw();
            }
            //fin d'une manche 


            // Somme des poids autour de la case vide
            int sumB = sommeVoisins(COULEURS[0]);
            int sumR = sommeVoisins(COULEURS[1]);

            /* version terminal -> score(sumB, sumR);
            System.out.println("Nouvelle Manche ? ");
            reponse = input.next().charAt(0);
            newDeal = estOui(reponse);
             */

            // Version StdDraw
            newDeal = scoreStdDraw(sumB, sumR);

        } while (newDeal);

        System.out.println("Bye Bye !");
        System.exit(0);
        afficheJeu();

    }

    
    //------------------------Affichage Terminal---------------------


    /**
     * Affiche le plateau de jeu en mode texte
     */
    public static void afficheJeu(){
        int idCase = 0;
        String vide = "                                        ";
        String valeur;
        String compteur; //initialisation de la variable compteur
        System.out.println();
        System.out.print("----------------------------------------");
        System.out.print("----------------------------------------");
        System.out.println("------");
        System.out.println();
        for(int i = 1; i <= N_LIGNES; i++){
            compteur = " " + idDebutLigne(i) + "\t";
            //création de la chaine de character qui affiche le numéro de début de ligne.
            System.out.print(compteur + ": "); //affichage du compteur
            // affichage d'une chaine d'espace qui sert à montrer le décalage
            System.out.print(vide.substring(0, vide.length()-(i*3)));

            // Parcours des valeurs de la ligne.
            // On sait que lez numéro de la ligne et égale au nombre de valeurs que contient celle ci.
            for(int j = 1; j <= i; j++) {

                // Si la case na pas été remplis, afficher un triplet d'underscore
                if(state[idCase].isEmpty()){
                    System.out.print(" ___ ");
                }
                // Sinon afficher le contenu de la case avec le contenu colorier
                // en fonction de son premier character.
                else {
                    valeur = state[idCase] + "  ";
                    if ( (valeur.substring(0, 1)).equals(COULEURS[0]) ){
                        System.out.print( " "+ B_BACKGROUND + T_WHITE
                                +valeur.substring(0, 3)+RESET+" " );
                    }
                    else {
                        System.out.print( " " + R_BACKGROUND
                                + T_WHITE +valeur.substring(0, 3)+RESET+" " );
                    }
                }
                // incrémente l'id de la case après la verification de chaque valeur
                idCase++;
            }
            System.out.println();
            System.out.println();
        }
        
    }


    //---------------------------Fonctionnement----------------------------------------------


    /**
     * Initialise le jeu avec un tableau vide qui sera remplacer pas un triplet d'underscore a l'affichage
     */
    public static void initJeu() {
        state = new String[N_CASES]; //initialise le tableau dans lequel sera stocker les valeurs
        for (int i = 0; i < N_CASES; i++ ){
            state[i] = "";
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
        // Vérifie si la position donnée et valide
        if (pos < 0 || pos > N_CASES -1) {
            System.out.println("error : invalid position number");
            return false;
        }
        // Si la case n'est pas vide retourner false et en imprimer un message d'erreur
        if ( !(state[pos].isEmpty()) ){
            System.out.println("case deja occuper entrer un autre valeur");
            return false;
        }
        // Sinon rentrer la couleur et la valeur du point poser et retourner true
        else {
            String temp =  couleur + val;
            state[pos] = temp;
            return true;
        }
    }


    /**
     * execute le tour du joueur
     * @param val valeur du jeton
     * @param text texte a afficher dans le terminal
     * @param joueur joueur qui joue 0 → joueur 1 et 1 → joueur 2
     */
    public static void tourJoueur(int val, String text, int joueur) {
        int idCaseJouer;
        boolean verification;
        System.out.println(text + " (joueur" + joueur + ")");
        // demande au joueur n°joueur d'entre un nombre
        do {
            // idCaseJouer = input.nextInt(); version terminal
            idCaseJouer = actionJoueur(); //StdDraw
            verification = jouer(COULEURS[joueur], val, idCaseJouer);
        }
        while (!verification);
        // si le numéro de case n'a pas pu être joué, recommencer l'opération

    }


    /**
     * joue la tour de l'IA
     * @param level niveau de l'IA
     * @param val valeur du jeton
     */
    public static void touria(int level, int val) {
        int idCaseJouer;
        boolean verification;
        // En fonction du niveau de l'IA demande à la bonne ia
        // de donner un numéro de case à jouer
        do {
            switch (level) {
                case 1: idCaseJouer = iaRouge1(); break;
                case 2: idCaseJouer = iaRouge2(); break;
                default: idCaseJouer = iaRouge(); break;
            }
            verification = jouer(COULEURS[1], val, idCaseJouer);
        }
        while (!verification);
        // si le numéro de case n'a pas pu être joué, recommencer l'opération
    }


    /**
     * Retourne l'indice de la case débutant la ligne idLigne
     * @param idLigne Indice de la ligne. La première ligne est la ligne #0.
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
     * @param idLigne Indice de la ligne. La première ligne est la ligne #0.
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
        for (int i = 0; i < N_CASES; i++){
            if (state[i].isEmpty())
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
        int idLigne = numLigne(idVide);
        if (idVide == 0){
            sommeVoisins += verificationCouleur(col, state[1]);
            sommeVoisins += verificationCouleur(col, state[2]);
        } //haut
        else if (idVide == idDebutLigne(N_LIGNES)){
            sommeVoisins += verificationCouleur(col, state[idVide-idLigne+1]);
            sommeVoisins += verificationCouleur(col, state[idVide+1]);
        } // bas droite
        else if (idVide == idFinLigne(N_LIGNES)){
            sommeVoisins += verificationCouleur(col, state[idVide-idLigne]);
            sommeVoisins += verificationCouleur(col, state[idVide-1]);
        } // bas gauche
        else if (idVide == idFinLigne(idLigne)){
            sommeVoisins += verificationCouleur(col, state[idVide-1]);
            sommeVoisins += verificationCouleur(col, state[idVide-idLigne]);
            sommeVoisins += verificationCouleur(col, state[idVide+idLigne]);
            sommeVoisins += verificationCouleur(col, state[idVide+idLigne+1]);
        }//case a droite
        else if (idVide == idDebutLigne(idLigne)){
            sommeVoisins += verificationCouleur(col, state[idVide+1]);
            sommeVoisins += verificationCouleur(col, state[idVide-idLigne+1]);
            sommeVoisins += verificationCouleur(col, state[idVide+idLigne]);
            sommeVoisins += verificationCouleur(col, state[idVide+idLigne+1]);
        }//case a gauche
        else if (idVide > idDebutLigne(N_LIGNES) && idVide < idFinLigne(N_LIGNES)){
            sommeVoisins += verificationCouleur(col, state[idVide+1]);
            sommeVoisins += verificationCouleur(col, state[idVide-1]);
            sommeVoisins += verificationCouleur(col, state[idVide-idLigne]);
            sommeVoisins += verificationCouleur(col, state[idVide-idLigne+1]);
        }//case bas
        else{
            sommeVoisins += verificationCouleur(col, state[idVide+1]);
            sommeVoisins += verificationCouleur(col, state[idVide-1]);
            sommeVoisins += verificationCouleur(col, state[idVide-idLigne]);
            sommeVoisins += verificationCouleur(col, state[idVide-idLigne+1]);
            sommeVoisins += verificationCouleur(col, state[idVide+idLigne]);
            sommeVoisins += verificationCouleur(col, state[idVide-idLigne+1]);

        } // centre
        return sommeVoisins;
    }


    /**
     * Fait la somme du poids autour d'une case donnée.
     * Nous avons créé une copie de la fonction somme voisin que l'on peut utiliser,
     * en cours de partie pour la methode iaRouge2.
     * @param col couleur a verifier
     * @param idVide case donnée
     * @return rend la somme des poids
     */
    public static int sommeVoisinsVides(String col, int idVide){
        int sommeVoisins = 0;
        int idLigne = numLigne(idVide);
        if (idVide == 0){
            sommeVoisins += verificationCouleur(col, state[1]);
            sommeVoisins += verificationCouleur(col, state[2]);
        } //haut
        else if (idVide == idDebutLigne(N_LIGNES)){
            sommeVoisins += verificationCouleur(col, state[idVide-idLigne+1]);
            sommeVoisins += verificationCouleur(col, state[idVide+1]);
        } // bas droite
        else if (idVide == idFinLigne(N_LIGNES)){
            sommeVoisins += verificationCouleur(col, state[idVide-idLigne]);
            sommeVoisins += verificationCouleur(col, state[idVide-1]);
        } // bas gauche
        else if (idVide == idFinLigne(idLigne)){
            sommeVoisins += verificationCouleur(col, state[idVide-1]);
            sommeVoisins += verificationCouleur(col, state[idVide-idLigne]);
            sommeVoisins += verificationCouleur(col, state[idVide+idLigne]);
            sommeVoisins += verificationCouleur(col, state[idVide+idLigne+1]);
        }//case a droite
        else if (idVide == idDebutLigne(idLigne)){
            sommeVoisins += verificationCouleur(col, state[idVide+1]);
            sommeVoisins += verificationCouleur(col, state[idVide-idLigne+1]);
            sommeVoisins += verificationCouleur(col, state[idVide+idLigne]);
            sommeVoisins += verificationCouleur(col, state[idVide+idLigne+1]);
        }//case a gauche
        else if (idVide > idDebutLigne(N_LIGNES) && idVide < idFinLigne(N_LIGNES)){
            sommeVoisins += verificationCouleur(col, state[idVide+1]);
            sommeVoisins += verificationCouleur(col, state[idVide-1]);
            sommeVoisins += verificationCouleur(col, state[idVide-idLigne]);
            sommeVoisins += verificationCouleur(col, state[idVide-idLigne+1]);
        }//case bas
        else{
            sommeVoisins += verificationCouleur(col, state[idVide+1]);
            sommeVoisins += verificationCouleur(col, state[idVide-1]);
            sommeVoisins += verificationCouleur(col, state[idVide-idLigne]);
            sommeVoisins += verificationCouleur(col, state[idVide-idLigne+1]);
            sommeVoisins += verificationCouleur(col, state[idVide+idLigne]);
            sommeVoisins += verificationCouleur(col, state[idVide-idLigne+1]);

        } // centre
        return sommeVoisins;
    }


    /**
     * rend le numéro de la ligne de la case avec pour id idCase
     * @param idCase id de la case dont on cherche la ligne
     * @return  numéro de la ligne
     */
    public static int numLigne(int idCase){
        for (int i = 1; i <= N_LIGNES; i++){
            if ( idDebutLigne(i) <= idCase && idFinLigne(i) >= idCase){
                return i;
            }
        }
        // rend un message d'erreur si le n° de ligne n'a pas pu être trouvé
        System.out.println("error : can't find ligne number");
        return -1;
    }


    /**
     * Vérifie la couleur de la case et rend la valuer de la case si la couleur correspond
     * @param col couleur rechercher
     * @param stateToCheck couleur + valeur de la case
     * @return rend la valeur de la case ou 0 si mauvaise couleur
     */
    public static int verificationCouleur(String col, String stateToCheck){
        int valeur = 0;
        // si la case et vide rendre 0
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
     * @param sumB somme des bleus
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
     * Algo naïf = la première case disponible
     * @return id de la case
     */
    public static int iaRouge(){
        for (int i = 0; i < N_CASES; i++ ){
            if (state[i].isEmpty())
                return i;
        }
        return 0;
    }


    /** Rend le prochain coup pour les rouges random
     * Cette ia génère une case aléatoire parmi les cases vides
     * @return id de la case
     */
    public static int iaRouge1(){
        // crée une liste que l'on remplit avec l'id des cases vide
        List<Integer> emptyCase = new ArrayList<>();
        for (int i = 0; i < N_CASES; i++ ){
            if (state[i].isEmpty())
                emptyCase.add(i);
        }
        // génère un entier entre 0 et la taille de la liste - 1
        // rend l'id de la case vide associer a ce nombre dans la liste 
        return emptyCase.get(rand.nextInt(emptyCase.size()));
    }


    /**
     * rend l'id de la case vide avec la plus grande somme rouge supérieur a la somme bleue
     * @return id de la case
     */
    public static int iaRouge2(){
        // crée une liste que l'on remplit avec l'id des cases vide
        List<Integer> emptyCase = new ArrayList<>();
        int max = 0;
        for (int i = 0; i < N_CASES; i++){
            if(state[i].isEmpty())
                emptyCase.add(i);
        }
        // id max initialiser avec l'id de la premiere case vide
        int idMax = emptyCase.get(0);
        // pour chaque case vide faire la somme du poids des cases adjacente
        // et si l'IA est perdente sur cette case
        // et si c'est la case ou elle perd le plus rendre l'id de cette case 
        for (Integer integer : emptyCase) {
            if (sommeVoisinsVides(COULEURS[1]
                    , integer) > sommeVoisinsVides(COULEURS[0]
                    , integer)
                    && sommeVoisinsVides(COULEURS[1]
                    , integer) > max) {
                max = sommeVoisinsVides(COULEURS[1], integer);
                idMax = integer;
            }
        }
        return idMax;

    }

    /*
		Écrire un véritable code sachant jouer.
		La ligne du return ci-dessous doit donc naturellement aussi être réécrite.
		Cette version ne permet que de reproduire le fonctionnement à 2 joueurs
		tout en conservant l'appel à la fonction,
		cela peut s'avérer utile lors du développement.
	*/


    // ------------------------------Affichage stdDraw-------------------------

    /**
     * Initialise l'interface STDDraw
     */
    public static void initJeuSTDDraw() {
        StdDraw.setXscale(-SIZE, SIZE); // fixe l'amplitude des abscisses dans la fenêtre
        StdDraw.setYscale(-SIZE, SIZE); // fixe l'amplitude des ordonnées dans la fenêtre
        StdDraw.clear(StdDraw.WHITE); //fond d'écrans en blanc
        StdDraw.setPenColor(StdDraw.BLACK);
        double yLigne= 80;//initialisation des ordonnées des lignes de valeurs
        double dCercle = 2 * R_CERCLE;
        int idCase=0;
        String idCaseString;
        double xCaseLignePaire;
        double xCaseLigneImpaire;
        int decalage=0;
        for (int ligne = 1; ligne <= N_LIGNES; ligne++){   // Parcours des lignes
            if ((ligne%2)==0)             // augmente le décalage toute les ligne pairs
                decalage++;
            for (int emplacement = 0 ; emplacement < ligne ; emplacement++){
                idCaseString = Integer.toString(idCase);
                xCaseLignePaire = R_CERCLE +dCercle*emplacement;
                xCaseLigneImpaire = (0+dCercle*emplacement);
                if ( ( ligne % 2 ) != 0){
                    StdDraw.circle(xCaseLigneImpaire-(dCercle*decalage)
                            , yLigne
                            , R_CERCLE);
                    StdDraw.text(xCaseLigneImpaire-(dCercle*decalage)
                            , yLigne
                            , idCaseString);
                }
                else {
                    StdDraw.circle(xCaseLignePaire-(dCercle*decalage)
                            , yLigne
                            , R_CERCLE);
                    StdDraw.text(xCaseLignePaire-(dCercle*decalage)
                            , yLigne
                            , idCaseString);
                }
                coordonnee[idCase][0]= xCaseLigneImpaire-(dCercle*decalage);
                coordonnee[idCase][1]= yLigne;
                idCase++;
            }
            yLigne-= dCercle+1;
        }
        /*
        Toute la partie avec le décalage est la car il était plus simple de créer
        un triangle rectangle plutôt qu'une pyramid une fois le triangle rectangle
        créer il ne restait plus qu'à décaler les niveaux plutôt que de donner les
        bonnes coordonnées directement

        Il y a une différenciation entre les lignes impaires et paire, car les dernières
        ne pouvait pas être centre comme les lignes impaires pour palier a ce problème
        il suffit de décaler les lignes paires d'une distance égale au rayon du cercle
        qui représente les cases
         */
    }

    /**
     * Affiche le plateau de jeu en mode graphique
     */
    public static void afficheJeuStdDraw(){
        double yLigne= 80;//initialisation de l'ordonnée des lignes de valeurs
        double dCercle = 2 * R_CERCLE;
        int idCase=0;
        int decalage=0;
        for (int ligne = 1; ligne <= N_LIGNES; ligne++){   // Parcours des lignes
            if ((ligne%2)==0)             // augmente le décalage toute les ligne pairs
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
     * @param yLigne ordonnée de la ligne
     */
    private static void afficheJeuStdDraw2(int idCase
            , int emplacement
            , int ligne
            , int decalage
            , double yLigne){
        double dCercle = 2 * R_CERCLE;
        double xCaseLignePaire;
        double xCaseLigneImpaire;
        if ( (state[idCase].substring(0, 1)).equals(COULEURS[0]) ){
            StdDraw.setPenColor(StdDraw.BLUE);
        }
        else {
            StdDraw.setPenColor(StdDraw.RED);
        }
        xCaseLignePaire = R_CERCLE +dCercle*emplacement;
        xCaseLigneImpaire = (0+dCercle*emplacement);
        if ( ( ligne % 2 ) != 0){
            StdDraw.filledCircle(xCaseLigneImpaire-(dCercle*decalage)
                    , yLigne
                    , R_CERCLE);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.text(xCaseLigneImpaire-(dCercle*decalage)
                    , yLigne
                    , state[idCase].substring(1));
        }
        else {
            StdDraw.filledCircle(xCaseLignePaire-(dCercle*decalage)
                    , yLigne
                    , R_CERCLE);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.text(xCaseLignePaire-(dCercle*decalage)
                    , yLigne
                    , state[idCase].substring(1));
        }

    }

    /**
     * Fait apparaitre une fenêtre graphique qui demande a l'utilisateur de choisir entre 1 et 2
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
            // Si le joueur clique sur la case jouer contre l'IA (droite) rendre true
            if (StdDraw.isMousePressed() && StdDraw.mouseX() >= 0){
                System.out.println(true);
                StdDraw.pause(500);
                return true;
            }
            // Si le joueur clique sur la case deux joueurs (gauche) rendre false
            else if (StdDraw.isMousePressed() && StdDraw.mouseX() < 0){
                System.out.println(false);
                StdDraw.pause(500);
                return false;
            }
        }
        while (true);
    }


    /**
     * demande le niveau de l'IA au joueur
     * @return le niveau de l'IA
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
        StdDraw.text(0, 90, "Difficulté de l'IA");
        StdDraw.setPenRadius(0.01);
        StdDraw.line(-100, 80, 100, 80);
        StdDraw.line(-100, 20, 100, 20);
        StdDraw.line(-100, -40, 100, -40);
        do {
            // Si le joueur clique sur la case du haut rendre 0 (abscisse entre 20 et 80)
            if (StdDraw.isMousePressed()
                    && StdDraw.mouseY() < 80
                    && StdDraw.mouseY() > 20){
                System.out.println(0);
                return 0;
            }
            // Si le joueur clique sur la case du milieu rendre 1 (abscisse entre -40 et 20)
            else if (StdDraw.isMousePressed()
                    && StdDraw.mouseY() < 20
                    && StdDraw.mouseY() > -40){
                System.out.println(1);
                return 1;
            }
            // Si le joueur clique sur la case du bas rendre 2 (abscisse entre -100 et -40)
            else if (StdDraw.mousePressed()
                    && StdDraw.mouseY() < -40){
                System.out.println(2);
                return 2;
            }
        }
        while (true);

    }

    /**
     * Demande au joueur la case qu'il veut jouer
     * Cette fonction récupère les coordonnées des cases obtenues lors de l'initialisation
     * et verifies si le joueur clique dans un carré de demis distance le rayon du cercle
     * et avec pour centre les coordonnées récupérer.
     * La zone de verification et carré elle est donc plus grande que les cercles
     * apparents, mais le fonctionnement restera identique.
     * @return l'id de la case que le joueur veux jouer
     */
    private static int actionJoueur(){
        StdDraw.pause(500);
        boolean test;
        do {
            for (int i = 0; i < N_CASES; i++){
                test = checkCliqueCase(i);
                if (test){
                    return i;
                }

            }
        }while (true);
    }

    /**
     * Vérifie si le joueur à clique sur la case i
     * @param i id de la case
     * @return true si le joueur à cliquer, false dans le cas contraire
     */
    private static Boolean checkCliqueCase(int i){
        if ((numLigne(i)%2)==0){
            if (StdDraw.isMousePressed()
                    && StdDraw.mouseY() <= (coordonnee[i][1]+ R_CERCLE)
                    && StdDraw.mouseY() >= (coordonnee[i][1]- R_CERCLE)
                    && StdDraw.mouseX() <= (coordonnee[i][0]+ R_CERCLE *2)
                    && StdDraw.mouseX() >= (coordonnee[i][0]- R_CERCLE *2)){
                System.out.println(i);
                return Boolean.TRUE;
            }
        }
        else {
            if (StdDraw.isMousePressed()
                    && StdDraw.mouseY() <= (coordonnee[i][1]+ R_CERCLE)
                    && StdDraw.mouseY() >= (coordonnee[i][1]- R_CERCLE)
                    && StdDraw.mouseX() <= (coordonnee[i][0]+ R_CERCLE)
                    && StdDraw.mouseX() >= (coordonnee[i][0]- R_CERCLE)){
                System.out.println(i);
                return Boolean.TRUE;
            }

        }
        return Boolean.FALSE;

    }


    /**
     * affiche le résultat de la manche et demande s'il veut faire une autre manche
     * cette methode affiche aussi le score des joueurs a traver les différentes manches
     * @param sumB somme bleu
     * @param sumR somme rouge
     */
    private static Boolean scoreStdDraw(int sumB, int sumR){
        StdDraw.pause(3000);
        StdDraw.setXscale(-100, 100); // fixe l'amplitude des abscisses dans la fenêtre
        StdDraw.setYscale(-100, 100); // fixe l'amplitude des ordonnées dans la fenêtre
        StdDraw.clear(StdDraw.WHITE); //fond d'écrans en blanc
        String contre =" contre ";

        // Résultat de la manche
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
            StdDraw.text(0, -32.5, "Égalité avec " + scoreRouges + PARTOUT);
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
        StdDraw.text(-50, -85, " Arrêter la partie ");

        do {
            if (StdDraw.isMousePressed()
                    && StdDraw.mouseX() >= 0
                    && StdDraw.mouseY() <= -70){
                System.out.println(true);
                StdDraw.pause(500);
                return true;
            }
            else if (StdDraw.isMousePressed()
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


