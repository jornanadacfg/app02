/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.cfg.model;


import com.atlascopco.hunspell.Hunspell;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
    
    private String newpathdic = "C:\\Users\\CarlosFernando\\Documents\\dicbr\\standard.dic";
    Map<Integer, String> vwordtext;
    Map<Integer, String> posErrorWord;
    ArrayList<String> erDoc;

    

  private PropertyChangeSupport props = new PropertyChangeSupport(this);

    public DocumentReference(File file) {
        this.file = file;
        this.countError = 0;
        loadDicNew();
    }

    public File getFile() {
        return file;
    }

    public String getContent(){
        String textoExtraido = "";
        String novoTexto = "";
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
        System.out.println("Texto -> ");
        System.out.println(st);
        System.out.println("Novo texto -> ");
        String nst = getCleanText(st);
        System.out.println(nst);
        System.out.println("Novo texto -> ");
        
        StringTokenizer token = new StringTokenizer(nst, " .,?!:/0123456789"); //caracateres que não interessam
        int i = 0;
        setDropCountError();
        this.vwordtext = new HashMap<Integer, String>();
        this.posErrorWord = new HashMap<Integer, String>();
        this.erDoc = new ArrayList<String>();

        while (token.hasMoreTokens()) {
            String s = token.nextToken();
            String cifen = cheIfen(s);
            addWord(i, s.trim());
            checkWord(i,s);
            i++;
            System.out.println(s);
        }
        
        return getCountError();
    }
    
    
    public String getCleanText(String st){
        String lines[] = st.split("\\r?\\n");
        StringBuilder std = new StringBuilder();
        
        for(String l: lines){
            
            if((l.length() - 1) > 0){
                
                if(l.charAt(l.length() - 1) == '-'){
                    std.append(l.substring(0, l.length() - 1));
                }else{
                    std.append(l.substring(0, l.length()) + " ");
                }
            }
            
            
        }
        
//        String novoTexto = st.replace ("\n", " ");
//        String novoTexto2 = novoTexto.replaceAll("\t", " ");
//        String novoTexto3 = novoTexto2.replaceAll("\n", " ");
        
        
        return std.toString();
    }
    
    private void addWord( int i, String s) {
        
        this.vwordtext.put(i, s.trim());
        
    }
    private void addErrorWord(int i, String s){
        setCountError(1);
        
        this.posErrorWord.put(i, s.trim());
        this.erDoc.add(s.trim());
    }
    
    public ArrayList<String> getListErrorsWords(){
        return this.erDoc;
        
    }
    
    public Map<Integer, String> getListPosErrorsWords(){
        
        return this.posErrorWord;
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
    
    public String cheIfen(String s){
        System.out.println("-------------------------");
        System.out.println("Palavra: " + s);
        String [] sp = s.split(s);
        for(String t:sp){
            System.out.println("t -> " + t);
        }
        System.out.println("-------------------------");
        return s;
    }
    
    public void checkWord(Integer i, String wordToCheck){
        if (speller.spell(wordToCheck.trim()) ) {
            System.out.println("---OK--- : " + wordToCheck.trim());
        } else if(!wordToCheck.equals("-")){
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
    
    public void loadDicNew(){
        try {
            File f = new File(newpathdic);
            BufferedReader b = new BufferedReader(new FileReader(f));
            String readLine = "";
            
            boolean bo = false;
            
            try {
                while ((readLine = b.readLine()) != null) {
                    if(readLine.contains("---"))
                        bo = true;
                    
                    if(bo && (!readLine.contains("---")))
                        this.speller.add(readLine);
                        System.out.println(readLine);
                        
                    
                }
            } catch (IOException ex) {
                Logger.getLogger(DocumentReference.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DocumentReference.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
            
    
    

}
