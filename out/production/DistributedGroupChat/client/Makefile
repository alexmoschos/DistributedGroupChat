default: client

CC=javac

client: Client.java CommandHandler.java MessageHandler.java InformationController.java Member.java Group.java Message.java FifoMessageHandler.java IsisMessageHandler.java
	$(CC) $^

clean: 
	rm *.class


run: client
	./run.sh


