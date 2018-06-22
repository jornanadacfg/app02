/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.cfg.controller;

import br.com.cfg.model.Docsword;
import br.com.cfg.model.DocumentReference;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.jdbc.BlobProxy;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;
import org.jdesktop.observablecollections.ObservableCollections;

/**
 *
 * @author CarlosFernando
 */
public class FXMLDocumentController implements Initializable {

    private List<DocumentReference> docs;
    private List<Docsword> docsfile;
    ObservableList<Document> list = FXCollections.observableArrayList();
    ObservableList<Estatistica> dataestat = FXCollections.observableArrayList();
    ObservableList<Estatistica> dataestatdb = FXCollections.observableArrayList();
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
    private TableView<Estatistica> tablestatdb;

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

    public void loadDadosTela() {
        if (this.listFiles != null) {
            this.lt = new ArrayList<Document>();
            this.docsfile = new ArrayList<Docsword>();
            this.docs = ObservableCollections.observableList(new ArrayList());
            for (File file : listFiles) {
                System.out.println("Arquivo: " + file.getName());
                System.out.println("Arquivo getAbsolutePath: " + file.getAbsolutePath());
                System.out.println("Arquivo getPath: " + file.getPath());

                Docsword dw = new Docsword();

                dw.setName_doc(file.getName());

                try {
                    dw.setDocfile(BlobProxy.generateProxy(getDocWord(file.getAbsolutePath())));
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }

                this.docsfile.add(dw);

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

    @FXML
    private void handleSalvarNoBDButtonAction(ActionEvent event) throws IOException {

        for (Docsword d : docsfile) {
            System.out.println("Name_doc -> " + d.getName_doc());
            System.out.println("Docfile -> " + d.getDocfile());
            saveDocDB(d);
        }

    }

    @FXML
    private void handleEstatisticaDoBDButtonAction(ActionEvent event) throws IOException {

        System.out.println("Estatística no Banco de dados...");

        Scene estatisticaDoBDScene = new Scene(new Group(), 1000, 500);



        BorderPane border = new BorderPane();
        border.setPrefSize(1000,500);
        border.setPadding(new Insets(20));
        Label lb = new Label("Estatísticas de erros nos documentos Word no BD");
        lb.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        BorderPane.setAlignment(lb, Pos.CENTER);
        

        border.setTop(lb);
        
        border.setLeft(addVBox());
        border.setCenter(addHBox());

        ((Group) estatisticaDoBDScene.getRoot()).getChildren().add(border);
        Stage stage = new Stage();
        stage.setX(200);
        stage.setY(100);
        stage.setTitle("Estatísticas de erros nos documentos Word no BD");
        stage.setScene(estatisticaDoBDScene);
        stage.show();

    }
    
     private HBox addHBox() {

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.setStyle("-fx-background-color: #FFFFFF;");




        ObservableList<PieChart.Data> pieChartData
                = FXCollections.observableArrayList(
                        new PieChart.Data("AElado", 11),
                        new PieChart.Data("Sugeto", 9),
                        new PieChart.Data("pesoa", 7),
                        new PieChart.Data("dezejo", 5),
                        new PieChart.Data("Outros", 3));
        final PieChart chart = new PieChart(pieChartData);
        chart.setTitle("Palavras com erro");
        
        hbox.getChildren().addAll(chart);
        
        return hbox;
    }
      private VBox addVBox() {
        
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10)); // Set all sides to 10
        vbox.setSpacing(8);              // Gap between nodes

        Text title = new Text("Erros");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        vbox.getChildren().add(title);

        tablestatdb = new TableView<>();
        tablestatdb.setMaxHeight(300);
        tablestatdb.setMaxWidth(1000);
        dataestatdb = FXCollections.observableArrayList(new Estatistica("AElado", "11"), new Estatistica("Sugeto", "9"), new Estatistica("pesoa", "7"), new Estatistica("dezejo", "5"), new Estatistica("Outros", "3"));

        TableColumn firstNameCol = new TableColumn("Palavras");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellValueFactory(
                new PropertyValueFactory<>("palavra"));

        TableColumn lastNameCol = new TableColumn("Quantidade");
        lastNameCol.setMinWidth(100);
        lastNameCol.setCellValueFactory(
                new PropertyValueFactory<>("numerros"));


        tablestatdb.getItems().clear();

        
        
        tablestatdb.setItems(dataestatdb);
        tablestatdb.refresh();
        tablestatdb.getColumns().addAll(firstNameCol, lastNameCol);

        firstNameCol.setSortable(false);
        lastNameCol.setSortType(TableColumn.SortType.DESCENDING);
        
        vbox.getChildren().add(tablestatdb);
        
        return vbox;
    }


    private void saveDocDB(Docsword doc) {

        ServiceRegistry standardRegistry = new StandardServiceRegistryBuilder().configure().build();

        SessionFactory sessionFactory = new MetadataSources(standardRegistry)
                .addAnnotatedClass(Docsword.class).buildMetadata()
                .buildSessionFactory();
        Session session = sessionFactory.openSession();

        String hql = "from Docsword where name_doc = '" + doc.getName_doc() + "'";
        Query query = session.createQuery(hql);
        List<Docsword> listDocsword = query.list();

        System.out.println("hql : " + hql);

        if (!(listDocsword.size() > 0)) {
            System.out.println("---Não existe!----");
            session.beginTransaction();
            session.save(doc);
            session.getTransaction().commit();

            System.out.println("listDocsword.size() : " + listDocsword.size());
        } else {
            System.out.println("---Existe!----");
        }

        session.close();
        sessionFactory.close();

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

    private static byte[] getDocWord(String filedoc) throws IOException {

        File file = new File(filedoc);
        FileInputStream inputStream = new FileInputStream(filedoc);

        byte[] fileBytes = new byte[(int) file.length()];
        inputStream.read(fileBytes);
        inputStream.close();

        return fileBytes;

    }
}
