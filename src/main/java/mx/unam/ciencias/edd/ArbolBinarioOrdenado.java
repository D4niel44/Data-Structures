package mx.unam.ciencias.edd;

import java.util.Iterator;

/**
 * <p>
 * Clase para árboles binarios ordenados. Los árboles son genéricos, pero
 * acotados a la interfaz {@link Comparable}.
 * </p>
 *
 * <p>
 * Un árbol instancia de esta clase siempre cumple que:
 * </p>
 * <ul>
 * <li>Cualquier elemento en el árbol es mayor o igual que todos sus
 * descendientes por la izquierda.</li>
 * <li>Cualquier elemento en el árbol es menor o igual que todos sus
 * descendientes por la derecha.</li>
 * </ul>
 */
public class ArbolBinarioOrdenado<T extends Comparable<T>> extends ArbolBinario<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Pila para recorrer los vértices en DFS in-order. */
        private Pila<Vertice> pila;

        /* Inicializa al iterador. */
        public Iterador() {
            pila = new Pila<>();
            agregarIzquierdo(raiz);
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override
        public boolean hasNext() {
            return !pila.esVacia();
        }

        /* Regresa el siguiente elemento en orden DFS in-order. */
        @Override
        public T next() {
            Vertice v = pila.saca();
            if (v.derecho != null)
                agregarIzquierdo(v.derecho);
            return v.elemento;
        }

        private void agregarIzquierdo(Vertice v) {
            while (v != null) {
                pila.mete(v);
                v = v.izquierdo;
            }
        }
    }

    /**
     * El vértice del último elemento agegado. Este vértice sólo se puede garantizar
     * que existe <em>inmediatamente</em> después de haber agregado un elemento al
     * árbol. Si cualquier operación distinta a agregar sobre el árbol se ejecuta
     * después de haber agregado un elemento, el estado de esta variable es
     * indefinido.
     */
    protected Vertice ultimoAgregado;

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros de
     * {@link ArbolBinario}.
     */
    public ArbolBinarioOrdenado() {
        super();
    }

    /**
     * Construye un árbol binario ordenado a partir de una colección. El árbol
     * binario ordenado tiene los mismos elementos que la colección recibida.
     * 
     * @param coleccion la colección a partir de la cual creamos el árbol binario
     *                  ordenado.
     */
    public ArbolBinarioOrdenado(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Agrega un nuevo elemento al árbol. El árbol conserva su orden in-order.
     * 
     * @param elemento el elemento a agregar.
     */
    @Override
    public void agrega(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException("El arbol no admite elementos nulos.");
        elementos++;
        if (raiz == null) {
            raiz = nuevoVertice(elemento);
            ultimoAgregado = raiz;
            return;
        }
        agrega(raiz, nuevoVertice(elemento));
    }

    private void agrega(Vertice v, Vertice nuevo) {
        if (nuevo.elemento.compareTo(v.elemento) <= 0) {
            if (v.izquierdo == null) {
                v.izquierdo = nuevo;
                nuevo.padre = v;
                ultimoAgregado = nuevo; 
            } else {
                agrega(v.izquierdo, nuevo);
            }
        } else {
            if (v.derecho == null) {
                v.derecho = nuevo;
                nuevo.padre = v;
                ultimoAgregado = nuevo;
            } else {
                agrega(v.derecho, nuevo);
            }
        }
    }

    /**
     * Elimina un elemento. Si el elemento no está en el árbol, no hace nada; si
     * está varias veces, elimina el primero que encuentre (in-order). El árbol
     * conserva su orden in-order.
     * 
     * @param elemento el elemento a eliminar.
     */
    @Override
    public void elimina(T elemento) {
        Vertice eliminar = vertice(this.busca(elemento));
        if (eliminar == null)
            return;
        elementos--;
        if (eliminar.izquierdo != null && eliminar.derecho != null)
            eliminar = intercambiaEliminable(eliminar);
        eliminaVertice(eliminar);
    }

    /**
     * Intercambia el elemento de un vértice con dos hijos distintos de
     * <code>null</code> con el elemento de un descendiente que tenga a lo más un
     * hijo.
     * 
     * @param vertice un vértice con dos hijos distintos de <code>null</code>.
     * @return el vértice descendiente con el que vértice recibido se intercambió.
     *         El vértice regresado tiene a lo más un hijo distinto de
     *         <code>null</code>.
     */
    protected Vertice intercambiaEliminable(Vertice vertice) {
        Vertice intercambiar = vertice.izquierdo;
        while (intercambiar.derecho != null)
            intercambiar = intercambiar.derecho;
        T aux = intercambiar.elemento;
        intercambiar.elemento = vertice.elemento;
        vertice.elemento = aux;
        return intercambiar;
    }

    /**
     * Elimina un vértice que a lo más tiene un hijo distinto de <code>null</code>
     * subiendo ese hijo (si existe).
     * 
     * @param vertice el vértice a eliminar; debe tener a lo más un hijo distinto de
     *                <code>null</code>.
     */
    protected void eliminaVertice(Vertice vertice) {
        if (vertice.izquierdo != null) {
            actualizaPadreHijo(vertice, vertice.izquierdo);
        } else {
            actualizaPadreHijo(vertice, vertice.derecho);
        }
    }

    private void actualizaPadreHijo(Vertice vertice, Vertice hijo) {
        if (vertice.padre == null) {
            raiz = hijo;
        } else if (vertice.padre.izquierdo == vertice) {
            vertice.padre.izquierdo = hijo;
        } else {
            vertice.padre.derecho = hijo;
        }
        if (hijo != null)
            hijo.padre = vertice.padre;
    }

    /**
     * Busca un elemento en el árbol recorriéndolo in-order. Si lo encuentra,
     * regresa el vértice que lo contiene; si no, regresa <code>null</code>.
     * 
     * @param elemento el elemento a buscar.
     * @return un vértice que contiene al elemento buscado si lo encuentra;
     *         <code>null</code> en otro caso.
     */
    @Override
    public VerticeArbolBinario<T> busca(T elemento) {
        return busca(raiz, elemento);
    }

    private VerticeArbolBinario<T> busca(Vertice v, T elemento) {
        if (v == null)
            return null;
        if (elemento.equals(v.elemento))
            return v;
        if (elemento.compareTo(v.elemento) <= 0) {
            return busca(v.izquierdo, elemento);
        } else {
            return busca(v.derecho, elemento);
        }

    }

    /**
     * Regresa el vértice que contiene el último elemento agregado al árbol. Este
     * método sólo se puede garantizar q// Aquí va su código.ue funcione
     * <em>inmediatamente</em> después de haber invocado al método {@link agrega}.
     * Si cualquier operación distinta a agregar sobre el árbol se ejecuta después
     * de haber agregado un elemento, el comportamiento de este método es
     * indefinido.
     * 
     * @return el vértice que contiene el último elemento agregado al árbol, si el
     *         método es invocado inmediatamente después de agregar un elemento al
     *         árbol.
     */
    public VerticeArbolBinario<T> getUltimoVerticeAgregado() {
        return ultimoAgregado;
    }

    /**
     * Gira el árbol a la derecha sobre el vértice recibido. Si el vértice no tiene
     * hijo izquierdo, el método no hace nada.
     * 
     * @param vertice el vértice sobre el que vamos a girar.
     */
    public void giraDerecha(VerticeArbolBinario<T> vertice) {
        Vertice v = vertice(vertice);
        if (v.izquierdo == null)
            return;
        actualizaPadreHijo(v, v.izquierdo);
        v.padre = v.izquierdo;
        v.izquierdo = v.padre.derecho;
        v.padre.derecho = v;
        if (v.izquierdo != null)
            v.izquierdo.padre = v;
    }

    /**
     * Gira el árbol a la izquierda sobre el vértice recibido. Si el vértice no
     * tiene hijo derecho, el método no hace nada.
     * 
     * @param vertice el vértice sobre el que vamos a girar.
     */
    public void giraIzquierda(VerticeArbolBinario<T> vertice) {
        Vertice v = vertice(vertice);
        if (v.derecho == null)
            return;
        actualizaPadreHijo(v, v.derecho);
        v.padre = v.derecho;
        v.derecho = v.padre.izquierdo;
        v.padre.izquierdo = v;
        if (v.derecho != null)
            v.derecho.padre = v;
    }

    /**
     * Realiza un recorrido DFS <em>pre-order</em> en el árbol, ejecutando la acción
     * recibida en cada elemento del árbol.
     * 
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsPreOrder(AccionVerticeArbolBinario<T> accion) {
        dfsPreOrder(raiz, accion);
    }

    private void dfsPreOrder(Vertice vertice, AccionVerticeArbolBinario<T> accion) {
        if (vertice == null)
            return;
        accion.actua(vertice);
        dfsPreOrder(vertice.izquierdo, accion);
        dfsPreOrder(vertice.derecho, accion);
    }

    /**
     * Realiza un recorrido DFS <em>in-order</em> en el árbol, ejecutando la acción
     * recibida en cada elemento del árbol.
     * 
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsInOrder(AccionVerticeArbolBinario<T> accion) {
        dfsInOrder(raiz, accion);
    }

    private void dfsInOrder(Vertice vertice, AccionVerticeArbolBinario<T> accion) {
        if (vertice == null)
            return;
        dfsInOrder(vertice.izquierdo, accion);
        accion.actua(vertice);
        dfsInOrder(vertice.derecho, accion);
    }

    /**
     * Realiza un recorrido DFS <em>post-order</em> en el árbol, ejecutando la
     * acción recibida en cada elemento del árbol.
     * 
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void dfsPostOrder(AccionVerticeArbolBinario<T> accion) {
        dfsPostOrder(raiz, accion);
    }

    private void dfsPostOrder(Vertice vertice, AccionVerticeArbolBinario<T> accion) {
        if (vertice == null)
            return;
        dfsPostOrder(vertice.izquierdo, accion);
        dfsPostOrder(vertice.derecho, accion);
        accion.actua(vertice);
    }

    /**
     * Regresa un iterador para iterar el árbol. El árbol se itera en orden.
     * 
     * @return un iterador para iterar el árbol.
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterador();
    }
}
