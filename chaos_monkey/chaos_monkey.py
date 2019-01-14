#!/usr/bin/env python3

from flask import Flask, render_template, request, redirect
import pika
import json
import logging

from confirm_receiver import ConfirmConsumer

app = Flask(__name__)

features = {"NOTIFY_CAR_CRASH": 0.2, "NOTIFY_PACKAGE_HOSTING": 0}

connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
submit_channel = connection.channel()
confirm_channel = connection.channel()

submit_channel.exchange_declare(exchange='submit_chaos_settings', exchange_type='fanout')
confirm_channel.exchange_declare(exchange='confirm_chaos_settings', exchange_type='fanout')

result = confirm_channel.queue_declare(exclusive=True)
queue_name = result.method.queue
confirm_channel.queue_bind(exchange='submit_chaos_settings', queue=queue_name)


def on_confirm(ch, method, properties, body):
    global features
    print("confirmed")
    print(body)
    features = json.loads(body)
    ch.stop_consuming()


confirm_channel.basic_consume(on_confirm,
                      queue=queue_name,
                      no_ack=True)


@app.route("/")
def index():
    status = request.args["status"] if "status" in request.args else ""
    return render_template('index.html', settings=features, status=status)


@app.route("/push_settings", methods=['POST'])
def template_test():
    submit_channel.basic_publish(exchange='submit_chaos_settings',
                        routing_key='',
                        body=json.dumps(request.form))
    print("submitted")
    confirm_channel.start_consuming()
    return redirect("/?status=success")


if __name__ == '__main__':
    app.run(debug=True, port=5008)