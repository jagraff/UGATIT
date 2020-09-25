FROM python:3.6 as looper

COPY requirements.txt .

RUN apt-get update \
	    && apt-get -y install libglib2.0-0 libgl1-mesa-glx \
	    && pip install -r requirements.txt


RUN apt-get -y install default-jdk ffmpeg

ENV INPUT_DIR="/tmp/UnProcessed"
ENV RESULT_DIR="/tmp/Processed"

RUN mkdir $INPUT_DIR && mkdir $RESULT_DIR

ADD gif-to-anime /gif-to-anime

WORKDIR /app

COPY *.py ./
COPY get_ckpt_and_run.sh .

CMD ["bash", "get_ckpt_and_run.sh"]
