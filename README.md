# Github Repositories API

This Spring Boot application provides an API to retrieve information about Github repositories for a specific user.

## Getting Started

Follow these instructions to get a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

Make sure you have the following software installed on your machine:

- Java (version 12 or later)
- Maven

### Installing

1. Clone the repository:

   ```bash
   git clone https://github.com/{username}/api-git-users-repo.git

    Navigate to the project directory:

    cd api-git-users-repo
    
    Build the project:
    
    mvn clean install
    
    Run the application:

    mvn spring-boot:run

The application should now be running on http://localhost:8080.
Usage
Get Github Repositories

    Example Request for all elements

    Endpoint: /api/repositories/{username}
    Parameters

    username (required): Github username for which you want to retrieve repositories.
    page (optional): Page number for pagination (default is 1).
    pageSize (optional): Number of items per page (default is 10).

    Example Request with pagination
    
    GET /api/repositories/{username}?page=1&pageSize=5
    
    Example Response
    
    json
    
    {
      "status": 200,
      "message": "Repositories retrieved successfully",
      "items": [
        {
          "repositoryName": "example-repo",
          "userLogin": "{username}",
          "branches": [
            {
              "name": "main",
              "lastCommitSha": "a1b2c3d4e5f6"
            },
            {
              "name": "feature-branch",
              "lastCommitSha": "abcdef123456"
            }
          ]
        },
        // ... other repositories
      ],
       "pagination": {
        "pageSize": 1,
        "nextPage": 3,
        "page": 2,
        "totalPages": "111",
        "previousPageLink": "http://localhost:8080/api/repositories/{username}?page=1&pageSize=1",
        "previousPage": 1,
        "nextPageLink": "http://localhost:8080/api/repositories/{username}?page=3&pageSize=1"
    },
    "total": 111
}

Contributing

Feel free to contribute to this project by opening issues or pull requests.
