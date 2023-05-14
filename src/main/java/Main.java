import controllers.DBController;
import repository.RepositoryManager;
import repository.SQLiteManager;

public class Main {
    public static void main(String[] args) {
        String xml1Path = "src/main/java/xml/1.xml";
        String xml2Path = "src/main/java/xml/2.xml";
        String patternPath = "src/main/java/xml/pattern.xsl";
        String dbFile;
        int n;

        DBController dbController = new DBController();
        dbController.getInputData();
        dbFile = dbController.getDbFile();
        n = dbController.getN();

        double startPoint = System.currentTimeMillis();

        RepositoryManager repositoryManager = new SQLiteManager(dbFile);
        dbController.insertIntoTable(repositoryManager, n);
        dbController.downloadToXML(repositoryManager, xml1Path);
        dbController.transformXMLFile(xml1Path, xml2Path, patternPath);
        System.out.println("Sum of FIELD values: " + dbController.getSumOfFields(xml2Path));

        double exTime = (System.currentTimeMillis() - startPoint) / 1000;
        System.out.println("Execution time: " + exTime + " sec.");
    }
}
