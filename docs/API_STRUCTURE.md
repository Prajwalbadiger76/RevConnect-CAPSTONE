# 🌐 RevConnect – API Endpoint Structure (Module-wise)

This document defines the standardized REST endpoint structure.

All team members MUST follow this structure to avoid conflicts.

Base URL:
```
/api
```

---

# 🟢 MODULE 1 – Authentication & Security

Base Path:
```
/api/auth
```

Endpoints:

- POST   /api/auth/register
- POST   /api/auth/login
- POST   /api/auth/logout
- GET    /api/auth/me

Notes:
- JWT token required for secured endpoints.
- Role-based access enforced via Spring Security.

---

# 🟢 MODULE 2 – Profile Management

Base Path:
```
/api/profiles
```

Endpoints:

- GET    /api/profiles/{username}
- PUT    /api/profiles/me
- GET    /api/profiles/search?query=
- PATCH  /api/profiles/privacy

For Business/Creator:

- PUT    /api/profiles/business/details
- POST   /api/profiles/business/products
- GET    /api/profiles/{username}/products

---

# 🟢 MODULE 3 – Post Management

Base Path:
```
/api/posts
```

Endpoints:

- POST   /api/posts
- PUT    /api/posts/{postId}
- DELETE /api/posts/{postId}
- GET    /api/posts/{postId}
- GET    /api/posts/user/{username}
- POST   /api/posts/{postId}/share
- PATCH  /api/posts/{postId}/pin
- POST   /api/posts/{postId}/schedule

Hashtags:

- GET    /api/posts/hashtag/{tag}

---

# 🟢 MODULE 4 – Social Interactions

Base Path:
```
/api/interactions
```

Likes:

- POST   /api/interactions/posts/{postId}/like
- DELETE /api/interactions/posts/{postId}/like

Comments:

- POST   /api/interactions/posts/{postId}/comment
- DELETE /api/interactions/comments/{commentId}
- GET    /api/interactions/posts/{postId}/comments

---

# 🟢 MODULE 5 – Network & Follow System

Base Path:
```
/api/network
```

Connections:

- POST   /api/network/connections/{userId}
- PATCH  /api/network/connections/{requestId}/accept
- PATCH  /api/network/connections/{requestId}/reject
- GET    /api/network/connections
- DELETE /api/network/connections/{userId}

Follows:

- POST   /api/network/follow/{userId}
- DELETE /api/network/unfollow/{userId}
- GET    /api/network/followers/{userId}
- GET    /api/network/following/{userId}

---

# 🟢 MODULE 6 – Feed, Notifications & Analytics

Feed:

Base Path:
```
/api/feed
```

- GET /api/feed
- GET /api/feed/trending
- GET /api/feed/filter?type=
- GET /api/feed/hashtag/{tag}

---

Notifications:

Base Path:
```
/api/notifications
```

- GET    /api/notifications
- GET    /api/notifications/unread-count
- PATCH  /api/notifications/{id}/read
- PATCH  /api/notifications/read-all

---

Analytics (Creator/Business Only)

Base Path:
```
/api/analytics
```

- GET /api/analytics/posts/{postId}
- GET /api/analytics/engagement
- GET /api/analytics/followers
