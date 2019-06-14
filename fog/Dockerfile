# Problems with Alpine due to GCC
FROM python:3.6

# TODO non root user
COPY requirements.txt /src/
WORKDIR /src

RUN pip install --no-cache-dir  -r requirements.txt

EXPOSE 5000/tcp
EXPOSE 5555/tcp
EXPOSE 5556/tcp
EXPOSE 5557/tcp

COPY src /src/

CMD python /src/server.py