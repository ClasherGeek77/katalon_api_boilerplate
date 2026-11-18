# katalon_api_boilerplate

A Katalon Studio API test project: 6 REST tests (list, get, create/update/delete, login, refused login, register) with the requests stored as Object Repository entries and the checks written as short Groovy scripts. It tests a local clone of the reqres.in API, so it needs no internet.

The `legacy` branch holds the full history of the older predecessor repos (`katalon_rest_api_automation`, `katalon_github_actions_ci`).

## when to use it

Use it to learn or start a Katalon REST project: request objects, variables from a profile, status and JSON checks, a suite.

Do not use it if:

- you want API tests as Python code with pytest: use https://github.com/ClasherGeek77/pytest_api_boilerplate
- you want to apply load, not check correctness: use https://github.com/ClasherGeek77/locust_boilerplate
- you want a browser or mobile test: use https://github.com/ClasherGeek77/katalon_web_boilerplate, https://github.com/ClasherGeek77/katalon_android_boilerplate or https://github.com/ClasherGeek77/katalon_ios_boilerplate

## what you need

| need | detail |
|---|---|
| Katalon Studio 7.9.1 | the version recorded in `.classpath`; the project was created with it. Download from https://katalon.com/download (older versions are listed on Katalon's release pages). On macOS 10.15, 7.9.1 is the build to use. On a newer OS a current Studio should open and upgrade the project (not verified). The scripts use only keywords that exist in 7.9.1 (see "how to read the code"). |
| Katalon account / licence | Studio asks you to sign in. Running from the command line needs Katalon Runtime Engine, a licensed product; check the current terms on https://katalon.com. Not verified here. |
| Python 3.8+ | only to run the reqres clone (standard library, no pip install) |
| git | to clone the repos |

For the command-line path (`./run_katalon.sh`) you also need a `katalon` console launcher, see "run from the command line" below. No browser is needed for this project.

## quick start

Start the target API, the local reqres.in clone, in one terminal and leave it running.

```bash
git clone https://github.com/ClasherGeek77/reqresin_clone.git
cd reqresin_clone
python3 server.py            # http://localhost:40000/api   (python3 server.py --port 8000 for another port)
```

Check it:

```bash
curl -s "http://localhost:40000/api/users/2"
```

You should see JSON with `"first_name": "Janet"` and `"email": "janet.weaver@reqres.in"`.

Get this project:

```bash
git clone https://github.com/ClasherGeek77/katalon_api_boilerplate.git
```

### run in Katalon Studio (the path that needs no scripts)

1. Open Katalon Studio and sign in.
2. File > Open Project, pick the cloned folder (it contains `katalon_boilerplate_api.prj`).
3. In the Tests Explorer open `Test Suites/api/api_smoke` and run it.

You should see 6 test cases pass, 0 failed. Reports land in `Reports/` (git-ignored).

### run from the command line

```bash
./run_katalon.sh                              # runs Test Suites/api/api_smoke
./run_katalon.sh "Test Suites/api/api_smoke"  # same, explicit
KATALON_BIN=/path/to/your/launcher ./run_katalon.sh
```

What the script does (about 30 lines, read it):

- changes into the project folder, then calls `$KATALON_BIN <project dir> -testSuitePath=<suite> -executionProfile=default -browserType=Chrome -retry=0 -data ~/.katalon_console/ws-api` (the browser flag is passed but an API suite does not open one)
- `KATALON_BIN` defaults to `~/.local/bin/katalon`. That launcher is not part of this repo. It must accept the project directory as its first argument followed by the options above, and start Katalon in console mode.
- with only the stock `katalonc` from Katalon Runtime Engine, put a small wrapper around it that adds `-runMode=console`, `-projectPath=<dir>` and your `-apiKey`. Not verified with the stock binary.
- optional `KATALON_WATCH`: path to an executable that takes the log path as its argument; the script runs it in the background for live progress.
- the log goes to `~/katalon_cli_runs/api-<timestamp>.log`; the script prints the `Test Suites:` / `Test Cases:` / `FAILED` / `ERROR` lines and exits with Katalon's exit code.

You should see `GREEN: katalonc exited 0, report in Reports/` as the last line and a `JUnit_Report.xml` under `Reports/` with `tests="6" failures="0"`. A failing run prints `RED: katalonc exit <n>, full log: ...`.

To aim at another server without editing the project, pass the global variable to Katalon (`-g_BASE_URL=http://host:port/` in console options) or change `BASE_URL` in `Profiles/default.glbl`. Keep the trailing slash. The `-g_` form is not verified here.

## the 6 tests

