from time import sleep
import requests
from argparse import ArgumentParser

rpc_id = 0

server_address = "http://localhost:8080"
incident_url = f"{server_address}/incident"
notification_url = f"{server_address}/notification"


def rpc_call(url, method, params):
    global rpc_id
    rpc_id += 1
    res = requests.post(
        url, json={"jsonrpc": "2.0", "method": method, "id": rpc_id, "params": params})
    response_object = res.json()
    return response_object.get("result", None), response_object.get("error", None), res.status_code


def request_webservice(url, method, params, output_message):
    print(f"#===== {output_message} =====#")
    res, err, status = rpc_call(url, method, params)
    if err:
        print(f"Error {status:d}: {err}")
        exit(status)
    print(f"Response: {res}")
    return res


def quit():
    exit()


def help():
    for command in commands.keys():
        print(command)


def notify_car_crash():
    request_webservice(incident_url, "notifyCarCrash", {
                       "username": username}, "You notified the car crash")


def pull_notification_for_user():
    request_webservice(notification_url, "pullNotificationForUser", {"username": username},
                       "You received a notification")


def wait_notifications():
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
                notification.notify(
                    title='You received a notification',
                    message=f'{res}',
                    app_name='blablamove',
                )
        except Exception as e:
            print(e)
            exit(1)


commands = {"help": help, "exit": quit, "notify_car_crash": notify_car_crash,
            "pull_notification": pull_notification_for_user,
            "wait_notifications": wait_notifications}

username = input("What is your username: ")
while True:
    command = input(f"({username}) command: ")
    if command not in commands:
        print("Command does not exist, type help to have the list of commands")
    else:
        commands[command]()
