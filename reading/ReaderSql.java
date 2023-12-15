package reading;
import java.sql.*;
import java.util.*;
import connexion.*;

public class ReaderSql {
    // Déclaration des attributs de la classe
    String package_table;
    String nomTable;
    List<String> importation;
    List<String> name_column;
    List<String> type_column;

    // Constructeur avec paramètres pour initialiser les attributs
    public ReaderSql(String pack, String nomTable, List<String> name_column, List<String> type_column, List<String> lib) {
        this.package_table = pack;
        this.nomTable = nomTable;
        this.name_column = name_column;
        this.type_column = type_column;
        this.importation = lib;
    }

    // Constructeur par défaut
    public ReaderSql() {}

    // Méthode pour récupérer les informations sur une table
    // Retourne une liste de ReaderSql si la table spécifiée est "*", sinon une liste avec un seul élément
    public ArrayList<ReaderSql> aboutThisTable(String techno, String base_using, String packa, String table, Connection connexion, String base, String user, String password) {
        ArrayList<ReaderSql> myreader = new ArrayList<>();
        ReaderSql reader = null;

        try {
            if (connexion == null) {
                connexion = new Connect().Connecter(base_using, base, user, password);
            }

            List<String> tablesToProcess;
            if ("*".equals(table)) {
                // Récupérer toutes les tables si table = "*"
                tablesToProcess = getAllTables(base_using, connexion, base, user, password);
            } else {
                // Sinon, traiter uniquement la table spécifiée
                tablesToProcess = Collections.singletonList(table);
            }

            // Pour chaque table à traiter, récupérer les informations sur les colonnes
            for (String tableName : tablesToProcess) {
                DatabaseMetaData metaData = connexion.getMetaData();
                ResultSet resultSet = metaData.getColumns(null, null, tableName, null);
                List<String> column_reader = new ArrayList<>();
                List<String> type_reader = new ArrayList<>();
                List<String> lib = new ArrayList<>();

                // Parcourir les résultats pour chaque colonne
                while (resultSet.next()) {
                    String nomColonne = resultSet.getString("COLUMN_NAME");
                    int type_columns = resultSet.getInt("DATA_TYPE");
                    column_reader.add(nomColonne);

                    // Déterminer le type de colonne en fonction de la technologie (JAVA ou C#)
                    if (techno.compareToIgnoreCase("JAVA") == 0) {
                        type_reader.add(getTypeForColumn_java(type_columns));
                    } else {
                        System.out.println("atomically");
                        type_reader.add(getTypeForColumn_csharp(type_columns));
                    }

                    // Obtenir les importations nécessaires
                    lib.add(getImporation(techno, type_columns));
                }

                // Créer un objet ReaderSql pour la table en cours
                reader = new ReaderSql(packa, tableName, column_reader, type_reader, lib);
                myreader.add(reader);
                resultSet.close();
            }

            // Fermer la connexion après avoir traité toutes les tables
            connexion.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Retourner la liste des ReaderSql créés
        return myreader;
    }

    // Méthode pour récupérer la liste de toutes les tables de la base de données
    public List<String> getAllTables(String base_using, Connection connexion, String base, String user, String password) {
        List<String> tables = new ArrayList<>();

        try {
            DatabaseMetaData metaData = connexion.getMetaData();
            ResultSet resultSet = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            // Parcourir les résultats pour chaque table
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                System.out.println(tableName);
                tables.add(tableName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Retourner la liste des noms de tables
        return tables;
    }

    // Méthode pour obtenir l'importation nécessaire en fonction de la technologie et du type de colonne
    public String getImporation(String techno, int columntype) throws SQLException {
        if (techno.compareToIgnoreCase("JAVA") == 0) {
            switch (columntype) {
                case Types.DATE:
                    return "java.sql.Date";
                case Types.TIMESTAMP:
                    return "java.time.LocalDateTime";
                default:
                    return "";
            }
        }
        return "";
    }

    // Méthode pour obtenir le type de colonne en Java en fonction du type SQL
    public String getTypeForColumn_java(int columntype) throws SQLException {
        switch (columntype) {
            case Types.BIT:
                return "BIT";
            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.BIGINT:
            case Types.INTEGER:
                return "int";
            case Types.FLOAT:
                return "float";
            case Types.REAL:
            case Types.DOUBLE:
            case Types.NUMERIC:
            case Types.DECIMAL:
                return "double";
            case Types.CHAR:
                return "CHAR";
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                return "String";
            case Types.DATE:
                return "Date";
            case Types.TIME:
                return "TIME";
            case Types.TIMESTAMP:
                return "LocalDateTime";
            case Types.BOOLEAN:
                return "boolean";
            default:
                return "UNKNOWN";
        }
    }

    // Méthode pour obtenir le type de colonne en C# en fonction du type SQL
    public String getTypeForColumn_csharp(int columntype) throws SQLException {
        switch (columntype) {
            case Types.BIT:
                return "BIT";
            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.BIGINT:
            case Types.INTEGER:
                return "int";
            case Types.FLOAT:
                return "float";
            case Types.REAL:
            case Types.DOUBLE:
            case Types.NUMERIC:
            case Types.DECIMAL:
                return "double";
            case Types.CHAR:
                return "CHAR";
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                return "string";
            case Types.DATE:
            case Types.TIMESTAMP:
                return "DateTime";
            case Types.TIME:
                return "TimeSpan";
            case Types.BOOLEAN:
                return "bool";
            default:
                return "UNKNOWN";
        }
    }

    // Méthodes getter et setter pour les attributs de la classe
    public String getPackage_table() {
        return package_table;
    }
    
    public void setPackage_table(String package_table) {
        this.package_table = package_table;
    }

    public String getNomTable() {
        return nomTable;
    }

    public void setNomTable(String nomTable) {
        this.nomTable = nomTable;
    }

    public List<String> getName_column() {
        return name_column;
    }

    public void setName_column(List<String> name_column) {
        this.name_column = name_column;
    }

    public List<String> getType_column() {
        return type_column;
    }

    public void setType_column(List<String> type_column) {
        this.type_column = type_column;
    }

    public List<String> getImportation() {
        return importation;
    }

    public void setImportation(List<String> importation) {
        this.importation = importation;
    }
}
