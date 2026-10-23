# 🚀 MARS-OPS — Autonomous Mars Colony Management & Emergency Simulator

**Alexandria University · Faculty of Engineering**

Computer and Communication Engineering — Specialized Scientific Programs

**Programming II · Fall 2026 · Lab 5**

> A Java object-oriented simulation that manages a Mars research colony: its personnel, robots, facilities, resources, missions, environmental conditions, and emergencies.

> **Note:** This README documents the intended design. Confirm that class names, rules, scenarios, and file paths match the final implementation before submission.

---

## 📑 Table of Contents

1. [System Description](#1-system-description)

2. [Main Components](#2-main-components)

3. [Project Structure](#3-project-structure)

4. [Design Decisions & OOP Concepts](#4-design-decisions--oop-concepts)

5. [Object Relationships](#5-object-relationships)

6. [Resource Allocation Rules](#6-resource-allocation-rules)

7. [Mission Rules & Mission States](#7-mission-rules--mission-states)

8. [Environmental Effects](#8-environmental-effects)

9. [Emergency Rules](#9-emergency-rules)

10. [Simulation & Time Management](#10-simulation--time-management)

11. [UML Diagrams](#11-uml-diagrams)

12. [Compiling & Running](#12-compiling--running)

13. [Demonstration Scenarios](#13-demonstration-scenarios)

14. [Assumptions & Other Implementation Decisions](#14-assumptions--other-implementation-decisions)

15. [Team](#15-team)

---

## 1. System Description

MARS-OPS is the software system for a small research colony on Mars. The colony operates with limited resources and a mix of human personnel, autonomous robots, habitats, laboratories, and specialized equipment.

The system is able to:

- Manage colony **personnel** and **robotic units**.

- Manage **facilities** and track **resources**.

- **Assign missions** and simulate the passage of time.

- Determine whether available entities **can perform** a mission.

- Handle **equipment failures** and **emergencies**, and update the colony state afterwards.

- Produce a **colony status report**.

The focus of the project is not just storing data, but modelling **how objects collaborate** instead of placing all the logic in a single class.

---

## 2. Main Components

| Component | Responsibility |

|---|---|

| **`MarsColony`** | Central coordinator. Owns the facilities, personnel and robots; receives and tracks missions; enforces the one-active-mission rule; computes colony-wide resource totals. |

| **`Simulation`** | Controls time. `advanceTime(n)` progresses missions, drains batteries, increases fatigue, updates the environment, and can trigger emergencies. |

| **Personnel** (`Scientist`, `Engineer`, `MedicalOfficer`) | Human agents with name, ID, health, location, skills, fatigue, and availability. |

| **Robots** (`ExplorationRover`, `RepairRobot`, `Drone`) | Robotic agents with battery level, damage status, location, and availability. |

| **Facilities** (`Habitat`, `Laboratory`, `StorageFacility`, `PowerStation`, `CommunicationStation`) | Hold resources and equipment; have a location, capacity, and operational status. |

| **`Resource`** types | Oxygen, Water, Food, Fuel, Battery Power, Medical Supplies, Spare Parts. |

| **Missions** (`ExplorationMission`, `RepairMission`, `ResearchMission`, `RescueMission`) | Describe the work to be done, requirements, duration, and state. |

| **Emergencies** (`OxygenLeak`, `Fire`, `RoverBreakdown`, `MedicalEmergency`) | Describe incidents, their required responders, and their outcomes. |

| **`Environment`** | Current Mars condition: `NORMAL`, `DUST_STORM`, `COMMUNICATION_DISRUPTION`. |

---

## 3. Project Structure

```

StudentID-Prog-II-Lab5/

├── src/

│   ├── Main.java                  # Testing class – runs all 5 demo scenarios

│   ├── colony/                    # MarsColony, Simulation, Environment

│   ├── agents/                    # Agent interface, CrewMember, personnel, robots

│   ├── facilities/                # Facility hierarchy, LifeSupportSystem, Equipment

│   ├── resources/                 # Resource, ResourceType, ResourceRequirement

│   ├── missions/                  # Mission hierarchy, MissionState

│   └── emergencies/               # Emergency hierarchy, EmergencyStatus

├── uml/

│   ├── class-diagram.png

│   ├── object-diagram.png

│   ├── state-machine-diagram.png

│   ├── sequence-mission.png

│   ├── sequence-oxygen-leak.png

│   ├── sequence-environment.png

│   ├── use-case-diagram.png

│   └── source/                    # Editable .drawio files

└── README.md

```

---

## 4. Design Decisions & OOP Concepts

### Encapsulation

All attributes (battery, health, oxygen quantity, mission state, …) are `private`. State changes go through methods that enforce the rules, e.g. a habitat's oxygen can never fall below zero and the colony can never consume more than is available.

### Abstraction

Abstract classes/interfaces define *what* an entity does without fixing *how*:

`Agent`, `Facility`, `Mission`, `Emergency`.

### Inheritance

- `CrewMember` → `Scientist`, `Engineer`, `MedicalOfficer`

- `Robot` → `ExplorationRover`, `RepairRobot`, `Drone`

- `Facility` → `Habitat`, `Laboratory`, `StorageFacility`, `PowerStation`, `CommunicationStation`

- `Mission` → `ExplorationMission`, `RepairMission`, `ResearchMission`, `RescueMission`

- `Emergency` → `OxygenLeak`, `Fire`, `RoverBreakdown`, `MedicalEmergency`

### Polymorphism & the Capability Model

A mission **never checks the concrete type** of an agent. Instead, every agent answers a question:

```java

if (agent.canPerform(mission)) {

    mission.assign(agent);

}

```

Each agent class overrides `canPerform(Mission)` (and `canHandle(Emergency)` for emergencies) according to its own abilities. New agent types can be added without modifying any mission or colony code.

### Collaboration over a God class

Responsibilities are split:

- `MarsColony` – receives missions, checks availability, aggregates resources.

- `Mission` – knows its requirements, duration, and state.

- `Agent` – knows whether it is capable and available.

- `Facility` – stores its own resources and equipment.

- `Simulation` – drives time.

- `Emergency` – knows which responders it needs and how to apply its consequences.

---

## 5. Object Relationships

| Relationship | Where it is used | Why | UML | Java |

|---|---|---|---|---|

| **Association** | `Engineer` ↔ `Mission` | An engineer works on a mission; each can exist without the other. | Solid line | A method parameter / reference (`repair(Mission m)`) |

| **Aggregation** | `MarsColony` ◇— `CrewMember`, `Robot` | Colony groups agents, but agents exist independently of the colony. | Hollow diamond | `List<CrewMember>` passed into the constructor |

| **Composition** | `Habitat` ◆— `LifeSupportSystem` | The life-support system is created by and dies with its habitat. | Filled diamond | `private final LifeSupportSystem lifeSupport = new LifeSupportSystem();` |

| **Inheritance** | `CrewMember` ◁— `Engineer`, `Scientist`, … | "is-a" specialisation with overridden behaviour. | Hollow triangle | `extends` |

---

## 6. Resource Allocation Rules

Resources are **stored in facilities** but **managed at the colony level**.

1. **Colony-wide totals** – The total of a resource is the sum of that resource across all currently **operational** facilities. Resources in destroyed/unavailable facilities do **not** count.

2. **Multiple sources** – A requirement may be met by drawing from several facilities. *(Example: Habitat A holds 300 O₂ and Storage holds 200 O₂ → a request for 450 is valid, a request for 600 is not.)*

3. **All-or-nothing consumption** – If the full quantity cannot be supplied, **nothing is consumed** and the operation fails with a clear reason.

4. **Local emergencies** – An oxygen leak reduces oxygen only in the affected habitat, which in turn lowers the colony-wide total.

5. **Status report** – The colony status report shows colony-wide resource totals.

6. **Allocation policy (our design decision)** –

   > **Policy:** *Storage facilities are drained first, then remaining operational facilities in the order they were registered with the colony.*

   >

   > **Rationale:** Storage facilities exist to hold reserves, so habitats keep their local oxygen for as long as possible.

Implementation note: the colony first **checks** the total available (`canSupply`), and only then **consumes** (`consume`), so a failed request never leaves resources partially deducted. Quantities can never become negative.

---

## 7. Mission Rules & Mission States

### Supported missions

| Mission | Description | Requirements |

|---|---|---|

| **Exploration** | Explores a specified region | Suitable vehicle, required scientific skill, sufficient battery, required equipment |

| **Repair** | Repairs a damaged facility/equipment | Engineer, repair robot, spare parts |

| **Research** | Scientist performs an experiment in a lab | Scientist with the right skill, laboratory, scientific equipment, sufficient resources |

| **Rescue** | Recovers a stranded entity | Appropriate vehicle, available personnel, communication capability |

### One active mission at a time

- A mission is submitted to the colony together with the agents it requires.

- If another mission is active → `FAILED` (reason recorded).

- If an agent lacks the required capability → `FAILED`.

- If resources are insufficient → `FAILED`.

- A rejected mission **never becomes active**.

- An accepted mission becomes `ASSIGNED` and stays the active mission until it completes or fails.

- **Failed missions are never retried** – a new mission must be created.

- When a mission ends (completed *or* failed), **all** its agents become available again.

- Facilities do **not** own or schedule missions; they are only referenced as targets/locations.

### Mission states

```

            ┌──────────── rejected (busy / no capability / no resources) ───────────┐

            │                                                                       ▼

 (submit) ──┴──► ASSIGNED ──► IN_PROGRESS ──► COMPLETED                          FAILED

                     │             │

                     │             └── equipment failure / environment / low battery ──► FAILED

                     └── (start conditions no longer met) ───────────────────────────► FAILED

```

| State | Meaning |

|---|---|

| `ASSIGNED` | Accepted by the colony; agents reserved; resources reserved. |

| `IN_PROGRESS` | Duration counter is running; each time unit advances it by 1. |

| `COMPLETED` | Required duration reached; agents released. |

| `FAILED` | Rejected or aborted with a recorded reason; agents released; never retried. |

State is represented by a `MissionState` **enum**, and transitions are controlled **only** through methods inside `Mission`, so illegal transitions (e.g. `FAILED → IN_PROGRESS`) are impossible.

---

## 8. Environmental Effects

| Condition | Effect |

|---|---|

| **Normal** | No restrictions. |

| **Dust storm** | Surface exploration missions **cannot begin**. Active rover missions **fail**. |

| **Communication disruption** | Remote drones cannot be controlled; communication-dependent missions (e.g. Rescue) cannot be initiated; communication-dependent operations may fail. |

Each mission/agent decides how it is affected by asking the `Environment` (e.g. `mission.isAllowedIn(environment)`), so adding a new condition does not require changing existing mission code.

### 🔸 What happens to missions already in progress when the environment changes?

>

> - **Dust storm begins:** any `IN_PROGRESS` **rover** mission immediately becomes `FAILED` with reason *"Dust storm: surface operations not allowed"*; its agents are released. Missions that do not depend on the surface (e.g. a lab Research mission) **continue**.

> - **Communication disruption begins:** `IN_PROGRESS` missions that depend on communication (drone operation, rescue) become `FAILED`; others continue.

> - **Conditions return to normal:** nothing is restarted automatically. Failed missions stay failed and must be re-created.

---

## 9. Emergency Rules

General rules:

- The location of an emergency is a **facility** or the **Martian surface**.

- Every agent has a current location. A rover on an exploration mission is on the surface; a broken-down rover **stays where it broke down**.

- **Location does not restrict who can respond.** Any available agent may be dispatched, and dispatching **moves** the agent to the affected facility.

- A responder is **appropriate** if it *reports* it can handle the emergency type (`agent.canHandle(emergency)`) — concrete types are never checked.

- A responder is **available** if it is **not part of the active mission** and is in usable condition.

- Emergencies **do not interrupt** active missions and are **not** subject to the one-active-mission limit.

- If required responders are unavailable: the emergency is marked `UNRESOLVED`, a clear reason is recorded, **no response resources are consumed**, and it is **not retried automatically**.

- Responders are released when the response ends.

### Required responders per emergency

| Emergency | Required responders | Response resources |

|---|---|---|

| **Oxygen Leak** *(given by spec)* | ≥ 1 human **and** ≥ 1 robot capable of repairs (e.g. Engineer + Repair Robot) | Spare parts, battery |

| **Fire** | ≥ 1 Engineer + ≥ 1 Repair Robot for fire response; a Medical Officer if personnel are in the lab | Spare parts, battery, medical supplies (if treating) |

| **Rover Breakdown** | ≥ 1 Engineer + ≥ 1 Repair Robot | Spare parts, battery |

| **Medical Emergency** | ≥ 1 Medical Officer | Medical supplies (and oxygen); relevant facility must be operational |

### 9.1 Oxygen Leak

1. Triggered manually.

2. Affected habitat identified.

3. **Initial loss** (amount defined by the scenario) is deducted from that habitat only.

4. Appropriate responders determined and checked for availability.

5. Response resources checked (all-or-nothing).

6. Responders dispatched and repair attempted.

7. **If repaired:** total oxygen loss = the initial amount.

   **If unresolved:** the **same amount is deducted again** → an unresolved leak costs **twice** a repaired leak.

8. Habitat and emergency states updated; responders released.

Rules: a habitat's oxygen **never drops below zero**; it is a **one-time deduction** (no continuous drain).

### 9.2 Fire

A laboratory catches fire. Consequences represented in the model: laboratory becomes **unavailable**, its equipment becomes **damaged**, oxygen consumption **increases**, fire-response personnel are required, and a **medical emergency** may be raised for personnel present in the lab.

### 9.3 Rover Breakdown

Occurs during an exploration mission. The system updates the rover's status (damaged), the mission status (`FAILED`), the rover's location (where it broke down), and the required repair resources. A repair operation may then be created.

### 9.4 Medical Emergency

The system checks that an appropriate medical officer is available, that medical supplies exist, and that the relevant facility is operational. After successful treatment, the patient's **health increases**.

---

## 10. Simulation & Time Management

```java

simulation.advanceTime(1);

```

Each time unit may cause:

- Active missions to progress by one unit (and complete/fail)

- Robot batteries to decrease

- Crew fatigue to increase

- Resource levels to change

- The environment to change

- Emergencies to occur

---

## 11. UML Diagrams

All diagrams were created with **draw.io** and are consistent with the final Java implementation.

| Diagram | File | Purpose |

|---|---|---|

| Class Diagram | [`uml/class-diagram.png`](uml/class-diagram.png) | Static structure: classes, interfaces, enums, attributes, methods, visibility, multiplicities, relationships |

| Object Diagram | [`uml/object-diagram.png`](uml/object-diagram.png) | Snapshot of a consistent system state (e.g. just before an exploration mission) |

| State Machine Diagram | [`uml/state-machine-diagram.png`](uml/state-machine-diagram.png) | Mission lifecycle: `ASSIGNED → IN_PROGRESS → COMPLETED / FAILED` |

| Sequence – Mission | [`uml/sequence-mission.png`](uml/sequence-mission.png) | Submission, checks, acceptance/rejection, time advancement, completion |

| Sequence – Oxygen Leak | [`uml/sequence-oxygen-leak.png`](uml/sequence-oxygen-leak.png) | Trigger, deduction, responders, resources, repair, release |

| Sequence – Environment | [`uml/sequence-environment.png`](uml/sequence-environment.png) | Dust storm blocking an exploration mission |

| Use Case Diagram | [`uml/use-case-diagram.png`](uml/use-case-diagram.png) | Colony Operator / Emergency Operator and system functionality |

Editable sources are in [`uml/source/`](uml/source/).

---

## 12. Compiling & Running

**Requirements:** JDK 17 or newer (`java -version`).

### Linux / macOS

```bash

git clone <repository-url>

cd <your-repo>

mkdir -p out

javac -d out $(find src -name "*.java")

java -cp out Main

```

### Windows (PowerShell)

```powershell

mkdir out

javac -d out (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object FullName)

java -cp out Main

```

### Using an IDE

Open the folder in IntelliJ IDEA / Eclipse / VS Code, mark `src` as the source root, and run `Main`.

---

## 13. Demonstration Scenarios

Running `Main` executes all five required scenarios in order. Each prints the system state and a clear success/failure message.

| # | Scenario | What it demonstrates | Expected result |

|---|---|---|---|

| 1 | **Successful Exploration** | Scientist + Exploration Rover + equipment + mission | Mission goes `ASSIGNED → IN_PROGRESS → COMPLETED`; state printed before and after |

| 2 | **Mission Rejection** | Mission submitted with an unsuitable agent | `Mission cannot be assigned.` — *Reason: Agent does not possess the required capability.* Mission never becomes active |

| 3 | **Resource Shortage** | Mission needing more resources than the colony-wide total | Rejected; **no resources consumed**; no negative quantities; clear failure reason |

| 4 | **Emergency Handling** | Oxygen leak in a habitat | Detected → habitat located → responders identified → repair attempted → resources consumed → habitat state updated |

| 5 | **Environmental Effect** | Dust storm | `Exploration Mission cannot start.` — *Reason: Surface operations are not allowed during a dust storm.* |

---

## 14. Assumptions & Other Implementation Decisions

- **Time unit:** one call to `advanceTime(1)` = one time unit. A mission starts counting when it becomes `IN_PROGRESS`.

- **Resource units:** integer quantities; mission costs (e.g. repair = 2 spare parts + 5 battery units) are defined per mission.

- **Fatigue / battery:** fatigue rises and battery drops by fixed amounts per time unit while an agent is on a mission.

- **Availability:** an agent is available iff it is not assigned to the active mission **and** is in usable condition (not damaged / not critically hurt / battery above minimum).

- **Oxygen-leak amount:** defined by the scenario passed to the emergency (we use `<N>` units in the demo).

- **Environment changes:** applied by `Simulation`, either manually triggered in the demo or on a schedule.

- **Validation:** invalid inputs (negative quantities, null agents) are rejected with an exception or a clear message.

---

## 15. Team

| Name | Student ID |

| *Yassin Tarek* | *10072* |

| *Eyad Tamer* | *10139* |

| *Amr Elbaghdady* | *10224* |

**Course staff:** Dr Mervat Mikhail · Dr Ahmed Elsayed

---

<sub>Alexandria University · Faculty of Engineering · Computer and Communication Engineering · Programming II · Fall 2026</sub>
