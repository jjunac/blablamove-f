FROM tiangolo/uwsgi-nginx-flask:python3.7
ENV LISTEN_PORT 5009
EXPOSE 5009
COPY . /app/
RUN pip install -r requirements.txt
ENTRYPOINT ["python", "/app/main.py"]