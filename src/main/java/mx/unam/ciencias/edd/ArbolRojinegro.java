package mx.unam.ciencias.edd;

/**
 * Clase para árboles rojinegros. Un árbol rojinegro cumple las siguientes
 * propiedades:
 *
 * <ol>
 * <li>Todos los vértices son NEGROS o ROJOS.</li>
 * <li>La raíz es NEGRA.</li>
 * <li>Todas las hojas (<code>null</code>) son NEGRAS (al igual que la
 * raíz).</li>
 * <li>Un vértice ROJO siempre tiene dos hijos NEGROS.</li>
 * <li>Todo camino de un vértice a alguna de sus hojas descendientes tiene el
 * mismo número de vértices NEGROS.</li>
 * </ol>
 *
 * Los árboles rojinegros se autobalancean.
 */
public class ArbolRojinegro<T extends Comparable<T>> extends ArbolBinarioOrdenado<T> {

    /**
     * Clase interna protegida para vértices.
     */
    protected class VerticeRojinegro extends Vertice {

        /** El color del vértice. */
        public Color color;

        /**
         * Constructor único que recibe un elemento.
         * 
         * @param elemento el elemento del vértice.
         */
        public VerticeRojinegro(T elemento) {
            super(elemento);
        }

        /**
         * Regresa una representación en cadena del vértice rojinegro.
         * 
         * @return una representación en cadena del vértice rojinegro.
         */
        public String toString() {
            return ((this.color == Color.ROJO) ? "R" : "N") + "{" + super.toString() + "}";
        }

        /**
         * Compara el vértice con otro objeto. La comparación es <em>recursiva</em>.
         * 
         * @param objeto el objeto con el cual se comparará el vértice.
         * @return <code>true</code> si el objeto es instancia de la clase
         *         {@link VerticeRojinegro}, su elemento es igual al elemento de éste
         *         vértice, los descendientes de ambos son recursivamente iguales, y los
         *         colores son iguales; <code>false</code> en otro caso.
         */
        @Override
        public boolean equals(Object objeto) {
            if (objeto == null || getClass() != objeto.getClass())
                return false;
            @SuppressWarnings("unchecked")
            VerticeRojinegro vertice = (VerticeRojinegro) objeto;
            return this.color == vertice.color && super.equals(objeto);
        }
    }

    /**
     * Constructor sin parámetros. Para no perder el constructor sin parámetros de
     * {@link ArbolBinarioOrdenado}.
     */
    public ArbolRojinegro() {
        super();
    }

    /**
     * Construye un árbol rojinegro a partir de una colección. El árbol rojinegro
     * tiene los mismos elementos que la colección recibida.
     * 
     * @param coleccion la colección a partir de la cual creamos el árbol rojinegro.
     */
    public ArbolRojinegro(Coleccion<T> coleccion) {
        super(coleccion);
    }

    /**
     * Construye un nuevo vértice, usando una instancia de {@link VerticeRojinegro}.
     * 
     * @param elemento el elemento dentro del vértice.
     * @return un nuevo vértice rojinegro con el elemento recibido dentro del mismo.
     */
    @Override
    protected Vertice nuevoVertice(T elemento) {
        return new VerticeRojinegro(elemento);
    }

    /**
     * Regresa el color del vértice rojinegro.
     * 
     * @param vertice el vértice del que queremos el color.
     * @return el color del vértice rojinegro.
     * @throws ClassCastException si el vértice no es instancia de
     *                            {@link VerticeRojinegro}.
     */
    public Color getColor(VerticeArbolBinario<T> vertice) {
        return ((VerticeRojinegro) vertice).color;
    }

    /**
     * Agrega un nuevo elemento al árbol. El método invoca al método
     * {@link ArbolBinarioOrdenado#agrega}, y después balancea el árbol recoloreando
     * vértices y girando el árbol como sea necesario.
     * 
     * @param elemento el elemento a agregar.
     */
    @Override
    public void agrega(T elemento) {
        super.agrega(elemento);
        VerticeRojinegro v = toVerticeRojinegro(ultimoAgregado);
        cambiarColor(v, Color.ROJO);
        rebalancearArbolAgrega(toVerticeRojinegro(v));
    }

    private void rebalancearArbolAgrega(VerticeRojinegro v) {
        if (v.padre == null) {
            v.color = Color.NEGRO;
            return;
        }
        VerticeRojinegro padre = toVerticeRojinegro(v.padre);
        if (esNegro(padre))
            return;
        VerticeRojinegro abuelo = toVerticeRojinegro(padre.padre);
        VerticeRojinegro tio = toVerticeRojinegro(obtenerHermano(padre));
        if (esRojo(tio)) {
            cambiarColor(padre, Color.NEGRO);
            cambiarColor(tio, Color.NEGRO);
            cambiarColor(abuelo, Color.ROJO);
            rebalancearArbolAgrega(toVerticeRojinegro(abuelo));
            return;
        }
        if (esHijoIzquierdo(v) != esHijoIzquierdo(padre)) {
            if (esHijoIzquierdo(v)) {
                super.giraDerecha(padre);
                v = toVerticeRojinegro(v.derecho);
            } else {
                super.giraIzquierda(padre);
                v = toVerticeRojinegro(v.izquierdo);
            }
            padre = toVerticeRojinegro(v.padre);
            abuelo = toVerticeRojinegro(padre.padre);
        }
        cambiarColor(padre, Color.NEGRO);
        cambiarColor(abuelo, Color.ROJO);
        if (esHijoIzquierdo(v)) {
            super.giraDerecha(abuelo);
        } else {
            super.giraIzquierda(abuelo);
        }
    }

