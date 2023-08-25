# *Thinktory Specification*

**Table of Contents**
1. [Introduction](#introduction)
2. [Functional Requirements](#functional-requirements)
3. [Non-Functional Requirements](#non-functional-requirements)
4. [API Endpoints](#api-endpoints)
5. [Use Cases](#use-cases)


## 1. *Introduction*
*Thinktory* is an open-source, free-to-use personal knowledge revision chatbot app designed to help users revise and manage their knowledge through customizable interactions. The app allows users to add theory, questions, topics, and labels to their personal knowledge base and seamlessly integrates with git repositories for synchronization and sharing purposes.

## 2. *Functional Requirements*

### 2.1 Add theory to a personal knowledge storage
- Users can add theory material to their knowledge base.

### 2.2 Add questions related to theory
- Users can create questions based on the theory they added.

### 2.3 Add standalone questions
- Users can add questions without any corresponding theory.

### 2.4 Add theory material to existing questions
- Users can supplement existing questions with additional theory material.

### 2.5 Add topics and labels for theory
- Users can categorize and organize their knowledge base using custom topics and labels.

### 2.6 Add questions to topics and assign labels
- Users can categorize and organize questions according to custom topics and labels.

### 2.7 Knowledge base convertibility
- Users can convert their personal knowledge base into a single file or a set of files.

### 2.8 Git repository synchronization
- Thinktory supports integration with git repositories for knowledge base synchronization.

### 2.9 Update knowledge base with file upload or repo link
- Users can update their knowledge base by uploading files or linking to a git repository.

### 2.10 Share theory and questions with a link
- Users can share their knowledge base or specific theory and questions via a unique link.

### 2.11 API for all users
- An API is provided for users to interact with Thinktory in a customizable and convenient manner.

## 3. *Non-Functional Requirements*

### 3.1 User-friendly interface
- The app should be easy to use and navigate for all users.

### 3.2 Performance
- The app should have fast response times and be able to handle a large number of user interactions.

### 3.3 Security
- User data must be securely stored, and all communications should be encrypted.

### 3.4 Accessibility
- The app should be accessible across multiple devices and platforms.

## 4. *API Endpoints*

*DRAFT*

- GET /concepts: Fetch all concepts for the authenticated user

- POST /concepts: Create a new concept for the authenticated user (theory or standalone question)

- GET /concepts/{conceptId}: Retrieve specific concept details by ID for the authenticated user

- PUT /concepts/{conceptId}: Update specific concept details by ID for the authenticated user

- DELETE /concepts/{conceptId}: Delete specific concept by ID for the authenticated user

- GET /topics: Fetch all topics for the authenticated user

- POST /topics: Create a new topic (and optional parent topic) for the authenticated user

- GET /topics/{topicId}: Retrieve specific topic details by ID for the authenticated user

- PUT /topics/{topicId}: Update specific topic details by ID for the authenticated user

- DELETE /topics/{topicId}: Delete specific topic by ID for the authenticated user

- GET /concepts/labels: Fetch unique labels in concepts for the authenticated user

- GET /concepts/filter: Filter concepts by topic, subtopic, or label for the authenticated user

- GET /search: Search for concepts by text query in title, description, or content (search both theories and questions)

- GET /suggestions: Get study suggestions based on categories, labels, or recent updates

- POST /upload: Upload content (e.g., git repository) to update the user's knowledge base

- POST /export: Export the user's knowledge base in various formats (e.g., Markdown, PDF)

- GET /stats: Retrieve knowledge base statistics, such as the number of theories, questions, and topic distributions

## 5. *Use Cases*

### 5.1 Students
- Students can use Thinktory to help them study for an upcoming exam by adding theory, creating questions, and organizing their study materials with topics and labels.

### 5.2 Professionals
- Professionals can use Thinktory to stay current with industry trends and knowledge by creating a personal knowledge base and using the chatbot to quiz themselves on the content.

### 5.3 Sharing knowledge bases
- Users can share their knowledge bases with others by exporting them to git repositories and hosting them on platforms like GitHub.
