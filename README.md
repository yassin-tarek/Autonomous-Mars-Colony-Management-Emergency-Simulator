# 🚀 MARS-OPS
## Autonomous Mars Colony Management & Emergency Simulator

MARS-OPS is a Java Object-Oriented Programming project that simulates the management of an autonomous Mars colony.

The system manages:

- 👨‍🚀 Human crew members
- 🤖 Robotic agents
- 🏢 Colony facilities
- 📦 Resources
- 🎯 Different types of missions
- 🚨 Emergency situations
- 🌪️ Mars environmental conditions
- ⏱️ Simulation time and mission lifecycle

The project demonstrates important **Object-Oriented Programming (OOP)** concepts such as inheritance, abstraction, polymorphism, encapsulation, enums, and composition.

---

# 📌 Project Features

## 👨‍🚀 Agent Management

The colony contains different types of agents.

### Crew Members

- 🔬 Scientist
- 🔧 Engineer
- 🏥 Medical Officer

### Robots

- 🚙 Exploration Rover
- 🔧 Repair Robot
- 🚁 Drone

Each agent has its own capabilities and operational behavior.

The system uses polymorphism to determine whether an agent can perform a specific mission.

---

# 🎯 Mission Management

The system supports multiple mission types:

- 🗺️ Exploration Mission
- 🔧 Repair Mission
- 🔬 Research Mission
- 🆘 Rescue Mission

Each mission contains information such as:

- Mission ID
- Mission name
- Mission type
- Duration
- Required resources
- Required capabilities
- Assigned agents
- Target facility
- Operation type
- Current mission state

---

# 🔄 Mission Lifecycle

A mission follows a lifecycle managed by the simulation system.

```text
ASSIGNED
    ↓
IN_PROGRESS
    ↓
COMPLETED
```

If a mission cannot be performed or encounters a problem:

```text
FAILED
```

The system also releases assigned agents when a mission is completed or failed.

---

# ⚠️ Mission Validation Rules

Before accepting a mission, the `SimulationManager` performs several validation checks.

### 1️⃣ Only One Active Mission

The colony cannot run multiple active missions at the same time.

```text
Mission A → Active
Mission B → Rejected
```

---

### 2️⃣ Environmental Restrictions

The Mars environment can restrict specific operations.

Examples include:

🌪️ **Dust Storm**

- Surface operations are restricted.

📡 **Communication Disruption**

- Remote drone operations may be restricted.
- Communication-dependent operations may be unavailable.

---

### 3️⃣ Agent Availability

An agent cannot be assigned if they are:

- Already unavailable
- Damaged
- Fatigued
- Not operationally ready

---

### 4️⃣ Capability Validation

Agents must have the required capabilities for a mission.

Examples:

```text
Scientist
    ↓
SCIENTIFIC_RESEARCH

Engineer
    ↓
EQUIPMENT_REPAIR

Medical Officer
    ↓
MEDICAL_CARE

Exploration Rover
    ↓
SURFACE_EXPLORATION
```

The project uses polymorphic methods such as:

```java
agent.canPerform(mission);
```

instead of depending on concrete type checks such as:

```java
instanceof
```

---

### 5️⃣ Resource Availability

A mission requires sufficient resources before it can begin.

Examples include:

- 🔋 Battery Power
- ⛽ Fuel
- 💧 Water
- 🍽️ Food
- 🫁 Oxygen
- 🏥 Medical Supplies
- 🔩 Spare Parts

Resources are managed through the colony's `ResourceManager`.

---

# 🚨 Emergency Management

The system supports different emergency situations.

Examples include:

- 🔥 Fire Emergency
- 🏥 Medical Emergency
- 🫁 Oxygen Leak Emergency
- 🚙 Rover Breakdown Emergency

Each emergency has a state that can represent its current situation.

```text
TRIGGERED
RESOLVED
UNRESOLVED
```

Agents with the appropriate capabilities can respond to emergencies.

---

# 🏢 Colony Facilities

The Mars colony contains different facilities.

The project currently includes:

- 🏠 Habitat
- 🔬 Laboratory
- ❤️ Life Support System
- ⚡ Power Station
- 📡 Communication Station
- 📦 Storage
- 🔧 Equipment

Facilities can store and manage resources required by missions and colony operations.

---

# 🌪️ Mars Environment

The `MarsEnvironment` class manages environmental conditions that affect operations.

Supported conditions include:

- NORMAL
- DUST_STORM
- COMMUNICATION_DISRUPTION

The environment can determine whether a specific operation is allowed.

Example:

```text
DUST_STORM
    ↓
SURFACE_OPERATION
    ↓
❌ Restricted
```

---

# ⏱️ Simulation and Time Management

The simulation system can advance time using:

```java
manager.advanceTime(timeUnits);
```

When time advances:

- The current simulation cycle increases.
- Active missions progress.
- Mission elapsed time is updated.
- Missions are completed when their duration is reached.
- Assigned agents become available again after completion.

