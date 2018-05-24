/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.cfg.controller;

import br.com.cfg.model.DocumentReference;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
    ObservableList<Estatistica> dataestat = FXCollections.observableArrayList();
    ObservableList<Listaerros> dataerros = FXCollections.observableArrayList();

    Map<String, Integer> palavraserradas;
    ArrayList<Document> lt;

    FileChooser fileChooser = new FileChooser();
    Desktop desktop = Desktop.getDesktop();
    
    List<File> listFiles;

    private String pdicionario = "";
    
    private Stage stageErros;
    //private String pathdicionario = "C:\\appcor\\libs\\standard.dic";

    @FXML
    private AnchorPane rootPane;

    @FXML
    private TableView<Document> docsErrrView;
    @FXML
    private TableColumn<Document, String> documents;
    @FXML
    private TableColumn<Document, String> erros;

    private TableView<Estatistica> tablestat;

    private TableView<Listaerros> tableerros;

    public FXMLDocumentController() {

        this.docs = ObservableCollections.observableList(new ArrayList());
    }

    @FXML
    private void handleCloseButtonAction(ActionEvent event) {
        Platform.exit();
    }

    //https://www.youtube.com/watch?v=ZzwvQ6pa_tk
    @FXML
    private void handleFileChooserMulyiButtonAction(ActionEvent event) {

        

        configureFileChooser(fileChooser);
        int i = 8;
        this.listFiles = fileChooser.showOpenMultipleDialog(new Stage());
        loadDadosTela();

    }
    
    
    public void loadDadosTela(){
        if (this.listFiles != null) {
            lt = new ArrayList<Document>();
            this.docs = ObservableCollections.observableList(new ArrayList());
            for (File file : listFiles) {
                System.out.println("Arquivo: " + file.getName());
                DocumentReference docReference = new DocumentReference(file);
                System.out.println("Quantidade de palavras erradas -> " + docReference.getCount());

                this.docs.add(docReference);

                lt.add(new Document(file.getName(), Integer.toString(docReference.getCount())));

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

    private void loadData() {
        list.removeAll(list);
        list.addAll(new Document("Documento 1", "5"));
        list.addAll(new Document("Documento 2", "11"));
        list.addAll(new Document("Documento 3", "34"));
        list.addAll(new Document("Documento 4", "21"));
        docsErrrView.getItems().addAll(list);

    }

    private void loadData(ArrayList<Document> lt) {
        
        System.out.println("loadData.........");
        docsErrrView.getItems().clear();
        list.removeAll(list);
        docsErrrView.refresh();
        for (Document doc : lt) {
            System.out.println("getDocument()........." + doc.getDocument());
            System.out.println("getErrors()........." + doc.getErrors());
            list.addAll(doc);
        }

        docsErrrView.getItems().addAll(list);

    }

    private void loadEstatisitica() {

        //Map<String, Integer> getPalavraserradas()
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeCols();
//        loadData();
        docsErrrView.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (e.isPrimaryButtonDown() && e.getClickCount() == 2) {

                    Button button1 = new Button("Adicionar ao dicionário");
                    button1.setDisable(true);

                    Scene secondScene = new Scene(new Group(), 500, 400);
                    VBox vbox = new VBox();
                    vbox.setSpacing(10);
                    //

                    tableerros = new TableView<>();
                    tableerros.setMaxHeight(300);
                    tableerros.setMaxWidth(200);
                    dataerros = FXCollections.observableArrayList(new Listaerros("AElado"), new Listaerros("Sugeto"));

                    loadErrorList(docsErrrView.getSelectionModel().getSelectedIndex());

                    TableColumn firstCol = new TableColumn("Palavras");
                    firstCol.setMinWidth(180);
                    firstCol.setCellValueFactory(
                            new PropertyValueFactory<>("palavraerro"));

                    //loadDataEstat();
                    tableerros.setItems(dataerros);
                    tableerros.getColumns().addAll(firstCol);

                    tableerros.setOnMousePressed(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent e) {
                            button1.setDisable(false);
                            System.out.println("#######################");
                            tableerros.getSelectionModel().getSelectedIndex();
                            System.out.println("##" + tableerros.getSelectionModel().getSelectedIndex() + "##");
                            Listaerros l = tableerros.getItems().get(tableerros.getSelectionModel().getSelectedIndex());
                            System.out.println("#######" + l.getPalavraerro() + "############");
                            pdicionario = l.getPalavraerro();

                            System.out.println("#######################");

                        }

                    });

                    button1.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e) {
                            loadDialog(pdicionario);
                        }
                    });

                    firstCol.setSortType(TableColumn.SortType.DESCENDING);

                    vbox.getChildren().add((tableerros));
                    vbox.getChildren().add((button1));
                    //button1
                    ((Group) secondScene.getRoot()).getChildren().add(vbox);
