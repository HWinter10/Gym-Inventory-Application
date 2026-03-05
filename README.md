--IN PROGRESS--
Secure Inventory and Sales Management Application

This project is a standalone desktop application designed to replace manual inventory tracking and disconnected sales records for a small business environment. The system centralizes product data, automates inventory deductions during transactions, and restricts sensitive operations using role-based access control.

The application is built using Java, JavaFX, and SQLite and follows a layered architecture separating the user interface, business logic, and data access components. Administrative actions such as inventory adjustments and user management are logged to maintain accountability and traceability.

Key features include:

• Role-based access control for owner, employee, and restricted users
• Secure authentication with hashed passwords
• Inventory tracking with automatic quantity updates during sales
• Administrative inventory adjustments with audit logging
• Local SQLite database for lightweight data persistence
• Modular layered architecture to improve maintainability

Technologies used:

Java
JavaFX
SQLite
JDBC
Maven

UML Class Architecture:
<img width="1243" height="1160" alt="M4 Class-diagram" src="https://github.com/user-attachments/assets/8e5e0aaf-94c7-4cb6-b0ea-26a9f4620a33" />


UML State-User:
<img width="624" height="346" alt="M4 user-state-diagram" src="https://github.com/user-attachments/assets/0e452238-0c5f-48a5-b85c-21c5784944ce" />


UML State-Inventory Object:
<img width="689" height="349" alt="M4 inventory-state-diagram" src="https://github.com/user-attachments/assets/ca8e69a7-721d-4aa4-bbd9-8d0df432ed71" />
