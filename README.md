# eCommerce Backend

This project is a Java Spring Boot backend for an eCommerce application, providing essential features such as user authentication, order management, wallet integration, and a discount system.

## Features

- **User Management**: Registration, authentication, and address management
- **Favorites**: Add products to your favorites list
- **Shopping Cart**: Add products to your cart and place orders
- **Order Management**: Place, cancel, and track order history
- **Payment Service**: Handle payments through Wallet integration
- **Wallet System**: Deposit funds, make payments, and view transaction history
- **Discount Codes**: Automatic code generation based on user spending
- **Price History**: Track the price changes of products over time
- **Notifications**: Receive updates on orders, email changes, and more
- **Comments & Reviews**: Post comments on purchased products and view reviews from other users
- **Security**: JWT-based authentication and authorization

## Tech Stack

- **Backend**: Java, Spring Boot, Spring Security
- **Database**: PostgreSQL
- **Authentication**: JWT-based security

## Getting Started

Follow these steps to get the project up and running on your local machine.

### Prerequisites

Before you begin, ensure that you have the following installed:

- **Java 17 or higher**
- **Maven**
- **PostgreSQL**
- **Git**

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/Shyesilbas/eCommerce.git
   cd ecommerce-backend
   ```

2. **Set up the Database**:
   - Create a PostgreSQL database named `ecommerce_db`.
   - Update the `application.properties` file located in `src/main/resources/` with your database credentials:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```

### Build and Run the Project

1. **Build the project**:
   ```bash
   mvn clean install
   ```

2. **Run the project**:
   ```bash
   mvn spring-boot:run
   ```

### Access the Application

Once the backend is running, you can access it at the following URL:

- **Backend URL**: [http://localhost:8080](http://localhost:8080)

### API Documentation

You can access all the API endpoints via Swagger UI at:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Contributing

Contributions are welcome! Please follow these steps to contribute:

1. **Fork the repository**.
2. **Create a feature branch**:
   ```bash
   git checkout -b feature/your-feature
   ```
3. **Commit your changes**:
   ```bash
   git commit -m "Add your feature"
   ```
4. **Push to the branch**:
   ```bash
   git push origin feature/your-feature
   ```
5. **Open a Pull Request** to the main repository.
