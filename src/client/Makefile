default: client

CC=javac

client: Client.java CommandHandler.java MessageHandler.java InformationController.java Member.java Group.java
	$(CC) $^

clean: 
	rm *.class