//                    ((Group) secondScene.getRoot()).getChildren().add(grid);
                    stageErros = new Stage();
                    stageErros.setX(200);
                    stageErros.setY(100);
                    stageErros.setTitle("Erros no documento");
                    stageErros.setScene(secondScene);
                    stageErros.show();

                    System.out.println(docsErrrView.getSelectionModel().getSelectedIndex());
                }
            }
        });
    }

    public void loadDialog(String p) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmação");
        String s = "Adicionar a palavra \n" + p + "\n ao dicionário ?";
        alert.setContentText(s);

        Optional<ButtonType> result = alert.showAndWait();

        if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
            //pathdicionario
            addDicionario(p);
            stageErros.hide();
            
            loadDadosTela();
            
            //loadErrorList(docsErrrView.getSelectionModel().getSelectedIndex());

        }
    }

    public void addDicionario(String str) {
        try {
            
            FileWriter fw = new FileWriter(DocumentReference.pathdicionario, true);
            BufferedWriter bf = new BufferedWriter(fw);
            bf.write(str);
            bf.newLine();
            bf.close();
            loadData(lt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEstatisiticaButtonAction(ActionEvent event) throws IOException {
        Scene secondScene = new Scene(new Group(), 500, 400);

        tablestat = new TableView<>();
        tablestat.setMaxHeight(300);
        dataestat = FXCollections.observableArrayList(new Estatistica("AElado", "3"), new Estatistica("Sugeto", "9"));

        TableColumn firstNameCol = new TableColumn("Palavras");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellValueFactory(
                new PropertyValueFactory<>("palavra"));

        TableColumn lastNameCol = new TableColumn("Quantidade");
        lastNameCol.setMinWidth(100);
        lastNameCol.setCellValueFactory(
                new PropertyValueFactory<>("numerros"));

        loadDataEstat();

        tablestat.setItems(dataestat);
        tablestat.getColumns().addAll(firstNameCol, lastNameCol);

        firstNameCol.setSortable(false);
        lastNameCol.setSortType(TableColumn.SortType.DESCENDING);

        Label label1 = new Label("Estatística");

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.getChildren().add((label1));

        vbox.getChildren().addAll(tablestat);

        ((Group) secondScene.getRoot()).getChildren().add(vbox);
        Stage stage = new Stage();
        stage.setX(200);
        stage.setY(100);
        stage.setTitle("Estatística");
        stage.setScene(secondScene);
        stage.show();

    }

    @FXML
    private void handleMostrarButtonAction(ActionEvent event) throws IOException {

//        Parent root = FXMLLoader.load(getClass().getResource("/fxml/FXML.fxml"));
//        
//        Scene scene = new Scene(root);
//        scene.getStylesheets().add("/styles/Styles.css");
//        Stage stage = new Stage();
//        stage.setTitle("JavaFX and Maven");
//        stage.setScene(scene);
//        stage.show();
    }

    public void initializeCols() {
        documents.setCellValueFactory(new PropertyValueFactory<>("document"));
        erros.setCellValueFactory(new PropertyValueFactory<>("errors"));

    }

    public static class Document {

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

    public static class Listaerros {

        private final SimpleStringProperty palavraerro;

        public Listaerros(String palavraerro) {
            this.palavraerro = new SimpleStringProperty(palavraerro);
        }

        public String getPalavraerro() {
            return palavraerro.get();
        }

        public void setPalavraerro(String s) {
            this.palavraerro.set(s);
        }
    }

    public static class Estatistica {

        private final SimpleStringProperty palavra;
        private final SimpleStringProperty numerros;

        public Estatistica(String p, String n) {
            this.palavra = new SimpleStringProperty(p);
            this.numerros = new SimpleStringProperty(n);
        }

        public String getPalavra() {
            return palavra.get();
        }

        public void setPalavra(String s) {
            this.palavra.set(s);
        }

        public String getNumerros() {
            return numerros.get();
        }

        public void setNumerros(String s) {
            this.numerros.set(s);
        }

    }

    public List<DocumentReference> getDocs() {
        return docs;
    }

    public void loadDocs() {
        List<DocumentReference> ld = getDocs();
        for (DocumentReference dr : ld) {
            System.out.println("---------------------------------------------------------- ");
            System.out.println("Texto -> " + dr.getContent());
            System.out.println("---------------------------------------------------------- ");
            System.out.println("Quantidade palavras -> " + dr.getCount());
            System.out.println("---------------------------------------------------------- ");
            System.out.println("Quantidade de palavras erradas -> " + dr.getCountError());
            System.out.println("---------------------------------------------------------- ");

        }
    }

    public String getErrorList(int index) {

        StringBuilder out = new StringBuilder();

        DocumentReference dr = docs.get(index);

        for (int i = 0; i < dr.getListErrorsWords().size(); i++) {

            out.append(dr.getListErrorsWords().get(i));
            out.append("\n");
        }
        return out.toString();
    }

    //
    private void loadErrorList(int index) {
        
        System.out.println("loadData.........");
        tableerros.getItems().clear();
        dataerros.removeAll(dataerros);
        tableerros.refresh();
        DocumentReference dr = docs.get(index);

        for (int i = 0; i < dr.getListErrorsWords().size(); i++) {

            dataerros.addAll(new Listaerros(dr.getListErrorsWords().get(i)));

        }

        tableerros.getItems().addAll(dataerros);

    }

    private void loadDataEstat() {
        this.palavraserradas = new HashMap<String, Integer>();

        for (DocumentReference d : docs) {
            ArrayList<String> e = d.getListErrorsWords();
            for (String s : e) {
                addPalavraserradas(s);
            }

        }

        Map<String, Integer> lt = getPalavraserradas();
        System.out.println("load Estatisitica.........");
        tablestat.getItems().clear();
        dataestat.removeAll(dataestat);
        tablestat.refresh();

        long i = 0;
        for (Map.Entry<String, Integer> p : lt.entrySet()) {

            dataestat.addAll(new Estatistica(p.getKey(), p.getValue().toString()));
        }

        tablestat.getItems().addAll(dataestat);

    }

    public void addPalavraserradas(String str) {
        if (palavraserradas.containsKey(str)) {
            int c = palavraserradas.get(str) + 1;
            palavraserradas.replace(str, c);

        } else {
            palavraserradas.put(str, 1);
        }

    }

    public Map<String, Integer> getPalavraserradas() {
        return palavraserradas;
    }
}
