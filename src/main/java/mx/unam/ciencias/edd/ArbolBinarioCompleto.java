package mx.unam.ciencias.edd;

import java.util.Iterator;


/**
 * <p>
 * Clase para árboles binarios completos.
 * </p>
 *
 * <p>
 * Un árbol binario completo agrega y elimina elementos de tal forma que el
 * árbol siempre es lo más cercano posible a estar lleno.
 * </p>
 */
public class ArbolBinarioCompleto<T> extends ArbolBinario<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Cola para recorrer los vértices en BFS. */
        private Cola<Vertice> cola;

        /* Inicializa al iterador. */
        public Iterador() {
            cola = new Cola<>();
            if (raiz != null)
                cola.mete(raiz);
        }

        /* Nos dice si hay un elemento siguiente. */
        @Override
        public boolean hasNext() {
            return !cola.esVacia();
        }

        /* Regresa el siguiente elemento en orden BFS. */
        @Override
        public T next() {
            Vertice aux = cola.saca();
            if (aux.izquierdo != null)
                cola.mete(aux.izquierdo);
            if (aux.derecho != null)
                cola.mete(aux.derecho);
            return aux.elemento;
        }
    }

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros de
     * {@link ArbolBinario}.
     */
    public ArbolBinarioCompleto() {
        super();
    }

    /**
     * Construye un árbol binario completo a partir de una colección. El árbol
     * binario completo tiene los mismos elementos que la colección recibida.
     * 
     * @param coleccion la colección a partir de la cual creamos el árbol binario
     *                  completo.
     */
    public ArbolBinarioCompleto(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Agrega un elemento al árbol binario completo. El nuevo elemento se coloca a
     * la derecha del último nivel, o a la izquierda de un nuevo nivel.
     * 
     * @param elemento el elemento a agregar al árbol.
     * @throws IllegalArgumentException si <code>elemento</code> es
     *                                  <code>null</code>.
     */
    @Override
    public void agrega(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException("El arbol no admite elementos nulos.");
        elementos++;
        if (raiz == null) {
            raiz = nuevoVertice(elemento);
            return;
        }
        Cola<Vertice> cola = new Cola<>();
        cola.mete(raiz);
        while (!cola.esVacia()) {
            Vertice vertice = cola.saca();
            if (vertice.izquierdo == null) {
                vertice.izquierdo = nuevoVertice(elemento);
                vertice.izquierdo.padre = vertice;
                return;
            }
            if (vertice.derecho == null) {
                vertice.derecho = nuevoVertice(elemento);
                vertice.derecho.padre = vertice;
                return;
            }
            cola.mete(vertice.izquierdo);
            cola.mete(vertice.derecho);
        }
    }

    /**
     * Elimina un elemento del árbol. El elemento a eliminar cambia lugares con el
     * último elemento del árbol al recorrerlo por BFS, y entonces es eliminado.
     * 
     * @param elemento el elemento a eliminar.
     */
    @Override
    public void elimina(T elemento) {
        Cola<Vertice> cola = new Cola<>();
        if (raiz != null)
            cola.mete(raiz);
        Vertice ultimo = null;
        Vertice eliminar = null;
        while (!cola.esVacia()) {
            ultimo = cola.saca();
            if (ultimo.izquierdo != null)
                cola.mete(ultimo.izquierdo);
            if (ultimo.derecho != null)
                cola.mete(ultimo.derecho);
            if (ultimo.elemento.equals(elemento))
                eliminar = ultimo;
        }
        if (eliminar == null)
            return;
        elementos--;
        if (ultimo == raiz) {
            raiz = null;
            return;
        }
        if (eliminar != ultimo)
            eliminar.elemento = ultimo.elemento;
        if (ultimo.padre.izquierdo == ultimo) {
            ultimo.padre.izquierdo = null;
        } else {
            ultimo.padre.derecho = null;
        }
    }


    /**
     * Regresa la altura del árbol. La altura de un árbol binario completo siempre
     * es ⌊log<sub>2</sub><em>n</em>⌋.
     * 
     * @return la altura del árbol.
     */
    @Override
    public int altura() {
        if (elementos == 0)
            return -1;
        int i = 0;
        int c = elementos;
        while (c >= 2) {
            c /= 2;
            i++;
        }
        return i;
    }

    /**
     * Realiza un recorrido BFS en el árbol, ejecutando la acción recibida en cada
     * elemento del árbol.testAltura
     * 
     * @param accion la acción a realizar en cada elemento del árbol.
     */
    public void bfs(AccionVerticeArbolBinario<T> accion) {
        Cola<Vertice> cola = new Cola<>();
        if (raiz != null)
            cola.mete(raiz);
        while (!cola.esVacia()) {
            Vertice vertice = cola.saca();
            if (vertice.izquierdo != null)
                cola.mete(vertice.izquierdo);
            if (vertice.derecho != null)
                cola.mete(vertice.derecho);
            accion.actua(vertice);
        }
    }

    /**
     * Regresa un iterador para iterar el árbol. El árbol se itera en orden BFS.
     * 
     * @return un iterador para iterar el árbol.
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterador();
    }
}
