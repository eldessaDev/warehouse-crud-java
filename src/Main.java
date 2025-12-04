import java.net.ConnectException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        MaterialDAO dao = new MaterialDAO();
        boolean running = true;

        System.out.println("--- WAREHOUSE MANAGEMENT SYSTEM ---");

        while (running){
            System.out.println("\nMenu:");
            System.out.println("1. View Inventory");
            System.out.println("2. Add New Material");
            System.out.println("3. Restock Material (Update Quantity)");
            System.out.println("4. Delete Material");
            System.out.println("5. Exit");
            System.out.println("Select Option: ");

            int option = scanner.nextInt();
            scanner.nextLine(); // Limpiar el Enter Fantasma(Muy Importante!)

            switch (option){
                case 1:
                    System.out.println("--- CURRENT INVENTORY ---");
                    ArrayList<Material> list = dao.getAllMaterials();
                    for (Material m : list){
                        System.out.println(m);
                    }
                    break;
                case 2:
                    System.out.println("--- ADDING NEW MATERIAL ---");

                    System.out.println("Name: ");
                    String name  = scanner.nextLine();

                    System.out.println("Brand: ");
                    String brand = scanner.nextLine();

                    System.out.println("Quantity: ");
                    int quantity = scanner.nextInt();

                    System.out.println("Price: ");
                    double price = scanner.nextDouble();

                    dao.addMaterial(0, name, brand, quantity, price);
                    break;
                case 3:
                    System.out.println("--- RESTOCKING ---");
                    System.out.println("Enter Material ID: ");
                    int idUpdate = scanner.nextInt();

                    System.out.println("Enter Quantity to ADD: ");
                    int quantityUpdate = scanner.nextInt();

                    dao.updateStock(idUpdate, quantityUpdate);
                    System.out.println("Material Quantity Updated");
                    break;
                case 4:
                    System.out.println("--- Delete Material ---");
                    System.out.println("Enter The Material ID to DELETE: ");
                    int idDelete = scanner.nextInt();
                    dao.deleteMaterial(idDelete);
                    System.out.println("Material ID = " + idDelete + " has been DELETED" );
                    break;
                case 5:
                    System.out.println("Exiting... Bye!");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid Option");

            }
        }
    }
}

class Material{

    // variables
    int id;
    String name;
    String brand;
    int quantity;
    double  price;

    // constructor
    public Material(int id, String name, String brand, int quantity, double price){
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.quantity = quantity;
        this.price = price;
    }
    // method for converting the object to a string
    public String toString(){
        return "ID: " + id + " | " + name + " | (" + brand + ") | Qty: " + quantity + " | Price: " + price;
    }

}

class MaterialDAO{
    // variables
    String url = "jdbc:mysql://localhost:3306/warehouse";
    String username = "eldessaDev";
    String password = "1983Mysql8689!";

    // GetAll Method With ArrayList
    public ArrayList<Material>getAllMaterials(){
        ArrayList<Material> list = new ArrayList<>();

        try{
            //creating conection object
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM materials");

            while (resultSet.next()){
                // get data from DB
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String brand = resultSet.getString("brand");
                int quantity = resultSet.getInt("quantity");
                double price = resultSet.getDouble("price");

                // creating material object
                Material material = new Material(id, name, brand, quantity, price);

                // add object in list
                list.add(material);
            }
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // addNewMaterial method
    public void addMaterial(int id, String name, String brand, int quantity, double price){

        try {

            Connection connection = DriverManager.getConnection(url, username, password);
            String sql = "INSERT INTO materials(name, brand, quantity, price) VALUES(?,?,?,?)";
            java.sql.PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, name);
            statement.setString(2, brand);
            statement.setInt(3, quantity);
            statement.setDouble(4, price);

            statement.executeUpdate();
            System.out.println("Save employee" + name);
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // updateMaterial method
    public void updateStock(int id, int newQuantity){
        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            String sql = "UPDATE materials set quantity = quantity + ? WHERE id = ?";
            java.sql.PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, newQuantity);
            statement.setInt(2, id);

            int rows = statement.executeUpdate();

            if (rows > 0){
                System.out.println("Quantity Updated Successfully!");
            }else {
                System.out.println("ERROR: Material ID: " + id + " Not Found.");
            }
            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // deleteMaterial method
    public void deleteMaterial(int id){
        try{
            Connection connection = DriverManager.getConnection(url, username, password);
            String sql =  "DELETE FROM materials WHERE id = ?";
            java.sql.PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, id);

            int rows = statement.executeUpdate();

            if(rows > 0){
                System.out.println("Material with ID: " + id + " was DELETED.");
            }else {
                System.out.println("ERROR: Material ID: " + id + " Not Found.");
            }
            connection.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}