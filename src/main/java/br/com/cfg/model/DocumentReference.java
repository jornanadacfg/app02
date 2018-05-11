/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.cfg.model;

import com.atlascopco.hunspell.Hunspell;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

/**
 *
 * @author Jan Mares
 */
public class DocumentReference {
    private final File file;
    private Integer countError = 0;
    private Tika tika;
    private Hunspell speller = new Hunspell("C:\\appcor\\libs\\xxxx.dic", "C:\\appcor\\libs\\xxxx.aff");
    Map<Integer, String> vwordtext;
    Map<Integer, String> posErrorWord;

    

  private PropertyChangeSupport props = new PropertyChangeSupport(this);

    public DocumentReference(File file) {
        this.file = file;
        this.countError = 0;
    }

    public File getFile() {
        return file;
    }

    public String getContent(){
        String textoExtraido = "";
        try {
            textoExtraido = getTika().parseToString(file);
        } catch (IOException ex) {
            Logger.getLogger(DocumentReference.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TikaException ex) {
            Logger.getLogger(DocumentReference.class.getName()).log(Level.SEVERE, null, ex);
        }
        return textoExtraido;
    }
    
    public int getCount() {
        String st = getContent();
        
        StringTokenizer token = new StringTokenizer(st, " .,?!:/0123456789"); //caracateres que não interessam
        int i = 0;
        setDropCountError();
        this.vwordtext = new HashMap<Integer, String>();
        this.posErrorWord = new HashMap<Integer, String>();

        while (token.hasMoreTokens()) {
            String s = token.nextToken();
            
            addWord(i, s.trim());
            checkWord(i,s);
            i++;
            System.out.println(s);
        }
        
        return getCountError();
    }
    
    
    private void addWord( int i, String s) {
        
        this.vwordtext.put(i, s.trim());
        
    }
    private void addErrorWord(int i, String s){
        setCountError(1);
        this.posErrorWord.put(i, s.trim());
    }
    
    public String getFileName(){
        return file.getName();
    }
    
    public Tika getTika() {
        if (tika == null) {
            tika = new Tika();
        }
        return tika;
    }
    
    public Map<Integer, String> getVwordtext() {
        return vwordtext;
    }
    
    public void checkWord(Integer i, String wordToCheck){
        if (speller.spell(wordToCheck.trim())) {
            System.out.println("---OK--- : " + wordToCheck.trim());
        } else {
            System.out.println("---ERRADO---: "  + wordToCheck.trim());
            addErrorWord(i, wordToCheck.trim());

        }
    }

    public Integer getCountError() {
        return countError;
    }

    public void setCountError(Integer countError) {
        this.countError = this.countError + countError;
    }
    
    public void setDropCountError(){
        this.countError = 0;
    }
            
    
    

}
