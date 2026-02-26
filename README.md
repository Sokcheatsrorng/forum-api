# Forum API - Spring Boot 3.3+

A comprehensive Stack Overflow-like forum API built with Spring Boot 3.3+, PostgreSQL, JWT authentication, and Swagger documentation.

## Features

✅ **User Management**
- User registration and authentication with JWT
- User profiles with reputation tracking
- Search and filter users by reputation

✅ **Post Management**
- Create questions and answers
- Full CRUD operations on posts
- Support for post hierarchy (questions and answers)
- Search posts by title/body
- Sort by newest, score, or views
- Tag-based categorization

✅ **Comment System**
- Add comments to posts
- Thread discussions
- Search comments by content

✅ **Voting System**
- Upvote and downvote posts
- Automatic score calculation
- Vote tracking per user

✅ **Tag System**
- Create and manage tags
- Popular tag suggestions
- Tag-based filtering

✅ **Security**
- JWT token-based authentication
- BCrypt password encryption
- Role-based access control
- Spring Security integration

✅ **API Documentation**
- Swagger/OpenAPI 3.0 documentation
- Interactive API explorer at `/swagger-ui.html`

✅ **Error Handling**
- Global exception handling
- Validation feedback
- Consistent error responses

## Technology Stack

- **Java 21**
- **Spring Boot 3.3.0**
- **Spring Data JPA**
- **Spring Security**
- **PostgreSQL 14+**
- **JWT (io.jsonwebtoken)**
- **Swagger/OpenAPI 3.0**
- **Gradle 7.x**
- **Lombok**

## Prerequisites

- Java 21 JDK
- PostgreSQL 14 or higher
- Gradle 7.x (or use included wrapper)
- Git

## Installation & Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd forum-api
```

### 2. Create PostgreSQL Database
```sql
CREATE DATABASE forum_db;
```

### 3. Update Database Configuration
Edit `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/forum_db
    username: your_postgres_username
    password: your_postgres_password
```

### 4. Update JWT Secret (Important!)
Edit `src/main/resources/application.yml`:
```yaml
spring:
  security:
    jwt:
      secret: change-this-to-a-secure-256-bit-key-in-production
```

### 5. Build the Project
```bash
./gradlew clean build
```

### 6. Run the Application
```bash
./gradlew bootRun
```

The API will start on `http://localhost:8080/api`

## API Endpoints

### Authentication (`/api/auth`)
- `POST /register` - Register new user
- `POST /login` - Login user
- `POST /validate-token` - Validate JWT token

### Users (`/api/users`)
- `GET /{userId}` - Get user by ID
- `GET /email/{email}` - Get user by email
- `PUT /{userId}` - Update user
- `DELETE /{userId}` - Delete user
- `GET /search?query=...` - Search users
- `GET /top-reputation?minReputation=0` - Get top users by reputation

### Posts (`/api/posts`)
- `POST /` - Create post
- `GET /{postId}` - Get post by ID
- `PUT /{postId}` - Update post
- `DELETE /{postId}` - Delete post
- `GET` - Get all posts
- `GET /sort/score` - Get posts by score
- `GET /sort/views` - Get posts by views
- `GET /user/{userId}` - Get user's posts
- `GET /type/{postTypeId}` - Get posts by type
- `GET /tag/{tagId}` - Get posts by tag
- `GET /answers/{parentId}` - Get answers to a question
- `GET /search?query=...` - Search posts
- `GET /search/relevance?query=...` - Search by relevance

### Comments (`/api/comments`)
- `POST /` - Create comment
- `GET /{commentId}` - Get comment
- `PUT /{commentId}` - Update comment
- `DELETE /{commentId}` - Delete comment
- `GET /post/{postId}` - Get comments by post
- `GET /user/{userId}` - Get user's comments
- `GET /search?query=...` - Search comments