Example:

```text
Mission Duration = 3

Time = 0 → ASSIGNED
Time = 1 → IN_PROGRESS
Time = 2 → IN_PROGRESS
Time = 3 → COMPLETED
```

---

# 🏗️ Project Structure

The project is organized into the following packages:

```text
marsops/
│
├── Agents/
│   ├── Agent.java
│   │
│   ├── Crew/
│   │   ├── CrewMember.java
│   │   ├── Engineer.java
│   │   ├── MedicalOfficer.java
│   │   └── Scientist.java
│   │
│   └── Robots/
│       ├── Robot.java
│       ├── Drone.java
│       ├── ExplorationRover.java
│       └── RepairRobot.java
│
├── colony/
│   ├── Colony.java
│   ├── MarsEnvironment.java
│   └── ResourceManager.java
│
├── emergencies/
│   ├── Emergency.java
│   ├── FireEmergency.java
│   ├── MedicalEmergency.java
│   ├── OxygenLeakEmergency.java
│   └── RoverBreakdownEmergency.java
│
├── Facilities/
│   ├── Facility.java
│   ├── CommunicationStation.java
│   ├── Equipment.java
│   ├── Habitat.java
│   ├── Laboratory.java
│   ├── LifeSupportSystem.java
│   ├── PowerStation.java
│   └── Storage.java
│
├── missions/
│   ├── Mission.java
│   ├── ExplorationMission.java
│   ├── RepairMission.java
│   ├── ResearchMission.java
│   ├── RescueMission.java
│   ├── MissionStatus.java
│   ├── Simulation.java
│   └── SimulationManager.java
│
├── AppEnums.java
│
└── Main.java
```

---

# 💡 OOP Concepts Used

## 🔒 Encapsulation

Important data is stored using private fields and accessed through controlled methods.

```java
private boolean available;
```

---

## 🧬 Inheritance

Different types of agents inherit from common base classes.

Example:

```text
Agent
│
├── CrewMember
│   ├── Scientist
│   ├── Engineer
│   └── MedicalOfficer
│
└── Robot
    ├── Drone
    ├── ExplorationRover
    └── RepairRobot
```

---

## 🎭 Polymorphism

Different agents can respond differently to the same operation.

For example:

```java
agent.canPerform(mission);
```

The system asks the object whether it can perform the mission without needing to know its exact concrete type.

---

## 🎨 Abstraction

Base classes such as `Agent`, `Mission`, `Emergency`, and `Facility` define common behavior that is shared by specialized classes.

---

## 🧩 Composition

The `Colony` acts as a central coordinator that contains and manages:

- Agents
- Facilities
- Environment
- Resource Manager
- Active Mission

---

# 🧪 Test Scenarios

The `Main` class contains multiple test scenarios that demonstrate the system's behavior.

Examples include:

### ✅ Successful Exploration

A scientist and exploration rover successfully complete an exploration mission.

---

### ❌ Mission Rejection

A mission is rejected when the assigned agents do not have the required capabilities.

---

### 📦 Resource Shortage

A mission is rejected when the colony does not have enough resources.

---

### 🌪️ Environmental Restrictions

A mission may be rejected when the current Mars environmental condition prevents its required operation.

---

### 🚨 Emergency Handling

The system demonstrates emergency situations and responses by appropriate agents.

---

# ▶️ How to Run

## Using the Command Line

Compile the project:

```bash
javac -d out $(find . -name "*.java")
```

Run the application:

```bash
java -cp out marsops.Main
```

> On Windows, you can compile the Java files using your IDE or configure the command according to your terminal environment.

---

# 🛠️ Technologies Used

- Java
- Object-Oriented Programming
- Java Collections Framework
- Enums
- Exception Handling
- Polymorphism
- Inheritance
- Abstraction
- Encapsulation

---

# 🎓 Educational Purpose

This project was developed as part of a **Programming II / Object-Oriented Programming** course.

The main goal is to apply Java OOP concepts to a realistic simulation system involving:

- Autonomous agents
- Resource management
- Mission validation
- Environmental restrictions
- Emergency management
- Time-based simulation

---

# 🚀 Future Improvements

Possible future improvements include:

- 📊 Graphical User Interface
- 💾 Database integration
- 🖥️ Real-time simulation dashboard
- 📈 Colony statistics and reports
- 🔄 Multiple concurrent missions
- 🤖 More autonomous decision-making
- 💾 Save and load simulation data
- 🌐 Web-based interface

---

# 👥 Contributors

This project was developed as a team project for a Java Programming II course.

Contributions and improvements can be managed using Git and GitHub.

---

## 🌌 MARS-OPS

**Autonomous Mars Colony Management & Emergency Simulator**

> Managing a colony on Mars through intelligent agents, missions, resources, environmental conditions, and emergency response systems. 🚀🔴