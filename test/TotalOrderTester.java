import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;



public class FifoTester {

    public static HashMap<String, Queue<String>> messages = new HashMap<>();
    

    public static void main(String[] args) throws IOException{

        // for all input files we read the messages
        for (String filename : args) {
            // open file
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
            String line = br.readLine();
            while (line != null) {
                // parse the input message and then put it in messages

                Scanner s = new Scanner(line);
                int seq = Integer.parseInt(s.useDelimiter(Pattern.compile("[a-zA-Z]")).next());
                String username = s.useDelimiter(" ").next();
                String message = s.nextLine();
                
                Queue<String> q = messages.get(username);
                if (q == null)
                    q = new LinkedList<>();
                
                q.add(message);

                if (!messages.containsKey(username))
                    messages.put(username, q);
                
                line = br.readLine();
            }
        }

        // now we read from stdin

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = br.readLine();
        while (line != null) {
            Scanner s = new Scanner(line);
            int seq = Integer.parseInt(s.useDelimiter(Pattern.compile("[a-zA-Z]")).next());
            String username = s.useDelimiter(" ").next();
            String message = s.nextLine();

            // we retreive this user's messages
            Queue<String> q = messages.get(username);
            if (q == null || q.isEmpty()) {
                System.out.println("Fifo test failed");
                return;
            }

            while (!q.isEmpty() && !q.peek().equals(message))
                q.remove();
            
            if (q.isEmpty()) {
                System.out.println("Fifo test failed");
                return;
            }

            q.remove();
            line = br.readLine();
        }
    }
}