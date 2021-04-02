#!/bin/bash

echo "========== START DEPLOY =========="

echo "nohup java -jar document-approval-0.0.1-SNAPSHOT.jar &"
cd ./target
nohup java -jar -Dserver.port=8080 document-approval-0.0.1-SNAPSHOT.jar &
tail -f nohup.out
echo "========== END DEPLOY =========="
