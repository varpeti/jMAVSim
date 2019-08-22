#!/bin/bash

export PX4_HOME_LAT=32.990699
export PX4_HOME_LON=-117.128320
export PX4_HOME_ALT=208.5

ant create_run_jar copy_res
cd out/production
# java -XX:GCTimeRatio=20 -Djava.ext.dir= -jar jmavsim_run.jar -tcp 127.0.0.1:4560 -lockstep
# java -XX:GCTimeRatio=20 -Djava.awt.headless=true -Djava.ext.dir= -jar jmavsim_run.jar -tcp 127.0.0.1:4560 -lockstep -no-gui
java -XX:GCTimeRatio=20 -Djava.ext.dir= -jar jmavsim_run.jar -tcp 127.0.0.1:4560 -lockstep -no-gui
cd ../..
