package main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Parser {

    public static SAT parse(String filePath) throws Exception {
        InputStream is = new FileInputStream(filePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        Scanner scanner = new Scanner(br);
        String token = scanner.nextLine();
        String[] splitRes;
        SAT sat = null;
        int clauseId = 1;

        while (!token.equals("%")) {

            // Split the line
            splitRes = token.trim().replaceAll("\\s+"," ").split(" ");

            // Skip the comments
            if (splitRes[0].equals("c")) {
                token = scanner.nextLine();
                continue;
            }

            // Initialise the GSAT problem with the problem's sizes given in the line starting with "p"
            if (splitRes[0].equals("p")) {
                try {
                    // splitRes[2] = number of atoms
                    // splitRes[3] = number of clauses
                    sat = new SAT(Integer.parseInt(splitRes[2]), Integer.parseInt(splitRes[3]));
                } catch (Exception e){
                    e.printStackTrace();
                }
                token = scanner.nextLine();
                continue;
            }

            for (int i = 0; i < splitRes.length - 1; i++) {
                assert sat != null;
                sat.addToClause(clauseId, Integer.parseInt(splitRes[i]));
            }
            clauseId++;
            try {
                token = scanner.nextLine();
            } catch (NoSuchElementException e) {
                break;
            }
        }
        return sat;
    }
}
