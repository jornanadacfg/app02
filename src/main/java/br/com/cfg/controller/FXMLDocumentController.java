/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.cfg.controller;


import br.com.cfg.model.DocumentReference;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jdesktop.observablecollections.ObservableCollections;

/**
 *
 * @author CarlosFernando
 */
public class FXMLDocumentController implements Initializable {
    
    private List<DocumentReference> docs;
    ObservableList<Document> list = FXCollections.observableArrayList();
    ArrayList<Document> lt;
    
    FileChooser fileChooser = new FileChooser();
    Desktop desktop = Desktop.getDesktop();
    
    @FXML
    private TableView<Document> docsErrrView;
    @FXML
    private TableColumn<Document, String> documents;
    @FXML
    private TableColumn<Document, String> erros;

    public FXMLDocumentController() {
        
        this.docs = ObservableCollections.observableList(new ArrayList());
    }
    
    
    
    @FXML
    private void handleCloseButtonAction(ActionEvent event) {
        Platform.exit();
    }
    
    @FXML
    private void handleFileChooserMulyiButtonAction(ActionEvent event) {

        lt = new ArrayList<Document>();
       

        configureFileChooser(fileChooser);
        int i = 8;
        List<File> list = fileChooser.showOpenMultipleDialog(new Stage());
        if (list != null) {
            for (File file : list) {


                i = i + 5;
                System.out.println("Arquivo: " + file.getName());
                System.out.println("AbsolutePath Arquivo: " + file.getAbsolutePath());
                System.out.println("Path Arquivo: " + file.getPath());

                lt.add(new Document(file.getName(),Integer.toString(i)));
                
                //docs.add(new DocumentReference(file).connect(this));
                docs.add(new DocumentReference(file));
                //openFile(file);
            }
            loadData(lt);
            
            loadDocs();

        }

    }
    
    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("Visualizar Documentos.");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Docs", "*.*"),
                new FileChooser.ExtensionFilter("DOCX", "*.docx"),
                new FileChooser.ExtensionFilter("DOC", "*.doc")
        );
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
    }

    private void openFile(File file) {
        try {
            desktop.open(file);
        } catch (IOException ex) {
            Logger.getLogger(
                    FXMLDocumentController.class.getName()).log(
                    Level.SEVERE, null, ex
            );
        }
    }
    
    private void loadData(){
        list.removeAll(list);
        list.addAll(new Document("Documento 1","5"));
        list.addAll(new Document("Documento 2","11"));
        list.addAll(new Document("Documento 3","34"));
        list.addAll(new Document("Documento 4","21"));
        docsErrrView.getItems().addAll(list);
        
    }
    private void loadData(ArrayList<Document> lt){
        System.out.println("loadData.........");
        docsErrrView.getItems().clear();
        list.removeAll(list);
        docsErrrView.refresh();
        for(Document doc:lt){
            System.out.println("getDocument()........." + doc.getDocument());
            System.out.println("getErrors()........." + doc.getErrors());
            list.addAll(doc);
        }
        
        docsErrrView.getItems().addAll(list);
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeCols();
        loadData();
    }    
    
    public void initializeCols(){
        documents.setCellValueFactory(new PropertyValueFactory<>("document"));
        erros.setCellValueFactory(new PropertyValueFactory<>("errors"));
        
        
    }
    public static class Document{
        private final SimpleStringProperty document;
        private final SimpleStringProperty errors;
        

        public Document(String document, String errors) {
            this.document = new SimpleStringProperty(document);
            this.errors = new SimpleStringProperty(errors);
            
        }

        public String getDocument() {
            return document.get();
        }

        public String getErrors() {
            return errors.get();
        }

        
        
        
        
        
    }
    
    public List<DocumentReference> getDocs() {
        return docs;
    }
    
    public void loadDocs(){
        List<DocumentReference> ld  = getDocs();
        for(DocumentReference dr: ld){
            System.out.println("---------------------------------------------------------- ");
            System.out.println("Texto -> " + dr.getContent());
            System.out.println("---------------------------------------------------------- ");
            System.out.println("Quantidade palavras -> " + dr.getCount());
            System.out.println("---------------------------------------------------------- ");
    
        }
    }
}
