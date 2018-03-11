#!/bin/bash

if [ $# -eq 0 ]
then 
    echo "Usage ./automated_tester protocol number_of_clients"
    exit 1
fi

BIN_DIR=../out/production/DistributedGroupChat
MSG_DIR=../../inputs
PROTOCOL=$1
CLIENTS=$2


# first things first compile the project source
cd ../
export ANT_HOME=~/apache-ant-1.10.2
export PATH=${ANT_HOME}/bin:${PATH}
ant

#compress files for transfer
cd ../
tar czvf bins.tar.gz DistributedGroupChat inputs

# compilei input controller
cd DistributedGroupChat/test
javac Controller.java

# initiate input controller
java Controller $CLIENTS &
controller_pid=$!

# wait for the special files to be created
while [ ! -e ./control_pipe ]
do
:
done

# open ssh connections with the remote machines except this one
for i in $(seq 1 $CLIENTS)
do
    ssh -tt distrib30@distrib-$(((($i - 1) % 5) + 1)) < client_${i} &
    ssh_clients[$i]=$!
done

# transfer files
# open ssh connections with the remote machines except this one
for i in $(seq 1 $CLIENTS)
do
    scp ../../bins.tar.gz distrib30@distrib-$(((($i - 1) % 5) + 1)):bins2.tar.gz
    # extract files
    echo "$i mkdir files" > control_pipe
    echo "$i mv bins2.tar.gz files/" > control_pipe
    echo "$i cd files" > control_pipe
    echo "$i tar xzvf bins2.tar.gz" > control_pipe

    # cd to binaries
    echo "$i cd DistributedGroupChat/out/production/DistributedGroupChat" > control_pipe
done

# start tracker
java -cp "${BIN_DIR}:${BIN_DIR}/lib/guava-19.0.jar" tracker.Tracker > /dev/null &
tracker_pid=$!

# wait for the tracker to be ready
sleep 2

tracker_ip=`ifconfig ens3 | grep "inet addr" | awk '{split($2, a, ":"); print a[2];}'`

#start clients 
# #start local client
# java -cp "${BIN_DIR}" client.Client debug=true tracker=localhost:3000 protocol=$PROTOCOL < client_1 &
# local_client=$!

#start remote clients
for i in $(seq 1 $CLIENTS)
do
    echo "$i java client.Client debug=true tracker=${tracker_ip}:3000 protocol=$PROTOCOL" > control_pipe
done

#register clients with server
for i in $(seq 1 $CLIENTS)
do
    echo "$i !r" > control_pipe
    echo "$i !j distrib" > control_pipe
done

# wait for 10 seconds for heartbeats to take place
sleep 10

# start sending messages
for i in $(seq 1 $CLIENTS)
do
    # do this concurrently
    cat ${MSG_DIR}/messages$i.txt > client_$i &
done

sleep 10

# quit clients
for i in $(seq 1 $CLIENTS)
do
    echo "$i !q" > control_pipe
done




echo "press enter to stop"
read nothing

# do cleanup
for i in $(seq 1 $CLIENTS)
do
    echo ${ssh_clients[$i]}
    kill -9 ${ssh_clients[$i]}
done
# kill -9 $local_client
kill -9 $tracker_pid
kill -9 $controller_pid
rm client_* control_pipe

# transfer files and clean up remote machines
for i in $(seq 1 $CLIENTS)
do
    scp distrib30@distrib-$(((($i - 1) % 5) + 1)):files/DistributedGroupChat/out/production/DistributedGroupChat/*distrib.txt ./ 
    ssh distrib30@distrib-$(((($i - 1) % 5) + 1)) rm -rf files
done

for i in $(seq 1 $CLIENTS)
do
    a=$(($i -1))
    tail -n 3 ${a}_distrib.txt | head -n 1 >> throughputs.txt
    tail -n 2 ${a}_distrib.txt | head -n 1 >> latencies.txt
    tail -n 1 ${a}_distrib.txt >> messages.txt
done

# # we can run the testers
# if [ ! "$PROTOCOL" == "fifo" ]; then
#     # first we check if messages have total order
#     javac TotalOrderTester.java
#     java TotalOrderTester *distrib*
# fi

# #then we check if messages have fifo order
# javac FifoTester.java
# for i in $(seq 1 $CLIENTS)
# do
#     # test for fifo consistency
#     a=$(($i -1))
#     java FifoTester ${MSG_DIR}/messages*.txt < ${a}_distrib.txt
# done


rm *distrib*