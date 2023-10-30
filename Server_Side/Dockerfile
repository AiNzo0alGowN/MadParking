FROM ubuntu:23.10
RUN apt-get update && apt-get install -y python3 python3-pip
RUN pip3 install flask --break-system-packages
WORKDIR /app
COPY . /app
CMD python3 ./app.py | tee output.log