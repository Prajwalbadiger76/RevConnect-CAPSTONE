# 📦 RevConnect – Module Assignment & Responsibilities

This document defines feature ownership and module breakdown for the RevConnect project.

Each team member is responsible for their assigned module.

---

# 🟢 MODULE 1 – Authentication & Security

## 🔹 Features to Build

- Register (Personal / Creator / Business)
- Login (email/username + password)
- Password encryption (BCrypt)
- JWT-based authentication
- Role-Based Access Control (PERSONAL, CREATOR, BUSINESS)
- Logout
- Profile privacy (public/private)
- Authorization filters
- Exception handling (401, 403)
- Secure endpoints
- Token validation middleware
- Global error handling

## 🔹 Deliverables

- `AuthController`
- `SecurityConfig`
- `JwtService`
- `UserDetailsService`
- Password Encoder configuration
- Role-based access implementation


---

# 🟢 MODULE 2 – User Profile Management

## 🔹 Personal User Profile Features

- Create profile
- Edit profile
- Upload profile picture
- View own profile
- View other users' profiles
- Search users by name/username
- Set profile privacy

## 🔹 Creator/Business Enhanced Profile

- Add category/industry
- Contact information
- Website & social links
- Business address
- Business hours
- Multiple external links
- Showcase products/services

## 🔹 Deliverables

- `ProfileController`
- `UserProfile` entity
- File upload service
- Profile DTOs
- Privacy handling logic


---

# 🟢 MODULE 3 – Post Management

## 🔹 Features

- Create post (text + hashtags)
- Edit post
- Delete post
- View posts on profile
- Share/Repost with attribution
- Schedule posts
- Pin posts
- Promotional posts
- CTA buttons ("Learn More", "Shop Now")
- Tag products/services

## 🔹 Deliverables

- `PostController`
- `Post` entity
- Hashtag logic
- Share logic
- Scheduling service
- Pin post logic


---

# 🟢 MODULE 4 – Social Interactions

## 🔹 Features

- Like post
- Unlike post
- Comment on post
- View comments
- Delete own comment
- Respond to comments (business/creator accounts)
- Engagement tracking

## 🔹 Deliverables

- `Like` entity
- `Comment` entity
- `SocialInteractionController`
- Engagement calculation logic

👤 **Assigned To:** Member 4

---

# 🟢 MODULE 5 – Network & Follow System

## 🔹 Features

- Send connection request
- Accept / Reject request
- View pending sent/received requests
- View connections list
- Remove connection
- Follow accounts
- Unfollow accounts
- View followers list
- View following list

## 🔹 Deliverables

- `Connection` entity
- `Follow` entity
- `NetworkController`
- Status management logic


---

# 🟢 MODULE 6 – Feed, Notifications & Analytics

## 🔹 Feed Features

- Personalized feed
- Trending hashtags
- Filter by user type
- Search posts by hashtag

## 🔹 Notification Features

- In-app notifications
- Unread notification count
- Mark notifications as read
- Notification history
- Notification preferences

## 🔹 Analytics (Creator/Business Accounts)

- Post analytics (likes, comments, shares)
- Engagement metrics
- Follower demographics
- Reach count

## 🔹 Deliverables

- Feed service
- `Notification` entity
- Analytics service
- Trending algorithm
- Notification listener system


---

# 🎯 Team Workflow Reminder

- Each member works in their own `feature/*` branch.
- All features must merge into `develop` first.
- Only stable code is merged into `main`.
- Keep modules isolated and avoid modifying other members’ domains without discussion.

---

