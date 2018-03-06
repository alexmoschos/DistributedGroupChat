
public class CommandHandler {

    public CommandHandler() {}

    public void execute(String command) {
        switch (command) {
            case "r":  // register user to tracker.
                Client.setClientId(registerClient());
                break;
            
            default: 
                System.out.println("Unkown command. Type !h for the help menu");
        }
    }

    public long registerClient() {
        return  1L;
    }

}

