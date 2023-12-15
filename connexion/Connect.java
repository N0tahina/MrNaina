package connexion ;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class Connect {
    Connection connection_choisi;
    public Connect(){}
    public Connection Connecter(String base_utiliser , String base , String user ,  String password){
       try{
            if(base_utiliser.compareTo("postgres") == 0) {
                this.connection_choisi = Postgres(base, user, password) ;
                System.out.println("👉 postgres");
            }else{
                this.connection_choisi = Mysql(base, user, password) ;
                System.out.println("👉 mysql");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return this.connection_choisi ;
   }
    public Connection Postgres(String base , String user ,  String password){
        Connection con = null ;
        try {
            Class.forName("org.postgresql.Driver");
            con  = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+base , user , password) ;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con ;
    }
    public Connection Mysql(String base, String user, String password){
        Connection con = null ;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+base, user, password);
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur lors du chargement du pilote JDBC : " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion à la base de données : " + e.getMessage());
        }
        return con ;
    }
}
