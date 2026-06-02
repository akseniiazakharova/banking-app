# 🏦 BankApp

A desktop banking application developed as a study project. The application serves as a client management and banking operations tool.

---

## 🛠 Tech Stack & Tools

* **Java 17** (OOP principles: Abstract classes, Inheritance, Interfaces, Polymorphism)
* **Java Swing** — Graphical User Interface (`JFrame`, `JPanel`, `JTable`, `JList`)
* **PostgreSQL** — Relational database (Transactions, `FOREIGN KEY` constraints)
* **JDBC** — Database connectivity (`PreparedStatement`, `ResultSet`)
* **Maven** — Dependency and build management
* **Architecture**: DAO (Data Access Object) Pattern

---

## 🚀 Features

* ✅ **Client Management:** Register and search bank clients.
* ✅ **Account Operations:** Open and manage checking and savings accounts.
* ✅ **Transactions:** Deposit and withdraw funds with data consistency.
* ✅ **Transaction History:** Track and view a complete log of financial operations.

---

## 📂 Project Structure

src/main/java/com/bankapp/
├── model/       # Domain models (Customer, Account, Transaction)
├── dao/         # Data Access Object layer (CustomerDao, AccountDao)
├── db/          # Database connection manager
├── ui/          # Swing GUI components (MainFrame, CustomerPanel)
└── util/        # Helper classes and configuration loaders
