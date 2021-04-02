#!/bin/bash

echo "========== START BUILD =========="

echo "mvn clean"
mvn clean && echo "Success!!!" || { echo "What the Fail"; exit 99; }

echo "mvn package"
mvn package && echo "Success!!!" || { echo "What the Fail"; exit 99; }

echo "========== END BUILD =========="
