package org.n3r.core.lang;

public class Tuple1<T1> {
    public T1 _1 = null;

    public Tuple1(T1 o1) {
        _1 = o1;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (_1 == null ? 0 : _1.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tuple1) {
            Tuple1<?> other = (Tuple1<?>) obj;
            return _1.equals(other._1);
        }

        return false;
    }

    @Override
    public String toString() {
        return "Tuple1 [" + _1 + "]";
    }

}
