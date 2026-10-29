# Employee Salary API

A small Spring Boot REST API for managing employees. Salary is derived from the employee's designation and is not stored or accepted in create/update requests.

## Requirements

- Java 21
- Maven 3.6.3+

## Run

```bash
mvn spring-boot:run
```

The API starts at `http://localhost:8080`. It uses an in-memory H2 database, so employee records are cleared when the application stops. The H2 console is available at `http://localhost:8080/h2-console` with JDBC URL `jdbc:h2:mem:employees`, username `sa`, and an empty password.

On startup, the application inserts three demo employees if the employee table is empty. Their sample records are recreated after each restart because H2 is in-memory.

Run the tests with:

```bash
mvn test
```

## Salary table

The sample salary amounts are defined in `Designation.java` and use the project's chosen currency unit:

| Designation | Salary |
| --- | ---: |
| INTERN | 30000 |
| JUNIOR_DEVELOPER | 60000 |
| SENIOR_DEVELOPER | 100000 |
| TEAM_LEAD | 130000 |
| MANAGER | 150000 |

Change the values in `Designation.java` to match your organization's salary policy.

## API

- `GET /api/designations` returns designation names and their salaries.
- `GET /api/employees` lists employees.
- `GET /api/employees/{id}` gets one employee.
- `POST /api/employees` creates an employee.
- `PUT /api/employees/{id}` replaces an employee's name, email, and designation.
- `DELETE /api/employees/{id}` deletes an employee.

Example create request:

```bash
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{"name":"Avery Lee","email":"avery@example.com","designation":"SENIOR_DEVELOPER"}'
```

The response includes the derived salary:

```json
{
  "id": 1,
  "name": "Avery Lee",
  "email": "avery@example.com",
  "designation": "SENIOR_DEVELOPER",
  "salary": 100000
}
```

The `designation` field must match one of the values listed by `GET /api/designations`. Salary is calculated from that designation on every response.

## Test cases

See [TEST_CASES.md](TEST_CASES.md) for positive and negative scenarios covering employee CRUD, designation salary mapping, request validation, invalid IDs, and current database behavior.
