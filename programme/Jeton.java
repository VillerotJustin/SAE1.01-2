
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by zulupero on 24/09/2021.
 */
public class Jeton {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    static final Scanner input = new Scanner(System.in);
    public static String[] state; //tableau valeur
    static final int NCASES = 21;
    static final int NLIGNES = 6; 
    static final String[] COULEURS = {"B", "R"};

    static boolean estOui(char reponse) {
        return "yYoO".indexOf(reponse) != -1;
    }
    
    public static void main(String[] args) throws InterruptedException {

        boolean newDeal;
        int scoreBleus = 0;
        int scoreRouges = 0;
    
        do {
            System.out.println("Jouer seul ? ");
            char reponse = input.next().charAt(0);
            boolean single = estOui(reponse);

            //----------inititalisation et création des variables---------
            initJeu();
            afficheJeu();

            Integer idCaseJouee;
            boolean verif;

            //---------------------Un joueur---------------------------
            if (single) {
                for ( int val = 1 ; val <= (NCASES-1)/2 ; val++){

                    System.out.println("Entre le numéro de la case " +
                            "ou vous voulez posez le jeton :");
                    do {
                        idCaseJouee = Integer.parseInt(input.next());
                        verif = jouer(COULEURS[0], val, idCaseJouee);
                    }
                    while (!verif);

                    //fin tour joueur 1
                    afficheJeu();

                    do {
                        idCaseJouee = iaRouge();
                        verif = jouer(COULEURS[1], val, idCaseJouee);
                    }
                    while (!verif);

                    afficheJeu();
                    //fin tour iA / Joueur 2
                }
            }
            else  { //---------------------Deux joueur--------------------------
                for ( int val = 1 ; val <= (NCASES-1)/2 ; val++){

                    System.out.println("Entre le numéro de la case " +
                            "ou vous voulez posez le jeton :");
                    do {
                        idCaseJouee = Integer.parseInt(input.next());
                        verif = jouer(COULEURS[0], val, idCaseJouee);
                    }
                    while (!verif);

                    //fin tour joueur 1
                    afficheJeu();

                    System.out.println("Entre le numéro de la case " +
                            "ou vous voulez posez le jeton : (joueur 2)");
                    do {
                        idCaseJouee = Integer.parseInt(input.next());
                        verif = jouer(COULEURS[1], val, idCaseJouee);
                    }
                    while (!verif);

                    afficheJeu();
                    //fin tour iA / Joueur 2
                }
            }


            int sumR = sommeVoisins("R");
            int sumB = sommeVoisins("B");

            if ( sumB < sumR)
                System.out.println("Les bleus gagnent par "+sumB+" à "+sumR);
            else if (sumB == sumR)
                System.out.println("Égalité : "+sumB+" partout !");
            else
                System.out.println("Les rouges gagnent par "+sumR+" à "+sumB);

            System.out.println("Nouvelle partie ? ");
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
        String valeur, cmptL;
        System.out.println("");
        System.out.print("----------------------------------------");
        System.out.println("------");
        System.out.println("");
        for(int i = 1; i <= NLIGNES; i++){
            cmptL = " " + idDebutLigne(i) + "    ";
            System.out.print(cmptL.substring(0, 3));
            System.out.print(" :" + vide.substring(0, 18-(i*3)));
            for(int j = 1; j <= i; j++) {
                if(state[idcase] == ""){
                    System.out.print(" ___ ");
                }
                else {
                    valeur = state[idcase] + "  ";
                    if ( (valeur.substring(0, 1)).equals(COULEURS[0]) ){
                        System.out.print( " " + ANSI_BLUE_BACKGROUND + ANSI_WHITE + valeur.substring(0, 3) + ANSI_RESET + " " );
                    }
                    else {
                        System.out.print( " " + ANSI_RED_BACKGROUND + ANSI_WHITE + valeur.substring(0, 3) + ANSI_RESET + " " );
                    }
                }
                idcase++;
            }
            System.out.println("");
            System.out.println("");
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
        if ( state[pos] != ""){
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
            if (state[i] == "")
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
        if (idVide == 0){
            if ( (state[1].substring(0, 1)).equals(col) ){
                sommeVoisins += Integer.parseInt(state[1].substring(1));
            }
            if ( (state[2].substring(0, 1)).equals(col) ){
                sommeVoisins += Integer.parseInt(state[2].substring(1));
            }
        } //haut
        else if (idVide == 15){
            if ( (state[10].substring(0, 1)).equals(col) ){
                sommeVoisins += Integer.parseInt(state[10].substring(1));
            }
            if ( (state[16].substring(0, 1)).equals(col) ){
                sommeVoisins += Integer.parseInt(state[16].substring(1));
            }
        } // bas droite
        else if (idVide == 20){
            if ( (state[14].substring(0, 1)).equals(col) ){
                sommeVoisins += Integer.parseInt(state[14].substring(1));
            }
            if ( (state[19].substring(0, 1)).equals(col) ){
                sommeVoisins += Integer.parseInt(state[19].substring(1));
            }
        } // bas gauche
        else if (idVide == 2 || idVide == 5|| idVide ==9 || idVide == 14){
            if ( (state[idVide-1].substring(0, 1)).equals(col) ){ //bon
                sommeVoisins += Integer.parseInt(state[idVide-1].substring(1));
            }
            switch (idVide){
                case 2:
                    if ( (state[0].substring(0, 1)).equals(col) ){
                        sommeVoisins+= Integer.parseInt(state[0].substring(1));
                    }
                    if ( (state[4].substring(0, 1)).equals(col) ){
                        sommeVoisins+= Integer.parseInt(state[4].substring(1));
                    }
                    if ( (state[5].substring(0, 1)).equals(col) ){
                        sommeVoisins+= Integer.parseInt(state[5].substring(1));
                    }

                    break;

                case 5:
                    if ( (state[2].substring(0, 1)).equals(col) ){
                        sommeVoisins+= Integer.parseInt(state[2].substring(1));
                    }
                    if ( (state[8].substring(0, 1)).equals(col) ){
                        sommeVoisins+= Integer.parseInt(state[8].substring(1));
                    }
                    if ( (state[9].substring(0, 1)).equals(col) ){
                        sommeVoisins+= Integer.parseInt(state[9].substring(1));
                    }

                    break;
                case 9:
                    if ( (state[5].substring(0, 1)).equals(col) ){
                        sommeVoisins+= Integer.parseInt(state[5].substring(1));
                    }
                    if ( (state[13].substring(0, 1)).equals(col) ){
                        sommeVoisins+= Integer.parseInt(state[13].substring(1));
                    }
                    if ( (state[14].substring(0, 1)).equals(col) ){
                        sommeVoisins+= Integer.parseInt(state[14].substring(1));
                    }
                    break;
                case 14:
                    if ( (state[9].substring(0, 1)).equals(col) ){
                        sommeVoisins+= Integer.parseInt(state[9].substring(1));
                    }
                    if ( (state[20].substring(0, 1)).equals(col) ){
                        sommeVoisins+= Integer.parseInt(state[20].substring(1));
                    }
                    if ( (state[19].substring(0, 1)).equals(col) ){
                        sommeVoisins+= Integer.parseInt(state[19].substring(1));
                    }
                    break;
            }

        }//case a droite
        else if (idVide == 1 || idVide == 3|| idVide ==6 || idVide == 10){
            if ( (state[idVide+1].substring(0, 1)).equals(col) ){ //bon
                sommeVoisins += Integer.parseInt(state[idVide+1].substring(1));
            }
            switch (idVide){
                case 1:
                    if ( (state[0].substring(0, 1)).equals(col) ){
                        sommeVoisins+= Integer.parseInt(state[0].substring(1));
                    }
                    if ( (state[4].substring(0, 1)).equals(col) ){
                        sommeVoisins+= Integer.parseInt(state[4].substring(1));
                    }
                    if ( (state[3].substring(0, 1)).equals(col) ){
                        sommeVoisins+= Integer.parseInt(state[3].substring(1));
                    }

                    break;

                case 3:
                    if ( (state[1].substring(0, 1)).equals(col) ){
                        sommeVoisins+= Integer.parseInt(state[1].substring(1));
                    }
                    if ( (state[7].substring(0, 1)).equals(col) ){
                        sommeVoisins+= Integer.parseInt(state[7].substring(1));
                    }
                    if ( (state[6].substring(0, 1)).equals(col) ){
                        sommeVoisins+= Integer.parseInt(state[6].substring(1));
                    }

                    break;
                case 6:
                    if ( (state[3].substring(0, 1)).equals(col) ){
                        sommeVoisins+= Integer.parseInt(state[3].substring(1));
                    }
                    if ( (state[10].substring(0, 1)).equals(col) ){
                        sommeVoisins+= Integer.parseInt(state[10].substring(1));
                    }
                    if ( (state[11].substring(0, 1)).equals(col) ){
                        sommeVoisins+= Integer.parseInt(state[11].substring(1));
                    }
                    break;
                case 10:
                    if ( (state[6].substring(0, 1)).equals(col) ){
                        sommeVoisins+= Integer.parseInt(state[6].substring(1));
                    }
                    if ( (state[15].substring(0, 1)).equals(col) ){
                        sommeVoisins+= Integer.parseInt(state[15].substring(1));
                    }
                    if ( (state[16].substring(0, 1)).equals(col) ){
                        sommeVoisins+= Integer.parseInt(state[16].substring(1));
                    }
                    break;
            }
        }//case a gauche
        else if (idVide > 15 && idVide < 20){
            if ( (state[idVide-1].substring(0, 1)).equals(col) ){
                sommeVoisins += Integer.parseInt(state[idVide-1].substring(1));
            }
            if ( (state[idVide+1].substring(0, 1)).equals(col) ){
                sommeVoisins += Integer.parseInt(state[idVide+1].substring(1));
            }
            if ( (state[idVide-6].substring(0, 1)).equals(col) ){
                sommeVoisins += Integer.parseInt(state[idVide-6].substring(1));
            }
            if ( (state[idVide-5].substring(0, 1)).equals(col) ){
                sommeVoisins += Integer.parseInt(state[idVide-5].substring(1));
            }//case en bas
        }//case bas
        else{
            if ( (state[idVide-1].substring(0, 1)).equals(col) ){
                sommeVoisins += Integer.parseInt(state[idVide-1].substring(1));
            }//bon
            if ( (state[idVide+1].substring(0, 1)).equals(col) ){
                sommeVoisins += Integer.parseInt(state[idVide+1].substring(1));
            }//bon
            if (idVide == 4){
                if ( (state[1].substring(0, 1)).equals(col) ){
                    sommeVoisins += Integer.parseInt(state[1].substring(1));
                }
                if ( (state[2].substring(0, 1)).equals(col) ){
                    sommeVoisins += Integer.parseInt(state[2].substring(1));
                }
                if ( (state[7].substring(0, 1)).equals(col) ){
                    sommeVoisins += Integer.parseInt(state[7].substring(1));
                }
                if ( (state[8].substring(0, 1)).equals(col) ){
                    sommeVoisins += Integer.parseInt(state[8].substring(1));
                }
            }
            else if (idVide == 7 || idVide == 8){
                if ( (state[idVide-4].substring(0, 1)).equals(col) ){
                    sommeVoisins += Integer.parseInt(state[idVide-4].substring(1));
                }
                if ( (state[idVide-3].substring(0, 1)).equals(col) ){
                    sommeVoisins += Integer.parseInt(state[idVide-3].substring(1));
                }
                if ( (state[idVide+4].substring(0, 1)).equals(col) ){
                    sommeVoisins += Integer.parseInt(state[idVide+4].substring(1));
                }
                if ( (state[idVide+5].substring(0, 1)).equals(col) ){
                    sommeVoisins += Integer.parseInt(state[idVide+5].substring(1));
                }
            }//bon
            else {
                if ( (state[idVide-5].substring(0, 1)).equals(col) ){
                    sommeVoisins += Integer.parseInt(state[idVide-5].substring(1));
                }
                if ( (state[idVide-4].substring(0, 1)).equals(col) ){
                    sommeVoisins += Integer.parseInt(state[idVide-4].substring(1));
                }
                if ( (state[idVide+5].substring(0, 1)).equals(col) ){
                    sommeVoisins += Integer.parseInt(state[idVide+5].substring(1));
                }
                if ( (state[idVide+6].substring(0, 1)).equals(col) ){
                    sommeVoisins += Integer.parseInt(state[idVide+6].substring(1));
                }
            }//bon


            System.out.println("centre");
        } // centre
        return sommeVoisins;
    }

    /**
     * Renvoie le prochain coup à jouer pour les rouges
     * Algo naïf = la première case dispo
     * @return id de la case
     */
    public static int iaRouge(){
	/*
		Écire un véritable code sachant jouer.
		La ligne du return ci-dessous doit donc naturellement aussi être ré-écrite.
		Cette version ne permet que de reproduire le fonctionnement à 2 joueurs 
		tout en conservant l'appel à la fonction,
		cela peut s'avérer utile lors du développement.
	*/
        return (int)(Math.random()*20);
    }
}