### Votes (`/api/votes`)
- `POST /` - Create vote (upvote/downvote)
- `GET /{voteId}` - Get vote
- `PUT /{voteId}` - Update vote
- `DELETE /{voteId}` - Delete vote
- `GET /post/{postId}` - Get post votes
- `GET /user/{userId}` - Get user's votes

### Tags (`/api/tags`)
- `POST /` - Create tag
- `GET /{tagId}` - Get tag
- `PUT /{tagId}` - Update tag
- `DELETE /{tagId}` - Delete tag
- `GET` - Get all tags
- `GET /popular` - Get popular tags
- `GET /top/{limit}` - Get top tags
- `GET /search?query=...` - Search tags

## API Documentation

Access Swagger UI at: `http://localhost:8080/api/swagger-ui.html`

API docs endpoint: `http://localhost:8080/api/v3/api-docs`

## Example Requests

### Register User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "displayName": "John Doe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

### Create Post (Authenticated)
```bash
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>" \
  -d '{
    "title": "How to use Spring Boot?",
    "body": "I want to learn Spring Boot from scratch...",
    "postTypeId": 1,
    "tagIds": [1, 2]
  }'
```

### Search Posts
```bash
curl -X GET "http://localhost:8080/api/posts/search?query=spring%20boot"
```

## Project Structure

```
src/main/java/com/forum/
├── controller/          # REST controllers
├── service/            # Business logic
├── repository/         # Data access layer
├── entity/             # JPA entities
├── dto/                # Data transfer objects
├── security/           # JWT & security
├── config/             # Application configuration
├── exception/          # Custom exceptions
└── ForumApiApplication.java  # Main application class
```

## Key Components

### Security Configuration
- JWT token generation and validation in `JwtProvider`
- Custom user details service in `CustomUserDetailsService`
- JWT filter in `JwtAuthenticationFilter`
- Security config in `SecurityConfig`

### Services
- **UserService**: User management and profile operations
- **AuthService**: Authentication and registration
- **PostService**: Post CRUD and search operations
- **CommentService**: Comment management
- **VoteService**: Voting system
- **TagService**: Tag management

### Repositories
- Custom query methods using `@Query` annotation
- Complex search and filter operations
- JPA repository interfaces for data access

## Important Notes

⚠️ **Before Production:**
1. Change JWT secret to a secure 256-bit key
2. Update PostgreSQL credentials
3. Configure CORS if frontend is on different domain
4. Set `spring.jpa.hibernate.ddl-auto` to `validate`
5. Enable HTTPS
6. Set appropriate log levels
7. Configure database backups

## MVC Architecture

The application follows the Model-View-Controller (MVC) pattern:

- **Model**: Entity and DTO classes
- **View**: JSON responses from REST endpoints
- **Controller**: REST controllers handling HTTP requests
- **Service**: Business logic layer
- **Repository**: Data persistence layer

## Database Schema

The application uses the following main entities:
- **User**: Forum users
- **Post**: Questions and answers
- **Comment**: Comments on posts
- **Vote**: Upvotes and downvotes
- **Tag**: Post categories
- **PostHistory**: Edit history tracking
- **Badge**: User achievements

## Testing

(Add test class examples as you develop)

## Troubleshooting

### Database Connection Error
- Verify PostgreSQL is running
- Check username/password in `application.yml`
- Ensure database `forum_db` exists

### JWT Token Issues
- Verify token is in Authorization header as: `Bearer <token>`
- Check token expiration time
- Validate JWT secret is consistent

### CORS Issues
- Configure `@CrossOrigin` annotations on controllers if needed
- Or configure CORS in `SecurityConfig`

## Future Enhancements

- [ ] Email notifications
- [ ] User following/followers
- [ ] Reputation badges
- [ ] Advanced search filters
- [ ] Elasticsearch integration
- [ ] Caching with Redis
- [ ] Rate limiting
- [ ] File uploads for posts/comments
- [ ] Real-time notifications with WebSockets

## License

MIT License

## Support

For issues and questions, please create an issue in the repository.

---

**Happy coding! Build amazing forum communities with this API.** 🚀
