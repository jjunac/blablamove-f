#!/usr/bin/env python3

from flask import Flask, render_template, request, redirect, jsonify
import pika
import json
import logging
import time
from utils import parse_properties

app = Flask(__name__)

time.sleep(7)

#######################
### S E T U P
#######################

app.logger.setLevel(logging.DEBUG)
app.logger.info("Setting up the chaos...")

settings = parse_properties("settings.properties")
app.logger.info("Loaded settings: " + str(settings))
app.logger.info("%d settings loaded" % len(settings))


#######################
### R O U T E S
#######################

@app.route("/")
def route_index():
    # status = request.args["status"] if "status" in request.args else ""
    return render_template('index.html', settings=settings, args=request.args)


@app.route("/settings", methods=['POST', 'GET'])
def route_settings():
    global settings
    if request.method == 'GET':
        return jsonify(settings)
    else:
        try:
            connection = pika.BlockingConnection(pika.ConnectionParameters(host='queue'))
            submit_channel = connection.channel()

            submit_channel.exchange_declare(exchange='submit_chaos_settings', exchange_type='fanout')
            submit_channel.basic_publish(exchange='submit_chaos_settings',
                                         routing_key='',
                                         body=json.dumps(request.form))
            settings = request.form
            print("Sending new settings to the nodes")
            return redirect("/?status=success")
        except BaseException as e:
            return redirect("/?status=fail&error=" + type(e).__name__)


if __name__ == '__main__':
    app.run(debug=True, port=5008)
