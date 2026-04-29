# Smart Campus API

REST API for rooms, sensors, and sensor readings (JAX-RS / Jersey, in-memory data).

## What you need

- **Java 17**
- **Apache NetBeans** (with Maven support)

## How to run (NetBeans)

1. Open the project in NetBeans.
2. In the Projects window, open  
   `Source Packages` → `com.mycompany.smart_campus` → **`Main.java`**.
3. **Right-click `Main.java`** → **Run File** (or press **Shift+F6**).

The API listens on **port 8090**. Base URL:

```
http://localhost:8090/api/v1/
```

## How to stop the server

1. In Terminal, go to your project folder (the one that contains `pom.xml`).
2. Run:

```bash
lsof -ti tcp:8090 | xargs kill -9
```


## Endpoints

All paths are under `http://localhost:8090/api/v1`.

| Method | Path | What it does |
|--------|------|----------------|
| GET | `/` | API info and links |
| GET | `/rooms` | List rooms |
| POST | `/rooms` | Create room (JSON body) |
| GET | `/rooms/{roomId}` | Get one room |
| DELETE | `/rooms/{roomId}` | Delete room (blocked if sensors still linked) |
| GET | `/sensors` | List sensors |
| GET | `/sensors?type=CO2` | List sensors filtered by type |
| POST | `/sensors` | Create sensor (JSON must use an existing `roomId`) |
| GET | `/sensors/{sensorId}/readings` | List readings for that sensor |
| POST | `/sensors/{sensorId}/readings` | Add a reading (updates sensor `currentValue`) |

## Quick check

```bash
curl -s http://localhost:8090/api/v1/
```

You should get JSON with API name, version, and links.

---

## Part 1 — Coursework answers (report)

### 1. Lifecycle of a JAX-RS resource class

Jersey (the JAX-RS stack we use) normally creates a new instance of a root resource class for each request. It is not a singleton unless you add something like `@Singleton`.

That is why we do not keep rooms or sensors in fields on the resource class — those fields would not be shared between requests anyway. Everything shared goes through `CampusStore`, which is one enum singleton, and the maps are `ConcurrentHashMap` so concurrent requests do not trash the data.

### 2. Hypermedia (HATEOAS) and the discovery endpoint

Hypermedia just means the JSON includes links (URLs, and sometimes which methods apply), not only plain data. The client can follow those links instead of hard-coding every path.

Static docs get stale; links are generated from the live request, so they stay correct for whatever host and port you are on. That saves hassle when the API evolves.

## Part 2 — Coursework answers (report)

### 1. List of room IDs only vs full room objects

If you only return IDs, the payload stays light and downloads fast, but the client usually has to call `GET /rooms/{id}` over and over to get names, capacity, sensor lists, and so on — more round trips and more client code.

We return full objects on `GET /rooms`, so one call is enough for a simple screen or report. The downside is a bigger response as the campus grows; at coursework scale that is acceptable, and you could add pagination later if needed.

### 2. Is `DELETE /rooms/{roomId}` idempotent?

Roughly yes in terms of end state. After the first successful delete the room is gone. If the client sends the same DELETE again, the room is still gone — we do not bring it back or corrupt anything. The second call returns 404 with a JSON error instead of 204, because there is nothing to delete anymore.

So repeating DELETE does not flip the server into a weird state; the room stays absent. Some APIs return 204 even on a repeat delete; we use 404 so it is obvious the resource was already removed.

## Part 3 — Coursework answers (report)

### 1. `@Consumes(MediaType.APPLICATION_JSON)` and wrong `Content-Type`

`POST /sensors` is declared to accept JSON. If the client sends `text/plain`, `application/xml`, or another type, JAX-RS checks `Content-Type` before our method runs. There is no reader for that combo, so the client gets 415 Unsupported Media Type. Jackson never sees the body and our handler does not run.

So the mismatch is handled by the framework, not with manual if-checks in the resource.

### 2. `@QueryParam("type")` vs path segment (e.g. `/sensors/type/CO2`)

With `?type=CO2`, `GET /sensors` is still one collection resource. No query param means return everyone; with a param you filter. You can add more query parameters later (status, paging) without new path trees.

Putting the type in the path (e.g. `/sensors/type/CO2`) suggests a separate resource per filter and multiplies URL patterns; mixing several filters gets clumsy.

For optional filters on a collection, query parameters are the usual approach.

## Part 4 — Coursework answers (report)

### 1. Sub-resource locator pattern — why it helps

`SensorResource` has a locator method (no HTTP verb on it) for `/sensors/{sensorId}/readings` that returns a `SensorReadingResource`. JAX-RS then dispatches GET/POST on that path to that class.

That splits concerns: one file for sensors, another for readings, instead of one giant controller with every nested route. Reading rules can change without touching sensor registration. If you add something like `/readings/{readingId}` later, it belongs with the reading resource. It is also easier to explain the project to someone new when the structure matches the URLs.

### 2. Reading history and `currentValue` consistency

`GET …/sensors/{id}/readings` returns the list we keep in `CampusStore` for that sensor.

`POST …/readings` appends to that list.

After each append, `CampusStore.appendReading` sets the parent sensor’s `currentValue` to the new reading’s value. Anyone who only lists sensors still sees the latest reading without walking the whole history; the store keeps the log and the “current” field aligned.

## Part 5 — Coursework answers (report)

### 1. Room not empty — 409 Conflict

If you try to delete a room that still has sensors, we throw `RoomNotEmptyException`. `RoomNotEmptyExceptionMapper` maps that to 409 Conflict with JSON (`ROOM_NOT_EMPTY`, message, `roomId`, `sensorCount`) so the client knows the room is not empty — no generic HTML error page.

### 2. Missing `roomId` on `POST /sensors` — 422 and why not 404

If the body names a room that does not exist, we throw `LinkedResourceNotFoundException` and the mapper returns 422 Unprocessable Entity with JSON (`LINKED_RESOURCE_NOT_FOUND`, etc.).

404 usually means “this URL is not a resource.” Here the URL `POST /sensors` is valid and the JSON parses fine; the problem is a bad reference inside the body (like a broken foreign key). 422 fits that better: the server understood the request but cannot apply the content. 404 would blur the line between “wrong address” and “bad payload.”

### 3. Sensor in `MAINTENANCE` — 403 Forbidden

If status is `MAINTENANCE` and the client POSTs a reading, we throw `SensorUnavailableException`. `SensorUnavailableExceptionMapper` responds with 403 Forbidden and JSON explaining the sensor cannot take readings in that state.

### 4. Catch-all 500 and stack traces in production

`GenericExceptionMapper` implements `ExceptionMapper<Throwable>` for surprises like `NullPointerException`. The client gets 500 with a short generic JSON body — no stack trace in the HTTP response. We still log the full exception on the server for debugging.

Leaking stack traces to clients is bad for security: they show class names, packages, sometimes file paths on the disk, line numbers, framework hints, and snippets of error text. That helps an attacker fingerprint the stack, guess versions, and look for known bugs. A generic message avoids handing them a map of the inside of the server.

### 5. JAX-RS filters vs `Logger` in every resource method

`RequestResponseLoggingFilter` implements `ContainerRequestFilter` and `ContainerResponseFilter`: it logs method and URI on the way in, and status on the way out, in one place.

Compared to pasting `Logger.info()` into every resource method, a filter covers new endpoints automatically, keeps log format consistent, and keeps resources focused on the actual work. If you change what you log, you edit one class instead of hunting through the whole codebase.
