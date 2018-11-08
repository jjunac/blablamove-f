#!/usr/bin/env python3

import requests
import argparse

parser = argparse.ArgumentParser()
parser.add_argument("--skip-externals", help="skip external service tests", action="store_true")
parser.add_argument("--host", help="Specify host. Default: localhost", type=str, default="localhost")
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


if not args.skip_externals:
    johanns_points_before = requests.get("http://" + args.host + ":5001/users/Johann").json().get("points", None)
    ericks_points_before = requests.get("http://" + args.host + ":5001/users/Erick").json().get("points", None)
    juliens_points_before = requests.get("http://" + args.host + ":5001/users/Julien").json().get("points", None)
    loics_points_before = requests.get("http://" + args.host + ":5001/users/Loic").json().get("points", None)

step("Johann notifies a car crash")
assert_equals(True, request_webservice("http://" + args.host + ":8080/incident", "notifyCarCrash",
                                       {"username": "Johann", "latitude": 10, "longitude": 25}))

step("Johann has been paid for his contribution")
if args.skip_externals:
    skipped()
else:
    johanns_points_after = requests.get("http://" + args.host + ":5001/users/Johann").json().get("points", None)
    assert_equals(True, johanns_points_after > johanns_points_before)

step("Johann's insurance has been notified")
if args.skip_externals:
    skipped()
else:
    assert_equals(True, requests.get("http://" + args.host + ":5000/insurances/Johann").json().get("requestedInsurance", None))

step("Jeremy and Thomas are notified that Johann had an accident")
assert_equals(1, len(request_webservice("http://" + args.host + ":8080/notification", "pullNotificationForUser", {"username": "Jeremy"})))
assert_equals(1, len(request_webservice("http://" + args.host + ":8080/notification", "pullNotificationForUser", {"username": "Thomas"})))

if args.skip_externals:
    skipped()
else:
    step("Looking for a driver nearby who can take the packages")
    response = requests.get("http://" + args.host + ":5002/find_driver?start_lat=10.0&start_long=12.0"
                            "&end_lat=10.0&end_long=42.0").json()
    driver = response["drivers"][0]
    assert_equals("Erick", driver["name"])
    assert_equals("10.0,12.0", driver["from"])
    assert_equals("10.0,42.0", driver["to"])

step("Erick is asked to take packages transported by Johann")
notifications = request_webservice("http://" + args.host + ":8080/notification", "pullNotificationForUser", {"username": "Erick"})
assert_equals(2, len(notifications))

jeremysAnswer = notifications[0]["answer"]
jeremysMissionId = jeremysAnswer["parameters"]["missionId"]
thomasAnswer = notifications[1]["answer"]
thomasMissionId = thomasAnswer["parameters"]["missionId"]

step("Erick accepts to take Jeremy's package")
parameters = {"missionId": jeremysMissionId, "username": jeremysAnswer["parameters"]["username"], "answer": True}
assert_equals(True, request_webservice("http://" + args.host + ":8080/" + jeremysAnswer["route"], jeremysAnswer["methodName"], parameters))

step("Johann is notified that Erick will take Jeremy's package")
assert_equals(1, len(request_webservice("http://" + args.host + ":8080/notification", "pullNotificationForUser", {"username": "Johann"})))

step("Jeremy is notified that Erick will take his package")
assert_equals(1, len(request_webservice("http://" + args.host + ":8080/notification", "pullNotificationForUser", {"username": "Jeremy"})))

step("Erick refuses to take Thomas' package")
parameters = {"missionId": thomasMissionId, "username": thomasAnswer["parameters"]["username"], "answer": False}
assert_equals(True, request_webservice("http://" + args.host + ":8080/" + thomasAnswer["route"], thomasAnswer["methodName"], parameters))

step("Julien is asked to host Thomas' package")
notifications = request_webservice("http://" + args.host + ":8080/notification", "pullNotificationForUser", {"username": "Julien"})
assert_equals(1, len(notifications))

thomasAnswer = notifications[0]["answer"]
thomasParcelId = thomasAnswer["parameters"]["parcelId"]

step("Julien accepts to take Thomas's package")
parameters = {"parcelId": thomasParcelId, "username": thomasAnswer["parameters"]["username"], "answer": True}
assert_equals(True, request_webservice("http://" + args.host + ":8080/" + thomasAnswer["route"], thomasAnswer["methodName"], parameters))

step("Johann is notified that Julien will host Thomas's package")
assert_equals(1, len(request_webservice("http://" + args.host + ":8080/notification", "pullNotificationForUser", {"username": "Johann"})))

step("Thomas is notified that Julien will host his package")
assert_equals(1, len(request_webservice("http://" + args.host + ":8080/notification", "pullNotificationForUser", {"username": "Thomas"})))

step("Erick takes Jeremy's package from Johann")
assert_equals(True, request_webservice("http://" + args.host + ":8080/package", "takePackage", {"missionId": jeremysMissionId, "username": "Erick"}))

step("Jeremy is notified that Erick took his package from Johann")
assert_equals(1, len(request_webservice("http://" + args.host + ":8080/notification", "pullNotificationForUser", {"username": "Jeremy"})))

step("Johann drops Thomas' package to Julien's house")
assert_equals(True, request_webservice("http://" + args.host + ":8080/package", "dropPackageToHost", {"parcelId": thomasParcelId, "username": "Julien"}))

step("Thomas is notified that Johann dropped his package to Julien's house")
assert_equals(1, len(request_webservice("http://" + args.host + ":8080/notification", "pullNotificationForUser", {"username": "Thomas"})))

step("Erick drops Jeremy's package to Jeremy's house")
assert_equals(True, request_webservice("http://" + args.host + ":8080/package", "missionFinished", {"mission": jeremysMissionId}))

step("Erick has been paid for his contribution")
if args.skip_externals:
    skipped()
else:
    ericks_points_after = requests.get("http://" + args.host + ":5001/users/Erick").json().get("points", None)
    assert_equals(True, ericks_points_after > ericks_points_before)

step("Loic takes Thomas' package from Julien's house")
assert_equals(True, request_webservice("http://" + args.host + ":8080/package", "takePackageFromHost", {"parcelId": thomasParcelId, "username": "Loic"}))

step("Thomas is notified that Loic took his package from Julien's house")
assert_equals(1, len(request_webservice("http://" + args.host + ":8080/notification", "pullNotificationForUser", {"username": "Thomas"})))

step("Loic drops Jeremy's package to Jeremy's house")
assert_equals(True, request_webservice("http://" + args.host + ":8080/package", "missionFinished", {"mission": 24}))

step("Loic has been paid for his contribution")
if args.skip_externals:
    skipped()
else:
    loics_points_after = requests.get("http://" + args.host + ":5001/users/Loic").json().get("points", None)
    assert_equals(True, loics_points_after > loics_points_before)

