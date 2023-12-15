package main;

import reading.*;
import writing.ControllerGenerator;
import writing.Writing;
import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Main {
    public static void main(String[] args) {
        try {
            // Chargement du fichier de configuration XML
            File xmlFile = new File("./config.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            
            // Récupération des éléments de configuration
            NodeList config = doc.getElementsByTagName("config");
            Element conf = (Element) config.item(0);

            // Récupération des paramètres de base
            String language = conf.getElementsByTagName("language").item(0).getTextContent();
            Element base = (Element) conf.getElementsByTagName("base").item(0);
            String serveur = base.getElementsByTagName("serveur").item(0).getTextContent();
            String database = base.getElementsByTagName("database").item(0).getTextContent();
            String table = base.getElementsByTagName("table").item(0).getTextContent();
            String user = base.getElementsByTagName("user").item(0).getTextContent();
            String password = base.getElementsByTagName("password").item(0).getTextContent();

            // Récupération des paramètres de chemin
            Element paths = (Element) conf.getElementsByTagName("paths").item(0);
            String path = paths.getElementsByTagName("path").item(0).getTextContent();
            String templ_p = paths.getElementsByTagName("template").item(0).getTextContent();
            String templ_c = paths.getElementsByTagName("template_c").item(0).getTextContent();
            String packages = paths.getElementsByTagName("package").item(0).getTextContent();
            String outputDirectory = paths.getElementsByTagName("outputDirectory").item(0).getTextContent();

            // Appel de la méthode aboutThisTable pour récupérer les informations sur la table
            // La connexion est passée en tant que null pour indiquer que la connexion doit être établie à l'intérieur de la méthode
            ArrayList<ReaderSql> read = new ReaderSql().aboutThisTable(language, serveur, packages, table, null, database, user, password);

            // Appel de la méthode de génération pour écrire les informations dans les fichiers de sortie
            new Writing().generation(read, path, language, templ_p);

            ControllerGenerator cg = new ControllerGenerator();
            cg.generation(read ,packages, language, templ_c, outputDirectory);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
