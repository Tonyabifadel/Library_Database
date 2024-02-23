package project;

import java.sql.*;   
import java.util.Scanner;

public class Library {

	private Connection connecting = null;
	private Statement statement = null;
	private Connect connect = new Connect();
	private static ResultSet query = null;

	public void createStatement() {
		
		try {
			connecting = connect.registerDriver();
			statement = connecting.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	

public void addBook() {
    Scanner scanner = new Scanner(System.in);

    boolean validName = false;
    boolean validPrice = false;

    String bookName = null;
    double quantity = 0;
    double price = 0;

    while (!validName) {
        System.out.print("Enter the name of the book: ");
        bookName = scanner.nextLine();

        // Check if a book with the same name exists
        String checkExistenceQuery = "SELECT * FROM books WHERE name = ?";
        try (PreparedStatement checkExistenceStatement = connecting.prepareStatement(checkExistenceQuery)) {
            checkExistenceStatement.setString(1, bookName);
            query = checkExistenceStatement.executeQuery();

            if (query.next()) {
                System.out.println("A book with the name '" + bookName + "' already exists. Please choose a different name.");
            } else {
                validName = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    while (!validPrice) {
        System.out.print("Enter the quantity of the book: ");
        quantity = scanner.nextDouble();

        System.out.print("Enter the price of the book: ");
        price = scanner.nextDouble();

        // Check if the price is greater than 0
        if (price > 0) {
            validPrice = true;
        } else {
            System.out.println("Invalid price. Price must be greater than 0.");
        }
    }

    // Add the book to the database
    String addBookQuery = "INSERT INTO books (name, quantity, price) VALUES (?, ?, ?)";
    try (PreparedStatement addBookStatement = connecting.prepareStatement(addBookQuery)) {
        addBookStatement.setString(1, bookName);
        addBookStatement.setDouble(2, quantity);
        addBookStatement.setDouble(3, price);

        int rowsAffected = addBookStatement.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Book '" + bookName + "' added successfully.");
        } else {
            System.out.println("Failed to add the book. Please try again.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}




	public void searchBookByName() {
	    Scanner scanner = new Scanner(System.in);
	    System.out.print("Enter the name of the book to search: ");
	    String bookName = scanner.nextLine();

	    String searchQuery = "SELECT * FROM books WHERE name LIKE ?";
	    try (PreparedStatement preparedStatement = connecting.prepareStatement(searchQuery)) {
	        preparedStatement.setString(1, "%" + bookName + "%");

	        query = preparedStatement.executeQuery();

	        while (query.next()) {
	            int id = query.getInt("id");
	            String name = query.getString("name");
	            double quantity = query.getDouble("quantity");
	            double price = query.getDouble("price");

	            System.out.println("Book ID: " + id + ", Name: " + name + ", Quantity: " + quantity + ", Price: $" + price);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	public void updateBook() {
	    Scanner scanner = new Scanner(System.in);

	    System.out.print("Enter the ID of the book to update: ");
	    int bookIdToUpdate = scanner.nextInt();
	    scanner.nextLine(); // Consume the newline character

	    System.out.print("Enter the new quantity: ");
	    double newQuantity = scanner.nextDouble();

	    System.out.print("Enter the new price: ");
	    double newPrice = scanner.nextDouble();

	    String updateQuery = "UPDATE books SET quantity = ?, price = ? WHERE id = ?";
	    try (PreparedStatement preparedStatement = connecting.prepareStatement(updateQuery)) {
	        preparedStatement.setDouble(1, newQuantity);
	        preparedStatement.setDouble(2, newPrice);
	        preparedStatement.setInt(3, bookIdToUpdate);

	        int rowsAffected = preparedStatement.executeUpdate();

	        if (rowsAffected > 0) {
	            System.out.println("Book with ID " + bookIdToUpdate + " updated successfully.");
	        } else {
	            System.out.println("Book with ID " + bookIdToUpdate + " not found.");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	public void deleteBook() {
	    Scanner scanner = new Scanner(System.in);

	    System.out.print("Enter the ID of the book to delete: ");
	    int bookIdToDelete = scanner.nextInt();
	    scanner.nextLine(); // Consume the newline character

	    String selectQuery = "SELECT * FROM books WHERE id = ?";
	    String deleteQuery = "DELETE FROM books WHERE id = ?";

	    try (PreparedStatement selectStatement = connecting.prepareStatement(selectQuery);
	         PreparedStatement deleteStatement = connecting.prepareStatement(deleteQuery)) {

	        selectStatement.setInt(1, bookIdToDelete);

	        query = selectStatement.executeQuery();

	        if (query.next()) {
	            int id = query.getInt("id");
	            String name = query.getString("name");
	            double quantity = query.getDouble("quantity");
	            double price = query.getDouble("price");

	            System.out.println("Book ID: " + id + ", Name: " + name + ", Quantity: " + quantity + ", Price: $" + price);

	            // Confirm deletion
	            System.out.print("Are you sure you want to delete this book? (yes/no): ");
	            String confirmation = scanner.nextLine().toLowerCase();

	            if (confirmation.equals("yes")) {
	                deleteStatement.setInt(1, bookIdToDelete);
	                int rowsAffected = deleteStatement.executeUpdate();

	                if (rowsAffected > 0) {
	                    System.out.println("Book with ID " + bookIdToDelete + " deleted successfully.");
	                } else {
	                    System.out.println("Book with ID " + bookIdToDelete + " not found.");
	                }
	            } else {
	                System.out.println("Deletion canceled.");
	            }
	        } else {
	            System.out.println("Book with ID " + bookIdToDelete + " not found.");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	
	//display the product and name of each book
	public void displayAllBooks() {
	    String displayQuery = "SELECT name, quantity FROM books";
	    String totalBooksQuery = "SELECT COUNT(*) as total_books FROM books";
	    String maxQuantityBookQuery = "SELECT name, quantity FROM books WHERE quantity = (SELECT MAX(quantity) FROM books)";

	    try {
	        // Display all books
	        query = statement.executeQuery(displayQuery);

	        System.out.println("~~~~~ All Books ~~~~~");
	        while (query.next()) {
	            String name = query.getString("name");
	            double quantity = query.getDouble("quantity");

	            System.out.println("Book: " + name + ", Quantity: " + quantity);
	        }
	        System.out.println("~~~~~~~~~~~~~~~~~~~~~");

	        // Display total number of books
	        ResultSet totalBooksResult = statement.executeQuery(totalBooksQuery);
	        if (totalBooksResult.next()) {
	            int totalBooks = totalBooksResult.getInt("total_books");
	            System.out.println("Total number of books: " + totalBooks);
	        }

	        // Display the book with the most quantity
	        ResultSet maxQuantityBookResult = statement.executeQuery(maxQuantityBookQuery);
	        if (maxQuantityBookResult.next()) {
	            String maxQuantityBookName = maxQuantityBookResult.getString("name");
	            double maxQuantity = maxQuantityBookResult.getDouble("quantity");
	            System.out.println("Book with the most quantity: " + maxQuantityBookName + ", Quantity: " + maxQuantity);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	
	
    public void deleteUser() {
        Scanner scanner = new Scanner(System.in);

        // Get user input
        System.out.println("\n---> Please enter your email:");
        String email = scanner.nextLine();
        System.out.println("---> Please enter your password:");
        String password = scanner.nextLine();
        String hashedPassword = hashing(password);

        // Check if the user exists
        String checkUserQuery = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (PreparedStatement checkUserStatement = connecting.prepareStatement(checkUserQuery)) {
            checkUserStatement.setString(1, email);
            checkUserStatement.setString(2, hashedPassword);

            query= checkUserStatement.executeQuery();

            if (query.next()) {
                // User found, proceed with deletion
                String deleteUserQuery = "DELETE FROM users WHERE email = ? AND password = ?";
                try (PreparedStatement deleteUserStatement = connecting.prepareStatement(deleteUserQuery)) {
                    deleteUserStatement.setString(1, email);
                    deleteUserStatement.setString(2, hashedPassword);

                    int rowsAffected = deleteUserStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("User with email '" + email + "' deleted successfully.");
                    } else {
                        System.out.println("Failed to delete user. Please try again.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                // User not found
                System.out.println("User with the provided email and password not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
	public String hashing(String password) {
		
		String hashed_pass = "hsa23";
		
		for(int i = 0;i<password.length();i++) {
			if(Character.isUpperCase(password.charAt(i))) {
				hashed_pass += (int) password.charAt(i) + 30 + "?"; 
			}
			else {
				hashed_pass += (int) password.charAt(i) + 10 + "&"; 

			}
			
		}
		
		hashed_pass += "reda";
		return hashed_pass;
		
	}
	
	 public void viewAllManagers() {
	        String manager = "SELECT * FROM managers";

	        try {
	             query = statement.executeQuery(manager);

	            System.out.println("~~~~~ All Managers ~~~~~");
	            while (query.next()) {
	                int managerId = query.getInt("manager_id");
	                String username = query.getString("username");
	                String email = query.getString("email");
	                int departmentId =query.getInt("department_id");
	                String address = query.getString("address");

	                System.out.println("Manager ID: " + managerId +
	                        ", Username: " + username +
	                        ", Email: " + email +
	                        ", Department ID: " + departmentId +
	                        ", Address: " + address);
	            }
	            System.out.println("~~~~~~~~~~~~~~~~~~~~~");

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    public void searchManager() {
	        Scanner scanner = new Scanner(System.in);

	        System.out.print("Enter username or email of the manager you are searching for: ");
	        String searchInput = scanner.nextLine();

	        String searchQuery = "SELECT * FROM managers WHERE username = ? OR email = ?";
	        try (PreparedStatement preparedStatement = connecting.prepareStatement(searchQuery)) {
	            preparedStatement.setString(1, searchInput);
	            preparedStatement.setString(2, searchInput);

	            query = preparedStatement.executeQuery();

	            System.out.println("~~~~~ Search Results ~~~~~");
	            while (query.next()) {
	                int managerId =  query.getInt("manager_id");
	                String username =  query.getString("username");
	                String email =  query.getString("email");
	                int departmentId =  query.getInt("department_id");
	                String address =  query.getString("address");

	                System.out.println("Manager ID: " + managerId +
	                        ", Username: " + username +
	                        ", Email: " + email +
	                        ", Department ID: " + departmentId +
	                        ", Address: " + address);
	            }
	            System.out.println("~~~~~~~~~~~~~~~~~~~~~");

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	
	
	//rewrite it
public boolean authenticate() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("_____{Select Your Option}_____\r\n" +
                           "1) Log in.\r\n" +
                           "2) Sign up.\r\n" +
                           "______________________________");

        int choice = getUserChoice(scanner);

        if (choice == 1) {
            boolean loggedIn = logInUser(scanner);
            if (loggedIn) {
                return true;
            }
        } else if (choice == 2) {
            boolean signedUp = signUpUser(scanner);
            if (signedUp) {
                return true;
            }
        }

        return false;
    }

    private int getUserChoice(Scanner scanner) {
        int choice;

        do {
            System.out.print("Enter your choice (1-2): ");
            choice = scanner.nextInt();

            if (choice != 1 && choice != 2) {
                System.out.println("\n---> Invalid Input. Try again.\n");
            }
        } while (choice != 1 && choice != 2);

        return choice;
    }

    private boolean logInUser(Scanner scanner) {
        int maxAttempts = 5;
        int attempts = 0;

        while (attempts < maxAttempts) {
            attempts++;

            System.out.println("\n---> Please enter your email:");
            String email = scanner.next();

            System.out.println("\n---> Please enter your password:");
            String password = scanner.next();
            String hashedPassword = hashing(password);

            String queryy = "SELECT username FROM users WHERE email = ? AND password = ?";
            
            try (PreparedStatement preparedStatement = connecting.prepareStatement(queryy)) {
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, hashedPassword);

                query = preparedStatement.executeQuery();

                if (query.next()) {
                    String name = query.getString("username");
                    System.out.println("____________________");
                    System.out.println("\n~~{ Greetings! }~~\n");
                    System.out.println("---> You are logged in as: " + name);
                    return true;
                } else {
                    System.out.println("\n**Invalid Email or Password. Try again.**");
                    int chances = maxAttempts - attempts;
                    System.out.println("  Chances: " + chances);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private boolean signUpUser(Scanner scanner) {
        int maxAttempts = 5;
        int attempts = 0;

        while (attempts < maxAttempts) {
            attempts++;

            System.out.println("\n---> Please enter your full name:");
            String name = scanner.next();

            System.out.println("\n---> Please enter your email:");
            String email = scanner.next();

            if (isEmailAlreadyExists(email)) {
                System.out.println("\n**This email already exists. Try again.**");
                int chances = maxAttempts - attempts;
                System.out.println("  Chances: " + chances);
                continue;
            }

            System.out.println("\n---> Please enter your password:");
            String password = scanner.next();
            String hashedPassword = hashing(password);

            System.out.println("\n---> Please enter your address:");
            String address = scanner.next();

            String insertQuery = "INSERT INTO users (username, email, password, address) VALUES (?, ?, ?, ?)";

            try (PreparedStatement insertStatement = connecting.prepareStatement(insertQuery)) {
                insertStatement.setString(1, name);
                insertStatement.setString(2, email);
                insertStatement.setString(3, hashedPassword);
                insertStatement.setString(4, address);

                int rowsAffected = insertStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("\n---> You have successfully signed up! Logging in...");
                    System.out.println("____________________");
                    System.out.println("\n~~{ Greetings! }~~\n");
                    System.out.println("---> You are logged in as: " + name);
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private boolean isEmailAlreadyExists(String email) {
        String checkEmailQuery = "SELECT username FROM users WHERE email = ?";

        try (PreparedStatement checkEmailStatement = connecting.prepareStatement(checkEmailQuery)) {
            checkEmailStatement.setString(1, email);
            ResultSet resultSet = checkEmailStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
public void displayMenu() {
    Scanner scanner = new Scanner(System.in);
    int mistakeCounter = 0;
    while (true) {
        // Display menu
        System.out.println("~~~~~ Library Management System ~~~~~");
        System.out.println("1. Display All Books");
        System.out.println("2. Search Manager");
        System.out.println("3. View All Managers");
        System.out.println("4. Add Book");
        System.out.println("5. Delete Book");
        System.out.println("6. Exit");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        // Get user's choice
        System.out.print("Enter your choice (1-6): ");
        int choice = scanner.nextInt();

        // Reset mistake counter when user chooses correctly
        if (isValidChoice(choice)) {
            mistakeCounter = 0;
        } else {
            mistakeCounter++;
            System.out.println("Invalid choice. Please enter a number between 1 and 6.");
        }

        // Check if user has made too many mistakes
        if (mistakeCounter >= 5) {
            System.out.println("Too many mistakes. Exiting. Thank you!");
            System.exit(0);
        }

        // Use switch case to execute corresponding functionality
        switch (choice) {
            case 1:
                displayAllBooks();
                break;
            case 2:
                searchManager();
                break;
            case 3:
                viewAllManagers();
                break;
            case 4:
                addBook();
                break;
            case 5:
                deleteBook();
                break;
            case 6:
                System.out.println("Exiting. Thank you!");
                System.exit(0);
            default:
                // This part is now handled above to increment the mistake counter
                break;
        }
    }
}

private boolean isValidChoice(int choice) {
    return choice >= 1 && choice <= 6;
}


	public static void main(String[] args) {
		
		Library l = new Library();
		l.createStatement();

		if(l.authenticate()) {
			l.displayMenu();
	
		
		}
		
	}
}
