# Employee Salary API Test Cases

This catalog describes the current API contract. A fresh application start seeds three demo employees into an empty H2 database. Use JSON requests and assert the HTTP status and response fields listed below; do not rely on the exact framework-generated error body.

## Positive cases

| ID | Scenario | Request / setup | Expected result |
| --- | --- | --- | --- |
| P01 | List designation salaries | `GET /api/designations` | `200`; returns all five designations with the values in `Designation.java`. |
| P02 | List demo employees on fresh startup | `GET /api/employees` after starting with an empty H2 database | `200`; returns Avery Lee, Jordan Kim, and Morgan Chen with their designation-based salaries. |
| P03 | Create an employee | `POST /api/employees` with a nonblank name, valid email, and valid designation | `201`; returns a generated ID and the submitted fields. |
| P04 | Create one employee for every designation | Repeat P03 for `INTERN`, `JUNIOR_DEVELOPER`, `SENIOR_DEVELOPER`, `TEAM_LEAD`, and `MANAGER` | Every request returns `201`; each response salary matches its designation's configured salary. |
| P05 | Derive salary on create | Create a `SENIOR_DEVELOPER` | Response salary is `100000`, regardless of a supplied extra `salary` property; request DTO does not accept salary. |
| P06 | Get an employee by ID | `GET /api/employees/{id}` using a created ID | `200`; returns that employee, including its derived salary. |
| P07 | List created employees | Create multiple employees, then `GET /api/employees` | `200`; list contains all created employees with their designation-based salaries. |
| P08 | Update employee fields | `PUT /api/employees/{id}` with valid name, email, and designation | `200`; returns updated fields and preserves the employee ID. |
| P09 | Recalculate salary after designation change | Update an employee from `INTERN` to `MANAGER` | `200`; salary changes to the configured `MANAGER` salary without a separate salary write. |
| P10 | Replace all editable fields | Update name, email, and designation in one valid PUT request | `200`; response reflects all three supplied fields and the derived salary. |
| P11 | Delete an existing employee | `DELETE /api/employees/{id}` using a created ID | `204`; a subsequent GET by that ID returns `404`. |
| P12 | Create a distinct employee with the same email | POST another employee using an email already present | `201` with the current implementation; email uniqueness is not enforced. Confirm whether this policy is acceptable for the product. |
| P13 | Recreate demo data after application restart | Create an employee, stop and restart the application, then list employees | `200`; the user-created record is gone and the three demo employees are seeded again because H2 is in-memory. |

## Negative cases

| ID | Scenario | Request / setup | Expected result |
| --- | --- | --- | --- |
| N01 | Get a missing employee | `GET /api/employees/999999` | `404`. |
| N02 | Update a missing employee | `PUT /api/employees/999999` with an otherwise valid body | `404`. |
| N03 | Delete a missing employee | `DELETE /api/employees/999999` | `404`. |
| N04 | Missing request body | POST or PUT without a body | `400`. |
| N05 | Malformed JSON | POST or PUT with invalid JSON syntax | `400`. |
| N06 | Empty request object | POST or PUT with `{}` | `400`; required fields are missing. |
| N07 | Missing name | Omit `name` | `400`. |
| N08 | Null, empty, or whitespace-only name | Set `name` to `null`, `""`, or whitespace | `400`; `@NotBlank` rejects these values. |
| N09 | Missing email | Omit `email` | `400`. |
| N10 | Null, empty, or whitespace-only email | Set `email` to `null`, `""`, or whitespace | `400`. |
| N11 | Invalid email format | Use a value such as `"not-an-email"` | `400`; `@Email` rejects it. |
| N12 | Missing or null designation | Omit `designation` or set it to `null` | `400`; `@NotNull` rejects it. |
| N13 | Unknown designation | Use a value not declared in the `Designation` enum | `400`; JSON enum conversion fails. |
| N14 | Wrong field types | Use a number for `name`, an object for `email`, or another non-enum value for `designation` | `400`; JSON request conversion fails. |
| N15 | Attempt to set salary directly | Include an extra `salary` property with a value that conflicts with the designation | With current Spring/Jackson defaults the extra property is ignored; create/update still derives salary from designation. Verify the response salary, not the submitted value. |
| N16 | Invalid body on update | PUT an existing ID with a blank name, malformed email, or null designation | `400`; employee must not be updated. |
| N17 | Invalid body with missing update ID | PUT a nonexistent ID with an invalid body | `400`; request validation occurs before service lookup. |
| N18 | Non-numeric employee ID | Use `/api/employees/not-a-number` for GET, PUT, or DELETE | `400`; path variable cannot convert to `Long`. |
| N19 | Employee ID exceeds Long range | Use a numeric path value greater than `Long.MAX_VALUE` | `400`; path variable conversion fails. |
| N20 | Negative or zero employee ID | Use `/api/employees/-1` or `/api/employees/0` when no matching row exists | `404`; these parse as valid Long values but do not identify an employee. |
| N21 | Unsupported request content type | POST or PUT a non-JSON body with `Content-Type: text/plain` | `415 Unsupported Media Type`. |
| N22 | Unsupported HTTP method | Use an unsupported method, such as PATCH, on an existing employee route | `405 Method Not Allowed`. |
| N23 | Unknown API route | Request an endpoint not defined by the controller | `404`. |

## Test execution

Run the current automated tests with:

```bash
mvn test
```

The JUnit suite is split into one test class per main Java class. It currently covers designation salary mapping, employee construction and updates, request validation, response mapping, repository persistence, service behavior, controller HTTP behavior, and application startup. Some catalog scenarios, including every individual malformed-input variant and restart behavior, remain documented cases rather than automated tests. Cases P12 and P13 describe current behavior rather than enforcing business rules that the application does not currently implement.
