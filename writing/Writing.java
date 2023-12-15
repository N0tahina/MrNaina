package writing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import reading.ReaderSql;

public class Writing {
    private ReaderSql sqlreading;

    public void generation(ArrayList<ReaderSql> read, String path, String language, String templ_p) {
        for (ReaderSql readerSql : read) {
            Writing instance = new Writing(readerSql);
            instance.generateClasses(path, language, templ_p);
        }
    }

    public String capitalizeFirstLetter(String input) {
        return input == null || input.isEmpty() ? input : input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public String afficheTemplate(String templ_p) {
        String cheminFichier = templ_p;
        StringBuilder contentTemplate = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(cheminFichier))) {
            String ligne;
            while ((ligne = bufferedReader.readLine()) != null) {
                contentTemplate.append(ligne);
            }
        } catch (IOException e) {
            // Gérer l'exception de manière appropriée (logger ou lancer une exception personnalisée)
            e.printStackTrace();
        }
        return contentTemplate.toString();
    }

    public String nameFichier(String techno) {
        return techno.compareToIgnoreCase("C#") == 0 ?
                capitalizeFirstLetter(this.sqlreading.getNomTable()) + ".cs" :
                capitalizeFirstLetter(this.sqlreading.getNomTable()) + ".java";
    }

    public String nameFichierC(String techno) {
        return techno.compareToIgnoreCase("C#") == 0 ?
                capitalizeFirstLetter(this.sqlreading.getNomTable()) + "Controller.cs" :
                capitalizeFirstLetter(this.sqlreading.getNomTable()) + "Controller.java";
    }

    public File pathFichier(String path, String nomFichier) throws IOException {
        String folderPath = path + this.sqlreading.getPackage_table();
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File fichier = new File(folder, nomFichier);
        if (!fichier.exists()) {
            fichier.createNewFile();
        }
        return fichier;
    }

    public String changePackage(String techno, String template) {
        return techno.compareToIgnoreCase("C#") == 0 ?
                template.replace("packaging", "namespace") :
                template.replace("packaging", "package");
    }

    public String importation() {
        StringBuilder imports = new StringBuilder();
        int compteur = 0;
        for (int i = 0; i < this.sqlreading.getImportation().size(); i++) {
            if (!this.sqlreading.getImportation().get(i).equalsIgnoreCase("")) {
                if (compteur == 0) {
                    imports.append("import ").append(this.sqlreading.getImportation().get(i)).append(";\n");
                } else {
                    imports.append("import").append(this.sqlreading.getImportation().get(i)).append(";\n");
                }
                compteur++;
            }
        }
        return imports.toString();
    }

    public String giveAttribut() {
        StringBuilder typeVariable = new StringBuilder();
        for (int i = 0; i < this.sqlreading.getType_column().size(); i++) {
            if (i == 0) {
                typeVariable.append(this.sqlreading.getType_column().get(i)).append("  ")
                        .append(this.sqlreading.getName_column().get(i)).append(";\n");
            } else {
                typeVariable.append("    ").append(this.sqlreading.getType_column().get(i)).append("  ")
                        .append(this.sqlreading.getName_column().get(i)).append(" ;\n");  // Ajout de l'espace après le point-virgule
            }
        }
        return typeVariable.toString();
    }
    

    public String getterSetters(String techno) {
        StringBuilder getterSetter = new StringBuilder();
        if (techno.compareToIgnoreCase("C#") == 0) {
            for (int i = 0; i < this.sqlreading.getType_column().size(); i++) {
                String getter = i == 0 ?
                        "\n  public " + this.sqlreading.getType_column().get(i) + " " +
                                capitalizeFirstLetter(this.sqlreading.getName_column().get(i)) +
                                "{get => " + this.sqlreading.getName_column().get(i) +
                                " ; set => " + this.sqlreading.getName_column().get(i) + " = value ; }\n" :
                        "    public " + this.sqlreading.getType_column().get(i) + " " +
                                capitalizeFirstLetter(this.sqlreading.getName_column().get(i)) +
                                "{get => " + this.sqlreading.getName_column().get(i) +
                                " ; set => " + this.sqlreading.getName_column().get(i) + " = value ; }\n";
                getterSetter.append(getter);
            }
        } else {
            for (int i = 0; i < this.sqlreading.getType_column().size(); i++) {
                String getter = i == 0 ?
                        "\n  public " + this.sqlreading.getType_column().get(i) + "  get" +
                                capitalizeFirstLetter(this.sqlreading.getName_column().get(i)) + "()" +
                                "{ \n        return this." + this.sqlreading.getName_column().get(i) + " ; \n    } \n" :
                        "    public " + this.sqlreading.getType_column().get(i) + "  get" +
                                capitalizeFirstLetter(this.sqlreading.getName_column().get(i)) + "()" +
                                "{ \n        return this." + this.sqlreading.getName_column().get(i) + " ; \n    } \n";
                String setter = "    public void set" + capitalizeFirstLetter(this.sqlreading.getName_column().get(i)) +
                        "(" + this.sqlreading.getType_column().get(i) + " " +
                        this.sqlreading.getName_column().get(i) + ")" +
                        "{ \n        this." + this.sqlreading.getName_column().get(i) +
                        " = " + this.sqlreading.getName_column().get(i) + "; \n    } \n";
                getterSetter.append(getter).append(setter);
            }
        }
        return getterSetter.toString();
    }

    public void generateClasses(String path, String techno, String templ_p) {
        String nomFichier = nameFichier(techno);
        String template = afficheTemplate(templ_p);
        try {
            File fichier = pathFichier(path, nomFichier);
            try (FileWriter writers = new FileWriter(fichier)) {
                String pakage = changePackage(techno, template).replace("templatepackage;", (this.sqlreading.getPackage_table() + ";\n"));
                String lib = pakage.replace("importing", importation());
                String nameClasses = lib.replace("template{", capitalizeFirstLetter(this.sqlreading.getNomTable()) + "{\n");
                String attriubt = nameClasses.replace("Type fields", giveAttribut());
                String contru = attriubt.replace("constructor", capitalizeFirstLetter(this.sqlreading.getNomTable()));
                String getterSetter = getterSetters(techno);
                String finalFichier = contru.replace("//getter//setter", getterSetter);
                writers.write(finalFichier);
            }
        } catch (IOException e) {
            // Gérer l'exception de manière appropriée (logger ou lancer une exception personnalisée)
            e.printStackTrace();
        }
    }

    public ReaderSql getSqlreading() {
        return sqlreading;
    }

    public void setSqlreading(ReaderSql sqlreading) {
        this.sqlreading = sqlreading;
    }

    public Writing(ReaderSql sqlreading) {
        this.sqlreading = sqlreading;
    }

    public Writing() {
    }
}
