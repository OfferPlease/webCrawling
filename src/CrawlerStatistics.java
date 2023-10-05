import java.io.File;
import java.io.FileWriter;
import java.io.IOException;  // Import the IOException class to handle errors
import java.util.List;

public class CrawlerStatistics {
    final private File newSite;
    final private File visit_newSite;
    final private File url_newSite;
    public CrawlerStatistics(){
        newSite = createFile("fetch_NewSite.csv");
        visit_newSite = createFile("visit_NewSite.csv");
        url_newSite = createFile("urls_NewSite.csv");
    }

    public void writeTo_newSite(List<String> input){
        writeRow(input, newSite.getPath());
    }
    public void writeToVisit_newSite(List<String> input){
        writeRow(input, visit_newSite.getPath());
    }
    public void writeToUrl_newSite(List<String> input){
        writeRow(input, url_newSite.getPath());
    }
    private void writeRow(List<String> input, String csvFilePath){
        // Write the column data to the existing CSV file
        try (FileWriter writer = new FileWriter(csvFilePath, true)) {
            for (String data : input) {
                writer.write(data);
                writer.write(",");  // newline character for each row
            }
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File createFile(String filename){
        try {
            File fileObj = new File(filename);
            if (fileObj.createNewFile()) {
                System.out.println("File created: " + fileObj.getName());
                return fileObj;
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return null;
    }

}
