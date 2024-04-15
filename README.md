# pharmacist

> A simple but powerful **Pharmacy Management** application.

## Authors

- [110817 André Páscoa](https://github.com/devandrepascoa)
- [110860 André Jesus](https://github.com/andre-j3sus)
- [110893 Nyckollas Brandão](https://github.com/Nyckoka)

Professor: João Garcia

@IST<br>
Master in Computer Science and Computer Engineering<br>
Mobile and Ubiquitous Computing - Group ??<br>
Summer Semester of 2023/2024

## Table of Contents 📜

- [pharmacist](#pharmacist)
  - [Authors](#authors)
  - [Table of Contents 📜](#table-of-contents-)
  - [Architecture 🏗️](#architecture-️)
  - [How to Run ▶️](#how-to-run-️)

<!--For more in-depth knowledge about the project, check the paper about it [here](./ist-meic-dad-g05.pdf).-->

---

## Architecture 🏗️

_section in construction_

<!--
The solution to the project is divided into 4 projects:

* **DadtkvClient**: Client application that communicates with the Transaction Managers, using the DADTKVService
  interface.
* **DadtkvTransactionManager**: Transaction Manager application.
* **DadtkvLeaseManager**: Lease Manager application.
* **DadtkvCore**: Contains the interfaces and classes that are common to all the other projects, including the
  configuration of the system. It also contains the System Manager application, which is used to start and shutdown the
  system.
-->
---

## How to Run ▶️

_section in construction_

<!--
The project can be run using the System Manager application, which is located in the DadtkvCore project.

The System Manager only receives one argument, which is the path to the configuration file (relative to the solution). A
configuration file is located in `DadtkvCore/Configuration/configuration_sample.txt`.

To run the system, follow these steps:

1. Open a terminal in the **solution's root directory**.
2. Run `dotnet clean` to clean the solution (if needed).
3. Run `dotnet build` to build the solution.
4. Run `dotnet run --project DadtkvCore/DadtkvCore.csproj <configuration_file_path>` to run the System
   Manager application. For example, as the configuration file is located in the Configuration folder, the command would
   be `dotnet run --project DadtkvCore/DadtkvCore.csproj "./DadtkvCore/Configuration/configuration_sample.txt"`.
-->