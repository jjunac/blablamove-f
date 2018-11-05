from time import sleep
import requests
from argparse import ArgumentParser

rpc_id = 0

server_address = "http://localhost:8080"
incident_url = f"{server_address}/incident"
notification_url = f"{server_address}/notification"


def limit_arg_number(number):
    def arg_number(func):
        def wrapper(*args):
            print(*args)
            if len(args) != number:
                print(f"wrong number of arguments for {func.__name__} expected {number}")
            else:
                func(*args)

        return wrapper

    return arg_number


def rpc_call(url, method, params):
    global rpc_id
    rpc_id += 1
    res = requests.post(
        url, json={"jsonrpc": "2.0", "method": method, "id": rpc_id, "params": params})
    response_object = res.json()
    return response_object.get("result", None), response_object.get("error", None), res.status_code


def request_webservice(url, method, params, output_message=None):
    if output_message:
        print(f"#===== {output_message} =====#")
    res, err, status = rpc_call(url, method, params)
    if err:
        print(f"Error {status:d}: {err}")
        exit(status)
    return res


def quit(*args):
    exit()


def help(*args):
    for command in commands.keys():
        print(command)


@limit_arg_number(2)
def notify_car_crash(*args):
    request_webservice(incident_url, "notifyCarCrash", {
        "username": username, "latitude": args[0], "longitude": args[1]}, "You notified the car crash")


def pull_notification_for_user(*args):
    notifications = request_webservice(notification_url, "pullNotificationForUser", {"username": username},
                                       "You received a notification")
    for notification in notifications:
        answer = notification["answer"]
        if answer:
            user_answer = input(notification["message"] + " ")
            responses = {"yes": True, "no": False}
            while user_answer.lower() not in responses:
                user_answer = input(notification["message"] + " ")
            parameters = answer["parameters"]
            parameters["answer"] = responses[user_answer]
            request_webservice(server_address + answer["route"], answer["methodName"], parameters, "You answered")
        else:
            print(notification["message"])


def wait_notifications(*args):
    while True:
        try:
            res, err, status = rpc_call(
                notification_url, "pullNotificationForUser", {"username": username})
            if err and status != 504:
                print(f"Error {status:d}: {err}")
                exit(status)
            elif not res:
                sleep(2)
            else:
                print(f"#===== You received a notification =====#")
                print(f"Response: {res}")
        except Exception as e:
            print(e)
            exit(1)


commands = {"help": help, "exit": quit, "notify_car_crash": notify_car_crash,
            "pull_notifications": pull_notification_for_user,
            "wait_notifications": wait_notifications}

username = input("What is your username: ")
while True:
    try:
        line = input(f"({username}) command: ").split(" ")
        command = line[0]
        if command not in commands:
            print("Command does not exist, type help to have the list of commands")
        else:
            commands[command](*line[1:])
    except EOFError:
        exit(0)
