package mx.unam.ciencias.edd;

/**
 * <p>
 * Clase para árboles AVL.
 * </p>
 *
 * <p>
 * Un árbol AVL cumple que para cada uno de sus vértices, la diferencia entre la
 * áltura de sus subárboles izquierdo y derecho está entre -1 y 1.
 * </p>
 */
public class ArbolAVL<T extends Comparable<T>> extends ArbolBinarioOrdenado<T> {

    /**
     * Clase interna protegida para vértices.
     */
    protected class VerticeAVL extends Vertice {

        /** La altura del vértice. */
        public int altura;

        /**
         * Constructor único que recibe un elemento.
         * 
         * @param elemento el elemento del vértice.
         */
        public VerticeAVL(T elemento) {
            super(elemento);
        }

        /**
         * Regresa la altura del vértice.
         * 
         * @return la altura del vértice.
         */
        @Override
        public int altura() {
            return altura;
        }

        /**
         * Regresa una representación en cadena del vértice AVL.
         * 
         * @return una representación en cadena del vértice AVL.
         */
        @Override
        public String toString() {
            int alturaI = (this.izquierdo == null) ? -1 : ((VerticeAVL) this.izquierdo()).altura;
            int alturaD = (this.derecho == null) ? -1 : ((VerticeAVL) this.derecho()).altura;
            return super.toString() + " " + this.altura + "/" + (alturaI - alturaD);
        }

        /**
         * Compara el vértice con otro objeto. La comparación es <em>recursiva</em>.
         * 
         * @param objeto el objeto con el cual se comparará el vértice.
         * @return <code>true</code> si el objeto es instancia de la clase
         *         {@link VerticeAVL}, su elemento es igual al elemento de éste vértice,
         *         los descendientes de ambos son recursivamente iguales, y las alturas
         *         son iguales; <code>false</code> en otro caso.
         */
        @Override
        public boolean equals(Object objeto) {
            if (objeto == null || getClass() != objeto.getClass())
                return false;
            @SuppressWarnings("unchecked")
            VerticeAVL vertice = (VerticeAVL) objeto;
            return (this.altura == vertice.altura) && super.equals(objeto);
        }
    }

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros de
     * {@link ArbolBinarioOrdenado}.
     */
    public ArbolAVL() {
        super();
    }

    /**
     * Construye un árbol AVL a partir de una colección. El árbol AVL tiene los
     * mismos elementos que la colección recibida.
     * 
     * @param coleccion la colección a partir de la cual creamos el árbol AVL.
     */
    public ArbolAVL(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Construye un nuevo vértice, usando una instancia de {@link VerticeAVL}.
     * 
     * @param elemento el elemento dentro del vértice.
     * @return un nuevo vértice con el elemento recibido dentro del mismo.
     */
    @Override
    protected Vertice nuevoVertice(T elemento) {
        return new VerticeAVL(elemento);
    }

    /**
     * Agrega un nuevo elemento al árbol. El método invoca al método
     * {@link ArbolBinarioOrdenado#agrega}, y después balancea el árbol girándolo
     * como sea necesario.
     * 
     * @param elemento el elemento a agregar.
     */
    @Override
    public void agrega(T elemento) {
        super.agrega(elemento);
        rebalanceaArbolAVL(ultimoAgregado.padre);
    }

    /**
     * Elimina un elemento del árbol. El método elimina el vértice que contiene el
     * elemento, y gira el árbol como sea necesario para rebalancearlo.
     * 
     * @param elemento el elemento a eliminar del árbol.
     */
    @Override
    public void elimina(T elemento) {
        Vertice verticeEliminar = vertice(busca(elemento));
        if (verticeEliminar == null)
            return;
        --elementos;
        if (verticeEliminar.hayIzquierdo() && verticeEliminar.hayDerecho())
            verticeEliminar = intercambiaEliminable(verticeEliminar);
        eliminaVertice(verticeEliminar);
        rebalanceaArbolAVL(verticeEliminar.padre);
    }

    private void rebalanceaArbolAVL(VerticeArbolBinario<T> vertice) {
        if (vertice == null)
            return;
        VerticeAVL v = toVerticeAVL(vertice);
        VerticeAVL izquierdo = toVerticeAVL(v.izquierdo);
        VerticeAVL derecho = toVerticeAVL(v.derecho);
        int balance = obtenerBalance(v);
        int altura = Math.max(obtenerAltura(izquierdo), obtenerAltura(derecho)) + 1;
        v.altura = altura;
        if (balance == -2) {
            VerticeAVL izquierdoHijo = toVerticeAVL(derecho.izquierdo);
            if (obtenerBalance(derecho) == 1) {
                super.giraDerecha(derecho);
                derecho.altura = altura - 2;
                izquierdoHijo.altura = altura - 1;
                derecho = toVerticeAVL(v.derecho);
                izquierdoHijo = toVerticeAVL(derecho.izquierdo);
            }
            super.giraIzquierda(v);
            v.altura = (obtenerAltura(izquierdoHijo) == altura - 2) ? altura - 1 : altura - 2;
            derecho.altura = v.altura + 1;
            v = derecho;
        } else if (balance == 2) {
            VerticeAVL derechoHijo = toVerticeAVL(izquierdo.derecho);
            if (obtenerBalance(izquierdo) == -1) {
                super.giraIzquierda(izquierdo);
                izquierdo.altura = altura - 2;
                derechoHijo.altura = altura - 1;
                izquierdo = toVerticeAVL(v.izquierdo);
                derechoHijo = toVerticeAVL(izquierdo.derecho);
            }
            super.giraDerecha(v);
            v.altura = (obtenerAltura(derechoHijo) == altura - 2) ? altura - 1 : altura - 2;
            izquierdo.altura = v.altura + 1;
            v = izquierdo;
        }
        rebalanceaArbolAVL(v.padre);
    }

    private int obtenerAltura(VerticeAVL vertice) {
        return (vertice == null) ? -1 : vertice.altura;
    }

    private int obtenerBalance(VerticeAVL vertice) {
        return obtenerAltura(toVerticeAVL(vertice.izquierdo)) - obtenerAltura(toVerticeAVL(vertice.derecho));
    }

    private VerticeAVL toVerticeAVL(VerticeArbolBinario<T> vertice) {
        return (VerticeAVL) vertice;
    }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles AVL no
     * pueden ser girados a la derecha por los usuarios de la clase, porque se
     * desbalancean.
     * 
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override
    public void giraDerecha(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException(
                "Los árboles AVL no  pueden " + "girar a la izquierda por el " + "usuario.");
    }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles AVL no
     * pueden ser girados a la izquierda por los usuarios de la clase, porque se
     * desbalancean.
     * 
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override
    public void giraIzquierda(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException(
                "Los árboles AVL no  pueden " + "girar a la derecha por el " + "usuario.");
    }
}
