/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.cfg.model;

/**
 *
 * @author mercu
 */
import java.sql.Blob;
 
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "DOCSWORD")
public class Docsword {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "DOC_ID" , updatable = false, nullable = false)
    private int id;
    
    @Column(unique = true)
    private String name_doc;
    
    @Column(name = "DOCFILE", unique = true)
    private Blob docfile;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName_doc() {
        return name_doc;
    }

    public void setName_doc(String name_doc) {
        this.name_doc = name_doc;
    }

    public Blob getDocfile() {
        return docfile;
    }

    public void setDocfile(Blob docfile) {
        this.docfile = docfile;
    }
    
    
   
    
}
