import functools
import uuid
from threading import Thread, Barrier
from time import sleep

import pika
import requests

QUEUE_HOST = "localhost"

rpc_id = 0

server_address = "http://localhost:8080"
incident_url = f"{server_address}/incident"
notification_url = f"{server_address}/notification"
package_url = f"{server_address}/package"

missions = []


def limit_arg_number(number):
    def arg_number(func):
        def wrapper(*args):
            if len(args) != number:
                print(
                    f"wrong number of arguments for {func.__name__} expected {number}")
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
    res, err, status = rpc_call(url, method, params)
    if output_message and res and not err:
        print(f"#===== {output_message} =====#")
    if err:
        print(f"Error {status:d}: {err}")
        # exit(status)
    return res


def quit(*args):
    exit()


def help(*args):
    for command in commands.keys():
        print(command)


@limit_arg_number(1)
def mission_finished(*args):
    print(args[0])
    if request_webservice(package_url, "missionFinished", {"mission": int(args[0])}):
        print(f"Mission {args[0]} finished")


@limit_arg_number(2)
def notify_car_crash(*args):
    request_webservice(incident_url, "notifyCarCrash", {
        "username": username, "latitude": args[0], "longitude": args[1]}, "You notified the car crash")


def pull_notification_for_user(*args):
    notifications = request_webservice(
        notification_url, "pullNotificationForUser", {"username": username})
    for notification in notifications:
        if "answer" in notification and notification["answer"] and notification["answer"]["route"] is not None:
            answer = notification["answer"]
            user_answer = input(notification["message"] + "(yes/no) ")
            responses = {"yes": True, "y": True, "n": False, "no": False}
            while user_answer.lower() not in responses:
                print("yes/no")
                user_answer = input(notification["message"] + " ")
            if responses[user_answer]:
                id = answer["parameters"].get("missionId", None) or answer["parameters"].get("parcelId", None)
                missions.append({"id": id, "message": notification["message"]})
                print("id", id)
            parameters = answer["parameters"]
            parameters["answer"] = responses[user_answer]
            request_webservice(
                server_address + answer["route"], answer["methodName"], parameters, "You answered")
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
                for notif in res:
                    # plyer.notification.notify(
                    #     title="yolo", message=notif['message'])
                    print(f"You received: {notif['message']}")
        except Exception as e:
            print(e)
            exit(1)


@limit_arg_number(1)
def take_package(*args):
    request_webservice(package_url, "takePackage", {
        "username": username, "missionId": args[0]}, "You took the package")


@limit_arg_number(1)
def take_package_from_host(*args):
    request_webservice(package_url, "takePackageFromHost", {
        "username": username, "parcelId": args[0]}, "You took the package")


@limit_arg_number(2)
def drop_package(*args):
    request_webservice(package_url, "dropPackageToHost", {
        "username": args[0], "parcelId": args[1]}, "You dropped the package")


def list_missions(*args):
    for mission in missions:
        print(f"id: {mission['id']} , objective: {mission['message']}")


@limit_arg_number(1)
def send_message(*args):
    queueWrapper.publish_message("test", args[0])


commands = {"help": help, "exit": quit, "notify_car_crash": notify_car_crash,
            "pull_notifications": pull_notification_for_user,
            "wait_notifications": wait_notifications, "take_package": take_package, "drop_package": drop_package,
            "mission_finished": mission_finished, "missions": list_missions,
            "take_package_from_host": take_package_from_host, "send": send_message}


class QueueWrapper:

    def __init__(self, host) -> None:
        self.host = host
        self.correlation_id = None
        self.connection = None
        self.channel = None
        self.callbacks = {}
        parameters = pika.ConnectionParameters(host=self.host)
        if self.connection is not None:
            self.connection.close()
        blocking_connection = pika.BlockingConnection(parameters)
        self.connection = blocking_connection
        self.channel = blocking_connection.channel()
        self.correlation_id = str(uuid.uuid4())
        self.thread = None

    def setup_wrapper(self):
        self.channel.start_consuming()

    def start_consuming(self):
        self.thread = Thread(target=self.setup_wrapper)
        self.thread.start()

    def _add_consume_callback(self, queue_name, callback):
        if queue_name not in self.callbacks:
            raise Exception("You have to add the queue first")
        self.channel.basic_consume(callback, queue=self.callbacks[queue_name])

    def add_response_callback(self, queue_name, callback):
        self._add_consume_callback(queue_name, callback)

    def _add_queue(self, queue_name, **kwargs):
        self.channel.queue_declare(queue=queue_name, **kwargs)
        response = self.channel.queue_declare(exclusive=True)
        self.callbacks[queue_name] = response.method.queue

    def add_queue(self, queue_name, **kwargs):
        self._add_queue(queue_name, **kwargs)

    def add_callback_threadsafe(self, callback):
        self.connection.add_callback_threadsafe(callback)

    def _publish_message(self, queue_name, message):
        if queue_name not in self.callbacks:
            raise Exception("You have to add the queue first")
        self.channel.basic_publish(exchange='',
                                   routing_key=queue_name,
                                   properties=pika.BasicProperties(reply_to=self.callbacks[queue_name],
                                                                   correlation_id=self.correlation_id),
                                   body=message)

    def publish_message(self, queue_name, message):
        self.add_callback_threadsafe(functools.partial(self._publish_message, queue_name, message))


def consumer_callback(channel, method, properties, body):
    print(f"you connected successfully: {body.decode('utf8')}")
    channel.basic_ack(delivery_tag=method.delivery_tag)
    b.wait()


def consumer_callback2(channel, method, properties, body):
    print(f"\rreceived: {body.decode('utf8')}")
    print(f"({username}) command: ", end="", flush=True)
    channel.basic_ack(delivery_tag=method.delivery_tag)


if __name__ == '__main__':
    queueWrapper = QueueWrapper(QUEUE_HOST)
    queueWrapper.add_queue("login")
    queueWrapper.add_queue("test")
    queueWrapper.add_response_callback("login", consumer_callback)
    queueWrapper.add_response_callback("test", consumer_callback2)
    queueWrapper.start_consuming()
    b = Barrier(2)
    try:
        username = input("What is your username: ")
        queueWrapper.publish_message("login", username)
        b.wait()
        while True:
            line = input(f"({username}) command: ").split(" ")
            command = line[0]
            if command not in commands:
                print("Command does not exist, type help to have the list of commands")
            else:
                commands[command](*line[1:])
    except EOFError:
        exit(0)
    finally:
        queueWrapper.channel.stop_consuming()
