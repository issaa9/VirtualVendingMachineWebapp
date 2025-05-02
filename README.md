Virtual Vending Machine Webapp

This Repository contains the entire Project code folder, that has been consistently updated through commits.

# Virtual Vending Machine Web Application

This is a full-stack virtual vending machine simulation system, featuring real-time product selection, cart and checkout, coin/card payments (via Stripe), smart recommendations,transactions history viewing, analytics, and admin stock controls. 
It is built with **Java Spring Boot**, **HTML (with Thymeleaf),CSS,JS**, and **MySQL**, with user-friendly front-end interfaces.

---

## Contents

- Full source code (Java/Spring Boot + Thymeleaf rendered UI)
- `build.gradle` (Gradle build configuration)
- HTML/CSS/JS front-end resources
- `application.properties` for configuration
- `data.sql` for initial database data
- `User Manual` (PDF)
- This README file

---

## Requirements

| Tool / Component     | Requirement                          |
|----------------------|---------------------------------------|
| **Java**             | JDK 17 or later                      |
| **Gradle**           | Bundled wrapper OR pre-installed     |
| **MySQL Server**     | Local DB connection with `vending_machine` DB   |
| **Web Browser**      | Modern browser (Chrome, Firefox, etc.) with JS enabled|
| **Operating System** | Windows 10+, macOS, Linux  |

Optional:
- Google account (for login via OAuth)
- Stripe test credentials (already integrated in test mode)

---

##  Setup Instructions

 1. Download the Project zip File

Extract the provided `.zip` or clone via Git:
 
 2. Open in a suitable IDE 

 e.g. IntelliJ IDEA, Eclipse 

 3. Setup MySQL connection

 Create a new local connection in MySQL and ensure it is connected to the IDE

 4. Check credentials

 If using 'root' as the username for the connection no action is needed. 
 If using a different username then update in 'application.properties': spring.datasource.username={yourusername}

 5. Initialise Environment Variables (I had to omit key passwords/keys/IDs for security)

 In the IDE set environment variables:

 DB_PASSWORD = (yourpassword)  [Required]

 GOOGLE_CLIENT_ID = (yourgoogleclientid)
 GOOGLE_CLIENT_SECRET=( yourgoogleclientsecret)
 [Optional, only needed to use Google OAuth2 login feature]


 6. Create the database 'vending_machine'

SQL Statement: "CREATE DATABASE vending_machine;"

OR if you want to use a database with a different name then modify 'application.properties':
spring.datasource.url=jdbc:mysql://localhost:3306/(yourdatabasename) [Not Recommended]


 7. Build and Run

 Build using gradle and run the main application ('FinalYearProjectApplication') locally


 8. Visit http://localhost:8080/login to get started


##  Project Details

 Login Options
Guest Mode (limited features): "Continue as Guest"

User Mode: Register with username/email/password

Google OAuth2: Available with your own Google client credentials set up in Environment Variables

Admin Credentials: 
Username: admin
Password: admins


 Key Features
-Full vending machine simulation

-Dynamic cart, checkout, real-time stock tracking

-Coin-based payment (with animations) and Stripe checkout (test mode) card payments

-Smart Recommendations (from a smart algorithm) with visual effects and hover-based explanations  

-Visual analytics dashboard (charts, graphs, summaries)

-Admin-only stock and auto-restocking controls

-View, filter/query, and sort full transaction history

-Receipt viewing and download as PDF

 Stripe Test Mode Notes
-Card Number: 4242 4242 4242 4242

-Any Name, CVC, Postcode, and Future Expiry Date are accepted

-Stripe test mode key (sk_test_...) is hardcoded into 'application.properties' for test transactions (no security concern as its test mode only)



File Overview
**Folder  -->  Folder Description**

src/ -->  Java, HTML, CSS, JS code
data.sql  -->  Populates DB with default products and admin account
application.properties  -->	 Configurations for DB, OAuth2, Stripe and other project things
build.gradle  -->  Gradle project definition
Virtual_Vending_Machine_User_Manual.pdf/ --> Step-by-step instructions/UI walkthrough
README.md  -->  (This) setup and execution guide



  Important Notes
-data.sql is used to insert default product data into the DB when the app starts and also the admin credentials.

-The application is configured to use ddl-auto=update, so schema changes are applied automatically.

-Avoided pushing sensitive data environment variables to any public repository.



 Troubleshooting
-Ensure the MySQL service is running and the vending_machine DB exists

-If login fails, ensure GOOGLE_CLIENT_ID and GOOGLE_CLIENT_SECRET are correctly set or use other login methods

-Use the guest option to bypass authentication for quick runthroughs and tests (remember not all features are accessible to Guests)


 Final Notes for Submission
This project contains:

All source code

Executable JAR

Configuration (build.gradle)

Database automatic populating via data.sql

Full user documentation


 Contact
Developer: Issa Aboobaker
Email: ia252@student.le.ac.uk
Institution: University of Leicester




