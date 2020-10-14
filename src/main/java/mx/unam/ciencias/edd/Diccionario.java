package mx.unam.ciencias.edd;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para diccionarios (<em>hash tables</em>). Un diccionario generaliza el
 * concepto de arreglo, mapeando un conjunto de <em>llaves</em> a una colección
 * de <em>valores</em>.
 */
public class Diccionario<K, V> implements Iterable<V> {

    /* Clase interna privada para entradas. */
    private class Entrada {

        /* La llave. */
        public K llave;
        /* El valor. */
        public V valor;

        /* Construye una nueva entrada. */
        public Entrada(K llave, V valor) {
            this.llave = llave;
            this.valor = valor;
        }
    }

    /* Clase interna privada para iteradores. */
    private class Iterador {

        /* En qué lista estamos. */
        private int indice;
        /* Iterador auxiliar. */
        private Iterator<Entrada> iterador;

        /*
         * Construye un nuevo iterador, auxiliándose de las listas del diccionario.
         */
        public Iterador() {
            indice = 0;
            mueveIterador();
        }

        /* Nos dice si hay una siguiente entrada. */
        public boolean hasNext() {
            return iterador != null;
        }

        /* Regresa la siguiente entrada. */
        public Entrada siguiente() {
            if (iterador == null)
                throw new NoSuchElementException("No quedan elementos por iterar.");
            Entrada siguiente = iterador.next();
            if (!iterador.hasNext()) {
                ++indice;
                mueveIterador();
            }
            return siguiente;
        }

        /* Mueve el iterador a la siguiente entrada válida. */
        private void mueveIterador() {
            while (indice < entradas.length) {
                if (entradas[indice] != null) {
                    iterador = entradas[indice].iterator();
                    return;
                }
                indice++;
            }
            iterador = null;
        }
    }

    /* Clase interna privada para iteradores de llaves. */
    private class IteradorLlaves extends Iterador implements Iterator<K> {

        /* Regresa el siguiente elemento. */
        @Override
        public K next() {
            return siguiente().llave;
        }
    }

    /* Clase interna privada para iteradores de valores. */
    private class IteradorValores extends Iterador implements Iterator<V> {

        /* Regresa el siguiente elemento. */
        @Override
        public V next() {
            return siguiente().valor;
        }
    }

    /** Máxima carga permitida por el diccionario. */
    public static final double MAXIMA_CARGA = 0.72;

    /* Capacidad mínima; decidida arbitrariamente a 2^6. */
    private static final int MINIMA_CAPACIDAD = 64;

    /* Dispersor. */
    private Dispersor<K> dispersor;
    /* Nuestro diccionario. */
    private Lista<Entrada>[] entradas;
    /* Número de valores. */
    private int elementos;

    /*
     * Truco para crear un arreglo genérico. Es necesario hacerlo así por cómo Java
     * implementa sus genéricos; de otra forma obtenemos advertencias del
     * compilador.
     */
    @SuppressWarnings("unchecked")
    private Lista<Entrada>[] nuevoArreglo(int n) {
        return (Lista<Entrada>[]) Array.newInstance(Lista.class, n);
    }

    /**
     * Construye un diccionario con una capacidad inicial y dispersor
     * predeterminados.
     */
    public Diccionario() {
        this(MINIMA_CAPACIDAD, (K llave) -> llave.hashCode());
    }

    /**
     * Construye un diccionario con una capacidad inicial definida por el usuario, y
     * un dispersor predeterminado.
     * 
     * @param capacidad la capacidad a utilizar.
     */
    public Diccionario(int capacidad) {
        this(capacidad, (K llave) -> llave.hashCode());
    }

    /**
     * Construye un diccionario con una capacidad inicial predeterminada, y un
     * dispersor definido por el usuario.
     * 
     * @param dispersor el dispersor a utilizar.
     */
    public Diccionario(Dispersor<K> dispersor) {
        this(MINIMA_CAPACIDAD, dispersor);
    }

