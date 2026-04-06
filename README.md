# FinancePRP - Role Based Finance Management Portal

Finance management is a Spring Boot + Thymeleaf + Hibernate project for managing users and financial records with manual session-based authentication and role-based access control.




- Session based login using `HttpSession`
- Role based actions with Thymeleaf and backend checks
- Roles:
  - **Viewer**: can only view dashboard data
  - **Analyst**: can view records and insights, add and edit records
  - **Admin**: can manage records, users, and assign roles
- H2 database with persistent file storage
- Insights dashboard with summary cards and charts
- Pagination for record listing
- Validation and error handling

## Tech stack

- Java 21
- Spring Boot 4
- Spring MVC
- Spring Data JPA / Hibernate
- Thymeleaf
- H2 Database
- Bean Validation (`spring-boot-starter-validation`)
- Chart.js for charts on the insights page

## Login flow



1. User opens `/signin`
2. User enters username and password
3. Application checks database using `UserService.authenticate(...)`
4. On success, the logged-in user is stored in session as `loggedInUser`
5. Controllers and Thymeleaf templates check role from session

## Default admin user

A default admin is created automatically if no admin exists.

- Username: `admin`
- Password: `admin123`

## Roles and permissions

### Viewer
- Can sign in
- Can open dashboard
- Can only view summary data
- Cannot add, edit, or delete records
- Cannot manage users
- Cannot open insights page

### Analyst
- Can sign in
- Can open dashboard
- Can open records page
- Can add and edit financial records
- Can open insights page
- Cannot delete records
- Cannot manage users

### Admin
- Full access
- Can create users
- Can update users
- Can delete users, except the last remaining admin
- Can assign roles
- Can create, update, and delete records
- Can open insights page

## Main features

### 1. Sign in page
- Professional UI
- User does not select role during sign in
- Role is picked from database

### 2. Dashboard
- Total income
- Total expense
- Net balance
- Top category
- Quick insights cards
- User management table visible only to Admin

### 3. Insights module
Available for Analyst and Admin.

Includes:
- Total income
- Total expense
- Net balance
- Top category
- Monthly trend chart
- Category distribution chart
- Category summary table
- Recent activity table

### 4. Financial records module
- Create record
- Edit record
- Delete record (Admin only)
- Filter by category, type, and date
- Pagination using page and size query params

Example:
- `/expenses?page=0&size=5`
- `/expenses?page=1&size=10&type=EXPENSE`

### 5. User management
Admin only.

- Create user
- Edit user
- Delete user
- Assign role
- Activate / deactivate user

## Validation and error handling

### Input validation
Validation is added using Bean Validation annotations.

#### User validation
- Username required
- Username length: 3 to 30
- Password required
- Password length: 4 to 100
- Role must be `VIEWER`, `ANALYST`, or `ADMIN`

#### Record validation
- Amount required
- Amount must be greater than 0
- Type must be `INCOME` or `EXPENSE`
- Category required
- Date required
- Notes length max 255

### Useful error handling
The project handles invalid or incomplete input in these ways:

- Validation messages shown below fields in forms
- Flash error messages after redirects
- Global exception handler for common cases
- Friendly error page for unexpected server errors

### Protected invalid operations
The backend prevents:

- Non-admin access to user management
- Viewer access to record creation or editing
- Viewer access to insights
- Deleting a non-existing record
- Editing a non-existing user or record
- Duplicate usernames
- Deleting the last admin user
- Changing the last admin to a non-admin role

### Status code intent
This project is mainly server-rendered with Thymeleaf, but the backend still uses appropriate semantics:

- **400** for invalid operations / bad requests
- **403** for forbidden actions
- **404** for missing resources
- **500** for unexpected server errors





## How to run the project

### Option 1: using Maven wrapper on Windows

From the project folder:

```bash
mvnw.cmd spring-boot:run
```

### Option 2: using Maven on any system

```bash
mvn spring-boot:run
```

### Build jar

```bash
mvn clean package
```

## Application URLs

- Sign in page: `http://localhost:8080/signin`
- Dashboard: `http://localhost:8080/dashboard`
- Records: `http://localhost:8080/expenses`
- Insights: `http://localhost:8080/insights`
- H2 console: `http://localhost:8080/h2-console`

## H2 database settings

From `application.properties`:

- JDBC URL: `jdbc:h2:file:./data/financeprp`
- Username: `test`
- Password: empty

## Step by step usage

1. Run the project
2. Open `/signin`
3. Log in with `admin / admin123`
4. Open dashboard
5. Create Viewer / Analyst / Admin users from **Add User**
6. Use records module to add financial data
7. Open insights page as Analyst or Admin
8. Test role-based restrictions by logging in as different users





