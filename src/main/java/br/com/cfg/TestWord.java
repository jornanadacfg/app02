/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.cfg;

import br.com.cfg.model.Docsword;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.jdbc.BlobProxy;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;

/**
 *
 * @author mercu
 */
public class TestWord {

    public static void main(String[] args) throws IOException {
        String filedoc = "C:\\appcor\\testou.doc";

        ServiceRegistry standardRegistry = new StandardServiceRegistryBuilder().configure().build();

        SessionFactory sessionFactory = new MetadataSources(standardRegistry)
                .addAnnotatedClass(Docsword.class).buildMetadata()
                .buildSessionFactory();
        Session session = sessionFactory.openSession();

        Docsword doc = new Docsword();

//        try {
//            doc.setDocfile(BlobProxy.generateProxy(getDocWord(filedoc)));
//        } catch (IOException ex) {
//            Logger.getLogger(TestWord.class.getName()).log(Level.SEVERE, null, ex);
//        }
        String hql = "from Docsword where name_doc = '" + "TESTE8" + "'";
        Query query = session.createQuery(hql);
        List<Docsword> listDocsword = query.list();

        System.out.println("hql : " + hql);

        if ((listDocsword.size() > 0)) {
            System.out.println("---Existe!----");

            for (Docsword d : listDocsword) {
                System.out.println("Name: " + d.getName_doc());
                saveFileDisk(d.getDocfile());
            }

        } else {
            System.out.println("---Não existe!----");
        }

//        if(!session.contains(doc)){
//            session.beginTransaction();
//            session.save(doc);
//            session.getTransaction().commit();
//        }
        session.close();
        sessionFactory.close();

        System.out.println("Doc word is saved successfully.");

    }

    private static void saveFileDisk(Blob docfile) throws IOException {
        Blob blob = docfile;
        try {
            byte[] blobBytes = blob.getBytes(1, (int) blob.length());
            saveBytesToFileWord(blobBytes);
        } catch (SQLException ex) {
            Logger.getLogger(TestWord.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static byte[] getDocWord(String filedoc) throws IOException {

        File file = new File(filedoc);
        FileInputStream inputStream = new FileInputStream(filedoc);

        byte[] fileBytes = new byte[(int) file.length()];
        inputStream.read(fileBytes);
        inputStream.close();

        return fileBytes;

    }

    private static void readPhotoOfPerson(int personId, String photoFilePath) throws IOException, SQLException {
//        Docsword docsword = (Docsword) session.get(Docsword.class, personId);
//        Blob blob = docsword.getDocfile();
//        byte[] blobBytes = blob.getBytes(1, (int) blob.length());
//        saveBytesToFile(photoFilePath, blobBytes);
//        blob.free();
    }

    private static void saveBytesToFile(String filePath, byte[] fileBytes) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(filePath);
        outputStream.write(fileBytes);
        outputStream.close();
    }

    private static void saveBytesToFileWord(byte[] fileBytes) throws IOException {
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream("C:\\appcor\\testou.doc");
            outputStream.write(fileBytes);
            outputStream.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TestWord.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
