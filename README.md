# Distributed Group Chat

## Description
This repository containt a distributed group chat application. A user can connect join a chat room and send and receive messages from that chat room.

The logistics of the tracker are done by a tracker. The tracker is responsible to store member information for each group. Furthermore, the system supports fault detection using a periodic "heartbeat" message to the tracker. Messages are divided into two categories. The control messages between each peer and the tracker need to be sent reliably, so they need to be TCP packets. The messages between the peers are sent using UDP. The reasoning behind this decision is that the group messages are many so it is better to use a protocol that has a low overhead. The drawback is that, because UDP is not reliable, some group messages may be lost. For the purposes of our system this drawback is acceptable.

Messages inside the group are sent using a B-Multicast. To ensure that clients see a cohesive picture of the messages received we decided to implement 2 ordering protocols.

### FIFO Ordering

In this protocol the ordering must satisfy the FIFO property. The FIFO property states that if clientA sends messages 1 and 2, then every other client must order those messages as 1 and 2 and not the other way. This is implemented in practice, by appending a sequence number, and the ID of the sending client to each message. If client B receives a message from client A with a sequence number higher than the expected the message is stored on a holdback queue. To ensure that the system doesn't end up in a stuck state where it ignores messages from a client(due to a lost message) the expected messages sequence numbers are moved using a timer. The timer is put in place only when we put a message in the holdback queue.

### Total Ordering + FIFO

In this protocol clients negotiate a total ordering for all messages received. This means that every client in the group will see the same ordering for all messages and this ordering must satisfy the FIFO property. This is done by first implementing the ISIS total ordering algorithm and then reordering the messages so that they achieve the FIFO property. The reordering algorithm works in a distributed way in 2 steps to ensure that the ordering has both properties. Because of that, there are 2 levels of timers to avoid stuck states, the first is in the total ordering step and the second is in the FIFO step.


## Installation
To build the project there are a couple of dependencies. A Java compiler and runtime environment and the ant build tool.
The tracker also has a dependency for guava and the installation process expects guava-19.0.jar to be inside $pathtorepo/src/lib/

```shell
wget http://central.maven.org/maven2/com/google/guava/guava/19.0/guava-19.0.jar -O src/lib/guava-19.0.jar
```
To build the project just execute the following command on the shell.
```shell
ant
```

## Usage
To run the tracker execute
```shell
./tracker
```
To run the client execute:
```shell
./client
```
You can also change the parameters of the client by editing the client file(e.g. to specify a specific IP address and not localhost for the tracker)

### Client control actions
The client supports the following actions
```
!r              -> Register a new client
!j <groupname>  -> Client joins <groupname>
!lm <groupname> -> List members of <groupname>
!w <groupname>  -> Client changes the group where his messages go
!e <groupname>  -> Client leaves <groupname>
!q              -> Client leaves the chat application
```

### Example Usage
Here is the example usage for the client(assuming the tracker is online)
```
./client
listening on address: 127.0.1.1
port: 59977
!r
1
[client-94]>!j distrib
[UserInfo{username='client-1', id=0, ip='127.0.1.1', port=56212}, UserInfo{username='client-94', id=1, ip='127.0.1.1', port=59977}]
[client-94]>I love distributed systems!
in distrib client-94 says :: I love distributed systems!
in distrib client-1 says :: Me too!
[client-94]>!q

```
