## Secure Inventory and Sales Management Application for Local Gym

A standalone desktop application designed to help a local small gym businesses track product inventory and sales without relying on manual counts or disconnected records.

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
Buttons that require certain permissions are disabled for lower level users. <br>
<img height="400" alt="role based access control depict" src="https://github.com/user-attachments/assets/f341e708-5aac-4d42-8443-7fa57e1ca773" />


#### Product Management
Administrative interface for creating and maintaining product catalog entries. <br>
<img height="400" alt="manage products" src="https://github.com/user-attachments/assets/862ed330-b276-4a97-ba73-b2b74a75e369" />


#### Sales Entry Screen
Simplified interface used by staff to record product sales. <br>
<img height="400" alt="record sale" src="https://github.com/user-attachments/assets/d418b3f0-e873-4f25-b927-e5fc62bef7a0" />



#### Inventory Adjustment Screen
Inventory adjustment screen with reason codes. <br>
<img height="400" alt="adjust inventory" src="https://github.com/user-attachments/assets/a6070ff4-268d-4dfd-a131-2a4e1efbd181" />


#### User Management
Interface used by administrators to manage system users and permissions. <br>
<img height="400" alt="manage users" src="https://github.com/user-attachments/assets/985031f4-c498-420f-9070-425679d02dfc" />


#### Database Tables (SQLite)
Example showing created database tables. <br>
<img height="400" alt="database tables" src="https://github.com/user-attachments/assets/fc362a9f-fa6c-435e-9ad1-e504dd186eca" />


#### Audit Logging
Example showing audit log entries recording system actions. <br>
<img height="400" alt="database logging" src="https://github.com/user-attachments/assets/2b81221b-3166-4778-be6c-5efdd0827896" />

