/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.cfg;

import com.atlascopco.hunspell.Hunspell;
import java.util.List;

/**
 *
 * @author CarlosFernando
 */
public class AppTest {
    public static void main(String[] args) {
        Hunspell speller = new Hunspell("C:\\appcor\\libs\\xxxx.dic", "C:\\appcor\\libs\\xxxx.aff");
        String wordToCheck = "sujeito";
        if (speller.spell(wordToCheck)) {
            System.out.println("---OK---");
        } else {

            System.out.println("---ERRADO---");
            List<String> suggestions = speller.suggest(wordToCheck);

            for(String s: suggestions){
                System.out.println("Sugestões: " + s);
            }
        }

    }
    
}
