#!/usr/bin/env python3

import requests

rpc_id = 0


def rpc_call(url, method, params):
    global rpc_id
    rpc_id += 1
    res = requests.post(url, json={"jsonrpc": "2.0", "method": method, "id": rpc_id, "params": params})
    response_object = res.json()
    return response_object.get("result", None), response_object.get("error", None), res.status_code


def request_webservice(url, method, params, output_message):
    print("#===== %s =====#" % output_message)
    res, err, status = rpc_call(url, method, params)
    if err:
        print("Error %d: %s" % (status, err))
        exit(status)
    print("Response: %s" % res)
    print()
    return res
    

assert request_webservice("http://localhost:8080/incident", "notifyCarCrash", {"username": "Johann"}, "Johann notify a car crash")
assert len(request_webservice("http://localhost:8080/notification", "pullNotificationForUser", {"username": "Johann"}, "Johann is notified that Erick will take the packages")) == 2
assert len(request_webservice("http://localhost:8080/notification", "pullNotificationForUser", {"username": "Erick"}, "Erick is notified that he will take Johann's packages")) == 2
assert len(request_webservice("http://localhost:8080/notification", "pullNotificationForUser", {"username": "Jeremy"}, "Jeremy is notified that Johann had an accident and that Erick will take his package")) == 2
assert len(request_webservice("http://localhost:8080/notification", "pullNotificationForUser", {"username": "Thomas"}, "Thomas is notified that Johann had an accident and that Erick will take his package")) == 2