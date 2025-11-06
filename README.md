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


## Project Details

**Login Options:**  
- Guest Mode (limited features): *Continue as Guest*  
- User Mode: Register with username/email/password  
- Google OAuth2: Available with your own Google client credentials (set in Environment Variables)  


**Admin Credentials:**  
- Username: `admin`  
- Password: `admins`  

---

### Key Features
- Full vending machine simulation  
- Dynamic cart, checkout, and real-time stock tracking  
- Coin-based payment (with animations) and Stripe checkout *(test mode)* card payments  
- Smart Recommendations (using a smart algorithm) with visual effects and hover-based explanations  
- Visual analytics dashboard *(charts, graphs, summaries)*  
- Admin-only stock management and auto-restocking controls  
- View, filter/query, and sort full transaction history  
- Receipt viewing and PDF download  

---

### Stripe Test Mode Notes
- **Card Number:** `4242 4242 4242 4242`  
- **Any Name, CVC, Postcode, and Future Expiry Date** are accepted  
- **Stripe Test Key:** Hardcoded into `application.properties` for demo purposes *(test mode only, no security risk)*  


## File Overview

| Folder / File | Description |
|----------------|-------------|
| `src/` | Java, HTML, CSS, and JS source code |
| `data.sql` | Populates the database with default products and admin account |
| `application.properties` | Configuration file for DB, OAuth2, Stripe, and other project settings |
| `build.gradle` | Gradle project build definition |
| `Virtual_Vending_Machine_User_Manual.pdf` | Step-by-step instructions and UI walkthrough |
| `README.md` | This setup and execution guide |




## Important Notes

- `data.sql` is used to insert default product data and the admin credentials into the database when the app starts.  
- The application uses `spring.jpa.hibernate.ddl-auto=update`, so schema changes are applied automatically.  
- Sensitive data and environment variables were intentionally not pushed to this public repository.  




## Troubleshooting
-Ensure the MySQL service is running and the vending_machine DB exists

-If login fails, ensure GOOGLE_CLIENT_ID and GOOGLE_CLIENT_SECRET are correctly set or use other login methods

-Use the guest option to bypass authentication for quick runthroughs and tests (remember not all features are accessible to Guests)



## Final Notes for Submission
This project contains:

  -All source code

  -Executable JAR

  -Configuration (build.gradle)

  -Database automatic populating via data.sql

  -Full user documentation    



Contact:

  Developer: Issa Aboobaker

  Email: issasecond@outlook.com

  Institution: University of Leicester (Graduate,2025)


## Folder Structure

```
VirtualVendingMachineWebapp/
│
├── src/ → Java source code, Thymeleaf templates, CSS, JS, resources (includes `data.sql`)
├── docs/ → Full documentation and deliverables
│   ├── Dissertation/ → Final written report (full dissertation)
│   ├── Interim_Report/ → Early project report (before development)
│   ├── Presentations/ → Interview slides (prototype stage) + Mini Viva slides (final presentation)
│   ├── User_Manual/ → User guide (PDF and DOCX versions)
│   ├── Project_Log/ → Final project log tracking progress
│   └── Project_Log.pdf
├── design/ → UI design wireframes (Pencil `.epgz` files)
│   └── Project_UI_Wireframes/
│       ├── Project_Wireframes.epgz
│       └── Project_Wireframes_Annotated.epgz
├── build.gradle, settings.gradle → Gradle configuration files
├── gradlew / gradlew.bat / gradle/ → Gradle wrapper scripts
├── .gitignore / .gitattributes → Git configuration
└── README.md → (This file)
```


## Environment Variables

| Variable | Purpose | Required | Notes |
|-----------|----------|-----------|--------|
| **DB_PASSWORD** | MySQL database password | ✅ | Used to authenticate the local database connection |
| **GOOGLE_CLIENT_ID** | OAuth2 client ID | Optional | Enables Google login |
| **GOOGLE_CLIENT_SECRET** | OAuth2 secret | Optional | Must match the Google client above |
| **STRIPE_TEST_KEY** *(recommended)* | Stripe test API key | Optional | Currently test key is hardcoded but can be moved here for flexibility |

*Sensitive credentials were intentionally kept as environment variables for security. The only visible key in `application.properties` is a **Stripe test key**, which cannot process real transactions.*



## Security and Data Handling Notes

-No sensitive credentials are committed — only placeholders or environment variable references.

-The database connection uses a local-only MySQL instance; no remote or cloud endpoints are exposed.

-Stripe integration operates strictly in test mode, ensuring that no real payments can occur.

-All user passwords (for registered accounts) are securely hashed within the database.

-Google OAuth2 login uses client credentials stored locally in your environment.


## Known Limitations

-The project requires a local MySQL instance for testing (not yet configured for H2 or hosted DBs).

-Google OAuth2 login only functions if valid credentials are supplied.

-Admin account is pre-seeded (see data.sql) for demonstration purposes.

-Stripe integration is limited to test-mode transactions (no live keys included).

-Frontend styling optimised for desktop; mobile responsiveness not yet implemented.

-No automated deployment pipeline — system is intended for local execution and academic demonstration.



## Project Timeline Context

This repository contains all phases of development and documentation in chronological order:

| Deliverable | Project Stage | Description |
|--------------|---------------|-------------|
| **Interim Report** | Before Development | Early analysis and design outlining objectives and proposed architecture |
| **Interview Slides** | Early Prototype Stage | Presentation showcasing the first working version and system overview |
| **Dissertation** | Final Report | Full 180+ page academic dissertation detailing design, implementation, testing, and evaluation |
| **Mini Viva Slides** | After Completion | Presentation summarising the final implemented system and dissertation outcomes |
| **User Manual** | Post-Development | Detailed guide for users on running and navigating the full system |
| **Project Log** | Throughout | Chronological record of milestones, updates, and reflections |

Together, these materials document the **complete lifecycle** of the Virtual Vending Machine system — from initial concept and design to final implementation, testing, and presentation.


## Contact

**README Author:** *Issa Aboobaker*  
Email: issasecond@outlook.com  
LinkedIn: [linkedin.com/in/issa-aboobaker](https://linkedin.com/in/issa-aboobaker)  
Institution: *University of Leicester (Graduate, 2025)*  

*This README was written and compiled personally by Issa Aboobaker to provide complete setup and documentation for the Virtual Vending Machine web application and has been altered since creation ready for upload to a personal GitHub repository.*