    /**
     * Construye un diccionario con una capacidad inicial y un método de dispersor
     * definidos por el usuario.
     * 
     * @param capacidad la capacidad inicial del diccionario.
     * @param dispersor el dispersor a utilizar.
     */
    public Diccionario(int capacidad, Dispersor<K> dispersor) {
        this.dispersor = dispersor;
        if (capacidad < MINIMA_CAPACIDAD)
            capacidad = MINIMA_CAPACIDAD;
        int n = 1;
        capacidad *= 2;
        while (n < capacidad)
            n *= 2;
        capacidad = n;
        entradas = nuevoArreglo(capacidad);
    }

    /**
     * Agrega un nuevo valor al diccionario, usando la llave proporcionada. Si la
     * llave ya había sido utilizada antes para agregar un valor, el diccionario
     * reemplaza ese valor con el recibido aquí.
     * 
     * @param llave la llave para agregar el valor.
     * @param valor el valor a agregar.
     * @throws IllegalArgumentException si la llave o el valor son nulos.
     */
    public void agrega(K llave, V valor) {
        if (llave == null || valor == null)
            throw new IllegalArgumentException("No se aceptan llaves ni valores nulos.");
        int hash = dispersarLLave(llave);
        if (entradas[hash] == null)
            entradas[hash] = new Lista<Entrada>();
        else
            for (Entrada e : entradas[hash])
                if (e.llave.equals(llave)) {
                    entradas[hash].elimina(e);
                    --elementos;
                }
        ++elementos;
        entradas[hash].agrega(new Entrada(llave, valor));
        if (carga() >= MAXIMA_CARGA) {
            Lista<Entrada>[] nuevoArreglo = nuevoArreglo(entradas.length * 2);
            Iterador iterador = new Iterador();
            while (iterador.hasNext()) {
                Entrada e = iterador.siguiente();
                hash = dispersarLLave(e.llave, nuevoArreglo.length - 1);
                if (nuevoArreglo[hash] == null)
                    nuevoArreglo[hash] = new Lista<Entrada>();
                nuevoArreglo[hash].agrega(e);
            }
            entradas = nuevoArreglo;
        }
    }

    private int dispersarLLave(K llave) {
        return dispersarLLave(llave, entradas.length - 1);
    }

    private int dispersarLLave(K llave, int mascara) {
        return dispersor.dispersa(llave) & (mascara);
    }

    /**
     * Regresa el valor del diccionario asociado a la llave proporcionada.
     * 
     * @param llave la llave para buscar el valor.
     * @return el valor correspondiente a la llave.
     * @throws IllegalArgumentException si la llave es nula.
     * @throws NoSuchElementException   si la llave no está en el diccionario.
     */
    public V get(K llave) {
        if (llave == null)
            throw new IllegalArgumentException("La llave no puede ser nula.");
        Entrada elementoBuscado = buscarEntrada(llave, dispersarLLave(llave));
        if (elementoBuscado == null)
            throw new NoSuchElementException("La llave introducida no se encuentra en el diccionario.");
        return elementoBuscado.valor;
    }

    /**
     * Nos dice si una llave se encuentra en el diccionario.
     * 
     * @param llave la llave que queremos ver si está en el diccionario.
     * @return <code>true</code> si la llave está en el diccionario,
     *         <code>false</code> en otro caso.
     */
    public boolean contiene(K llave) {
        return (llave == null) ? false : buscarEntrada(llave, dispersarLLave(llave)) != null;
    }

    /*
     * Busca en el diccionario la entrada asociada a la llave. Regresa null si no la
     * encuentra
     */
    private Entrada buscarEntrada(K llave, int hash) {
        if (entradas[hash] == null)
            return null;
        for (Entrada e : entradas[hash])
            if (e.llave.equals(llave))
                return e;
        return null;
    }

