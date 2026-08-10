# QuizBuilder

QuizBuilder is a full-stack web application that allows users to create, customize, publish, and take their own quizzes.

The project is being developed with a focus on clean architecture, RESTful API design, relational database modeling, automated testing, and an interactive React frontend.

## Tech Stack

### Backend

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* Hibernate
* PostgreSQL
* Jakarta Bean Validation
* Lombok
* Maven

### Frontend

* React
* TypeScript
* TanStack Query
* React Router
* CSS

### Testing

* JUnit
* Mockito
* Spring Boot Test

### Development Tools

* IntelliJ IDEA
* Git
* GitHub

## Planned Features

* User registration and authentication
* Create and manage quizzes
* Add, edit, and delete questions
* Multiple question types
* Multiple-choice questions
* True/false questions
* Multiple-select questions
* Short-answer questions
* Reorder questions
* Save quizzes as drafts
* Publish quizzes
* Share quizzes with other users
* Take published quizzes
* Automatic scoring
* View quiz results
* Track quiz attempts
* Quiz statistics

## Architecture

The backend follows a layered architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

The React frontend communicates with the Spring Boot backend through a REST API.

```text
React + TypeScript
        │
        │ REST API
        ▼
   Spring Boot
        │
        │ JPA / Hibernate
        ▼
    PostgreSQL
```

## Domain Model

The initial domain model is centered around four primary entities:

```text
User
 │
 └── Quiz
      │
      └── Question
           │
           └── Answer
```

Additional entities, such as quiz attempts and submitted answers, will be added as the application develops.

## Question Types

The initial version is planned to support:

* Multiple Choice
* True/False
* Multiple Select
* Short Answer

Additional question types may be added in future versions.

## Project Goals

This project is intended to provide practical experience with:

* Full-stack application development
* REST API design
* Relational database modeling
* Spring Boot
* React and TypeScript
* Authentication and authorization
* Form validation
* Automated testing
* Client/server state management
* Git and GitHub workflows
* Clean and maintainable software architecture

## Getting Started

### Prerequisites

You will need:

* Java 21
* Maven
* PostgreSQL
* Node.js and npm
* Git

### Database Setup

Create a PostgreSQL database named:

```text
quizbuilder
```

The backend expects PostgreSQL to be available on the default port:

```text
5432
```

Configure your local database password through the `DB_PASSWORD` environment variable.

Do not commit database passwords or other credentials to the repository.

### Running the Backend

Clone the repository:

```bash
git clone https://github.com/AidanNolan-maker/QuizBuilder.git
cd QuizBuilder
```

On Windows, run:

```bash
mvnw.cmd spring-boot:run
```

On macOS/Linux:

```bash
./mvnw spring-boot:run
```

The backend runs on:

```text
http://localhost:8080
```

### Frontend

The React frontend will be added during a later development phase. Setup instructions will be added once the frontend has been created.

## Development Status

QuizBuilder is currently under active development.

### Progress

* [x] Spring Boot project created
* [x] Maven configured
* [x] PostgreSQL configured
* [x] Database connection verified
* [x] Git repository initialized
* [ ] Domain model
* [ ] Database entities
* [ ] Quiz CRUD API
* [ ] Question management API
* [ ] React frontend
* [ ] Quiz builder
* [ ] Authentication
* [ ] Quiz publishing
* [ ] Quiz player
* [ ] Quiz results
* [ ] Quiz attempts
* [ ] Statistics

## License

License information will be added later.
