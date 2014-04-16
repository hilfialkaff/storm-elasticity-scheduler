#!/bin/bash

CONFIG_NAME="/proj/CS525/exp/stormCluster/project/storm-elasticity-scheduler/config"
USERNAME=`cat $CONFIG_NAME | head -n 1`
SLEEP_TIME=`cat $CONFIG_NAME | head -n 2 | tail -n 1`
METRIC_PATH=`cat $CONFIG_NAME | head -n 3 | tail -n 1` # Path to metrics file on remote host
RESULT_PATH=`cat $CONFIG_NAME | head -n 4 | tail -n 1` # Path to metrics file will be stored
NUM_NODE=`cat $CONFIG_NAME | head -n 5 | tail -n 1`

sudo rm -r $RESULT_PATH
if [ ! -d "$RESULT_PATH" ]; then
	echo "created directory $RESULT_PATH"
	mkdir $RESULT_PATH
fi
if [ ! -d $RESULT_PATH/data ]; then
	echo "created directory $RESULT_PATH/data"
	mkdir $RESULT_PATH/data
fi

while :
do
	if [ -f $RESULT_PATH/metrics.log ];
	then
		echo "/***********METRICS EXISTS!!!*************/"
		rm $RESULT_PATH/data/metrics_tmp.log
		for ((i=1; i<=$5; ++i )) ; do
			HOSTNAME=node-${i}.stormcluster.cs525.emulab.net
			echo "!--${HOSTNAME}--!"

			echo "$RESULT_PATH/data/current_metrics_$HOSTNAME > $RESULT_PATH/data/previous_metrics_$HOSTNAME"
			cat $RESULT_PATH/data/current_metrics_$HOSTNAME > $RESULT_PATH/data/previous_metrics_$HOSTNAME

			echo "rm $RESULT_PATH/data/current_metrics_$HOSTNAME"
			rm $RESULT_PATH/data/current_metrics_$HOSTNAME

			echo "scp  $USERNAME@$HOSTNAME:$METRIC_PATH $RESULT_PATH/data/current_metrics_$HOSTNAME"
			scp $USERNAME@$HOSTNAME:$METRIC_PATH $RESULT_PATH/data/current_metrics_$HOSTNAME

			echo "diff $RESULT_PATH/data/current_metrics_$HOSTNAME $RESULT_PATH/data/previous_metrics_$HOSTNAME | grep execute_count | grep -v "^---" | grep -v "^[0-9c0-9]" | sed 's/^..//' > $RESULT_PATH/data/latest_interval_metrics_$HOSTNAME"
			diff $RESULT_PATH/data/current_metrics_$HOSTNAME $RESULT_PATH/data/previous_metrics_$HOSTNAME | grep execute_count | grep -v "^---" | grep -v "^[0-9c0-9]" | sed 's/^..//' > $RESULT_PATH/data/latest_interval_metrics_$HOSTNAME

			echo "cat $RESULT_PATH/data/latest_interval_metrics_$HOSTNAME >> $RESULT_PATH/data/metrics.log"
			cat $RESULT_PATH/data/latest_interval_metrics_$HOSTNAME >> $RESULT_PATH/data/metrics_tmp.log
		done
		cat $RESULT_PATH/data/metrics_tmp.log > $RESULT_PATH/metrics.log
	else
		echo "/************FIRST TIME!!!!!**************/"
		for ((i=1; i<=$5; ++i )) ; do
			HOSTNAME=node-${i}.stormcluster.cs525.emulab.net
			echo "!--${HOSTNAME}--!"

			echo "scp $USERNAME@$HOSTNAME:$METRIC_PATH $RESULT_PATH/data/current_metrics_$HOSTNAME"
			sshpass  -p "$6" scp $USERNAME@$HOSTNAME:$METRIC_PATH $RESULT_PATH/data/current_metrics_$HOSTNAME

			echo "$RESULT_PATH/data/current_metrics_$HOSTNAME >> $RESULT_PATH/metrics.log"
			cat $RESULT_PATH/data/current_metrics_$HOSTNAME | grep execute_count >> $RESULT_PATH/metrics.log
		done
	fi
	#diff metrics.log metrics1 | grep execute_count | grep -v "^---" | grep -v "^[0-9c0-9]" | sed 's/^..//'
	#cat $RESULT_PATH/* > $RESULT_PATH/metrics.log
	sleep $SLEEP_TIME
done
