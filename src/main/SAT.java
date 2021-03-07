package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SAT {

    List<Atom> atoms = new ArrayList<>();
    List<Clause> clauses = new ArrayList<>();


    public SAT(int atomsNb, int clausesNb) {
        for (int i = 0; i < atomsNb; i++)
            atoms.add(new Atom(i+1).randomize());
    }

    private SAT(SAT sat) {
        for (Atom atom : sat.atoms)
            atoms.add(new Atom(atom));
        for (Clause clause : sat.clauses)
            clauses.add(new Clause(clause));
    }

    public void addToClause(int clauseId, int atomId) {
        for (Clause clause : clauses) {
            if (clause.id == clauseId) {
                clause.addAtom(atomId);
                return;
            }
        }

        // Else (if the clause does not exist), create the clause
        Clause newClause = new Clause(clauseId);
        newClause.addAtom(atomId);
        clauses.add(newClause);
    }

    public static SAT repeatWalkSAT(SAT sat, int loops, long maxIterationsPerLoop, boolean useOptimisedWalkSAT) {
        if (useOptimisedWalkSAT)
            System.out.println("\n##########     WalkSAT (Optimised)      ##########");
        else
            System.out.println("\n##########       WalkSAT (Random)       ##########");
        System.out.println("- Clauses: " + sat.clauses.size());
        System.out.println("- Total atoms: " + sat.atoms.size());
        System.out.println("- Loops: " + loops);
        System.out.println("- Maximum iterations per loop: " + maxIterationsPerLoop + "\n");

        SAT bestResult = null;
        int bestUnsatisfiedClauses = Integer.MAX_VALUE;
        for (int i = 0; i < loops; i++) {
            System.out.print("Loop " + (i + 1) + ": ");

            SAT result;
            if (useOptimisedWalkSAT)
                result = optimisedWalkSAT(sat, maxIterationsPerLoop, 0.3, true);
            else
                result = randomWalkSAT(sat, maxIterationsPerLoop, true);

            int unsatisfiedClauses = 0;
            for (Clause clause : result.clauses) {
                if (clause.violation(result.atoms))
                    unsatisfiedClauses++;
            }

            System.out.print("best solution is " + unsatisfiedClauses + " unsatisfied clauses");
            if (unsatisfiedClauses < bestUnsatisfiedClauses) {
                bestUnsatisfiedClauses = unsatisfiedClauses;
                bestResult = result;

                System.out.println(" (current best)");
                if (bestUnsatisfiedClauses == 0)
                    break;
            }
            else {
                System.out.println(" (ignored: " + unsatisfiedClauses + " >= " + bestUnsatisfiedClauses + ")");
            }
        }

        assert bestResult != null;
        if (bestUnsatisfiedClauses == 0) {
            System.out.println("\nFound a solution:");
        }
        else {
            System.out.println("\nProbably (?) unsatisfiable.");
            System.out.println("Best solution found (" + bestUnsatisfiedClauses + " unsatisfied clauses):");
        }
        System.out.println(bestResult.atoms);
        System.out.println("##################################################\n");

        return bestResult;
    }

    public static SAT randomWalkSAT(SAT sat, long maxIterations, boolean silence) {
        if (!silence) {
            System.out.println("\n##########       WalkSAT (Random)       ##########");
            System.out.println("- Clauses: " + sat.clauses.size());
            System.out.println("- Total atoms: " + sat.atoms.size());
            System.out.println("- Maximum iterations: " + maxIterations + "\n");
        }

        // 1. Make a copy of the current SAT
        SAT satCopy = new SAT(sat);

        // 2. Put the unsatisfied clauses in a list
        List<Clause> unsatisfiedClauses = new ArrayList<>();
        for (Clause clause : satCopy.clauses)
            if (clause.violation(satCopy.atoms))
                unsatisfiedClauses.add(clause);

        // 3. If the satCopy assignment (atoms) is already satisfying all the clauses, return the satCopy
        if (unsatisfiedClauses.isEmpty()) {
            if (!silence)
                System.out.println("Iterations: 0");
            return satCopy;
        }

        // 4. Else, begin the algorithm
        long iterations = 0;
        int minUnsatisfied = unsatisfiedClauses.size();

        if (!silence)
            System.out.println("Minimum unsatisfied clauses at iteration " + iterations + " = " + minUnsatisfied);

        Random random = new Random();
        while (!unsatisfiedClauses.isEmpty() && iterations < maxIterations) {
            iterations++;

            // 4.1. Choose a random unsatisfied clause
            int clauseIndex = random.nextInt(unsatisfiedClauses.size());
            Clause chosenClause = unsatisfiedClauses.get(clauseIndex);

            // 4.2. Choose a random atom in that clause (as their are all unsatisfied)
            int atomIndex = random.nextInt(chosenClause.atoms.size());
            Atom chosenAtom = chosenClause.atoms.get(atomIndex);

            // 4.3. Switch this atom (True->False or False->True) in the satCopy, so that it satisfies this clause
            for (Atom atom : satCopy.atoms) {
                if (atom.id == chosenAtom.id)
                    atom.isPositive = !atom.isPositive;
            }

            // 4.4. Actualize the "unsatisfiedClauses" list
            for (Clause clause : satCopy.clauses) {
                if (clause.atoms.contains(chosenAtom)) {
                    if (unsatisfiedClauses.contains(clause)) {
                        unsatisfiedClauses.remove(clause);
                    }
                    else {
                        if (clause.violation(satCopy.atoms))
                            unsatisfiedClauses.add(clause);
                    }
                }
            }

            if (unsatisfiedClauses.size() < minUnsatisfied) {
                minUnsatisfied = unsatisfiedClauses.size();
                if (!silence)
                    System.out.println("Minimum unsatisfied clauses at iteration " + iterations + " = " + minUnsatisfied);
            }
        }

        // 5. If the "unsatisfiedClauses" list is empty, then the current assignment satisfies the SAT problem
        if (!silence) {
            System.out.println("\n" + iterations + "/" + maxIterations + " iterations.");
            if (unsatisfiedClauses.isEmpty()) {
                System.out.println("Found a solution:");
            }
            else {
                System.out.println("Probably unsatisfiable.");
                System.out.println("Best solution found:");
            }
            System.out.println(satCopy.atoms);

            System.out.println("##################################################\n");
        }
        return satCopy;
    }

    public static SAT optimisedWalkSAT(SAT sat, long maxIterations, double randomProbability, boolean silence) {
        if (!silence) {
            System.out.println("\n##########     WalkSAT (Optimised)      ##########");
            System.out.println("- Clauses: " + sat.clauses.size());
            System.out.println("- Total atoms: " + sat.atoms.size());
            System.out.println("- Maximum iterations: " + maxIterations + "\n");
        }

        // 1. Make a copy of the current SAT
        SAT satCopy = new SAT(sat);

        // 2. Put the unsatisfied clauses in a list
        List<Clause> unsatisfiedClauses = new ArrayList<>();
        for (Clause clause : satCopy.clauses)
            if (clause.violation(satCopy.atoms))
                unsatisfiedClauses.add(clause);

        // 3. If the satCopy assignment (atoms) is already satisfying all the clauses, return the satCopy
        if (unsatisfiedClauses.isEmpty()) {
            if (!silence)
                System.out.println("Iterations: 0");
            return satCopy;
        }

        // 4. Else, begin the algorithm
        long iterations = 0;
        int minUnsatisfied = unsatisfiedClauses.size();

        if (!silence)
            System.out.println("Minimum unsatisfied clauses at iteration " + iterations + " = " + minUnsatisfied);

        Random random = new Random();
        while (!unsatisfiedClauses.isEmpty() && iterations < maxIterations) {
            iterations++;

            // 4.1. Choose a random unsatisfied clause
            int clauseIndex = random.nextInt(unsatisfiedClauses.size());
            Clause chosenClause = unsatisfiedClauses.get(clauseIndex);

            // 4.2. Choose the atom that, once flipped, would make the assignment satisfy the most clauses
            List<Integer> flippedAtomsId = new ArrayList<>();
            List<Integer> flippedAtomsBreakScore = new ArrayList<>();

            for (int i = 0; i < chosenClause.atoms.size(); i++) {
                Atom atom = satCopy.atoms.get(chosenClause.atoms.get(i).id - 1);
                atom.isPositive = !atom.isPositive; // Flip it

                List<Clause> unsatisfiedClausesCopy = new ArrayList<>(unsatisfiedClauses);
                for (Clause clause : satCopy.clauses) {
                    if (clause.atoms.contains(atom)) {
                        if (unsatisfiedClausesCopy.contains(clause)) {
                            unsatisfiedClausesCopy.remove(clause);
                        }
                        else {
                            if (clause.violation(satCopy.atoms))
                                unsatisfiedClausesCopy.add(clause);
                        }
                    }
                }

                int breakScore = unsatisfiedClausesCopy.size();
                flippedAtomsId.add(atom.id);
                flippedAtomsBreakScore.add(breakScore);

                atom.isPositive = !atom.isPositive; // Don't forget to flip it back
            }

            // Calculate the minimum break score
            int minBreakScore = Integer.MAX_VALUE;
            for (Integer integer : flippedAtomsBreakScore) {
                if (integer < minBreakScore)
                    minBreakScore = integer;
            }

            // Select the atoms that have the minimum break score
            List<Atom> bestAtomsToFlip = new ArrayList<>();
            for (int i = 0; i < flippedAtomsBreakScore.size(); i++) {
                if (flippedAtomsBreakScore.get(i) == minBreakScore)
                    bestAtomsToFlip.add(satCopy.atoms.get(flippedAtomsId.get(i) - 1));
            }

            // If the minimum break score is 0, randomly choose between the atoms that have a break score of 0
            // Else, either pick a randomly chosen atom that have the minimum break score, or a randomly chosen atom
            // (no matter what its break score is) (depending on "randomProbability")
            Atom bestAtomToFlip;
            if (minBreakScore == 0) {
                bestAtomToFlip = bestAtomsToFlip.get(random.nextInt(bestAtomsToFlip.size()));
            }
            else {
                double probability = random.nextDouble() % 1;
                if (probability > randomProbability) {
                    bestAtomToFlip = bestAtomsToFlip.get(random.nextInt(bestAtomsToFlip.size()));
                }
                else {
                    bestAtomToFlip = chosenClause.atoms.get(random.nextInt(chosenClause.atoms.size()));
                }
            }

            // 4.3. Switch this atom (True->False or False->True) in the satCopy, so that it satisfies this clause
            for (Atom atom : satCopy.atoms) {
                if (atom.id == bestAtomToFlip.id)
                    atom.isPositive = !atom.isPositive;
            }

            // 4.4. Actualize the "unsatisfiedClauses" list
            for (Clause clause : satCopy.clauses) {
                if (clause.atoms.contains(bestAtomToFlip)) {
                    if (unsatisfiedClauses.contains(clause)) {
                        unsatisfiedClauses.remove(clause);
                    }
                    else {
                        if (clause.violation(satCopy.atoms))
                            unsatisfiedClauses.add(clause);
                    }
                }
            }

            if (unsatisfiedClauses.size() < minUnsatisfied) {
                minUnsatisfied = unsatisfiedClauses.size();
                if (!silence)
                    System.out.println("Minimum unsatisfied clauses at iteration " + iterations + " = " + minUnsatisfied);
            }
        }

        // 5. If the "unsatisfiedClauses" list is empty, then the current assignment satisfies the SAT problem
        if (!silence) {
            System.out.println("\n" + iterations + "/" + maxIterations + " iterations.");
            if (unsatisfiedClauses.isEmpty()) {
                System.out.println("Found a solution:");
            }
            else {
                System.out.println("Probably unsatisfiable.");
                System.out.println("Best solution found:");
            }
            System.out.println(satCopy.atoms);

            System.out.println("##################################################\n");
        }
        return satCopy;
    }

    @Override
    public String toString() {
        String toReturn = "SAT problem:\n"
                + "# Variables: " + atoms.size() + "\n"
                + "# Clauses: " + clauses.size();
        for (Clause clause : clauses)
            toReturn += "\n" + clause;
        return toReturn;
    }
}
