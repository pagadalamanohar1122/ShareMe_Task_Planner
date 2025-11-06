# ShareMe TaskSphere

A full-stack task management application built with Spring Boot and React.

## Architecture

- **Backend**: Spring Boot 3.2 with JPA, Security, JWT authentication, Swagger API docs
- **Frontend**: React 18 with TypeScript, Vite, Tailwind CSS, React Router
- **Database**: MySQL 8 with Flyway migrations
- **Authentication**: JWT-based stateless authentication

## Features

-  User registration and login
-  JWT-based authentication
-  Password reset functionality
- Protected dashboard
-  Responsive UI with Tailwind CSS
-  API documentation with Swagger/OpenAPI
-  Database migrations with Flyway
-  Project management (placeholder)
-  Task planning (placeholder)
-  Progress tracking (placeholder)

## API Endpoints

### Authentication Endpoints
- `POST /api/auth/signup` - Register new user
- `POST /api/auth/login` - Login user
- `GET /api/auth/me` - Get current user (protected)
- `POST /api/auth/forgot` - Request password reset
- `POST /api/auth/reset` - Reset password with token

### API Documentation
- `GET /swagger-ui/index.html` - Swagger UI
- `GET /v3/api-docs` - OpenAPI JSON specification

## Prerequisites

- Java 17 or later
- Node.js 18 or later
- MySQL 8.0 or later
- Maven 3.8 or later

## Database Setup

1. Create MySQL database and user:
```sql
CREATE DATABASE shareme;
CREATE USER 'shareme'@'localhost' IDENTIFIED BY 'ShareMe@123';
GRANT ALL PRIVILEGES ON shareme.* TO 'shareme'@'localhost';
FLUSH PRIVILEGES;
```

2. Database migrations will run automatically when starting the backend.

## Running the Application

### Backend (Spring Boot)

```bash
cd backend
mvn spring-boot:run
```

The backend will start on http://localhost:8080

### Frontend (React)

```bash
cd frontend
npm install
npm run dev
```

The frontend will start on http://localhost:3000

## Testing the Application

1. Visit http://localhost:3000 to access the frontend
2. Visit http://localhost:8080/swagger-ui/index.html to access the API documentation
3. Create a new account or login with existing credentials
4. Explore the dashboard and features

## Project Structure

```
task_managment/
├── backend/                 # Spring Boot application
│   ├── src/main/java/      # Java source code
│   ├── src/main/resources/ # Application properties and static resources
│   └── pom.xml             # Maven dependencies
├── frontend/               # React application
│   ├── src/                # TypeScript/React source code
│   ├── public/             # Static assets
│   └── package.json        # npm dependencies
└── database/
    └── schema/             # Flyway database migrations
```

## Configuration

### Backend Configuration
Key configuration in `application.properties`:
- Database connection settings
- JWT secret and token expiration
- Flyway migration settings
- CORS configuration
- Swagger/OpenAPI settings

### Frontend Configuration
Environment variables in `.env`:
- `VITE_API_BASE_URL` - Backend API URL
- `VITE_WS_URL` - WebSocket URL (future use)

## Security Features

- Password hashing with BCrypt
- JWT tokens with configurable expiration
- CORS protection
- Input validation
- SQL injection protection via JPA
- CSRF protection disabled for stateless API
- Rate limiting (recommended for production)

## Development

### Code Style
- Backend: Java code follows Spring Boot conventions
- Frontend: TypeScript with strict mode, ESLint configuration
- Database: Snake_case naming convention

### Adding New Features
1. Database: Create new Flyway migration files
2. Backend: Add entities, repositories, services, controllers
3. Frontend: Add components, pages, API services
4. Update API documentation with Swagger annotations

## Production Deployment

Before deploying to production:

1. **Security**:
   - Change JWT secret to a strong, unique value
   - Use environment variables for sensitive configuration
   - Enable HTTPS
   - Configure proper CORS origins
   - Add rate limiting

2. **Database**:
   - Use connection pooling
   - Configure proper backup strategy
   - Monitor database performance

3. **Application**:
   - Build frontend for production: `npm run build`
   - Package backend: `mvn clean package`
   - Configure proper logging levels
   - Set up monitoring and health checks

## Troubleshooting

### Common Issues

1. **Database Connection Failed**:
   - Verify MySQL is running
   - Check database credentials in application.properties
   - Ensure database and user exist

2. **JWT Token Issues**:
   - Check token expiration settings
   - Verify JWT secret configuration
   - Clear browser localStorage and retry

3. **CORS Errors**:
   - Verify frontend URL in CORS configuration
   - Check that both frontend and backend are running

4. **Build Issues**:
   - Ensure correct Java version (17+)
   - Ensure correct Node.js version (18+)
   - Clear Maven/npm caches and retry

## Contributing

1. Follow the existing code style and patterns
2. Add tests for new functionality
3. Update documentation as needed
4. Ensure all security requirements are met

## License

This project is for educational and demonstration purposes.