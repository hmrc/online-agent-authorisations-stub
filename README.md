# online-agent-authorisations-stub

This stub stands in for oaa-exb, the downstream that holds agents' authorisation requests, so [online-agent-authorisations](https://github.com/hmrc/online-agent-authorisations) can run locally and in test environments.

It also holds the OAA Tampermonkey script, which signs you in to the frontend through auth-login-stub with ready-made agent profiles.

## Running the service
Before starting, you will need to have [service-manager](https://github.com/hmrc/service-manager) installed/configured

### Dependencies
All dependencies can be found in [AppDependencies.scala](project/AppDependencies.scala)

### Running locally:
Service Manager:
- Start dependent services `sm2 --start OAA_ALL`
- Stop this service `sm2 --stop ONLINE_AGENT_AUTHORISATIONS_STUB`
- Start the server locally on `port 20112` with `sbt run`

The service runs at http://localhost:20112/online-agent-authorisations-stub

The backend only calls the stub when `features.stub-oaa-exb-enabled = true` in its `application.conf`.

### Testing:
- Run unit tests: `sbt test`
- Run integration tests: `sbt it/test`
- To run all tests and coverage: `sbt clean compile coverage test it/test coverageOff coverageReport`

The build needs 100% statement coverage.

## Stub data

Responses are JSON files under [conf](conf), read from the classpath. Each endpoint has a folder, with a file per agent code and a `default` file for any agent code without one:

```
conf/resources.<endpoint>/
├── <agentCode>/<response>.json   # returned for that agent code
└── default/<response>.json       # returned for any other agent code
```

Agent codes are only used to find a file when they are letters and numbers. Anything else gets the default.

### Scenario agent codes

| Agent code | Result |
|---|---|
| `AGENTCODE1` | Success, with data |
| Any other agent code (for example `NOREQUESTS`, or auth-login-stub's default `123456789`) | Success, with the default data |
| `INTERNALSERVERERROR` | `500 Internal Server Error` |

To add a scenario, add a folder named after the agent code with the response files in it, and add a matching profile to the Tampermonkey script.

## Tampermonkey script

The script is at [config/tamperMonkey](app/uk/gov/hmrc/onlineagentauthorisationsstub/config/tamperMonkey).

1. Install [Tampermonkey](https://www.tampermonkey.net/) on Chrome (or Greasemonkey on Firefox).
2. Add the script as a new userscript.
3. Go to http://localhost:9949/auth-login-stub/gg-sign-in and pick a profile.

Each profile sets the affinity group, agent code, email and enrolments, and shows which page it should land on (`AC1`, `Activate`, `Unauthorised` or `Error`). The scenario agent codes above are under "Request Counts".

When you change the script, bump the version on line 5 and in `scriptVersion` so Tampermonkey picks up the update.

## Adding a new endpoint

1. Add the request model to `models/requests`, with a `Json.format`.
2. Add a controller action that validates the body and returns the stub data. Use the same path as oaa-exb.
3. Add the route to [app.routes](conf/app.routes) and the controller to the `controllers` block in [application.conf](conf/application.conf).
4. Add the stub data under `conf/resources.<endpoint>/`, including a `default` file.
5. Add tests for every scenario.

### License

This code is open source software licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html).
