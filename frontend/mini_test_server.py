#!/usr/bin/env python
import argparse

import pika

connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))

channel = connection.channel()

parser = argparse.ArgumentParser()
parser.add_argument("queue_name", nargs="?", default="login")
args = parser.parse_args()

channel.queue_declare(queue=args.queue_name)


def fib(n):
    if n == 0:
        return 0
    elif n == 1:
        return 1
    else:
        return fib(n - 1) + fib(n - 2)


def on_request(ch, method, props, body):
    n = body.decode("utf8")

    print(f"received {n}")
    response = n

    ch.basic_publish(exchange='',
                     routing_key=props.reply_to,
                     properties=pika.BasicProperties(correlation_id= \
                                                         props.correlation_id),
                     body=str(response))
    ch.basic_ack(delivery_tag=method.delivery_tag)


channel.basic_qos(prefetch_count=1)
channel.basic_consume(on_request, queue=args.queue_name)

print(" [x] Awaiting RPC requests")
channel.start_consuming()
