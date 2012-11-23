package concurrentdemo;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class DoProcessBuilder {
    public static void main(String args[]) throws Exception {
        String[] cmd = new String[] { "rsync", "-avz", "--progress",
                "/app/etm-file/test/etm-file/goodsdetail",
                "bingoo@10.142.195.61:/home/bingoo"
                // --password-file
        };

        Process process = new ProcessBuilder(cmd).start();
        String line;
        PrintStream fis = new PrintStream(new FileOutputStream("resultOk.txt"));

        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while ((line = br.readLine()) != null)
            fis.println(line);

        fis.close();

        PrintStream fes = new PrintStream(new FileOutputStream("resultErr.txt"));
        br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        while ((line = br.readLine()) != null)
            fes.println(line);

        fes.close();

        int val = process.waitFor();
        process.destroy();

        if (val != 0) throw new Exception("Exception during RSync; return code = " + val);
    }
}
