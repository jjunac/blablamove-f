#!/usr/bin/env python3

import requests

RED = "\033[91m"
GREEN = "\033[92m"
WHITE = "\033[00m"
YELLOW = "\033[93m"

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

def print_color(text, color): print(color, text, WHITE)

def step(title): print_color("#=== %s ===#" % title, YELLOW)

def assert_equals(expected, actual):
    res = expected == actual
    print("\tResult:", end="")
    if res:
        print_color("OK", GREEN)
        print()
    else:
        print_color("ERROR", RED)
        exit(1)

step("Johann notify a car crash")
assert_equals(True, request_webservice("http://localhost:8080/incident", "notifyCarCrash", {"username": "Johann"}))

step("Johann is notified that Erick will take the packages")
assert_equals(2, len(request_webservice("http://localhost:8080/notification", "pullNotificationForUser", {"username": "Johann"})))

step("Erick is notified that he will take Johann's packages")
assert_equals(2, len(request_webservice("http://localhost:8080/notification", "pullNotificationForUser", {"username": "Erick"})))

step("Jeremy is notified that Johann had an accident and that Erick will take his package")
assert_equals(1, len(request_webservice("http://localhost:8080/notification", "pullNotificationForUser", {"username": "Jeremy"})))

step("Thomas is notified that Johann had an accident and that Erick will take his package")
assert_equals(2, len(request_webservice("http://localhost:8080/notification", "pullNotificationForUser", {"username": "Thomas"})))