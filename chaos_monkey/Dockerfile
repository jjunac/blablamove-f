FROM python:3-alpine

COPY ./requirements.txt /app/requirements.txt
WORKDIR /app

RUN pip install -r requirements.txt

COPY . /app

EXPOSE 5008

CMD [ "python", "./main.py" ]
