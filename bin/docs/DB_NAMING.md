# 🗄 RevConnect – Database Naming Contract (Finalized)

⚠️ IMPORTANT:
All team members MUST follow these exact table names, entity names, and column names.
No changes without team discussion.

Database: Oracle
Naming Convention: UPPERCASE table names, snake_case columns.

---

# 🟢 1️⃣ USER & ROLE MODULE

## TABLE: USERS
Entity: User

Columns:

- id (PK, NUMBER)
- email (VARCHAR2, UNIQUE, NOT NULL)
- username (VARCHAR2, UNIQUE, NOT NULL)
- password (VARCHAR2, NOT NULL)
- role (VARCHAR2) → PERSONAL / CREATOR / BUSINESS
- is_private (NUMBER(1)) → 0 or 1
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

Relationships:
- One-to-One with PROFILES
- One-to-Many with POSTS
- One-to-Many with COMMENTS
- One-to-Many with LIKES
- One-to-Many with SHARES

---

# 🟢 2️⃣ PROFILE MODULE

## TABLE: PROFILES
Entity: Profile

Columns:

- id (PK)
- user_id (FK → USERS.id, UNIQUE)
- full_name
- bio
- profile_picture_url
- location
- website
- category
- contact_email
- phone_number
- business_address
- business_hours

Relationship:
USERS (1) —— (1) PROFILES

Each user has exactly one profile.

---

# 🟢 3️⃣ POST MODULE

## TABLE: POSTS
Entity: Post

Columns:

- id (PK)
- user_id (FK → USERS.id)
- content (CLOB)
- post_type (VARCHAR2) → NORMAL / PROMOTIONAL
- is_pinned (NUMBER(1))
- scheduled_at (TIMESTAMP)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

Relationship:
USERS (1) —— (N) POSTS

---

## TABLE: HASHTAGS
Entity: Hashtag

Columns:

- id (PK)
- name (VARCHAR2, UNIQUE)

---

## TABLE: POST_HASHTAGS
Entity: PostHashtag

Columns:

- post_id (FK → POSTS.id)
- hashtag_id (FK → HASHTAGS.id)

Composite Primary Key:
(post_id, hashtag_id)

Relationship:
POSTS (N) —— (N) HASHTAGS

---

# 🟢 4️⃣ SOCIAL INTERACTIONS MODULE

## TABLE: LIKES
Entity: Like

Columns:

- id (PK)
- user_id (FK → USERS.id)
- post_id (FK → POSTS.id)
- created_at (TIMESTAMP)

Constraint:
UNIQUE (user_id, post_id)

---

## TABLE: COMMENTS
Entity: Comment

Columns:

- id (PK)
- user_id (FK → USERS.id)
- post_id (FK → POSTS.id)
- content (CLOB)
- created_at
- updated_at

Relationship:
POSTS (1) —— (N) COMMENTS

---

## TABLE: SHARES
Entity: Share

Columns:

- id (PK)
- user_id (FK → USERS.id)
- post_id (FK → POSTS.id)
- shared_at (TIMESTAMP)

---

# 🟢 5️⃣ NETWORK MODULE

## TABLE: CONNECTIONS
Entity: Connection

Columns:

- id (PK)
- sender_id (FK → USERS.id)
- receiver_id (FK → USERS.id)
- status (VARCHAR2) → PENDING / ACCEPTED / REJECTED
- created_at (TIMESTAMP)

Constraint:
UNIQUE (sender_id, receiver_id)

---

## TABLE: FOLLOWS
Entity: Follow

Columns:

- id (PK)
- follower_id (FK → USERS.id)
- following_id (FK → USERS.id)
- created_at (TIMESTAMP)

Constraint:
UNIQUE (follower_id, following_id)

---

# 🟢 6️⃣ NOTIFICATION MODULE

## TABLE: NOTIFICATIONS
Entity: Notification

Columns:

- id (PK)
- recipient_id (FK → USERS.id)
- sender_id (FK → USERS.id)
- type (VARCHAR2) → LIKE / COMMENT / FOLLOW / CONNECTION / SHARE
- reference_id (NUMBER)
- is_read (NUMBER(1))
- created_at (TIMESTAMP)

Relationship:
USERS (1) —— (N) NOTIFICATIONS

---

# 🟢 7️⃣ BUSINESS MODULE

## TABLE: PRODUCTS
Entity: Product

Columns:

- id (PK)
- user_id (FK → USERS.id)
- name
- description
- price (NUMBER)
- image_url
- created_at

---

## TABLE: POST_PRODUCTS
Entity: PostProduct

Columns:

- post_id (FK → POSTS.id)
- product_id (FK → PRODUCTS.id)

Composite Primary Key:
(post_id, product_id)

---

# 🟢 8️⃣ ANALYTICS MODULE (Optional Physical Table)

## TABLE: POST_ANALYTICS
Entity: PostAnalytics

Columns:

- id (PK)
- post_id (FK → POSTS.id, UNIQUE)
- total_likes (NUMBER)
- total_comments (NUMBER)
- total_shares (NUMBER)
- reach_count (NUMBER)
- updated_at (TIMESTAMP)

---

# 🧠 GLOBAL RULES

1. All foreign keys must use *_id naming.
2. All boolean values use NUMBER(1) (0 or 1) in Oracle.
3. All enum values must be stored as VARCHAR2.
4. Do NOT rename tables or columns without team approval.
5. Use singular entity names (User, Post, Comment).
6. Use plural table names (USERS, POSTS, COMMENTS).

---

# 🏗 ENTITY OWNERSHIP (Module Mapping)

Authentication → USERS  
Profile → PROFILES  
Posts → POSTS, HASHTAGS, POST_HASHTAGS  
Social → LIKES, COMMENTS, SHARES  
Network → CONNECTIONS, FOLLOWS  
Notifications → NOTIFICATIONS  
Business → PRODUCTS, POST_PRODUCTS  
Analytics → POST_ANALYTICS  

---

