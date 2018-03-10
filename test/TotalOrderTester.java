import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;



public class TotalOrderTester {

    public static Queue<Queue<String>> messages = new LinkedList<>();
    

    public static void main(String[] args) throws IOException{

        // for all input files we read the messages
        for (String filename : args) {
            Queue<String> q = new LinkedList<>();
            // open file
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
            String line = br.readLine();
            while (line != null) {
                // parse the input message and then put it in messages
                q.add(line);
                line = br.readLine();
            }
            messages.add(q);
        }

        Queue<String>[] msgs = messages.toArray(new Queue[messages.size()]);
        for (int i = 0; i < msgs.length; i++) {
            for (int j = i + 1; j < msgs.length; j++) {
               // first we need to create duplicates of the original data
                Queue<String> q1 = new LinkedList(msgs[i]);
                Queue<String> q2 = new LinkedList(msgs[j]);

                // now we remove any unique messages
                for (String s : q1.toArray(new String[q1.size()])) {
                    if (!q2.contains(s))
                        q1.remove(s);
                }

               for (String s : q2.toArray(new String[q2.size()])) {
                    if (!q1.contains(s))
                        q2.remove(s);
                }


                // then the two queues have to be equal

                if (!q1.equals(q2)) {
                    System.out.println("Total order test failed");
                    return;
                }
            }
        }
    }
}