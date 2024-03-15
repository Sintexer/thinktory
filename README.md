# *Thinktory Specification*

Thinktory is a learning application that organizes knowledge into concepts, each consisting of a theory, title, and a set of questions. Users learn concepts through quizzes generated by the app, which prioritizes concepts that users have answered less frequently. Users self-assess their answers, providing feedback that the app uses to track progress.

## API Mappings Structure for Thinktory

**Resource** | **HTTP Method** | **Endpoint** | **Description**
---|---|---|---|
**Concept** | GET | /concepts | (Paged) Get all concepts owned by the authenticated user.
**Concept** | POST | /concepts | Create a new concept.
**Concept** | GET | /concepts/{id} | Get a specific concept by ID.
**Concept** | PUT | /concepts/{id} | Update a specific concept.
**Concept** | DELETE | /concepts/{id} | Delete a specific concept.
**Concept** | GET | /concepts/{id}/questions | (Paged) Get all questions associated with a specific concept.
**Question** | POST | /concepts/{id}/questions | Create a new question for a specific concept.
**Question** | GET | /concepts/{id}/questions/{question_id} | Get a specific question by ID.
**Question** | PUT | /concepts/{id}/questions/{question_id} | Update a specific question.
**Question** | DELETE | /concepts/{id}/questions/{question_id} | Delete a specific question.
**Quiz** | POST | /quizzes | Create a new quiz.
**Quiz** | GET | /quizzes/{id} | Get a specific quiz by ID.
**Quiz** | DELETE | /quizzes/{id} | Delete a specific quiz.
**Collection** | GET | /collections | (Paged) Get all collections owned by the authenticated user.
**Collection** | POST | /collections | Create a new collection.
**Collection** | GET | /collections/{id} | Get a specific collection by ID.
**Collection** | PUT | /collections/{id} | Update a specific collection.
**Collection** | DELETE | /collections/{id} | Delete a specific collection.
**Collection** | GET | /collections/{id}/concepts | (Paged) Get all concepts associated with a specific collection.
**Collection** | POST | /collections/{id}/concepts | Add a concept to a specific collection.
**Collection** | DELETE | /collections/{id}/concepts/{concept_id} | Remove a concept from a specific collection.
**Authentication** | POST | /auth/login | Authenticate a user and generate a JWT token.
**Authentication** | POST | /auth/register | Register a new user.
