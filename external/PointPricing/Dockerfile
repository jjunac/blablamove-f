FROM tiangolo/uwsgi-nginx-flask:python3.7
ENV LISTEN_PORT 5000
ENV UWSGI_CHEAPER 0
ENV UWSGI_PROCESSES 1
EXPOSE 5001
COPY ./requirements.txt /app
RUN pip install -r requirements.txt
COPY ./*.py /app/