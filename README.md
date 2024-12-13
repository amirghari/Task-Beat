# TaskBeat

TaskBeat is an Android application designed to help users manage nad track their daily tasks and health-related activities while promoting overall wellness. With advanced AI features, heart rate monitoring, and health tips, TaskBeat combines innovation and user-centric design to improve productivity and well-being.

---

## **Features**

### **1. Health Monitoring**
- Log essential health data, including weight, height, blood pressure, glucose levels, and water intake.
- Monitor heart rate using your phone's camera and receive insights into your overall health.

### **2. AI Integration & Health Recommendations**
- Powered by the **Gemini Gemma-2-2B Model**, TaskBeat customizes health recommendations and personal tasks based on user inputs.
- Encourages wellness through actionable AI-driven suggestions.

### **3. Data Visualization**
- Offers an intuitive dashboard to visualize health trends over time.
- Displays heart rate graphs and tracks progress toward health goals.

### **4. Seamless Integration**
- Supports user registration and login via **Firebase Authentication** and **Google OAuth**, ensuring a secure and seamless experience.

---

## **Software Architecture**

### **1. Architecture**
TaskBeat follows a **Model-View-ViewModel (MVVM)** architecture to ensure clean, modular, and maintainable code:
- **View (UI)**: Built using **Jetpack Compose** for modern, declarative UI.
- **ViewModel**: Manages UI-related data and lifecycle awareness using **LiveData**.
- **Repository Pattern**: Abstracts data access and manages local and remote sources seamlessly.
- **Dependency Injection (DI)**: Ensures modularity and scalability.

### **2. Data Storage**
- **Room Database**: Handles local storage for tasks and reminders.
- **Firebase Firestore**: Manages remote storage for syncing user data.

### **3. User Authentication**
- **Firebase Authentication**: Enables secure sign-up and login functionality.
- **OAuth Integration**: Supports authentication via Google accounts for a frictionless experience.

### **4. Design Patterns**
- **MVVM**: Promotes separation of concerns, enhancing testability and maintainability.
- **Repository Pattern**: Simplifies data access logic, unifying local and remote data management.

---

## **Project Goals**

### **1. Health Monitoring**
- Provide users with tools to log and track key health data like weight, height, blood pressure, glucose levels, and water intake.
- Leverage phone cameras for heart rate monitoring, offering users valuable health insights.

### **2. AI Integration & Health Recommendations**
- Utilize the **Gemini Gemma-2-2B Model** to personalize user experiences by providing tailored health recommendations and task suggestions.

### **3. Data Visualization**
- Develop a user-friendly dashboard to present health trends and goal progress clearly.

### **4. Seamless Integration**
- Ensure smooth registration and login processes with **Firebase Authentication** and Google OAuth.

---

## **Tech Stack**

- **Language**: Kotlin
- **Development Environment**: Android Studio
- **UI Framework**: Jetpack Compose
- **Local Storage**: Room Database
- **Remote Storage**: Firebase Firestore
- **AI Integration**: Gemini Gemma-2-2B Model for task recommendations
- **Notifications**: Firebase Cloud Messaging (FCM)
- **Authentication**: Firebase Authentication & OAuth integration
- **Data Visualization**: Graphs and dashboards via Jetpack Compose.

---

## **Getting Started**

### **1. Prerequisites**
- Android Studio (latest version).
- Android device or emulator with heart rate sensor or camera access.

### **2. Installation**
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/TaskBeat.git
