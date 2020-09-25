#!/bin/bash

CHECKPOINT_DIR="/app/checkpoint/"

if [ -d "$CHECKPOINT_DIR" ]; then
	echo "Found $CHECKPOINT_DIR"
else
	CHECKPOINT_FILE="/app/checkpoint.zip"
	if [ -f "$CHECKPOINT_FILE" ]; then
		echo "Found $CHECKPOINT_FILE; skipping download"
	else
		CHECKPOINT_URL="https://jacob-machine-unlearning.s3.amazonaws.com/checkpoints.zip"

		echo "Downloading checkpoint file"
		curl $CHECKPOINT_URL -o $CHECKPOINT_FILE
		unzip $CHECKPOINT_FILE
	fi
fi

echo "done"

python looper.py
