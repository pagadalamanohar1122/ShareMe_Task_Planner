package com.tasksphere.shareme.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ShareMe TaskSphere API")
                        .description("""
                                # Professional Task Management System API
                                
                                Complete API documentation for ShareMe TaskSphere - An enterprise-grade task management platform.
                                
                                ## Features:
                                - **Authentication & Authorization**: JWT-based secure authentication with role-based access control
                                - **Task Management**: Full CRUD operations with advanced filtering, search, and status management
                                - **Project Management**: Organize tasks within projects with comprehensive project lifecycle management
                                - **Personal Notes**: Task-specific personal notes with reminder tags and content management
                                - **File Attachments**: Secure upload, download, and management of task-related files
                                - **Real-time Updates**: Live task status updates and notifications
                                - **Advanced Search**: Multi-criteria search with filtering capabilities
                                - **User Management**: Complete user registration, authentication, and profile management
                                
                                ## API Standards:
                                - RESTful design principles with consistent HTTP methods
                                - Comprehensive error handling with detailed error responses
                                - Input validation and sanitization for all endpoints
                                - Secure by default (Bearer token required for protected endpoints)
                                - Standardized JSON response formats
                                - Proper HTTP status codes and pagination support
                                
                                ## Getting Started:
                                1. **Register**: Create a new account using `/api/auth/signup`
                                2. **Login**: Get your JWT token via `/api/auth/login`
                                3. **Authenticate**: Include token in Authorization header: `Bearer <your-token>`
                                4. **Explore**: Start managing your projects, tasks, and notes!
                                
                                ## Security:
                                - All endpoints require valid JWT authentication (except public auth endpoints)
                                - Tokens expire after 15 minutes for enhanced security
                                - Role-based access control for different user levels
                                - Input validation and XSS protection
                                
                                ## Error Handling:
                                All API responses follow a consistent error format with proper HTTP status codes:
                                - **200**: Success with data
                                - **201**: Resource created successfully
                                - **400**: Bad request / Validation errors
                                - **401**: Unauthorized / Invalid token
                                - **403**: Forbidden / Insufficient permissions
                                - **404**: Resource not found
                                - **500**: Internal server error
                                
                                ## Support:
                                For technical support, contact our development team or visit our documentation portal.
                                """)
                        .version("v2.1.0")
                        .contact(new Contact()
                                .name("ShareMe TaskSphere Development Team")
                                .email("api-support@shareme-tasksphere.com")
                                .url("https://docs.shareme-tasksphere.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Local Development Server - Use for testing and development"),
                        new Server()
                                .url("https://api.shareme-tasksphere.com")
                                .description("Production Server - Live production environment"),
                        new Server()
                                .url("https://staging-api.shareme-tasksphere.com")
                                .description("Staging Server - Pre-production testing environment")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("""
                                                Enter JWT Bearer token obtained from `/api/auth/login` endpoint.
                                                
                                                **Format**: Bearer <your-jwt-token>
                                                
                                                **Example**: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
                                                
                                                **Note**: Tokens automatically expire after 15 minutes for security.
                                                Refresh tokens as needed using the login endpoint.
                                                """)));
    }
}