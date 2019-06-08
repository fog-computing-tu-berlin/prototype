# Problems with Alpine due to GCC
FROM python:3.6

# TODO non root user
COPY src requirements.txt /src/
WORKDIR /src

RUN pip install --no-cache-dir  -r requirements.txt

EXPOSE 5000/tcp
EXPOSE 5555/tcp

CMD python /src/server.py