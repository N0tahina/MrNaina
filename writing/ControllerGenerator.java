package writing;

import reading.ReaderSql;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ControllerGenerator {

    public void generation( ArrayList<ReaderSql> read, String path, String techno, String templ_p, String output_dir){
        for (ReaderSql readerSql : read) {
            generateController( path, techno, templ_p, readerSql, output_dir);
        }
    }

    public void generateController(String path, String techno, String templ_p, ReaderSql sqlreading, String output_dir) {
        try {

            // Read the template from the file
            String template = new String(Files.readAllBytes(Paths.get(templ_p)));
            String generatedCode = template
                    .replace("${EntityName}",sqlreading.getNomTable())
                    .replace("${EntityNamePlural}",sqlreading.getNomTable() + "s")
                    .replace("${ControllerName}",sqlreading.getNomTable() + "Controller")
                    .replace("${ServiceName}",sqlreading.getNomTable().substring(0, 1).toUpperCase()+ sqlreading.getNomTable().substring(1) + "Service")
                    .replace("${ClassName}",sqlreading.getNomTable().substring(0, 1).toUpperCase()+ sqlreading.getNomTable().substring(1))
                    .replace("${ClassNamePlural}",sqlreading.getNomTable().substring(0, 1).toUpperCase()+ sqlreading.getNomTable().substring(1)+"s")
                    .replace("${serviceVariable}",sqlreading.getNomTable().toLowerCase() + "Service")
                    .replace("${entityVariable}",sqlreading.getNomTable().toLowerCase());

            // Specify the output directory and file name
            Path outputPath = Paths.get(output_dir + sqlreading.getNomTable() + "Controller.java");

            // Create directories if they don't exist
            Files.createDirectories(outputPath.getParent());

            // Write the generated code to the new file
            Files.write(outputPath, generatedCode.getBytes());

            System.out.println("Controller generated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}