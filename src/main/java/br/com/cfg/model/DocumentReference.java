/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.cfg.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.SoftReference;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.html.BoilerpipeContentHandler;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.observablecollections.ObservableCollections;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 *
 * @author Jan Mares
 */
public class DocumentReference {
    private final File file;
    private Integer count = 0;
    private Tika tika;
    Map<String, Integer> vwordtext = new HashMap<String, Integer>();

    

  private PropertyChangeSupport props = new PropertyChangeSupport(this);

    public DocumentReference(File file) {
        this.file = file;
        this.count = 0;
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
        StringTokenizer token = new StringTokenizer(st, " .,?:"); //caracateres que não interessam
        int i = 0;

        while (token.hasMoreTokens()) {
            String s = token.nextToken();
            addWord(s,i);
            i++;
            System.out.println(s);
        }
        
        return getVwordtext().size();
    }
    
    
    private void addWord(String s, int i) {
        
        this.vwordtext.put(s, i);
        
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
    
    public Map<String, Integer> getVwordtext() {
        return vwordtext;
    }

}
