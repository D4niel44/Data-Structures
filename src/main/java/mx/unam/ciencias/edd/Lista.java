package mx.unam.ciencias.edd;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>
 * Clase genérica para listas doblemente ligadas.
 * </p>
 *
 * <p>
 * Las listas nos permiten agregar elementos al inicio o final de la lista,
 * eliminar elementos de la lista, comprobar si un elemento está o no en la
 * lista, y otras operaciones básicas.
 * </p>
 *
 * <p>
 * Las listas no aceptan a <code>null</code> como elemento.
 * </p>
 *
 * @param <T> El tipo de los elementos de la lista.
 */
public class Lista<T> implements Coleccion<T> {

    /* Clase interna privada para nodos. */
    private class Nodo {
        /* El elemento del nodo. */
        public T elemento;
        /* El nodo anterior. */
        public Nodo anterior;
        /* El nodo siguiente. */
        public Nodo siguiente;

        /* Construye un nodo con un elemento. */
        public Nodo(T elemento) {
            this.elemento = elemento;
        }
    }

    /* Clase interna privada para iteradores. */
    private class Iterador implements IteradorLista<T> {
        /* El nodo anterior. */
        public Nodo anterior;
        /* El nodo siguiente. */
        public Nodo siguiente;

        /* Construye un nuevo iterador. */
        public Iterador() {
            siguiente = cabeza;
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override
        public boolean hasNext() {
            return siguiente != null;
        }

        /* Nos da el elemento siguiente. */
        @Override
        public T next() {
            if (siguiente == null) {
                throw new NoSuchElementException("La lista no tiene un siguiente elemento");
            }
            anterior = siguiente;
            siguiente = siguiente.siguiente;
            return anterior.elemento;
        }

        /* Nos dice si hay un elemento anterior. */
        @Override
        public boolean hasPrevious() {
            return anterior != null;
        }

        /* Nos da el elemento anterior. */
        @Override
        public T previous() {
            if (anterior == null) {
                throw new NoSuchElementException("La lista no tiene un siguiente elemento");
            }
            siguiente = anterior;
            anterior = anterior.anterior;
            return siguiente.elemento;
        }

        /* Mueve el iterador al inicio de la lista. */
        @Override
        public void start() {
            anterior = null;
            siguiente = cabeza;
        }

        /* Mueve el iterador al final de la lista. */
        @Override
        public void end() {
            siguiente = null;
            anterior = rabo;
        }
    }

    /* Primer elemento de la lista. */
    private Nodo cabeza;
    /* Último elemento de la lista. */
    private Nodo rabo;
    /* Número de elementos en la lista. */
    private int longitud;

    /**
     * Regresa la longitud de la lista. El método es idéntico a
     * {@link #getElementos}.
     * 
     * @return la longitud de la lista, el número de elementos que contiene.
     */
    public int getLongitud() {
        return longitud;
    }

    /**
     * Regresa el número elementos en la lista. El método es idéntico a
     * {@link #getLongitud}.
     * 
     * @return el número elementos en la lista.
     */
    @Override
    public int getElementos() {
        return longitud;
    }

    /**
     * Nos dice si la lista es vacía.
     * 
     * @return <code>true</code> si la lista es vacía, <code>false</code> en otro
     *         caso.
     */
    @Override
    public boolean esVacia() {
        return rabo == null;
    }

    /**
     * Agrega un elemento a la lista. Si la lista no tiene elementos, el elemento a
     * agregar será el primero y último. El método es idéntico a
     * {@link #agregaFinal}.
     * 
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *                                  <code>null</code>.
     */
    @Override
    public void agrega(T elemento) {
        agregaFinal(elemento);
    }

    /**
     * Agrega un elemento al final de la lista. Si la lista no tiene elementos, el
     * elemento a agregar será el primero y último.
     * 
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *                                  <code>null</code>.
     */
    public void agregaFinal(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("La lista no admite elementos vacios");
        }
        Nodo n = new Nodo(elemento);
        longitud++;
        if (rabo == null) {
            cabeza = n;
            rabo = n;
        } else {
            rabo.siguiente = n;
            n.anterior = rabo;
            rabo = n;
        }
    }

