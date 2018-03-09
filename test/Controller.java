import java.lang.Throwable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.Runtime;
import java.lang.Process;

public class Controller {
    public static void main(String[] args) throws Throwable{

            HashMap<String, BufferedWriter> pipes = new HashMap<>();

            int number_of_clients = Integer.parseInt(args[0]);
            for (int i = 1; i<= number_of_clients; i++) {
                // we create a named pipe for each client
                Process p = Runtime.getRuntime().exec("mkfifo client_"+ String.valueOf(i));
                p.waitFor();
            }

            // also create a named pipe for others to communicate with us
            Process p = Runtime.getRuntime().exec("mkfifo control_pipe");
            p.waitFor();

            System.out.println("listening");

            while (true) {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("control_pipe")));
                String command = br.readLine();
                //System.out.println(command);
                while (command != null) {
                    Scanner s = new Scanner(command);
                    s.useDelimiter(" ");
                    String client = s.next();
                    String msg = s.nextLine().substring(1) + "\n";

                    BufferedWriter bw = pipes.get(client);
                    if (bw == null)
                        bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("client_" + client)));

                    if (!pipes.containsKey(client))
                        pipes.put(client, bw);
                    
                    bw.write(msg);
                    bw.flush();

                    command = br.readLine();
                }

                br.close();
            }
    }
}