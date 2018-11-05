#!/usr/bin/env python3

import requests
import argparse

parser = argparse.ArgumentParser()
parser.add_argument("--skip-externals", help="skip external service tests", action="store_true")
args = parser.parse_args()

DEFAULT = "\033[00m"
RED = "\033[91m"
GREEN = "\033[92m"
YELLOW = "\033[93m"
BLUE = "\033[94m"
MAGENTA = "\035[95m"
CYAN = "\033[96m"

rpc_id = 0


def rpc_call(url, method, params):
    global rpc_id
    rpc_id += 1
    res = requests.post(url, json={"jsonrpc": "2.0", "method": method, "id": rpc_id, "params": params})
    response_object = res.json()
    return response_object.get("result", None), response_object.get("error", None), res.status_code


def request_webservice(url, method, params):
    res, err, status = rpc_call(url, method, params)
    if err:
        print("\tError %d: %s" % (status, err))
        exit(status)
    print("\tResponse: %s" % res)
    return res


def print_color(text, color): print(color, text, DEFAULT)


def step(title): print_color("#=== %s ===#" % title, BLUE)


def skipped():
    print("\tResult:", end="")
    print_color("SKIPPED", YELLOW)
    print()


def assert_equals(expected, actual):
    res = expected == actual
    print("\tResult:", end="")
    if res:
        print_color("OK", GREEN)
        print()
    else:
        print_color("ERROR", RED)
        print("\tExpected:", expected)
        print("\tActual:  ", actual)
        exit(1)


step("Johann notify a car crash")
assert_equals(True, request_webservice("http://localhost:8080/incident", "notifyCarCrash",
                                       {"username": "Johann", "latitude": 10, "longitude": 25}))

step("Johann's insurance has been notified")
if args.skip_externals:
    skipped()
else:
    assert_equals(True, requests.get("http://localhost:5000/insurances/Johann").json().get("requestedInsurance", None))

step("Erick is asked to take Johann's packages")
notifications = request_webservice("http://localhost:8080/notification", "pullNotificationForUser", {"username": "Erick"})
assert_equals(2, len(notifications))

step("Erick accept to take Jeremy's package")
parameters = {"missionId": notifications[0]["answer"]["parameters"]["missionId"], "username": "Erick", "answer": True}
assert_equals(True, request_webservice("http://localhost:8080/package", "answerToPendingMission", parameters))

step("Erick refuse to take Thomas' package")
parameters = {"missionId": notifications[1]["answer"]["parameters"]["missionId"], "username": "Erick", "answer": True}
assert_equals(True, request_webservice("http://localhost:8080/package", "answerToPendingMission", parameters))

step("Johann is notified that Erick will take the packages")
assert_equals(2, len(
    request_webservice("http://localhost:8080/notification", "pullNotificationForUser", {"username": "Johann"})))

step("Jeremy is notified that Johann had an accident and that Erick will take his package")
assert_equals(2, len(
    request_webservice("http://localhost:8080/notification", "pullNotificationForUser", {"username": "Jeremy"})))

step("Thomas is notified that Johann had an accident and that Erick will take his package")
assert_equals(2, len(
    request_webservice("http://localhost:8080/notification", "pullNotificationForUser", {"username": "Thomas"})))

if not args.skip_externals:
    nb_points_before = requests.get("http://localhost:5001/users/Johann").json().get("points", None)

step("The package is dropped")
assert_equals(True, request_webservice("http://localhost:8080/package", "missionFinished", {"mission": 8}))

if args.skip_externals:
    skipped()
else:
    nb_points_after = requests.get("http://localhost:5001/users/Johann").json().get("points", None)
    assert_equals(True, nb_points_after > nb_points_before)

if args.skip_externals:
    skipped()
else:
    response = requests.get("http://localhost:5002/find_driver?start_lat=10.0&start_long=12.0"
                            "&end_lat=10.0&end_long=42.0").json()
    driver = response["drivers"][0]
    assert_equals("Erick", driver["name"])
    assert_equals("10.0,12.0", driver["from"])
    assert_equals("10.0,42.0", driver["to"])


step("Erick take Jeremy's package and Jeremy is notified")
request_webservice("http://localhost:8080/package",
                   "takePackage",
                   {"missionId": notifications[0]["answer"]["parameters"]["missionId"], "username": "Erick"})
assert_equals(1, len(request_webservice("http://localhost:8080/notification", "pullNotificationForUser", {"username": "Jeremy"})))

step("Johann drops Thomas' package to Julien's house and Thomas is notified")
request_webservice("http://localhost:8080/package",
                   "dropPackageToHost",
                   {"missionId": notifications[1]["answer"]["parameters"]["missionId"], "username": "Julien"})
assert_equals(1, len(request_webservice("http://localhost:8080/notification", "pullNotificationForUser", {"username": "Thomas"})))

step("Loic takes Thomas' package from Julien's house and Thomas is notified")
request_webservice("http://localhost:8080/package",
                   "takePackageFromHost",
                   {"missionId": notifications[1]["answer"]["parameters"]["missionId"], "username": "Loic"})
assert_equals(1, len(request_webservice("http://localhost:8080/notification", "pullNotificationForUser", {"username": "Thomas"})))