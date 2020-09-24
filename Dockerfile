FROM python:3.6 as base

WORKDIR /app

COPY checkpoint.zip .

RUN unzip checkpoint.zip


FROM base as looper

COPY . .

RUN apt-get update \
	    && apt-get -y install libglib2.0-0 libgl1-mesa-glx

RUN pip install -r requirements.txt

ENV INPUT_DIR="/in"
ENV RESULT_DIR="/out"

RUN mkdir $INPUT_DIR && mkdir $RESULT_DIR

CMD ["python", "looper.py"]
