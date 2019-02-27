FROM python:3.7.2-stretch
WORKDIR /app
COPY . .
RUN pip3 install pika==0.12.0
ENTRYPOINT ["python", "main.py"]