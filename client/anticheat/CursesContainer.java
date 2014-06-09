/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.anticheat;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author xStr0nGx
 */
public class CursesContainer {

//    private String word_to_check;
    private static final String[] curses = initializeCurses();

    public static String[] initializeCurses() {
        String[] tempCurses = {"Alcoholic", "Amateur", "Analphabet", "Anarchist", "Ape", "Arse", "Arselicker", "Ass", "Ass master", "Ass-kisser", "Ass-nugget", "Ass-wipe", "Asshole", "Backwoodsman", "Balls", "Barbar", "Bastard", "Beavis", "Biest", "Bitch", "Blubber gut", "Bogeyman", "Booby", "Boozer", "Bozo", "Brain-fart", "Brainless", "Brainy", "Brontosaurus", "Brownie", "Bugger", "silly", "Bulloks", "Bum", "Bum-fucker", "Butt", "Buttfucker", "Butthead", "Cannibal", "Cave man", "Chaavanist", "Chaot", "Chauvi", "Cheater", "Children fucker", "Clit", "Cock", "Cock master", "Cock up", "Cockboy", "Cockfucker", "Cockroach", "Coky", "Con merchant", "Con-man", "Country bumpkin", "Cretin", "Criminal", "Cunt", "Cunt sucker", "Daywalker", "Derr brain", "Desperado", "Dickhead", "Dinosaur", "Diz brain", "dirty", "Dogshit", "Drakula", "Dreamer", "Drunkard", "Dufus", "Dulles", "Dumbo", "Dummy", "Dumpy", "Egoist", "Eunuch", "Exhibitionist", "Fart", "Fart", "shitty", "Fatso", "Fuck", "Fuck face", "Fuck head", "Fuck noggin", "Fucker", "Gangster", "Grouch", "Grumpy", "Hell dog", "Hillbilly", "Hippie", "Homo", "Homosexual", "Hooligan", "Horse fucker", "Idiot", "Ignoramus", "Jack-ass", "Jerk", "Motherfucker", "Mucky pub", "Neanderthal", "Nerfhearder", "Nobody", "Nurd", "Nuts", "numb", "Oddball", "Oger", "Oil dick", "Old fart", "Whore", "Zit", "Mofo"};
        for (int i = 0; i < tempCurses.length; i++) {
            tempCurses[i] = standardize(tempCurses[i]);
        }
        return tempCurses;
    }

//    public static String getLatestWord() {
//        return word_to_check;
//    }
//
//    public static void setLatestWord(String new_word) {
//        this.word_to_check = new_word;
//    }
    public static String[] getListOfCurses() {
        return curses;
    }

    public static boolean checkWord(String text) {
        text = standardize(text); //in order to disable the case sensitive.
        String words[] = text.split(" ");
        String cursesz[] = initializeCurses();
        for (String word : words) {
            for (String curse : cursesz) {
                if (word.equals(curse)) {
                    return false; //is a curse
                }
            }
        }
        return true; //approved
    }

    public static String standardize(String text) {
        text = text.toLowerCase();
        StringBuilder out = new StringBuilder();
        for (char c : text.toCharArray()) {
            switch (c) {
                case 'l':
                case '1':
                    out.append('i');
                    break;
                case '3':
                    out.append('e');
                    break;
                case '5':
                    out.append('s');
                    break;
                default:
                    out.append(c);
                    break;
            }
        }
        return out.toString();
    }
}
