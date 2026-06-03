# 🚀 Trackly Learning Journal

> A personal engineering log documenting concepts, design decisions, trade-offs, mistakes, and lessons learned while building Trackly.

---

# 🎯 Project Goal

Trackly is not just a CRUD application.

The goal of this project is to:

- Learn backend engineering fundamentals.
- Understand system design trade-offs.
- Build production-style features instead of tutorial-style implementations.
- Develop confidence in explaining architectural decisions.
- Think like a product engineer rather than a tutorial follower.

---

# 🔐 Authentication Module

## What We Built

- User Registration
- User Login
- Password Hashing
- JWT Authentication (HS256)
- Role-Based Authorization
- Refresh Token Mechanism
- Session Management
- Logout Foundation
- Replay Attack Mitigation
- Multi-Device Session Support

---

# 🧠 Core Concepts Learned

## Authentication vs Authorization

### Authentication

Answers:

> "Who are you?"

Examples:

- Login
- JWT Validation
- Refresh Token Validation

### Authorization

Answers:

> "What are you allowed to do?"

Examples:

- Admin-only endpoints
- Resource ownership checks
- Role-based access control

---

## Stateless Authentication

JWT access tokens allow the server to remain stateless.

The server does not store login sessions for access tokens.

Each request carries proof of identity within the token.

---

## JWT Structure

JWT consists of:

### Header

Contains metadata:

- Token type
- Signing algorithm (HS256)

### Payload

Contains claims:

- User ID
- Role
- Issued At
- Expiration Time

### Signature

Generated using:

- Encoded Header
- Encoded Payload
- Secret Key

Purpose:

- Verify authenticity
- Detect tampering

---

## SecurityContextHolder

One of the most important Spring Security concepts learned.

JWT validation alone is not enough.

Spring only considers a user authenticated after an Authentication object is stored inside SecurityContextHolder.

Without this:

- JWT may be valid
- Spring still treats user as anonymous

Result:

- Authorization fails
- Protected endpoints return 401

---

# 🔄 Request Flow Through The System

```text
Request
 ↓
SecurityFilterChain
 ↓
JwtAuthenticationFilter
 ↓
JWT Validation
 ↓
Load User From DB
 ↓
SecurityContextHolder
 ↓
Authorization Checks
 ↓
Controller
 ↓
Service
 ↓
Repository
 ↓
Response
```

Key realization:

SecurityContextHolder is the bridge between authentication and authorization.

---

# ⚖️ Design Decisions & Trade-offs

## Why Access Tokens Expire Quickly

Decision:

15–30 minute expiry

Reason:

If an access token is stolen, damage is limited.

Trade-off:

Users need a mechanism to obtain new access tokens.

Solution:

Refresh tokens.

---

## Why Refresh Tokens Exist

Without refresh tokens:

- Users must login repeatedly
- Poor user experience

Refresh tokens allow issuance of new access tokens without forcing login.

---

## Why Refresh Tokens Are Stored In Database

### Alternative

Pure JWT refresh tokens.

### Chosen Approach

Database-backed refresh tokens.

### Reason

Supports:

- Logout
- Revocation
- Session management
- Multi-device login

### Trade-off

Extra database lookup required.

---

## Why Refresh Tokens Are Hashed

Refresh tokens are treated similarly to passwords.

Stored:

```text
Raw Token → Hash → Database
```

Reason:

If database is compromised, active refresh tokens remain protected.

---

## Why HttpOnly Cookies Were Used

Refresh tokens are stored inside HttpOnly cookies.

Benefits:

- JavaScript cannot access them
- Reduced XSS exposure
- Browser sends automatically

---

## Why We Chose Fixed Expiry

Decision:

Refresh tokens expire after a fixed duration (7 days).

Example:

```text
Day 1 → R1 Created
Day 3 → R2 Rotated
Day 5 → R3 Rotated

All expire on Day 7
```

Reason:

Limits damage if a refresh token is stolen.

---

## Sliding Expiry (Rejected)

Alternative:

Each refresh extends expiry.

Reason Rejected:

A stolen refresh token could potentially remain usable indefinitely.

---

