package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class Patient {
    private Connection connection;
    private Scanner scanner;

    public Patient(Connection connection, Scanner scanner){
        this.connection = connection;
        this.scanner = scanner;
    }
      public void addPatient(){
          System.out.println("Enter Patient Name: ");
          String name = scanner.next();
          System.out.println("Enter Patient Age: ");
          int age = scanner.nextInt();
          System.out.println("Enter Patient Gender: ");
          String gender = scanner.next();

          try{
              String query = "INSERT INTO patients(name, age, gender) VALUES(?, ?, ?)";
              PreparedStatement preparedStatement = connection.prepareStatement(query);
              preparedStatement.setString(1, name);
              preparedStatement.setInt(2, age);
              preparedStatement.setString(3, gender);
              int affectedRows = preparedStatement.executeUpdate();
              if(affectedRows>0){
                  System.out.println("Patient added successfully!!!");
              }else{
                  System.out.println("Failed to add Patient!!!");
              }

          }
          catch (SQLException e){
              e.printStackTrace();
          }
      }

    public void viewPatients(){
        String query = "select * from patients";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("Patients: ");
            System.out.println("+------------+--------------------+----------+------------+");
            System.out.println("| Patient Id | Name               | Age      | Gender     |");
            System.out.println("+------------+--------------------+----------+------------+");
            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String gender = resultSet.getString("gender");
                System.out.printf("| %-10s | %-18s | %-8s | %-10s |\n", id, name, age, gender);
                System.out.println("+------------+--------------------+----------+------------+");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public boolean getPatientById(int id){
        String query = "SELECT * FROM patients WHERE id = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }else{
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    public void removePatient() {
        System.out.print("Enter Patient ID to remove: ");
        int id = scanner.nextInt();

        String query = "DELETE FROM patients WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Patient removed successfully!");
            } else {
                System.out.println("Patient not found or already removed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void updatePatientInfo() {
        System.out.print("Enter Patient ID to update: ");
        int id = scanner.nextInt();

        if (!getPatientById(id)) {
            System.out.println("❌ Patient not found!");
            return;
        }

        System.out.print("Enter new name: ");
        String name = scanner.next();
        System.out.print("Enter new age: ");
        int age = scanner.nextInt();
        System.out.print("Enter new gender: ");
        String gender = scanner.next();

        String query = "UPDATE patients SET name = ?, age = ?, gender = ? WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, age);
            preparedStatement.setString(3, gender);
            preparedStatement.setInt(4, id);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Patient info updated successfully!");
            } else {
                System.out.println("❌ Failed to update patient info.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void searchPatientById() {
        System.out.print("Enter Patient ID to search: ");
        int id = scanner.nextInt();

        String query = "SELECT * FROM patients WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("+------------+--------------------+----------+------------+");
                System.out.println("| Patient Id | Name               | Age      | Gender     |");
                System.out.println("+------------+--------------------+----------+------------+");
                int patientId = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String gender = rs.getString("gender");

                System.out.printf("| %-10s | %-18s | %-8s | %-10s |\n", patientId, name, age, gender);
                System.out.println("+------------+--------------------+----------+------------+");
            } else {
                System.out.println("❌ Patient not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

