# 🧠 RevConnect – Database Design (High-Level ERD)

This document explains the database structure of RevConnect.
The schema is divided into logical modules so each team member understands their domain clearly.

---

# 📦 Module Breakdown

The database is divided into the following domains:

1. User & Roles
2. Profile Management
3. Posts & Hashtags
4. Social Interactions
5. Network System
6. Notifications
7. Business Features
8. Analytics

---

# 🟢 1️⃣ USER & ROLE MODULE

## 👤 USERS Table

Stores all registered users (Personal, Creator, Business).

**Fields:**

- id (PK)
- email (UNIQUE)
- username (UNIQUE)
- password
- role (PERSONAL / CREATOR / BUSINESS)
- is_private (boolean)
- created_at
- updated_at

This is the core table of the system.

---

# 🟢 2️⃣ PROFILE MODULE

## PROFILES Table

Stores user profile details.

**Fields:**

- id (PK)
- user_id (FK → USERS.id)
- full_name
- bio
- profile_picture_url
- location
- website
- category (for creator/business)
- contact_email
- phone_number
- business_address
- business_hours

**Relationship:**

USERS (1) -------- (1) PROFILES

Each user has exactly one profile.

---

# 🟢 3️⃣ POSTS & HASHTAGS MODULE

## POSTS Table

Stores all user posts.

**Fields:**

- id (PK)
- user_id (FK → USERS.id)
- content
- post_type (NORMAL / PROMOTIONAL)
- is_pinned
- scheduled_at
- created_at
- updated_at

**Relationship:**

USERS (1) -------- (N) POSTS

One user can create many posts.

---

## HASHTAGS Table

**Fields:**

- id (PK)
- name (UNIQUE)

---

## POST_HASHTAG (Join Table)

Used for Many-to-Many relationship.

**Fields:**

- post_id (FK → POSTS.id)
- hashtag_id (FK → HASHTAGS.id)

**Relationship:**

POSTS (N) -------- (N) HASHTAGS

---

# 🟢 4️⃣ SOCIAL INTERACTION MODULE

## LIKES Table

Stores likes on posts.

**Fields:**

- id (PK)
- user_id (FK → USERS.id)
- post_id (FK → POSTS.id)
- created_at

**Constraint:**

UNIQUE (user_id, post_id)

A user can like a post only once.

---

## COMMENTS Table

Stores comments on posts.

**Fields:**

- id (PK)
- user_id (FK → USERS.id)
- post_id (FK → POSTS.id)
- content
- created_at
- updated_at

**Relationship:**

POSTS (1) -------- (N) COMMENTS

---

## SHARES Table

Stores repost/share actions.

**Fields:**

- id (PK)
- user_id (FK → USERS.id)
- post_id (FK → POSTS.id)
- shared_at

---

# 🟢 5️⃣ NETWORK MODULE

## CONNECTIONS Table (Personal ↔ Personal)

Used for connection requests between personal users.

**Fields:**

- id (PK)
- sender_id (FK → USERS.id)
- receiver_id (FK → USERS.id)
- status (PENDING / ACCEPTED / REJECTED)
- created_at

---

## FOLLOWS Table (User → Creator/Business)

Stores follower relationships.

**Fields:**

- id (PK)
- follower_id (FK → USERS.id)
- following_id (FK → USERS.id)
- created_at

**Constraint:**

UNIQUE (follower_id, following_id)

---

# 🟢 6️⃣ NOTIFICATION MODULE

## NOTIFICATIONS Table

Stores in-app notifications.

**Fields:**

- id (PK)
- recipient_id (FK → USERS.id)
- sender_id (FK → USERS.id)
- type (LIKE / COMMENT / FOLLOW / CONNECTION / SHARE)
- reference_id (post_id or connection_id)
- is_read (boolean)
- created_at

**Relationship:**

USERS (1) -------- (N) NOTIFICATIONS

---

# 🟢 7️⃣ BUSINESS FEATURES MODULE

## PRODUCTS Table

Stores products/services for business accounts.

**Fields:**

- id (PK)
- user_id (FK → USERS.id)
- name
- description
- price
- image_url

---

## POST_PRODUCTS (Join Table)

Allows tagging products in posts.

**Fields:**

- post_id (FK → POSTS.id)
- product_id (FK → PRODUCTS.id)

---

# 🟢 8️⃣ ANALYTICS MODULE (Optional Physical Table)

Analytics can be:

✔ Calculated dynamically  
OR  
✔ Stored physically in DB  

If stored:

## POST_ANALYTICS Table

**Fields:**

- id (PK)
- post_id (FK → POSTS.id)
- total_likes
- total_comments
- total_shares
- reach_count
- updated_at

---

# 🏗 Overall Relationship Overview

```
USERS
 ├── PROFILES
 ├── POSTS
 │     ├── LIKES
 │     ├── COMMENTS
 │     ├── SHARES
 │     └── POST_HASHTAG
 ├── CONNECTIONS
 ├── FOLLOWS
 ├── NOTIFICATIONS
 └── PRODUCTS
```

---

# 📌 Important Notes for Developers

- Always use proper foreign key relationships.
- Use ENUM types for role, status, post_type, notification type.
- Avoid circular dependencies in JPA mappings.
- Use LAZY loading for collections.
- Use DTOs for API responses.

---

# 🎯 Module Ownership (For Team Assignment)

- Authentication → USERS
- Profile Management → PROFILES
- Posts → POSTS + HASHTAGS
- Social Interactions → LIKES, COMMENTS, SHARES
- Network → CONNECTIONS, FOLLOWS
- Notifications → NOTIFICATIONS
- Business Features → PRODUCTS
- Analytics → POST_ANALYTICS

---
