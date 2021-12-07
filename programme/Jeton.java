import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by zulupero on 24/09/2021.
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
            afficheJeu();

            Integer idCaseJouee;
            boolean verif;
            int level = 0;

            //------------------------Question debut de partie----------------

            System.out.println("Jouer seul ? ");
            char reponse = input.next().charAt(0);
            boolean single = estOui(reponse);

            if (single){
                System.out.println("Entrer le niveau de l'IA (0, 1 ou 2) : ");
                level = input.nextInt();
            }
            //------------------------Déroulement de la manche----------------

            for ( int val = 1 ; val <= (NCASES-1)/2 ; val++) {
                System.out.println();
                System.out.println(text);
                do {
                    idCaseJouee = Integer.parseInt(input.next());
                    verif = jouer(COULEURS[0], val, idCaseJouee);
                }
                while (!verif);
                //fin tour joueur 1
                afficheJeu();

                if (single) {
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
                } else {
                    System.out.println(text + " (joueur 2)");
                    do {//---------------------Deux joueur--------------------
                        idCaseJouee = Integer.parseInt(input.next());
                        verif = jouer(COULEURS[1], val, idCaseJouee);
                    }
                    while (!verif);
                }
                afficheJeu();
            }


            int sumB = sommeVoisins(COULEURS[0]);
            int sumR = sommeVoisins(COULEURS[1]);

            score(sumB, sumR);

            System.out.println("Nouvelle Manche ? ");
            reponse = input.next().charAt(0);
            newDeal = estOui(reponse);
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
        if ( (stateToCheck.substring(0, 1)).equals(col) ){
            valeur += Integer.parseInt(stateToCheck.substring(1));
        }
        return valeur;
    }

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
     *
     * @return id de la case
     */
    public static int iaRouge2(){
        return rand.nextInt(20);
    }

    /*
		Écire un véritable code sachant jouer.
		La ligne du return ci-dessous doit donc naturellement aussi être ré-écrite.
		Cette version ne permet que de reproduire le fonctionnement à 2 joueurs
		tout en conservant l'appel à la fonction,
		cela peut s'avérer utile lors du développement.
	*/

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
            System.out.println("Égalité : "+sumB+" partout !");
        else {
            System.out.println("Les rouges gagnent par "+sumR+" à "+sumB);
            scoreRouges++;
        }
        if ( scoreRouges < scoreBleus){
            System.out.println("Les bleus gagnent la partie par "
                    +scoreBleus+" manche à "+scoreRouges);
        }
        else if (scoreRouges == scoreBleus)
            System.out.println("Égalité : "+scoreRouges+" partout !");
        else {
            System.out.println("Les Rouges gagnent la partie par "
                    +scoreRouges+" manche à "+scoreBleus);
        }
    }
}
