# 🌱 Plant Buddy: Plant Care & Watering Scheduler

![Plant Buddy Dashboard](httpsd://i.imgur.com/83gA0b0.png) 
This project is a full-stack, responsive web application designed to help users efficiently manage and care for their plants. [cite_start]It provides a beautiful, modern interface for tracking plant collections, uploading images, and receiving smart, flexible care reminders. [cite: 3]

[cite_start]Built using Java (Spring Boot) for the backend and a cloud-hosted PostgreSQL (Supabase) database, the application is secure, scalable, and easy to use. [cite: 5]

## ✨ Key Features

* **Secure User Management:** Full user registration and login functionality using **Spring Security** (with password hashing).
* **Complete Plant CRUD:** Users can **Add**, **Edit**, **Delete**, and **View** all their plants.
* **Smart Scheduling:** The care scheduler is flexible. [cite_start]When a task is marked "complete," the *next* task is scheduled based on the date of completion, not a rigid calendar. [cite: 11]
* [cite_start]**Dynamic Dashboard:** A main dashboard provides an "at-a-glance" view of **Overdue Tasks** and **Upcoming Tasks**. [cite: 6, 13]
* [cite_start]**Cloud Image Uploads:** A custom-built Java service uploads plant images directly to a **Supabase Storage** bucket. [cite: 10]
* **Modern UI/UX:**
    * Beautiful, responsive, and clean UI built with **Tailwind CSS (CDN)**.
    * A persistent, client-side **Dark Mode** toggle.
    * A "Quick View" dashboard with a separate "View All Plants" page for scalability.
* [cite_start]**Dynamic Widgets:** Includes a "Today's Care Tip" widget and a styled (static) local weather card. [cite: 14]

## ⚙️ Tech Stack

| Component | Technology | Role |
| :--- | :--- | :--- |
| **Backend** | Java 17, Spring Boot 3 | Core application logic, security, and API creation. |
| **Database** | PostgreSQL (Hosted on Supabase) | [cite_start]Data storage for users, plants, and tasks. [cite: 5] |
| **File Storage** | Supabase Storage | Hosts all user-uploaded plant images. |
| **Data Access** | Spring Data JPA / Hibernate | Manages all database queries and relationships. |
| **Security** | Spring Security | Handles authentication, authorization, and CSRF. |
| **Frontend** | Thymeleaf | Server-side rendering of HTML pages. |
| **Styling** | Tailwind CSS (Play CDN) | Utility-first CSS for the complete modern UI. |

---

## 🚀 Getting Started: Setup & Installation

Follow these steps to get a local copy of the project running on your machine.

### Prerequisites

* **Java JDK 17** or newer.
* **A free Supabase Account** (for the PostgreSQL database and file storage).

### 1. Clone the Repository

```bash
git clone [Your-GitHub-Repo-URL]
cd PCnWS
