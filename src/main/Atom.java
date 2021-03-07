package main;

import java.util.Objects;
import java.util.Random;

public class Atom {

    int id;
    boolean isPositive;


    public Atom(int id) {
        if (id < 0) {
            this.id = -id;
            isPositive = false;
        }
        else {
            this.id = id;
            isPositive = true;
        }
    }

    public Atom(Atom atom) {
        id = atom.id;
        isPositive = atom.isPositive;
    }

    public Atom randomize() {
        Random random = new Random();
        isPositive = random.nextBoolean();
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Atom atom = (Atom) o;
        return id == atom.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isPositive);
    }

    @Override
    public String toString() {
        if (isPositive) return "" + id;
        else return "!" + id;
    }
}
