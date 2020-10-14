package mx.unam.ciencias.edd;

/**
 * Clase para pilas genéricas.
 */
public class Pila<T> extends MeteSaca<T> {

    /**
     * Regresa una representación en cadena de la pila.
     * @return una representación en cadena de la pila.
     */
    @Override public String toString() {
        Nodo n = this.cabeza;
        String s = "";
        while (n != null) {
            s += n.elemento + "\n";
            n = n.siguiente;
        }
        return s;
    }

    /**
     * Agrega un elemento al tope de la pila.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *         <code>null</code>.
     */
    @Override public void mete(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException("La pila no acepta elementos vacios.");
        Nodo n = new Nodo(elemento);
        if (cabeza == null) {
            cabeza = rabo = n;
        } else {
            n.siguiente = cabeza;
            cabeza = n;
        }
    }
}
