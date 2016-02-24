#!/usr/bin/env bash
START_TIME=`date +%d_%m_%Y_%H_%M_%S`
./run-activiti5.sh $START_TIME
./run-activiti6.sh $START_TIME
open -a "Google Chrome" --args file://`pwd`/target/classes/report_template.html