FROM tiangolo/uwsgi-nginx-flask:python3.7
ENV LISTEN_PORT 5000
EXPOSE 5000
COPY ./requirements.txt /app
RUN pip install -r requirements.txt
COPY ./*.py /app/