#!/usr/bin/env python3

import pika
import json

connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
channel = connection.channel()

channel.exchange_declare(exchange='submit_chaos_settings', exchange_type='fanout')

def callback(ch, method, properties, body):
    print(" [x] %r" % body)

if __name__ == '__main__':
    result = channel.queue_declare(exclusive=True)
    queue_name = result.method.queue

    channel.queue_bind(exchange='submit_chaos_settings', queue=queue_name)

    print(' [*] Waiting for logs. To exit press CTRL+C')

    channel.basic_consume(callback,
                        queue=queue_name,
                        no_ack=True)

    channel.start_consuming()