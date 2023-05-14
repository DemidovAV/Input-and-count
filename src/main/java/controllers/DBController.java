package controllers;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import repository.RepositoryManager;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class DBController {
    private String dbFile;
    private int n;
    Scanner scanner = new Scanner(System.in);

    public String getDbFile() {
        return dbFile;
    }

    public int getN() {
        return n;
    }

    public void insertIntoTable(RepositoryManager rs, int n) {
        try {
            rs.connect();
            rs.setAutoCommit(false);
            rs.execute("DROP TABLE IF EXISTS TEST;");
            rs.execute("CREATE TABLE TEST (FIELD int NOT NULL PRIMARY KEY);");
            rs.createPrepareStatement("INSERT INTO TEST (FIELD) VALUES (?);");
            for (int i = 1; i <= n; i++) {
                rs.prepStatementSetInt(1, i).prepStatementAddBatch();
            }
            rs.prepStatementExecuteBatch();
            rs.commit();
        } finally {
            rs.disconnect();
        }
    }

    public void downloadToXML(RepositoryManager rs, String file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            rs.connect();
            ResultSet resultSet = rs.executeQuery("SELECT * FROM TEST;");
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
            writer.write("<entries>\n");
            while (resultSet.next()) {
                writer.write("    <entry>\n");
                writer.write("        <field>");
                writer.write(Integer.toString(resultSet.getInt(1)));
                writer.write("</field>\n");
                writer.write("    </entry>\n");
            }
            writer.write("</entries>");
            writer.flush();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        } finally {
            rs.disconnect();
        }
    }

    public void transformXMLFile(String inputFile, String outputFile, String pattern) {
        try {
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer(new StreamSource(pattern));
            transformer.transform(new StreamSource(inputFile), new StreamResult(outputFile));
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public long getSumOfFields(String filename) {
        long sum = 0;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            Document doc = documentBuilder.parse(new File(filename));
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("entry");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                sum += Integer.parseInt(element.getAttribute("field"));
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
        return sum;
    }

    public void getInputData() {
        this.dbFile = scanner.nextLine();
        this.n = scanner.nextInt();
        scanner.close();
    }

}
