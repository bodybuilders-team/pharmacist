# pharmacist

> A simple but powerful **Pharmacy Management** application ⚕️.

## Authors

- [110817 André Páscoa](https://github.com/devandrepascoa)
- [110860 André Jesus](https://github.com/andre-j3sus)
- [110893 Nyckollas Brandão](https://github.com/Nyckoka)

Professor: João Garcia

@IST<br>
Master in Computer Science and Computer Engineering<br>
Mobile and Ubiquitous Computing - Group 03<br>
Summer Semester of 2023/2024

## Table of Contents 📜

- [Architecture 🏗️](#architecture-️)
    - [Backend](#backend)
    - [Frontend](#frontend)
- [How to Run ▶️](#how-to-run-️)

<!--For more in-depth knowledge about the project, check the paper about it [here](./ist-meic-cmu-g03.pdf).-->

---

## Architecture 🏗️

### Backend

The backend code is located in the [`src/backend`](./src/backend) directory.

The backend is a **REST API** built using **[Spring Boot](https://spring.io/projects/spring-boot)** and
**[Kotlin](https://kotlinlang.org/)**. It is responsible for managing the data and the business logic of the
application.

It's implemented following the **layered architecture** pattern, with the following layers:

- **HTTP**: responsible for handling the HTTP requests and responses, and the HTTP pipeline.
- **Service**: responsible for the business logic of the application.
- **Repository**: responsible for managing the data.

For simplicity, the backend uses an **in-memory database** to store the data. This means that the data is not persisted
between runs of the application. A future improvement could be to use a **relational database** to store the data.

### Frontend

The frontend code is located in the [`src/frontend`](./src/frontend) directory.

The frontend is a **mobile application** for **Android** built using **[Kotlin](https://kotlinlang.org/)** and
**[Jetpack Compose](https://developer.android.com/develop/ui/compose)**. It is
responsible for providing a user interface for the application.

The frontend code is organized as follows:

- [Domain](src/frontend/app/src/main/kotlin/pt/ulisboa/ist/pharmacist/domain): contains the domain models of the
  application.
- [Service](src/frontend/app/src/main/kotlin/pt/ulisboa/ist/pharmacist/service): contains the service classes that
  interact with the backend.
- [Session](src/frontend/app/src/main/kotlin/pt/ulisboa/ist/pharmacist/session): contains the session classes that
  manage the user session.
- [UI](src/frontend/app/src/main/kotlin/pt/ulisboa/ist/pharmacist/ui): contains the UI components of the application.
    - [Screens](src/frontend/app/src/main/kotlin/pt/ulisboa/ist/pharmacist/ui/screens): contains the screens of the
      application, where each screen is implemented using a:
        - **<ScreenName>Screen.kt**: the screen composable.
        - **<ScreenName>ViewModel.kt**: the view model of the screen, responsible for managing the screen's state.
        - **<ScreenName>Activity.kt**: the activity that hosts the screen.

---

## How to Run ▶️

To run the project, follow these steps:

1. Clone the repository:

```bash
git clone
```

2. Run the **Docker Compose** command in the `src/backend` directory to start the server:

```bash
cd src/backend
docker-compose up
```

3. Open the frontend project in **Android Studio** and run the application on an **Android Emulator** or a **Physical
   Device**.
