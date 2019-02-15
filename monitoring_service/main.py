#!/usr/bin/env python3

from flask import Flask, render_template, request, redirect, jsonify
import pika

app = Flask(__name__)


connection = pika.BlockingConnection(pika.ConnectionParameters(host='queue'))
channel = connection.channel()

channel.exchange_declare(exchange='chaos_logs_exchange', exchange_type='fanout')
result = channel.queue_declare(exclusive=True)
queue_name = result.method.queue

channel.queue_bind(exchange='chaos_logs_exchange',
                   queue=queue_name)


def callback(ch, method, properties, body):
    print(" [x] %r" % body)

channel.basic_consume(callback,
                      queue=queue_name,
                      no_ack=True)

channel.start_consuming()

if __name__ == '__main__':
    app.run(debug=True, port=5009)