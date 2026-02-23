# 🚀 RevConnect – Git Workflow Guidelines

This project follows a structured Git workflow to ensure clean collaboration and avoid conflicts.

Please read and follow these instructions carefully.

---

# 📌 Branch Structure

We use the following branches:

- `main` → Stable and demo-ready code only
- `develop` → Integration branch (all features merge here first)
- `feature/*` → Individual feature branches

Examples:
- feature/auth
- feature/profile
- feature/posts
- feature/network
- feature/feed

---

# ⚠ Important Rules

❌ Do NOT push directly to `main`  
❌ Do NOT push directly to `develop`  
✅ Always create and work in your own `feature/*` branch  
✅ Merge feature branches into `develop` via Pull Request  

---

# 🛠 Step-by-Step Workflow

## 1️⃣ Clone the Repository

```bash
git clone <REPOSITORY_URL>
cd RevConnect-CAPSTONE
```

---

## 2️⃣ Switch to Develop Branch

```bash
git checkout develop
git pull origin develop
```

Always create feature branches from `develop`.

---

## 3️⃣ Create Your Feature Branch

Replace `your-feature-name` with your module name.

Example:
- auth
- profile
- posts
- network
- feed

```bash
git checkout -b feature/your-feature-name
git push -u origin feature/your-feature-name
```

Example:
```bash
git checkout -b feature/profile
git push -u origin feature/profile
```

---

## 4️⃣ Work on Your Feature

After completing your work:

```bash
git add .
git commit -m "Implemented profile creation and update APIs"
git push
```

---

## 5️⃣ Create Pull Request

Go to GitHub:

Pull Requests → New Pull Request

Base branch:
```
develop
```

Compare branch:
```
feature/your-feature-name
```

Click:
→ Create Pull Request  
→ Merge Pull Request  

---

# 🔁 After Your PR is Merged

Update your local develop branch:

```bash
git checkout develop
git pull origin develop
```

Delete your feature branch locally (optional but recommended):

```bash
git branch -d feature/your-feature-name
```

Delete it from remote (optional):

```bash
git push origin --delete feature/your-feature-name
```

---

# 🧠 Important Best Practices

- Always pull latest `develop` before starting new work
- Write clear commit messages
- Do not modify other members’ features without discussion
- Resolve conflicts carefully before merging
- Keep your feature branch focused on ONE feature only

---

# 🎯 Workflow Summary

```
develop → feature/xyz → develop → main
```

Only the Team Lead merges `develop` into `main`.

---

# ✅ Commit Message Format

Use clear and professional messages:

- Implemented login API with JWT
- Added profile update functionality
- Fixed validation bug in registration
- Added post creation endpoint

Avoid messages like:
- update
- changes
- done

---

# 🏁 Final Note

Following this workflow ensures:

- Clean Git history
- No accidental overwrites
- Easier debugging
- Professional collaboration

Let’s build RevConnect properly 🚀
