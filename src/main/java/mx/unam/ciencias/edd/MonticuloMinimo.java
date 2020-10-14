package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para montículos mínimos (<i>min heaps</i>).
 */
public class MonticuloMinimo<T extends ComparableIndexable<T>> implements Coleccion<T>, MonticuloDijkstra<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Índice del iterador. */
        private int indice;

        /* Nos dice si hay un siguiente elemento. */
        @Override
        public boolean hasNext() {
            return indice < elementos;
        }

        /* Regresa el siguiente elemento. */
        @Override
        public T next() {
            if (indice >= elementos)
                throw new NoSuchElementException("No hay mas elementos.");
            return arbol[indice++];
        }
    }

    /* Clase estática privada para adaptadores. */
    private static class Adaptador<T extends Comparable<T>> implements ComparableIndexable<Adaptador<T>> {

        /* El elemento. */
        private T elemento;
        /* El índice. */
        private int indice;

        /* Crea un nuevo comparable indexable. */
        public Adaptador(T elemento) {
            this.elemento = elemento;
            indice = -1;
        }

        /* Regresa el índice. */
        @Override
        public int getIndice() {
            return indice;
        }

        /* Define el índice. */
        @Override
        public void setIndice(int indice) {
            this.indice = indice;
        }

        /* Compara un adaptador con otro. */
        @Override
        public int compareTo(Adaptador<T> adaptador) {
            return this.elemento.compareTo(adaptador.elemento);
        }
    }

    /* El número de elementos en el arreglo. */
    private int elementos;
    /* Usamos un truco para poder utilizar arreglos genéricos. */
    private T[] arbol;

    /*
     * Truco para crear arreglos genéricos. Es necesario hacerlo así por cómo Java
     * implementa sus genéricos; de otra forma obtenemos advertencias del
     * compilador.
     */
    @SuppressWarnings("unchecked")
    private T[] nuevoArreglo(int n) {
        return (T[]) (new ComparableIndexable[n]);
    }

    /**
     * Constructor sin parámetros. Es más eficiente usar
     * {@link #MonticuloMinimo(Coleccion)} o {@link #MonticuloMinimo(Iterable,int)},
     * pero se ofrece este constructor por completez.
     */
    public MonticuloMinimo() {
        arbol = nuevoArreglo(100);
    }

    /**
     * Constructor para montículo mínimo que recibe una colección. Es más barato
     * construir un montículo con todos sus elementos de antemano (tiempo
     * <i>O</i>(<i>n</i>)), que el insertándolos uno por uno (tiempo
     * <i>O</i>(<i>n</i> log <i>n</i>)).
     * 
     * @param coleccion la colección a partir de la cuál queremos construir el
     *                  montículo.
     */
    public MonticuloMinimo(Coleccion<T> coleccion) {
        this(coleccion, coleccion.getElementos());
    }

    /**
     * Constructor para montículo mínimo que recibe un iterable y el número de
     * elementos en el mismo. Es más barato construir un montículo con todos sus
     * elementos de antemano (tiempo <i>O</i>(<i>n</i>)), que el insertándolos uno
     * por uno (tiempo <i>O</i>(<i>n</i> log <i>n</i>)).
     * 
     * @param iterable el iterable a partir de la cuál queremos construir el
     *                 montículo.
     * @param n        el número de elementos en el iterable.
     */
    public MonticuloMinimo(Iterable<T> iterable, int n) {
        arbol = nuevoArreglo(n);
        elementos = n;
        int c = 0;
        for (T elemento : iterable) {
            arbol[c] = elemento;
            elemento.setIndice(c++);
        }
        for (int i = (n / 2) - 1; i >= 0; i--)
            acomodaHaciaAbajo(arbol[i]);
    }

    /**
     * Agrega un nuevo elemento en el montículo.
     * 
     * @param elemento el elemento a agregar en el montículo.
     */
    @Override
    public void agrega(T elemento) {
        if (arbol.length == elementos) {
            T[] arbolNuevo = nuevoArreglo(elementos * 2);
            for (int i = 0; i < elementos; i++)
                arbolNuevo[i] = arbol[i];
            arbol = arbolNuevo;
        }
        arbol[elementos] = elemento;
        elemento.setIndice(elementos++);
        acomodaHaciaArriba(elemento);
    }

    /**
     * Elimina el elemento mínimo del montículo.
     * 
     * @return el elemento mínimo del montículo.
     * @throws IllegalStateException si el montículo es vacío.
     */
    @Override
    public T elimina() {
        if (elementos == 0)
            throw new IllegalStateException("El montículo es vacío.");
        T elementoEliminado = arbol[0];
        intercambia(elementoEliminado, arbol[--elementos]);
        acomodaHaciaAbajo(arbol[0]);
        elementoEliminado.setIndice(-1);
        return elementoEliminado;
    }

    /**
     * Elimina un elemento del montículo.
     * 
     * @param elemento a eliminar del montículo.
     */
    @Override
    public void elimina(T elemento) {
        if (elemento.getIndice() < 0 || elemento.getIndice() >= elementos)
            return;
        T elementoIntercambiar = arbol[--elementos];
        intercambia(elemento, elementoIntercambiar);
        reordena(elementoIntercambiar);
        elemento.setIndice(-1);
    }

    /**
     * Nos dice si un elemento está contenido en el montículo.
     * 
     * @param elemento el elemento que queremos saber si está contenido.
     * @return <code>true</code> si el elemento está contenido, <code>false</code>
     *         en otro caso.
     */
    @Override
    public boolean contiene(T elemento) {
        int indice = elemento.getIndice();
        return !(indice < 0 || indice >= elementos) && elemento.equals(arbol[indice]);
    }

    /**
     * Nos dice si el montículo es vacío.
     * 
     * @return <code>true</code> si ya no hay elementos en el montículo,
     *         <code>false</code> en otro caso.
     */
    @Override
    public boolean esVacia() {
        return elementos == 0;
    }

    /**
     * Limpia el montículo de elementos, dejándolo vacío.
     */
    @Override
    public void limpia() {
        elementos = 0;
        for (int i = 0; i < arbol.length; i++)
            arbol[i] = null;
    }

    /**
     * Reordena un elemento en el árbol.
     * 
     * @param elemento el elemento que hay que reordenar.
     */
    @Override
    public void reordena(T elemento) {
        acomodaHaciaAbajo(elemento);
        acomodaHaciaArriba(elemento);
    }

    private void acomodaHaciaAbajo(T elemento) {
        int indiceI = (elemento.getIndice() * 2) + 1;
        int indiceD = (elemento.getIndice() * 2) + 2;
        T izquierdo = (indiceI < elementos) ? arbol[indiceI] : null;
        T derecho = (indiceD < elementos) ? arbol[indiceD] : null;
        T hijoMenor = null;
        if (izquierdo == null && derecho == null)
            return;
        if (izquierdo == null)
            hijoMenor = derecho;
        else if (derecho == null)
            hijoMenor = izquierdo;
        else
            hijoMenor = (izquierdo.compareTo(derecho) <= 0) ? izquierdo : derecho;
        if (elemento.compareTo(hijoMenor) > 0) {
            intercambia(elemento, hijoMenor);
            acomodaHaciaAbajo(elemento);
        }
    }

    private void acomodaHaciaArriba(T elemento) {
        int indicePadre = (elemento.getIndice() - 1) / 2;
        if (elemento.compareTo(arbol[indicePadre]) >= 0)
            return;
        intercambia(elemento, arbol[indicePadre]);
        acomodaHaciaArriba(arbol[indicePadre]);
    }

    /**
     * Regresa el número de elementos en el montículo mínimo.
     * 
     * @return el número de elementos en el montículo mínimo.
     */
    @Override
    public int getElementos() {
        return elementos;
    }

    /**
     * Regresa el <i>i</i>-ésimo elemento del árbol, por niveles.
     * 
     * @param i el índice del elemento que queremos, en <em>in-order</em>.
     * @return el <i>i</i>-ésimo elemento del árbol, por niveles.
     * @throws NoSuchElementException si i es menor que cero, o mayor o igual que el
     *                                número de elementos.
     */
    @Override
    public T get(int i) {
        if (i < 0 || i >= elementos)
            throw new NoSuchElementException();
        return arbol[i];
    }

    /**
     * Regresa una representación en cadena del montículo mínimo.
     * 
     * @return una representación en cadena del montículo mínimo.
     */
    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < elementos; i++)
            s += arbol[i].toString() + ", ";
        return s;
    }

    /**
     * Nos dice si el montículo mínimo es igual al objeto recibido.
     * 
     * @param objeto el objeto con el que queremos comparar el montículo mínimo.
     * @return <code>true</code> si el objeto recibido es un montículo mínimo igual
     *         al que llama el método; <code>false</code> en otro caso.
     */
    @Override
    public boolean equals(Object objeto) {
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        @SuppressWarnings("unchecked")
        MonticuloMinimo<T> monticulo = (MonticuloMinimo<T>) objeto;
        for (int i = 0; i < elementos; i++)
            if (!arbol[i].equals(monticulo.arbol[i]))
                return false;
        return true;
    }

    /**
     * Regresa un iterador para iterar el montículo mínimo. El montículo se itera en
     * orden BFS.
     * 
     * @return un iterador para iterar el montículo mínimo.
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Ordena la colección usando HeapSort.
     * 
     * @param <T>       tipo del que puede ser el arreglo.
     * @param coleccion la colección a ordenar.
     * @return una lista ordenada con los elementos de la colección.
     */
    public static <T extends Comparable<T>> Lista<T> heapSort(Coleccion<T> coleccion) {
        Lista<Adaptador<T>> lista1 = new Lista<>();
        Lista<T> lista2 = new Lista<>();
        int i = 0;
        for (T elemento : coleccion) {
            Adaptador<T> adaptadorElemento = new Adaptador<>(elemento);
            adaptadorElemento.setIndice(i++);
            lista1.agregaFinal(adaptadorElemento);
        }
        MonticuloMinimo<Adaptador<T>> monticulo = new MonticuloMinimo<>(lista1);
        while (!monticulo.esVacia())
            lista2.agregaFinal(monticulo.elimina().elemento);
        return lista2;
    }

    /**
     * Intercambia dos elementos del montículo.
     */
    private void intercambia(T elemento1, T elemento2) {
        int indice1 = elemento1.getIndice();
        int indice2 = elemento2.getIndice();
        arbol[indice1] = elemento2;
        arbol[indice2] = elemento1;
        elemento1.setIndice(indice2);
        elemento2.setIndice(indice1);
    }
}
