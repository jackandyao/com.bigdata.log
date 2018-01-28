/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 *
 * @author Administrator
 */
public class CalDescartes {

	
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static void ProcessSrcFile(String inFile, String outFile, int num) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    new FileInputStream(inFile), "UTF-8"));

            BufferedWriter outPut = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(outFile), "UTF-8"));

            String line = "";
            int count = 0;
            while ((line = input.readLine()) != null) {
                if (line.trim().equals("")) {
                    continue;
                }

                count++;
                if (count == num) {
                    break;
                }
                String[] vs = line.split("\t");
                if (vs.length == 7) {
                    if (!isNumeric(vs[0]) || !isNumeric(vs[4]) || !isNumeric(vs[5]) || !isNumeric(vs[6])) {
                        continue;
                    }
                    float score = Integer.parseInt(vs[5]) + Integer.parseInt(vs[6]) * 0.5f;
                    outPut.write(vs[0] + "," + vs[4] + "," + score);
                    outPut.newLine();
                }

            }
            input.close();
            outPut.close();
        } catch (Exception e) {
        	e.printStackTrace();
        }

    }

    public static void main(String[] args) {
      
    	int n=1000;
        String file = "D://user_order_base_7_process.txt";   
        String outFile = "D://als//"+n+".txt";
        ProcessSrcFile(file, outFile, n);
//        Integer []numList=new Integer[]{110000,120000,130000,140000,160000,200000};
//        for(Integer num:numList){
//        	   String outFile = "D://als//"+num+".txt";
//               ProcessSrcFile(file, outFile, 100);
//        }
    	
    }

}