| test case | what it checks |
|---|---|
| `api-001-users-list-returns-the-documented-page` | `GET api/users?page=1`: 200, `page` 1, `per_page` 6, `total` 12 |
| `api-002-get-user-2-returns-janet` | `GET api/users/2`: 200, `data.email` `janet.weaver@reqres.in`, `data.first_name` `Janet` |
| `api-003-user-create-update-delete-journey` | `POST api/users`: 201, `id` `4`, `createdAt` present; `PUT api/users/4`: 200, `job` `senior tester`, `updatedAt` present; `DELETE api/users/4`: 204 |
| `api-004-login-with-credentials-answers-a-token` | `POST api/login`: 200 and a non-empty `token` |
| `api-005-login-without-password-is-refused` | `POST api/login` with no password: 400, `error` `Missing password` |
| `api-006-register-answers-id-and-token` | `POST api/register`: 200, `id` `4`, non-empty `token` |

Naming: test cases are `api-<number>-<what happens, in words>`; the suite is `api_smoke`. Folders mirror the area: `Test Cases/api/`, `Scripts/api/`, `Test Suites/api/`.

## how to read the code

```
Object Repository/reqres/*.rs                            8 request objects (method, url, headers, body)
Test Cases/api/*.tc                                      the test case entries (name + id only)
Scripts/api/<test case>/Script<timestamp>.groovy         the steps and checks of each test case
Test Suites/api/api_smoke.ts                             the ordered list of the 6 test cases
Profiles/default.glbl                                    global variables; BASE_URL = http://localhost:40000/
settings/internal/                                       Studio settings; analytics off
run_katalon.sh                                           command-line runner
```

A request object holds everything about one call. Open `Object Repository/reqres/user_create.rs` in Studio: method POST, URL `${BASE_URL}api/users`, header `Content-Type: application/json`, a JSON body. `${BASE_URL}` is a request variable whose default is `GlobalVariable.BASE_URL`, taken from the profile. (For API projects the "locator" of a thing you test is this request object; xpath locators belong to the web and mobile projects.)

A script sends the object and checks the answer:

```groovy
Response = WS.sendRequest(findTestObject('reqres/users_list'))
WS.verifyResponseStatusCode(Response, 200)
WS.verifyElementPropertyValue(Response, 'per_page', 6)
```

Keywords that exist in Katalon 7.9.1 and are used here: `WS.sendRequest`, `WS.verifyResponseStatusCode`, `WS.verifyElementPropertyValue(response, 'json.path', value)`, `Response.getResponseText()`. Helpers from newer Katalon versions (for example `WS.verifyResponseBodyContains` or `getResponseJson()`) do not exist in 7.9.1 and throw `MissingMethodException`. For a value you cannot predict (token, `createdAt`) the scripts parse the body with `JsonSlurper` and call `KeywordUtil.markFailedAndStop(...)` when it is missing.

Test `api-003` is a sequence: its create, update and delete requests depend on each other (the clone always answers a create with id `4`, like the live service did).

Debugging a failure: run one test case at a time. The console has no single-test flag, so make a one-test suite: duplicate `api_smoke` in Studio, keep one test case link, run that. Run the full suite last.

Not included on purpose: Cucumber, data-driven CSV binding, TestOps/cloud (analytics stays off), custom keyword classes, docker.

## if it breaks

| symptom | cause | fix |
|---|---|---|
| every request fails with connection refused | the reqres clone is not running | start it (`python3 server.py` in `reqresin_clone`), check `curl http://localhost:40000/api/users/2` |
| 404 from the clone, URL looks like `http://localhost:40000api/users` | `BASE_URL` lost its trailing slash | keep the slash: `http://localhost:40000/` |
| `MissingMethodException` on a `WS.` call | a keyword from a newer Katalon was added | use the 7.9.1 keywords listed above, or upgrade the engine |
| `api-003` fails on `id` `4` | you are not testing the clone (a real server hands out other ids) | run against `reqresin_clone` |
| `./run_katalon.sh`: no such file or directory | no launcher at `~/.local/bin/katalon` | set `KATALON_BIN`, or run in Studio |
| the run opens a GUI window or waits on a login dialog | the launcher did not select console mode, or Katalon is not licensed/signed in | use a launcher that starts console mode; sign in / license Studio first |
| Studio shows missing-jar or wrong-path warnings after opening | `.classpath` and `console.properties` hold the install path of whoever last saved the project | let Studio regenerate them (reopen the project); do not commit that machine-specific diff |

## next

- the same API as Python code: https://github.com/ClasherGeek77/pytest_api_boilerplate
- load against the same API: https://github.com/ClasherGeek77/locust_boilerplate
- the target API: https://github.com/ClasherGeek77/reqresin_clone
- Katalon for a web UI: https://github.com/ClasherGeek77/katalon_web_boilerplate