    private void cambiarColor(VerticeRojinegro v, Color color) {
        v.color = color;
    }

    private boolean esRojo(VerticeRojinegro v) {
        return v != null && v.color == Color.ROJO;
    }

    private boolean esNegro(VerticeRojinegro vertice) {
        return vertice == null || vertice.color == Color.NEGRO;
    }

    private boolean esHijoIzquierdo(Vertice v) {
        return v.padre.izquierdo != null && v.padre.izquierdo == v;
    }

    private VerticeRojinegro toVerticeRojinegro(VerticeArbolBinario<T> vertice) {
        return (VerticeRojinegro) vertice;
    }

    /**
     * Elimina un elemento del árbol. El método elimina el vértice que contiene el
     * elemento, y recolorea y gira el árbol como sea necesario para rebalancearlo.
     * 
     * @param elemento el elemento a eliminar del árbol.
     */
    @Override
    public void elimina(T elemento) {
        VerticeRojinegro eliminar = toVerticeRojinegro(busca(elemento));
        if (eliminar == null)
            return;
        elementos--;
        if (eliminar.hayIzquierdo() && eliminar.hayDerecho())
            eliminar = toVerticeRojinegro(intercambiaEliminable(vertice(eliminar)));
        VerticeRojinegro hijo = null;
        if (!eliminar.hayIzquierdo() && !eliminar.hayDerecho()) {
            eliminar.izquierdo = nuevoVertice(null);
            hijo = toVerticeRojinegro(eliminar.izquierdo);
            hijo.padre = eliminar;
            cambiarColor(hijo, Color.NEGRO);
        } else
            hijo = toVerticeRojinegro((eliminar.izquierdo != null) ? eliminar.izquierdo : eliminar.derecho);
        eliminaVertice(eliminar);
        if (esRojo(hijo))
            hijo.color = Color.NEGRO;
        else if (!esRojo(eliminar))
            rebalancearArbolElimina(hijo);
        if (hijo.elemento == null)
            eliminaVertice(hijo);
    }

    private void rebalancearArbolElimina(VerticeRojinegro v) {
        if (v.padre == null)
            return;
        VerticeRojinegro padre = toVerticeRojinegro(v.padre);
        VerticeRojinegro hermano = toVerticeRojinegro(obtenerHermano(v));
        if (esRojo(hermano)) {
            cambiarColor(padre, Color.ROJO);
            cambiarColor(hermano, Color.NEGRO);
            if (esHijoIzquierdo(v))
                super.giraIzquierda(padre);
            else
                super.giraDerecha(padre);
            hermano = toVerticeRojinegro(obtenerHermano(v));
        }
        VerticeRojinegro hermanoI = toVerticeRojinegro(hermano.izquierdo);
        VerticeRojinegro hermanoD = toVerticeRojinegro(hermano.derecho);
        if (esNegro(padre) && esNegro(hermano) && esNegro(hermanoI) && esNegro(hermanoD)) {
            cambiarColor(hermano, Color.ROJO);
            rebalancearArbolElimina(padre);
            return;
        }
        if (esNegro(hermano) && esNegro(hermanoI) && esNegro(hermanoD)) {
            cambiarColor(hermano, Color.ROJO);
            cambiarColor(padre, Color.NEGRO);
            return;
        }
        if ((esHijoIzquierdo(v) && esRojo(hermanoI)) || (!esHijoIzquierdo(v) && esRojo(hermanoD))) {
            cambiarColor(hermano, Color.ROJO);
            if (esHijoIzquierdo(v)) {
                cambiarColor(hermanoI, Color.NEGRO);
                super.giraDerecha(hermano);
            } else {
                cambiarColor(hermanoD, Color.NEGRO);
                super.giraIzquierda(hermano);
            }
            hermano = toVerticeRojinegro(obtenerHermano(v));
            hermanoI = toVerticeRojinegro(hermano.izquierdo);
            hermanoD = toVerticeRojinegro(hermano.derecho);
        }
        cambiarColor(hermano, padre.color);
        cambiarColor(padre, Color.NEGRO);
        if (esHijoIzquierdo(v)) {
            cambiarColor(hermanoD, Color.NEGRO);
            super.giraIzquierda(padre);
        } else {
            cambiarColor(hermanoI, Color.NEGRO);
            super.giraDerecha(padre);
        }
    }

    private VerticeArbolBinario<T> obtenerHermano(VerticeRojinegro v) {
        return (esHijoIzquierdo(v) ? v.padre.derecho : v.padre.izquierdo);
    }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles
     * rojinegros no pueden ser girados a la izquierda por los usuarios de la clase,
     * porque se desbalancean.
     * 
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override
    public void giraIzquierda(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException(
                "Los árboles rojinegros no " + "pueden girar a la izquierda " + "por el usuario.");
    }

    /**
     * Lanza la excepción {@link UnsupportedOperationException}: los árboles
     * rojinegros no pueden ser girados a la derecha por los usuarios de la clase,
     * porque se desbalancean.
     * 
     * @param vertice el vértice sobre el que se quiere girar.
     * @throws UnsupportedOperationException siempre.
     */
    @Override
    public void giraDerecha(VerticeArbolBinario<T> vertice) {
        throw new UnsupportedOperationException(
                "Los árboles rojinegros no " + "pueden girar a la derecha " + "por el usuario.");
    }
}
