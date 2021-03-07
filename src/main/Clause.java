package main;

import java.util.ArrayList;
import java.util.List;

public class Clause {

    int id;
    List<Atom> atoms = new ArrayList<>();


    public Clause(int id) {
        this.id = id;
    }

    public Clause(Clause clause) {
        id = clause.id;
        for (Atom atom : clause.atoms)
            atoms.add(new Atom(atom));
    }

    public void addAtom(int atomId) {
        atoms.add(new Atom(atomId));
    }

    public boolean violation(List<Atom> assignment) {
        for (Atom clauseAtom : atoms) {
            for (Atom assignmentAtom : assignment) {
                if (clauseAtom.id == assignmentAtom.id) {
                    if (clauseAtom.isPositive == assignmentAtom.isPositive)
                        return false;
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        String toReturn = "Clause " + id + ": ";
        for (Atom atom : atoms)
            toReturn += atom + " v ";
        return toReturn.substring(0, toReturn.length() - 3);
    }
}
