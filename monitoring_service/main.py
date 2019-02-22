import pika
from time import strftime, gmtime

if __name__ == '__main__':
    print("Connecting to rabbimq")
    connection = pika.BlockingConnection(pika.ConnectionParameters(host='queue', port=5672, connection_attempts=5))
    channel = connection.channel()
    channel.exchange_declare(exchange='chaos_logs_exchange', exchange_type='fanout')
    result = channel.queue_declare(exclusive=True)
    queue_name = result.method.queue

    channel.queue_bind(exchange='chaos_logs_exchange',
                       queue=queue_name)


    def callback(ch, method, properties, body):
        print(f"{strftime('%Y-%m-%d %H:%M:%S', gmtime())} {body.decode('utf-8')}")


    channel.basic_consume(callback,
                          queue=queue_name,
                          no_ack=True)

    print("Monitor up and running")
    channel.start_consuming()
