package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "Biswas0175";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);
        String loggedInGmail = null; // Gmail login session

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);

            while (true) {
                if (loggedInGmail == null) {
                    System.out.println("=== LOGIN ===");
                    System.out.print("Enter your Gmail: ");
                    String email = scanner.next();
                    if (email.endsWith("@gmail.com")) {
                        loggedInGmail = email;
                        System.out.println("✅ Logged in as: " + loggedInGmail + "\n");
                    } else {
                        System.out.println("❌ Only Gmail addresses are allowed!\n");
                        continue;
                    }
                }

                // Menu after login
                System.out.println("HOSPITAL MANAGEMENT SYSTEM (Logged in as: " + loggedInGmail + ")");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. Search Patient by ID");
                System.out.println("4. View Doctors");
                System.out.println("5. Book Appointment");
                System.out.println("6. Remove Patient");
                System.out.println("7. Update Patient Info");
                System.out.println("8. View Appointment History");
                System.out.println("9. Delete Appointment");
                System.out.println("10. Logout");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        patient.addPatient();
                        break;
                    case 2:
                        patient.viewPatients();
                        break;
                    case 3:
                        patient.searchPatientById();
                        break;
                    case 4:
                        doctor.viewDoctors();
                        break;
                    case 5:
                        bookAppointment(patient, doctor, connection, scanner);
                        break;
                    case 6:
                        patient.removePatient();
                        break;
                    case 7:
                        patient.updatePatientInfo();
                        break;
                    case 8:
                        viewAppointmentHistory(connection);
                        break;
                    case 9:
                        deleteAppointment(connection, scanner);
                        break;
                    case 10:
                        System.out.println("👋 Logged out successfully.\n");
                        loggedInGmail = null;
                        break;
                    default:
                        System.out.println("❌ Invalid choice. Try again.");
                        break;
                }
                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner){
        System.out.print("Enter Patient Id: ");
        int patientId = scanner.nextInt();
        System.out.print("Enter Doctor Id: ");
        int doctorId = scanner.nextInt();
        System.out.print("Enter appointment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();
        if(patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)){
            if(checkDoctorAvailability(doctorId, appointmentDate, connection)){
                String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?, ?, ?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if(rowsAffected>0){
                        System.out.println("Appointment Booked!");
                    }else{
                        System.out.println("Failed to Book Appointment!");
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }else{
                System.out.println("Doctor not available on this date!!");
            }
        }else{
            System.out.println("Either doctor or patient doesn't exist!!!");
        }
    }
    public static void viewAppointmentHistory(Connection connection) {
        String query = "SELECT a.id, p.name AS patient_name, d.name AS doctor_name, a.appointment_date " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.id " +
                "JOIN doctors d ON a.doctor_id = d.id " +
                "ORDER BY a.appointment_date DESC";

        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            System.out.println("Appointment History:");
            System.out.println("+----------------+--------------------+--------------------+--------------------+");
            System.out.println("| Appointment ID | Patient Name       | Doctor Name        | Appointment Date   |");
            System.out.println("+----------------+--------------------+--------------------+--------------------+");

            while (rs.next()) {
                int id = rs.getInt("id");
                String patient = rs.getString("patient_name");
                String doctor = rs.getString("doctor_name");
                String date = rs.getString("appointment_date");
                System.out.printf("| %-14d | %-18s | %-18s | %-18s |\n", id, patient, doctor, date);
                System.out.println("+----------------+--------------------+--------------------+--------------------+");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void deleteAppointment(Connection connection, Scanner scanner) {
        System.out.print("Enter Appointment ID to delete: ");
        int appointmentId = scanner.nextInt();

        String query = "DELETE FROM appointments WHERE id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, appointmentId);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Appointment deleted successfully!");
            } else {
                System.out.println("❌ Appointment ID not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection){
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count==0){
                    return true;
                }else{
                    return false;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}