#!/bin/bash
#$1 - username
#$2 - sleep time
#$3 - path to metrics file on remote host
#$4 - path to folder at which metrics files will be stored
#$5 - num of nodes
USERNAME=${1}
SCRIPT="sudo rm $3"
if [ ! -d "$4" ]; then
	echo "created directory $4"
	mkdir $4
fi
if [ ! -d $4/data ]; then
	echo "created directory $4/data"
	mkdir $4/data
fi

while :
do
	if [ -f $4/metrics.log ];
	then
		echo "/***********METRICS EXISTS!!!*************/"
		rm ${4}/data/metrics_tmp.log
		for ((i=1; i<=$5; ++i )) ; do
			HOSTNAME=node-${i}.stormcluster.cs525.emulab.net
			echo "!--${HOSTNAME}--!"
 
			echo "${4}/data/current_metrics_$HOSTNAME > ${4}/data/previous_metrics_$HOSTNAME"
			cat ${4}/data/current_metrics_$HOSTNAME > ${4}/data/previous_metrics_$HOSTNAME 

			echo "rm ${4}/data/current_metrics_$HOSTNAME"
			rm ${4}/data/current_metrics_$HOSTNAME

			echo "scp  $USERNAME@$HOSTNAME:$3 ${4}/data/current_metrics_$HOSTNAME"
			scp $USERNAME@$HOSTNAME:$3 ${4}/data/current_metrics_$HOSTNAME

			echo "diff ${4}/data/current_metrics_$HOSTNAME ${4}/data/previous_metrics_$HOSTNAME | grep execute_count | grep -v "^---" | grep -v "^[0-9c0-9]" | sed 's/^..//' > ${4}/data/latest_interval_metrics_$HOSTNAME"
			diff ${4}/data/current_metrics_$HOSTNAME ${4}/data/previous_metrics_$HOSTNAME | grep execute_count | grep -v "^---" | grep -v "^[0-9c0-9]" | sed 's/^..//' > ${4}/data/latest_interval_metrics_$HOSTNAME
			
			echo "cat ${4}/data/latest_interval_metrics_$HOSTNAME >> ${4}/data/metrics.log"
			cat ${4}/data/latest_interval_metrics_$HOSTNAME >> ${4}/data/metrics_tmp.log
		done
		cat ${4}/data/metrics_tmp.log > ${4}/metrics.log
	else
		echo "/************FIRST TIME!!!!!**************/"
		for ((i=1; i<=$5; ++i )) ; do
			HOSTNAME=node-${i}.stormcluster.cs525.emulab.net
			echo "!--${HOSTNAME}--!"

			echo "scp $USERNAME@$HOSTNAME:$3 ${4}/data/current_metrics_$HOSTNAME"
			scp $USERNAME@$HOSTNAME:$3 ${4}/data/current_metrics_$HOSTNAME
			
			echo "${4}/data/current_metrics_$HOSTNAME >> ${4}/metrics.log"
			cat ${4}/data/current_metrics_$HOSTNAME >> ${4}/metrics.log
		done	
		#ssh -o StrictHostKeyChecking=no -l ${USERNAME} ${HOSTNAME} "${SCRIPT}"
	fi
	#diff metrics.log metrics1 | grep execute_count | grep -v "^---" | grep -v "^[0-9c0-9]" | sed 's/^..//'
	#cat ${4}/* > ${4}/metrics.log
	sleep $2
done

