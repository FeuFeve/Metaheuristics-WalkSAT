package main;

public class Main {

    public static void main(String[] args) throws Exception {
        String file = "uf250-01.cnf";
        String filepath = "files/" + file;
        System.out.println("File: " + file);

        SAT sat = Parser.parse(filepath);
//        System.out.println(sat);

        long startTime = System.nanoTime();

//        SAT randomSatResult = SAT.randomWalkSAT(sat, 1000000, false);
//        SAT optimizedSatResult = SAT.optimisedWalkSAT(sat, 1000000, 0.3, false);

        SAT satResult = SAT.repeatWalkSAT(sat, 10, 1000000, true);

        long endTime = System.nanoTime();
        System.out.println("Execution time = " + getTime(startTime, endTime) + "s");
    }

    public static double getTime(long startTimeNs, long endTimeNs) {
        double deltaTime = (endTimeNs - startTimeNs) / 1000000000.0;
        return Math.round(deltaTime * 1000) / 1000.0;
    }
}
