package test;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Save {
    public static void writeTxt(String str) {
        String fileName="D:\\movie.txt";
        try
        {
            FileWriter writer=new FileWriter(fileName,true);
            SimpleDateFormat format=new SimpleDateFormat();

            writer.write(str+"\n");
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

