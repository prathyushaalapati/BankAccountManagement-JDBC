
import java.sql.*;
import java.util.Scanner;

public class BankAccount {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        String url = "jdbc:oracle:thin:@localhost:1521/FREE";
        String user = "system";
        String password = "Prathyu@7";

        try {
            Class.forName("oracle.jdbc.OracleDriver");

            Connection con = DriverManager.getConnection(
                    url, user, password);

            System.out.println("Database connected successfully!");

            Statement st = con.createStatement();

            st.executeUpdate(
                    "CREATE TABLE BANK_ACCOUNT (" +
                            "ACCOUNT_NO NUMBER PRIMARY KEY, " +
                            "CUSTOMER_NAME VARCHAR2(50), " +
                            "BALANCE NUMBER(12,2))");

            System.out.println("Table created successfully!");

        } catch (SQLException e) {
            if (e.getErrorCode() == 955) {
                System.out.println("Table already exists!");
            } else {
                System.out.println(e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found!");
        }

        try {
            Class.forName("oracle.jdbc.OracleDriver");

            Connection con = DriverManager.getConnection(
                    url, user, password);

            int choice;

            do {
                System.out.println("\n--- BANK ACCOUNT MENU ---");
                System.out.println("1. Create Account");
                System.out.println("2. Deposit Money");
                System.out.println("3. Withdraw Money");
                System.out.println("4. Display Balance");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");

                choice = sc.nextInt();

                switch (choice) {

                    case 1:
                        System.out.print("Enter Account Number: ");
                        int accNo = sc.nextInt();

                        sc.nextLine();

                        System.out.print("Enter Customer Name: ");
                        String name = sc.nextLine();

                        System.out.print("Enter Initial Balance: ");
                        double balance = sc.nextDouble();

                        if (balance < 0) {
                            System.out.println(
                                    "Balance cannot be negative!");
                            break;
                        }

                        String insert = "INSERT INTO BANK_ACCOUNT " +
                                "VALUES (?, ?, ?)";

                        try (PreparedStatement ps = con.prepareStatement(insert)) {

                            ps.setInt(1, accNo);
                            ps.setString(2, name);
                            ps.setDouble(3, balance);

                            ps.executeUpdate();

                            System.out.println(
                                    "Account created successfully!");
                        } catch (SQLException e) {
                            if (e.getErrorCode() == 1) {
                                System.out.println(
                                        "Account number already exists!");
                            } else {
                                System.out.println(e.getMessage());
                            }
                        }
                        break;

                    case 2:
                        System.out.print("Enter Account Number: ");
                        accNo = sc.nextInt();

                        System.out.print("Enter Deposit Amount: ");
                        double deposit = sc.nextDouble();

                        if (deposit <= 0) {
                            System.out.println(
                                    "Enter a positive amount!");
                            break;
                        }

                        String depositSQL = "UPDATE BANK_ACCOUNT " +
                                "SET BALANCE = BALANCE + ? " +
                                "WHERE ACCOUNT_NO = ?";

                        try (PreparedStatement ps = con.prepareStatement(depositSQL)) {

                            ps.setDouble(1, deposit);
                            ps.setInt(2, accNo);

                            int rows = ps.executeUpdate();

                            if (rows > 0) {
                                System.out.println(
                                        "Money deposited successfully!");
                            } else {
                                System.out.println("Account not found!");
                            }
                        }
                        break;

                    case 3:
                        System.out.print("Enter Account Number: ");
                        accNo = sc.nextInt();

                        System.out.print("Enter Withdrawal Amount: ");
                        double withdraw = sc.nextDouble();

                        if (withdraw <= 0) {
                            System.out.println(
                                    "Enter a positive amount!");
                            break;
                        }

                        String withdrawSQL = "UPDATE BANK_ACCOUNT " +
                                "SET BALANCE = BALANCE - ? " +
                                "WHERE ACCOUNT_NO = ? " +
                                "AND BALANCE >= ?";

                        try (PreparedStatement ps = con.prepareStatement(withdrawSQL)) {

                            ps.setDouble(1, withdraw);
                            ps.setInt(2, accNo);
                            ps.setDouble(3, withdraw);

                            int rows = ps.executeUpdate();

                            if (rows > 0) {
                                System.out.println(
                                        "Money withdrawn successfully!");
                            } else {
                                System.out.println(
                                        "Account not found or insufficient balance!");
                            }
                        }
                        break;

                    case 4:
                        System.out.print("Enter Account Number: ");
                        accNo = sc.nextInt();

                        String selectSQL = "SELECT * FROM BANK_ACCOUNT " +
                                "WHERE ACCOUNT_NO = ?";

                        try (PreparedStatement ps = con.prepareStatement(selectSQL)) {

                            ps.setInt(1, accNo);

                            try (ResultSet rs = ps.executeQuery()) {

                                if (rs.next()) {
                                    System.out.println(
                                            "Account Number: "
                                                    + rs.getInt("ACCOUNT_NO"));

                                    System.out.println(
                                            "Customer Name: "
                                                    + rs.getString("CUSTOMER_NAME"));

                                    System.out.println(
                                            "Available Balance: Rs. "
                                                    + rs.getDouble("BALANCE"));
                                } else {
                                    System.out.println("Account not found!");
                                }
                            }
                        }
                        break;

                    case 5:
                        System.out.println("Thank you!");
                        break;

                    default:
                        System.out.println("Invalid choice!");
                }

            } while (choice != 5);

            con.close();

        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found!");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } finally {
            sc.close();
        }
    }
}