    /**
     * Agrega un elemento al inicio de la lista. Si la lista no tiene elementos, el
     * elemento a agregar será el primero y último.
     * 
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *                                  <code>null</code>.
     */
    public void agregaInicio(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("la lista no admite elementos vacios");
        }
        Nodo n = new Nodo(elemento);
        longitud++;
        if (cabeza == null) {
            cabeza = n;
            rabo = n;
        } else {
            cabeza.anterior = n;
            n.siguiente = cabeza;
            cabeza = n;
        }
    }

    /**
     * Inserta un elemento en un índice explícito.
     *
     * Si el índice es menor o igual que cero, el elemento se agrega al inicio de la
     * lista. Si el índice es mayor o igual que el número de elementos en la lista,
     * el elemento se agrega al fina de la misma. En otro caso, después de mandar
     * llamar el método, el elemento tendrá el índice que se especifica en la lista.
     * 
     * @param i        el índice dónde insertar el elemento. Si es menor que 0 el
     *                 elemento se agrega al inicio de la lista, y si es mayor o
     *                 igual que el número de elementos en la lista se agrega al
     *                 final.
     * @param elemento el elemento a insertar.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *                                  <code>null</code>.
     */
    public void inserta(int i, T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("La lista no admite elementos vacios");
        }
        if (i < 1) {
            agregaInicio(elemento);
        } else if (i >= longitud) {
            agregaFinal(elemento);
        } else {
            Nodo n = new Nodo(elemento);
            longitud++;
            n.siguiente = getAuxiliar(i);
            n.anterior = n.siguiente.anterior;
            n.siguiente.anterior.siguiente = n;
            n.siguiente.anterior = n;
        }
    }

    /**
     * metodo auxiliar para inserta y get
     */
    private Nodo getAuxiliar(int p) {
        Nodo n = cabeza;
        for (int i = 0; i < p; i++) {
            n = n.siguiente;
        }
        return n;
    }

    /**
     * Metodo Auxiliar para eliminar un elemento
     */
    private void eliminaAuxiliar(Nodo n) {
        longitud--;
        if (cabeza == rabo) {
            limpia();
        } else if (n == cabeza) {
            cabeza = cabeza.siguiente;
            cabeza.anterior = null;
        } else if (n == rabo) {
            rabo = rabo.anterior;
            rabo.siguiente = null;
        } else {
            n.anterior.siguiente = n.siguiente;
            n.siguiente.anterior = n.anterior;
        }
    }

    /**
     * Elimina un elemento de la lista. Si el elemento no está contenido en la
     * lista, el método no la modifica.
     * 
     * @param elemento el elemento a eliminar.
     */
    @Override
    public void elimina(T elemento) {
        Nodo nodo = buscar(elemento);
        if (nodo != null) {
            eliminaAuxiliar(nodo);
        }
    }

    /**
     * Elimina el primer elemento de la lista y lo regresa.
     * 
     * @return el primer elemento de la lista antes de eliminarlo.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T eliminaPrimero() {
        if (esVacia()) {
            throw new NoSuchElementException("La lista es vacia");
        }
        T elemento = cabeza.elemento;
        eliminaAuxiliar(cabeza);
        return elemento;
    }

    /**
     * Elimina el último elemento de la lista y lo regresa.
     * 
     * @return el último elemento de la lista antes de eliminarlo.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T eliminaUltimo() {
        if (esVacia()) {
            throw new NoSuchElementException("La lista es vacia");
        }
        T elemento = rabo.elemento;
        eliminaAuxiliar(rabo);
        return elemento;
    }

    /**
     * Metodo Auxiliar que busca un nodo
     */
    private Nodo buscar(T elemento) {
        Nodo nodo = cabeza;
        while (nodo != null) {
            if (nodo.elemento.equals(elemento)) {
                return nodo;
            }
            nodo = nodo.siguiente;
        }
        return null;
    }

    /**
     * Nos dice si un elemento está en la lista.
     * 
     * @param elemento el elemento que queremos saber si está en la lista.
     * @return <code>true</code> si <code>elemento</code> está en la lista,
     *         <code>false</code> en otro caso.
     */
    @Override
    public boolean contiene(T elemento) {
        return buscar(elemento) != null;
    }

    /**
     * Regresa la reversa de la lista.
     * 
     * @return una nueva lista que es la reversa la que manda llamar el método.
     */
    public Lista<T> reversa() {
        Lista<T> reversa = new Lista<>();
        Nodo nodo = cabeza;
        while (nodo != null) {
            reversa.agregaInicio(nodo.elemento);
            ;
            nodo = nodo.siguiente;
        }
        return reversa;
    }

    /**
     * Regresa una copia de la lista. La copia tiene los mismos elementos que la
     * lista que manda llamar el método, en el mismo orden.
     * 
     * @return una copiad de la lista.
     */
    public Lista<T> copia() {
        Lista<T> copia = new Lista<>();
        Nodo nodo = cabeza;
        while (nodo != null) {
            copia.agregaFinal(nodo.elemento);
            nodo = nodo.siguiente;
        }
        return copia;
    }

    /**
     * Limpia la lista de elementos, dejándola vacía.
     */
    @Override
    public void limpia() {
        cabeza = null;
        rabo = null;
        longitud = 0;
    }

    /**
     * Regresa el primer elemento de la lista.
     * 
     * @return el primer elemento de la lista.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T getPrimero() {
        if (cabeza == null) {
            throw new NoSuchElementException("la lista es vacia");
        }
        return cabeza.elemento;
    }

    /**
     * Regresa el último elemento de la lista.
     * 
     * @return el primer elemento de la lista.
     * @throws NoSuchElementException si la lista es vacía.
     */
    public T getUltimo() {
        if (rabo == null) {
            throw new NoSuchElementException("La lista es vacia");
        }
        return rabo.elemento;
    }

    /**
     * Regresa el <em>i</em>-ésimo elemento de la lista.
     * 
     * @param i el índice del elemento que queremos.
     * @return el <em>i</em>-ésimo elemento de la lista.
     * @throws ExcepcionIndiceInvalido si <em>i</em> es menor que cero o mayor o
     *                                 igual que el número de elementos en la lista.
     */
    public T get(int i) {
        if (i < 0 || i >= longitud) {
            throw new ExcepcionIndiceInvalido("El indice esta fuera de los limites");
        }
        if (i == 0) {
            return getPrimero();
        }
        if (i == longitud - 1) {
            return getUltimo();
        }
        return getAuxiliar(i).elemento;
    }

    /**
     * Regresa el índice del elemento recibido en la lista.
     * 
     * @param elemento el elemento del que se busca el índice.
     * @return el índice del elemento recibido en la lista, o -1 si el elemento no
     *         está contenido en la lista.
     */
    public int indiceDe(T elemento) {
        int i = 0;
        Nodo n = cabeza;
        while (n != null) {
            if (n.elemento.equals(elemento)) {
                return i;
            }
            n = n.siguiente;
            i++;
        }
        return -1;
    }

    /**
     * Regresa una representación en cadena de la lista.
     * 
     * @return una representación en cadena de la lista.
     */
    @Override
    public String toString() {
        String cadena = "[";
        Nodo nodo = cabeza;
        while (nodo != null) {
            cadena += nodo.elemento;
            if (nodo != rabo) {
                cadena += ", ";
            }
            nodo = nodo.siguiente;
        }
        return cadena + "]";
    }

    /**
     * Nos dice si la lista es igual al objeto recibido.
     * 
     * @param objeto el objeto con el que hay que comparar.
     * @return <code>true</code> si la lista es igual al objeto recibido;
     *         <code>false</code> en otro caso.
     */
    @Override
    public boolean equals(Object objeto) {
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        @SuppressWarnings("unchecked")
        Lista<T> lista = (Lista<T>) objeto;
        if (this == objeto) {
            return true;
        }
        if (this.longitud != lista.longitud) {
            return false;
        }
        Nodo nodo = this.cabeza;
        Nodo nodo2 = lista.cabeza;
        while (nodo != null) {
            if (!nodo.elemento.equals(nodo2.elemento)) {
                return false;
            }
            nodo2 = nodo2.siguiente;
            nodo = nodo.siguiente;
        }
        return true;
    }

    /**
     * Regresa un iterador para recorrer la lista en una dirección.
     * 
     * @return un iterador para recorrer la lista en una dirección.
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Regresa un iterador para recorrer la lista en ambas direcciones.
     * 
     * @return un iterador para recorrer la lista en ambas direcciones.
     */
    public IteradorLista<T> iteradorLista() {
        return new Iterador();
    }

    /**
     * Regresa una copia de la lista, pero ordenada. Para poder hacer el
     * ordenamiento, el método necesita una instancia de {@link Comparator} para
     * poder comparar los elementos de la lista.
     * 
     * @param comparador el comparador que la lista usará para hacer el
     *                   ordenamiento.
     * @return una copia de la lista, pero ordenada.
     */
    public Lista<T> mergeSort(Comparator<T> comparador) {
        if (this.cabeza == null || this.cabeza == this.rabo)
            return this.copia();
        Lista<T> l1 = new Lista<>();
        Lista<T> l2 = new Lista<>();
        Nodo n = this.cabeza;
        int i = 0;
        while (n != null) {
            if (i++ < longitud / 2) {
                l1.agregaFinal(n.elemento);
            } else {
                l2.agregaFinal(n.elemento);
            }
            n = n.siguiente;
        }
        l1 = l1.mergeSort(comparador);
        l2 = l2.mergeSort(comparador);
        return mezcla(l1, l2, comparador);
    }

    /**
     * Metodo auxiliar que recibe dos listas ordenadas y las junta manteniendo el
     * orden
     */
    private Lista<T> mezcla(Lista<T> lista1, Lista<T> lista2, Comparator<T> comparador) {
        Lista<T> listaMezclada = new Lista<>();
        Nodo nodo1 = lista1.cabeza;
        Nodo nodo2 = lista2.cabeza;
        while (nodo1 != null && nodo2 != null) {
            if (comparador.compare(nodo1.elemento, nodo2.elemento) <= 0) {
                listaMezclada.agregaFinal(nodo1.elemento);
                nodo1 = nodo1.siguiente;
            } else {
                listaMezclada.agregaFinal(nodo2.elemento);
                nodo2 = nodo2.siguiente;
            }
        }
        Nodo auxiliar = (nodo1 != null) ? nodo1 : nodo2;
        while (auxiliar != null) {
            listaMezclada.agregaFinal(auxiliar.elemento);
            auxiliar = auxiliar.siguiente;
        }
        return listaMezclada;
    }

    /**
     * Regresa una copia de la lista recibida, pero ordenada. La lista recibida
     * tiene que contener nada más elementos que implementan la interfaz
     * {@link Comparable}.
     * 
     * @param <T>   tipo del que puede ser la lista.
     * @param lista la lista que se ordenará.
     * @return una copia de la lista recibida, pero ordenada.
     */
    public static <T extends Comparable<T>> Lista<T> mergeSort(Lista<T> lista) {
        return lista.mergeSort((a, b) -> a.compareTo(b));
    }

    /**
     * Busca un elemento en la lista ordenada, usando el comparador recibido. El
     * método supone que la lista está ordenada usando el mismo comparador.
     * 
     * @param elemento   el elemento a buscar.
     * @param comparador el comparador con el que la lista está ordenada.
     * @return <code>true</code> si el elemento está contenido en la lista,
     *         <code>false</code> en otro caso.
     */
    public boolean busquedaLineal(T elemento, Comparator<T> comparador) {
        Nodo n = cabeza;
        while (n != null) {
            if (comparador.compare(n.elemento, elemento) == 0)
                return true;
            n = n.siguiente;
        }
        return false;
    }

    /**
     * Busca un elemento en una lista ordenada. La lista recibida tiene que contener
     * nada más elementos que implementan la interfaz {@link Comparable}, y se da
     * por hecho que está ordenada.
     * 
     * @param <T>      tipo del que puede ser la lista.
     * @param lista    la lista donde se buscará.
     * @param elemento el elemento a buscar.
     * @return <code>true</code> si el elemento está contenido en la lista,
     *         <code>false</code> en otro caso.
     */
    public static <T extends Comparable<T>> boolean busquedaLineal(Lista<T> lista, T elemento) {
        return lista.busquedaLineal(elemento, (a, b) -> a.compareTo(b));
    }
}
