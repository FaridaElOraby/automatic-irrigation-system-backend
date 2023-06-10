# Irrigation System Simulation

This repository contains the code and documentation for a simulated irrigation system built using Spring Boot in Java. <br/>
The system allows users to simulate the watering of agricultural lands by scheduling watering slots.

---

## Problem Description:

As a irrigation system which helps the automatic irrigation of agricultural lands without human intervention, system has to
be designed to fulfil the requirement of maintaining and configuring the plots of land by the irrigation time slots and the
amount of water required for each irrigation period.
The irrigation system should have integration interface with a sensor device to direct letting the sensor to irrigate based on
the configured time slots/amount of water.
Requirements' Document can be found [here](./Backend_Full-stack%20exercise_v5.0.pdf)

---

## Contents

The repository contains the following files:

- README.md: This file
- [UML diagram](./uml_diagram.png): The UML diagram for the irrigation system
- [Folder Structure](./folder_structure.pdf): The folder structure for the irrigation system
- Task: Folder containing Spring Boot project

---

## Installation

To install the irrigation system simulation, follow these steps:

1. Clone the repository to your local machine.
2. Open the project in your preferred Java IDE. This project is implemented using Java 8.
3. Run the TaskApplication.java file to start the Spring Boot application.
4. The application runs on port 3052.
5. The Swagger documentation can be found at [/swagger-ui/index.html#](http://localhost:3052/swagger-ui/index.html#/).
6. The H2 Database console can be found at [/h2-console](http://localhost:3052/h2-console).
   - JDBC URL: [jdbc:h2:file:./mydatabase](./mydatabase.mv.db)
   - User Name: sa
   - Password: (leave blank)

---

## Usage

The irrigation system simulation provides a REST API for interacting with the system. The following endpoints are available:

### Health Check API:

- GET /health:
  > returns server status and upTime in seconds

### Land APIs:

- POST /api/lands: <br/>

  > Create land and automatically create a sensor assigned to this land. The sesnor's property "water dispense per second" is also specified in the request body. This land is still not configured so no scheduled watering slots are assigned to it. </br>
  >
  > #### Enums: <br/>
  >
  > #### [Crop Types](./src/main/java/io/irrigation/task/model/enums/CropType.java) <br/>
  >
  > #### [Land Types](./src/main/java/io/irrigation/task/model/enums/LandType.java) <br/>

- POST /api/lands/{id}/configure:

  > Configure land with irrigationRateInSeconds, waterAmount and maxRetries. Each land can only have one configuration. This API adds a new configuration to an unconfigured land or reconfigures and overrides the old configuration if exists.

- GET /api/lands/{id}:
  > Get land details which are landId, name, crop type, land type, assigned sensor Id and the configuration details which are irrigationRateInSeconds, waterAmount, maxRetries.
- GET /api/lands:
  > Get all lands with land details which are landId, name, crop type, land type, assigned sensor Id, irrigationRateInSeconds, waterAmount, maxRetries
- GET /api/lands/{id}/slots:
  > Get land details which are landId, name, crop type, land type, assigned sensor Id and the configuration details which are irrigationRateInSeconds, waterAmount, maxRetries.et scheduled land watering slots with information on slotId, slot status, retries, maxRetries.
- PUT /api/lands/{id}:
  > Update land with optional fields: name, crop type and land type
- DELETE /api/lands/{id}:
  > Delete land and automatically delete a sensor assigned to this land and any of the land's watering slots

### Sensor APIs:

- GET /api/sensors/{id}:
  > Get sensor details whcih are sensorId, status, and water dispense per second
- PUT /api/sensors/{id}:
  > Update sensor with required field water dispense per second
- GET /api/sensors:
  > Get all sensors with sensorId, status and water dispense per second

---

## System Design:

The irrigation system allows users to **create lands** and **configure** them. A **sensor** is automatically created and linked to this land. The sensor's only responsibility is to water this land.

When configuring a land, the user can specify the configuration properties or rely on the system to **recommend a configuration** based on the crop type and land type specified when creating the land. Once a land is configured, a **watering slot** is scheduled with the current date as its irrigation start date.

A scheduled function **runSlots()** runs every 20 seconds and checks if there are any pending slots that are due to be executed. If there are pending slots that are due to be executed, the function **sends a signal to the sensor** to start watering the land and specifies the duration of watering. The duration of watering is calculated based on the amount of water needed for this land and the amount of water the sensor can dispense per second.

Once the watering slot is done watering the land, it **schedules the next slot** based on the rate of irrigation specified when configuring the land. If the **slot failed** to water the land, an **alert is printed** in the console with the slotId.

**Seeding data** is added at the beginning of running the application if the database is empty. The seeding data creates records for the **land configuration recommendation** based on crop type and land type. The recommendation is simulated by adding random configuration for every crop type and land type duo. The **seeding data creates one land record** with the name "Seeded Land" and configures it based on the recommendation. Once this land is configured, the first watering slot is scheduled.

---

## Implementation Time:

- 10 hours watching Spring Boot course as it was my first time using it.
- 15 hours setting up Sprint Boot and downloading IDE and dependcies.
- 5 hours setting up H2 Embedded Database
- 20 hours implementing APIs and Services

Total: 50 hours in 10 days (around 5 hours per day)

---

## Future Development:

- Secure the end points.
- Full Unit tests
- Integration test
- Handle Exceptions
- Response Strategies for each status code, now the applicatin relies on http status codes only.

---
