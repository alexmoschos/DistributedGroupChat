#!/bin/bash

if [ $# -eq 0 ]
then 
    echo "Usage ./automated_tester bin_dir messages_dir protocol number_of_clients"
    exit 1
fi

BIN_DIR=$1
MSG_DIR=$2
PROTOCOL=$3
CLIENTS=$4

echo $CLIENTS

# compilei input controller
javac Controller.java

# initiate input controller
java Controller $CLIENTS &
controller_pid=$!

# start tracker
java -cp "${BIN_DIR}:${BIN_DIR}/lib/guava-19.0.jar" tracker.Tracker > /dev/null &
tracker_pid=$!

# wait for the special files to be created
while [ ! -e ./control_pipe ]
do
:
done

for i in $(seq 1 $CLIENTS)
do
    java -cp "${BIN_DIR}" client.Client debug=true tracker=localhost:3000 protocol=$PROTOCOL < client_$i &
    clients[$i]=$!
    echo started client $i
done


# register clients and join distrib group
for i in $(seq 1 $CLIENTS)
do
    echo "$i !r" > control_pipe
    echo "$i !j distrib" > control_pipe
done

# wait for 2 seconds for the heartbeat to reach every client
sleep 4

# start sending messages
for i in $(seq 1 $CLIENTS)
do
    # do this concurrently
    cat ${MSG_DIR}/messages$i.txt > client_$i &
done


echo "enter to stop"
read nothing

# do cleanup
for i in $(seq 1 $CLIENTS)
do
    kill -9 ${clients[$i]}
done
kill -9 $tracker_pid
kill -9 $controller_pid
rm control_pipe client_*


for i in $(seq 1 $CLIENTS)
do
    tail -n 1 ${i}_distrib.txt >> throughputs.txt
done



# if [ ! "$PROTOCOL" == "fifo" ]; then
#     # first we check if messages have total order
#     java TotalOrderTester *distrib*
# fi

# #then we check if messages have fifo order
# for i in $(seq 1 $CLIENTS)
# do
#     # test for fifo consistency
#     a=$(($i -1))
#     java FifoTester ${MSG_DIR}/messages*.txt < ${a}_distrib.txt
# done


rm *distrib*