# 🔁 Refresh Token Rotation

## Decision

Every refresh request:

```text
Old Refresh Token → Revoked
New Refresh Token → Issued
```

---

## Why?

To reduce replay attack risk.

---

# 🛡 Security Concepts

## Replay Attack

A replay attack occurs when an attacker steals a valid refresh token and attempts to reuse it.

Without rotation:

```text
Attacker steals R1
↓
Uses R1 repeatedly
↓
Unlimited access until expiry
```

With rotation:

```text
R1 Used
↓
R1 Revoked
↓
R2 Issued

Attacker Uses R1 Again
↓
401 Unauthorized
```

---

## Token Theft Race Condition

Limitation discussed:

If attacker steals the latest active refresh token and refreshes before the real user:

- Attacker receives next token
- User gets logged out
- Attacker temporarily wins the session

Advanced systems implement reuse detection to mitigate this.

---

## Multi-Device Sessions

Decision:

Allow multiple active refresh tokens.

Reason:

User may be logged in from:

- Phone
- Laptop
- Different browser

Logout only revokes the current session.

---

## Resource Enumeration

Returning 403 can reveal a resource exists.

Example:

```http
GET /jobs/42
```

Response:

```http
403 Forbidden
```

This tells attackers:

> Job 42 exists.

Alternative:

```http
404 Not Found
```

Now attackers cannot distinguish:

- Resource does not exist
- Resource belongs to someone else

Preferred approach for user-owned resources.

---

# 🚨 401 vs 403

## 401 Unauthorized

Meaning:

Authentication failed.

Examples:

- No token
- Invalid token
- Expired token
- Empty SecurityContext

---

## 403 Forbidden

Meaning:

Authentication succeeded but authorization failed.

Examples:

- USER accessing ADMIN endpoint
- Ownership validation failure

---

# 🔧 Custom Security Components

## CustomAuthenticationEntryPoint

Purpose:

Handle authentication failures.

Returns:

```json
{
  "status": 401,
  "error": "Unauthorized"
}
```

Reason:

Consistent API responses.

---

## CustomAccessDeniedHandler

Purpose:

Handle authorization failures.

Returns:

```json
{
  "status": 403,
  "error": "Forbidden"
}
```

Reason:

Consistent API responses.

---

## Why GlobalExceptionHandler Cannot Replace Them

Security failures occur before controller execution.

Flow:

```text
Request
 ↓
Security Layer
 ↓
Controller
```

GlobalExceptionHandler only handles exceptions from:

- Controller
- Service
- Repository

Not Spring Security failures.

---

# ⚡ Performance vs Consistency

## Option A: Trust JWT Claims

Pros:

- Faster
- No database lookup

Cons:

- Role changes delayed
- Deleted users remain valid until token expiry

---

## Option B: Database Lookup (Chosen)

Pros:

- Immediate role updates
- Deleted users lose access instantly

Cons:

- Additional database query

Decision:

Consistency prioritized over performance.

---

# 🤔 Common Confusions & Lessons

Things that were initially confusing:

- Why SecurityContextHolder is necessary
- Difference between 401 and 403
- Why refresh endpoint uses permitAll()
- Why JWT validation alone is insufficient
- Why refresh token rotation exists
- Why DB lookup is done despite JWT being stateless
- Controller vs Service vs Repository responsibilities
- Where ownership checks should live

---

# 🐞 Mistakes Encountered

- Missing @ExceptionHandler annotation on validation handler
- Confusion between authentication and authorization
- Misunderstanding refresh token rotation initially
- Forgetting why SecurityContextHolder was required
- Mixing fixed expiry and sliding expiry concepts

---

# 📌 Not Yet Implemented

Future enhancements:

- Password Change
- Forgot Password Flow
- Global Logout
- Refresh Token Reuse Detection
- Device Fingerprinting
- AI Features
- Deployment & Scaling

---

# 🏆 Biggest Takeaway

Security is not a single feature.

It is a collection of:

- Design decisions
- Trade-offs
- Lifecycle management
- Threat modeling
- Consistent architecture

The goal is not just to make the application work.

The goal is to understand *why* each decision was made.