    /**
     * Elimina el valor del diccionario asociado a la llave proporcionada.
     * 
     * @param llave la llave para buscar el valor a eliminar.
     * @throws IllegalArgumentException si la llave es nula.
     * @throws NoSuchElementException   si la llave no se encuentra en el
     *                                  diccionario.
     */
    public void elimina(K llave) {
        if (llave == null)
            throw new IllegalArgumentException("La llave no puede ser nula.");
        int hash = dispersarLLave(llave);
        Entrada entradaEliminar = buscarEntrada(llave, hash);
        if (entradaEliminar == null)
            throw new NoSuchElementException("La llave introducida no se encuentra en el diccionario.");
        entradas[hash].elimina(entradaEliminar);
        elementos--;
        if (entradas[hash].esVacia())
            entradas[hash] = null;
    }

    /**
     * Nos dice cuántas colisiones hay en el diccionario.
     * 
     * @return cuántas colisiones hay en el diccionario.
     */
    public int colisiones() {
        int colisiones = 0;
        for (int i = 0; i < entradas.length; i++)
            if (entradas[i] != null)
                colisiones += entradas[i].getElementos() - 1;
        return colisiones;
    }

    /**
     * Nos dice el máximo número de colisiones para una misma llave que tenemos en
     * el diccionario.
     * 
     * @return el máximo número de colisiones para una misma llave.
     */
    public int colisionMaxima() {
        int colisionMaxima = 0;
        for (int i = 0; i < entradas.length; i++) {
            if (entradas[i] != null && colisionMaxima < entradas[i].getElementos() - 1)
                colisionMaxima = entradas[i].getElementos() - 1;
        }
        return colisionMaxima;
    }

    /**
     * Nos dice la carga del diccionario.
     * 
     * @return la carga del diccionario.
     */
    public double carga() {
        return ((double) elementos) / entradas.length;
    }

    /**
     * Regresa el número de entradas en el diccionario.
     * 
     * @return el número de entradas en el diccionario.
     */
    public int getElementos() {
        return elementos;
    }

    /**
     * Nos dice si el diccionario es vacío.
     * 
     * @return <code>true</code> si el diccionario es vacío, <code>false</code> en
     *         otro caso.
     */
    public boolean esVacia() {
        return elementos == 0;
    }

    /**
     * Limpia el diccionario de elementos, dejándolo vacío.
     */
    public void limpia() {
        entradas = nuevoArreglo(entradas.length);
        elementos = 0;
    }

    /**
     * Regresa una representación en cadena del diccionario.
     * 
     * @return una representación en cadena del diccionario.
     */
    @Override
    public String toString() {
        if (elementos == 0)
            return "{}";
        StringBuilder cadena = new StringBuilder();
        Iterador iterador = new Iterador();
        cadena.append("{ ");
        while (iterador.hasNext()) {
            Entrada e = iterador.siguiente();
            cadena.append(String.format("'%s': '%s', ", e.llave.toString(), e.valor.toString()));
        }
        cadena.append("}");
        return cadena.toString();
    }

    /**
     * Nos dice si el diccionario es igual al objeto recibido.
     * 
     * @param o el objeto que queremos saber si es igual al diccionario.
     * @return <code>true</code> si el objeto recibido es instancia de Diccionario,
     *         y tiene las mismas llaves asociadas a los mismos valores.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        @SuppressWarnings("unchecked")
        Diccionario<K, V> d = (Diccionario<K, V>) o;
        if (this.elementos != d.elementos)
            return false;
        Iterador iterador = new Iterador();
        while (iterador.hasNext()) {
            Entrada e1 = iterador.siguiente();
            Entrada e2 = d.buscarEntrada(e1.llave, d.dispersarLLave(e1.llave));
            if (e2 == null || !e1.valor.equals(e2.valor))
                return false;
        }
        return true;
    }

    /**
     * Regresa un iterador para iterar las llaves del diccionario. El diccionario se
     * itera sin ningún orden específico.
     * 
     * @return un iterador para iterar las llaves del diccionario.
     */
    public Iterator<K> iteradorLlaves() {
        return new IteradorLlaves();
    }

    /**
     * Regresa un iterador para iterar los valores del diccionario. El diccionario
     * se itera sin ningún orden específico.
     * 
     * @return un iterador para iterar los valores del diccionario.
     */
    @Override
    public Iterator<V> iterator() {
        return new IteradorValores();
    }
}
