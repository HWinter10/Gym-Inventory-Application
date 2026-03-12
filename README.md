<Strong>Status:</Strong> IN PROGRESS
<br>
## Secure Inventory and Sales Management Application

A standalone desktop application designed to help small businesses track product inventory and sales without relying on manual counts or disconnected records.

The system centralizes product data, automatically updates inventory when sales occur, and restricts sensitive operations through role based access control.

This project is being developed as a capstone project using a layered architecture that separates the user interface, business logic, and data access components.

### Key features include:
<ul>
  <li>Role-based access control for owner, employee, and restricted users</li>
  <li>Secure authentication with hashed passwords</li>
  <li>Inventory tracking with automatic quantity updates during sales</li>
  <li>Administrative inventory adjustments with audit logging</li>
  <li>Local SQLite database for lightweight data persistence</li>
  <li>Modular layered architecture to improve maintainability</li>
</ul>

### Technologies used:
<ul>
  <li>Java</li>
  <li>JavaFX</li>
  <li>SQLite</li>
  <li>JDBC</li>
  <li>Maven</li>
</ul>


### Application Preview:
#### Role Based Access Control (Restricted User View)
Buttons that require certain permissions are disabled for lower level users.
<img height="350" alt="role based access control" src="https://github.com/user-attachments/assets/52ff91e7-ea70-428d-9231-62b4c05b0d07" />


#### Product Management
Administrative interface for creating and maintaining product catalog entries.
<img height="350" alt="product management" src="https://github.com/user-attachments/assets/dbd7f39e-cca3-46c9-92ce-088458c8b5d2" />


#### Sales Entry Screen
Simplified interface used by staff to record product sales. <br>
<img height="350" alt="sales entry" src="https://github.com/user-attachments/assets/3e19d5af-5766-4770-8304-7438135a3de0" />


#### User Management
Interface used by administrators to manage system users and permissions.
<img height="350" alt="user management" src="https://github.com/user-attachments/assets/ebedd767-7562-4f26-a62d-fb57a4f894f3" />


#### Database Tables (SQLite)
Example showing created database tables.
<img height="350" alt="database tables" src="https://github.com/user-attachments/assets/fc362a9f-fa6c-435e-9ad1-e504dd186eca" />


#### Audit Logging
Example showing audit log entries recording system actions.
<img height="350" alt="database logging" src="https://github.com/user-attachments/assets/2b81221b-3166-4778-be6c-5efdd0827896" />

