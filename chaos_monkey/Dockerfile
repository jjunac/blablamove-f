FROM tiangolo/uwsgi-nginx-flask:python3.7
ENV LISTEN_PORT 5008
ENV UWSGI_CHEAPER 0
ENV UWSGI_PROCESSES 1
EXPOSE 5008
COPY . /app/
RUN pip install -r requirements.txt