FROM python:3.6 as looper

COPY . .

RUN apt-get update \
	    && apt-get -y install libglib2.0-0 libgl1-mesa-glx

RUN pip install -r requirements.txt

ENV INPUT_DIR="/in"
ENV RESULT_DIR="/out"

RUN mkdir $INPUT_DIR && mkdir $RESULT_DIR

ENTRYPOINT ["get_ckpt_and_run.sh"]
