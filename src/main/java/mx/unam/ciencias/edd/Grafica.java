package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para gráficas. Una gráfica es un conjunto de vértices y aristas, tales
 * que las aristas son un subconjunto del producto cruz de los vértices.
 */
public class Grafica<T> implements Coleccion<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Iterador auxiliar. */
        private Iterator<Vertice> iterador;

        /* Construye un nuevo iterador, auxiliándose de la lista de vértices. */
        public Iterador() {
            iterador = vertices.iterator();
        }

        /* Nos dice si hay un siguiente elemento. */
        @Override
        public boolean hasNext() {
            return iterador.hasNext();
        }

        /* Regresa el siguiente elemento. */
        @Override
        public T next() {
            return iterador.next().elemento;
        }
    }

    /* Clase interna privada para vértices. */
    private class Vertice implements VerticeGrafica<T>, ComparableIndexable<Vertice> {

        /* El elemento del vértice. */
        public T elemento;
        /* El color del vértice. */
        public Color color;
        /* La distancia del vértice. */
        public double distancia;
        /* El índice del vértice. */
        public int indice;
        /* La lista de vecinos del vértice. */
        public Diccionario<T, Vecino> vecinos;

        /* Crea un nuevo vértice a partir de un elemento. */
        public Vertice(T elemento) {
            this.elemento = elemento;
            vecinos = new Diccionario<>();
            color = Color.NINGUNO;
            distancia = Double.MAX_VALUE;
        }

        /* Regresa el elemento del vértice. */
        @Override
        public T get() {
            return elemento;
        }

        /* Regresa el grado del vértice. */
        @Override
        public int getGrado() {
            return vecinos.getElementos();
        }

        /* Regresa el color del vértice. */
        @Override
        public Color getColor() {
            return color;
        }

        /* Regresa un iterable para los vecinos. */
        @Override
        public Iterable<? extends VerticeGrafica<T>> vecinos() {
            return vecinos;
        }

        /* Define el índice del vértice. */
        @Override
        public void setIndice(int indice) {
            this.indice = indice;
        }

        /* Regresa el índice del vértice. */
        @Override
        public int getIndice() {
            return this.indice;
        }

        /* Compara dos vértices por distancia. */
        @Override
        public int compareTo(Vertice vertice) {
            return (int) (this.distancia - vertice.distancia);
        }
    }

    /* Clase interna privada para vértices vecinos. */
    private class Vecino implements VerticeGrafica<T> {

        /* El vértice vecino. */
        public Vertice vecino;
        /* El peso de la arista conectando al vértice con su vértice vecino. */
        public double peso;

        /*
         * Construye un nuevo vecino con el vértice recibido como vecino y el peso
         * especificado.
         */
        public Vecino(Vertice vecino, double peso) {
            this.vecino = vecino;
            this.peso = peso;
        }

        /* Regresa el elemento del vecino. */
        @Override
        public T get() {
            return vecino.elemento;
        }

        /* Regresa el grado del vecino. */
        @Override
        public int getGrado() {
            return vecino.vecinos.getElementos();
        }

        /* Regresa el color del vecino. */
        @Override
        public Color getColor() {
            return vecino.color;
        }

        /* Regresa un iterable para los vecinos del vecino. */
        @Override
        public Iterable<? extends VerticeGrafica<T>> vecinos() {
            return vecino.vecinos;
        }
    }

    /*
     * Interface para poder usar lambdas al buscar el elemento que sigue al
     * reconstruir un camino.
     */
    @FunctionalInterface
    private interface BuscadorCamino {
        /* Regresa true si el vértice se sigue del vecino. */
        public boolean seSiguen(Grafica.Vertice v, Grafica.Vecino a);
    }

    /* Vértices. */
    private Diccionario<T, Vertice> vertices;
    /* Número de aristas. */
    private int aristas;

    /**
     * Constructor único.
     */
    public Grafica() {
        vertices = new Diccionario<>();
    }

    /**
     * Regresa el número de elementos en la gráfica. El número de elementos es igual
     * al número de vértices.
     * 
     * @return el número de elementos en la gráfica.
     */
    @Override
    public int getElementos() {
        return vertices.getElementos();
    }

    /**
     * Regresa el número de aristas.
     * 
     * @return el número de aristas.
     */
    public int getAristas() {
        return aristas;
    }

    /**
     * Agrega un nuevo elemento a la gráfica.
     * 
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si el elemento es <code>null</code> o ya
     *                                  había sido agregado a la gráfica.
     */
    @Override
    public void agrega(T elemento) {
        if (elemento == null)
            throw new IllegalArgumentException("La gráfica no acepta elementos vacíos.");
        if (this.contiene(elemento))
            throw new IllegalArgumentException("La gráfica ya contiene el elemento.");
        vertices.agrega(elemento, new Vertice(elemento));
    }

    /**
     * Conecta dos elementos de la gráfica. Los elementos deben estar en la gráfica.
     * El peso de la arista que conecte a los elementos será 1.
     * 
     * @param a el primer elemento a conectar.
     * @param b el segundo elemento a conectar.
     * @throws NoSuchElementException   si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b ya están conectados, o si a es
     *                                  igual a b.
     */
    public void conecta(T a, T b) {
        conecta(a, b, 1);
    }

    /**
     * Busca el vértice que contiene al elemento dado.
     * 
     * @param elemento Elemento a buscar.
     * @return Vertice que contiene al elemento buscado o null si el elemento no se
     *         encuentra en la gráfica.
     */
    private Vertice busquedaVertice(T elemento) {
        return (vertices.contiene(elemento)) ? vertices.get(elemento) : null;
    }

    /**
     * Busca el vecino del vértice dado que tiene como elemento al elemento dado.
     * 
     * @param vertice  Vertice donde buscar el vecino.
     * @param elemento Elemento del vecino.
     * @return El vecino de vertice que contiene a elemento o null si no se encontró
     *         el elemento.
     */
    private Vecino busquedaVecino(Vertice vertice, T elemento) {
        return (vertice.vecinos.contiene(elemento)) ? vertice.vecinos.get(elemento) : null;
    }

    /**
     * Conecta dos elementos de la gráfica. Los elementos deben estar en la gráfica.
     * 
     * @param a    el primer elemento a conectar.
     * @param b    el segundo elemento a conectar.
     * @param peso el peso de la nueva vecino.
     * @throws NoSuchElementException   si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b ya están conectados, si a es igual
     *                                  a b, o si el peso es no positivo.
     */
    public void conecta(T a, T b, double peso) {
        if (peso <= 0)
            throw new IllegalArgumentException("El peso debe ser un valor positivo.");
        Vertice va = busquedaVertice(a);
        Vertice vb = busquedaVertice(b);
        if (va == null)
            throw new NoSuchElementException("El elemento a no se encuentra en la gráfica.");
        if (vb == null)
            throw new NoSuchElementException("El elemento b no se encuentra en la gráfica.");
        if (va == vb)
            throw new IllegalArgumentException("El elemento a es igual al elemento b.");
        if (sonVecinos(a, b))
            throw new IllegalArgumentException("Los elementos a y b ya están conectados.");
        va.vecinos.agrega(b, new Vecino(vb, peso));
        vb.vecinos.agrega(a, new Vecino(va, peso));
        aristas++;
    }

    /**
     * Desconecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica y estar conectados entre ellos.
     * 
     * @param a el primer elemento a desconectar.
     * @param b el segundo elemento a desconectar.
     * @throws NoSuchElementException   si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados.
     */
    public void desconecta(T a, T b) {
        Vertice va = busquedaVertice(a);
        Vertice vb = busquedaVertice(b);
        if (va == null)
            throw new NoSuchElementException("El elemento a no se encuentra en la gráfica.");
        if (vb == null)
            throw new NoSuchElementException("El elemento b no se encuentra en la gráfica.");
        if (!va.vecinos.contiene(b))
            throw new IllegalArgumentException("Los elementos a y b no están conectados.");
        va.vecinos.elimina(b);
        vb.vecinos.elimina(a);
        aristas--;
    }

    /**
     * Nos dice si el elemento está contenido en la gráfica.
     * 
     * @return <code>true</code> si el elemento está contenido en la gráfica,
     *         <code>false</code> en otro caso.
     */
    @Override
    public boolean contiene(T elemento) {
        return vertices.contiene(elemento);
    }

    /**
     * Elimina un elemento de la gráfica. El elemento tiene que estar contenido en
     * la gráfica.
     * 
     * @param elemento el elemento a eliminar.
     * @throws NoSuchElementException si el elemento no está contenido en la
     *                                gráfica.
     */
    @Override
    public void elimina(T elemento) {
        Vertice v = busquedaVertice(elemento);
        if (v == null)
            throw new NoSuchElementException("El elemento no está contenido en la gráfica.");
        vertices.elimina(elemento);
        for (Vecino vecino : v.vecinos) {
            vecino.vecino.vecinos.elimina(elemento);
            aristas--;
        }
    }

    /**
     * Nos dice si dos elementos de la gráfica están conectados. Los elementos deben
     * estar en la gráfica.
     * 
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @return <code>true</code> si a y b son vecinos, <code>false</code> en otro
     *         caso.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     */
    public boolean sonVecinos(T a, T b) {
        Vertice va = busquedaVertice(a);
        Vertice vb = busquedaVertice(b);
        if (va == null)
            throw new NoSuchElementException("El elemento a no se encuentra en la gráfica.");
        if (vb == null)
            throw new NoSuchElementException("El elemento b no se encuentra en la gráfica.");
        return busquedaVecino(va, b) != null;
    }

    /**
     * Regresa el peso de la arista que comparten los vértices que contienen a los
     * elementos recibidos.
     * 
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @return el peso de la arista que comparten los vértices que contienen a los
     *         elementos recibidos.
     * @throws NoSuchElementException   si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados.
     */
    public double getPeso(T a, T b) {
        Vertice va = busquedaVertice(a);
        Vertice vb = busquedaVertice(b);
        if (va == null)
            throw new NoSuchElementException("El elemento a no se encuentra en la gráfica.");
        if (vb == null)
            throw new NoSuchElementException("El elemento b no se encuentra en la gráfica.");
        Vecino vecino = busquedaVecino(va, b);
        if (vecino == null)
            throw new IllegalArgumentException("Los elementos a y b no están conectados.");
        return vecino.peso;
    }

    /**
     * Define el peso de la arista que comparten los vértices que contienen a los
     * elementos recibidos.
     * 
     * @param a    el primer elemento.
     * @param b    el segundo elemento.
     * @param peso el nuevo peso de la arista que comparten los vértices que
     *             contienen a los elementos recibidos.
     * @throws NoSuchElementException   si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados, o si peso es
     *                                  menor o igual que cero.
     */
    public void setPeso(T a, T b, double peso) {
        if (peso <= 0)
            throw new IllegalArgumentException("El peso debe ser un valor positivo.");
        Vertice va = busquedaVertice(a);
        Vertice vb = busquedaVertice(b);
        if (va == null)
            throw new NoSuchElementException("El elemento a no se encuentra en la gráfica.");
        if (vb == null)
            throw new NoSuchElementException("El elemento b no se encuentra en la gráfica.");
        Vecino vecinoA = busquedaVecino(va, b);
        Vecino vecinoB = busquedaVecino(vb, a);
        if (vecinoA == null || vecinoB == null)
            throw new IllegalArgumentException("Los elementos a y b no están conectados.");
        vecinoA.peso = peso;
        vecinoB.peso = peso;
    }

    /**
     * Regresa el vértice correspondiente el elemento recibido.
     * 
     * @param elemento el elemento del que queremos el vértice.
     * @throws NoSuchElementException si elemento no es elemento de la gráfica.
     * @return el vértice correspondiente el elemento recibido.
     */
    public VerticeGrafica<T> vertice(T elemento) {
        Vertice vertice = busquedaVertice(elemento);
        if (vertice == null)
            throw new NoSuchElementException("El elemento no se encuentra en la gráfica.");
        return vertice;
    }

    /**
     * Define el color del vértice recibido.
     * 
     * @param vertice el vértice al que queremos definirle el color.
     * @param color   el nuevo color del vértice.
     * @throws IllegalArgumentException si el vértice no es válido.
     */
    public void setColor(VerticeGrafica<T> vertice, Color color) {
        if (vertice == null || (vertice.getClass() != Vertice.class && vertice.getClass() != Vecino.class))
            throw new IllegalArgumentException("Vértice inválido");
        if (vertice.getClass() == Vertice.class) {
            Vertice v = (Vertice) vertice;
            v.color = color;
        }
        if (vertice.getClass() == Vecino.class) {
            Vecino v = (Vecino) vertice;
            v.vecino.color = color;
        }
    }

    /**
     * Nos dice si la gráfica es conexa.
     * 
     * @return <code>true</code> si la gráfica es conexa, <code>false</code> en otro
     *         caso.
     */
    public boolean esConexa() {
        dfs(vertices.iteradorLlaves().next(), (VerticeGrafica<T> v) -> {
        });
        for (Vertice v : vertices)
            if (v.color == Color.ROJO)
                return false;
        return true;
    }

    /**
     * Realiza la acción recibida en cada uno de los vértices de la gráfica, en el
     * orden en que fueron agregados.
     * 
     * @param accion la acción a realizar.
     */
    public void paraCadaVertice(AccionVerticeGrafica<T> accion) {
        for (Vertice vertice : vertices)
            accion.actua(vertice);
    }

    /**
     * Realiza la acción recibida en todos los vértices de la gráfica, en el orden
     * determinado por BFS, comenzando por el vértice correspondiente al elemento
     * recibido. Al terminar el método, todos los vértices tendrán color
     * {@link Color#NINGUNO}.
     * 
     * @param elemento el elemento sobre cuyo vértice queremos comenzar el
     *                 recorrido.
     * @param accion   la acción a realizar.
     * @throws NoSuchElementException si el elemento no está en la gráfica.
     */
    public void bfs(T elemento, AccionVerticeGrafica<T> accion) {
        Vertice v = busquedaVertice(elemento);
        if (v == null)
            throw new NoSuchElementException("El elemento no se encuentra contenido en la gráfica.");
        for (Vertice vertice : vertices)
            vertice.color = Color.ROJO;
        Cola<Vertice> cola = new Cola<>();
        cola.mete(v);
        v.color = Color.NINGUNO;
        while (!cola.esVacia()) {
            v = cola.saca();
            accion.actua(v);
            for (Vecino vecino : v.vecinos) {
                Vertice verticeVecino = vecino.vecino;
                if (verticeVecino.color == Color.ROJO) {
                    verticeVecino.color = Color.NINGUNO;
                    cola.mete(verticeVecino);
                }
            }
        }
    }

    /**
     * Realiza la acción recibida en todos los vértices de la gráfica, en el orden
     * determinado por DFS, comenzando por el vértice correspondiente al elemento
     * recibido. Al terminar el método, todos los vértices tendrán color
     * {@link Color#NINGUNO}.
     * 
     * @param elemento el elemento sobre cuyo vértice queremos comenzar el
     *                 recorrido.
     * @param accion   la acción a realizar.
     * @throws NoSuchElementException si el elemento no está en la gráfica.
     */
    public void dfs(T elemento, AccionVerticeGrafica<T> accion) {
        Vertice v = busquedaVertice(elemento);
        if (v == null)
            throw new NoSuchElementException("El elemento no se encuentra contenido en la gráfica.");
        for (Vertice vertice : vertices)
            vertice.color = Color.ROJO;
        Pila<Vertice> pila = new Pila<>();
        pila.mete(v);
        v.color = Color.NINGUNO;
        while (!pila.esVacia()) {
            v = pila.saca();
            accion.actua(v);
            for (Vecino vecino : v.vecinos) {
                Vertice verticeVecino = vecino.vecino;
                if (verticeVecino.color == Color.ROJO) {
                    verticeVecino.color = Color.NINGUNO;
                    pila.mete(verticeVecino);
                }
            }
        }
    }

    /**
     * Nos dice si la gráfica es vacía.
     * 
     * @return <code>true</code> si la gráfica es vacía, <code>false</code> en otro
     *         caso.
     */
    @Override
    public boolean esVacia() {
        return vertices.esVacia();
    }

    /**
     * Limpia la gráfica de vértices y aristas, dejándola vacía.
     */
    @Override
    public void limpia() {
        vertices.limpia();
        aristas = 0;
    }

    /**
     * Regresa una representación en cadena de la gráfica.
     * 
     * @return una representación en cadena de la gráfica.
     */
    @Override
    public String toString() {
        String s = "{";
        for (Vertice v : vertices) {
            s += v.elemento + ", ";
            v.color = Color.ROJO;
        }
        s += "}, {";
        for (Vertice v : vertices) {
            v.color = Color.NEGRO;
            for (Vecino vecino : v.vecinos)
                if (vecino.getColor() != Color.NEGRO)
                    s += "(" + v.elemento + ", " + vecino.get() + "), ";
        }
        return s + "}";
    }

    /**
     * Nos dice si la gráfica es igual al objeto recibido.
     * 
     * @param objeto el objeto con el que hay que comparar.
     * @return <code>true</code> si la gráfica es igual al objeto recibido;
     *         <code>false</code> en otro caso.
     */
    @Override
    public boolean equals(Object objeto) {
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        @SuppressWarnings("unchecked")
        Grafica<T> grafica = (Grafica<T>) objeto;
        if (this.vertices.getElementos() != grafica.vertices.getElementos() || this.aristas != grafica.aristas)
            return false;
        for (Vertice v : this.vertices) {
            Vertice v2 = grafica.busquedaVertice(v.elemento);
            if (v2 == null)
                return false;
            for (Vecino vecino : v.vecinos) {
                boolean a = false;
                for (Vecino vecino2 : v2.vecinos)
                    if (vecino.get().equals(vecino2.get())) {
                        a = true;
                        break;
                    }
                if (!a)
                    return false;
            }
        }
        return true;
    }

    /**
     * Regresa un iterador para iterar la gráfica. La gráfica se itera en el orden
     * en que fueron agregados sus elementos.
     * 
     * @return un iterador para iterar la gráfica.
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterador();
    }

    /**
     * Calcula una trayectoria de distancia mínima entre dos vértices.
     * 
     * @param origen  el vértice de origen.
     * @param destino el vértice de destino.
     * @return Una lista con vértices de la gráfica, tal que forman una trayectoria
     *         de distancia mínima entre los vértices <code>a</code> y
     *         <code>b</code>. Si los elementos se encuentran en componentes conexos
     *         distintos, el algoritmo regresa una lista vacía.
     * @throws NoSuchElementException si alguno de los dos elementos no está en la
     *                                gráfica.
     */
    public Lista<VerticeGrafica<T>> trayectoriaMinima(T origen, T destino) {
        Lista<VerticeGrafica<T>> trayectoria = new Lista<>();
        Vertice verticeOrigen = busquedaVertice(origen);
        Vertice verticeDestino = busquedaVertice(destino);
        if (verticeOrigen == null || verticeDestino == null)
            throw new NoSuchElementException("Uno de los elementos no se encunetra en la gráfica.");
        if (origen == destino) {
            trayectoria.agrega(verticeOrigen);
            return trayectoria;
        }
        this.paraCadaVertice(v -> {
            ((Vertice) v).distancia = Double.MAX_VALUE;
        });
        verticeOrigen.distancia = 0;
        Cola<Vertice> cola = new Cola<>();
        cola.mete(verticeOrigen);
        while (!cola.esVacia()) {
            Vertice v = cola.saca();
            for (Vecino vecino : v.vecinos) {
                Vertice verticeVecino = vecino.vecino;
                if (verticeVecino.distancia == Double.MAX_VALUE) {
                    verticeVecino.distancia = v.distancia + 1;
                    cola.mete(verticeVecino);
                }
            }
        }
        if (verticeDestino.distancia == Double.MAX_VALUE)
            return trayectoria;
        trayectoria = reconstruirTrayectoria(verticeOrigen, verticeDestino,
                (v, a) -> (a.vecino.distancia == v.distancia - 1));
        return trayectoria;
    }

    /**
     * Calcula la ruta de peso mínimo entre el elemento de origen y el elemento de
     * destino.
     * 
     * @param origen  el vértice origen.
     * @param destino el vértice destino.
     * @return una trayectoria de peso mínimo entre el vértice <code>origen</code> y
     *         el vértice <code>destino</code>. Si los vértices están en componentes
     *         conexas distintas, regresa una lista vacía.
     * @throws NoSuchElementException si alguno de los dos elementos no está en la
     *                                gráfica.
     */
    public Lista<VerticeGrafica<T>> dijkstra(T origen, T destino) {
        Lista<VerticeGrafica<T>> trayectoria = new Lista<>();
        Vertice verticeOrigen = busquedaVertice(origen);
        Vertice verticeDestino = busquedaVertice(destino);
        if (verticeOrigen == null || verticeDestino == null)
            throw new NoSuchElementException("Uno de los elementos no se encuentra en la gráfica.");
        if (origen == destino) {
            trayectoria.agrega(verticeOrigen);
            return trayectoria;
        }
        this.paraCadaVertice(v -> {
            ((Vertice) v).distancia = Double.MAX_VALUE;
        });
        verticeOrigen.distancia = 0;
        int elementos = vertices.getElementos();
        MonticuloDijkstra<Vertice> monticulo = null;
        if (aristas > (elementos * (elementos - 1)) / 2 - elementos)
            monticulo = new MonticuloArreglo<Vertice>(vertices, vertices.getElementos());
        else
            monticulo = new MonticuloMinimo<>(vertices, vertices.getElementos());
        while (!monticulo.esVacia()) {
            Vertice v = monticulo.elimina();
            for (Vecino vecino : v.vecinos) {
                Vertice verticeVecino = vecino.vecino;
                if (verticeVecino.distancia > v.distancia + vecino.peso) {
                    verticeVecino.distancia = v.distancia + vecino.peso;
                    monticulo.reordena(verticeVecino);
                }
            }
        }
        if (verticeDestino.distancia == Double.MAX_VALUE)
            return trayectoria;
        trayectoria = reconstruirTrayectoria(verticeOrigen, verticeDestino,
                (v, a) -> (a.vecino.distancia + a.peso == v.distancia));
        return trayectoria;
    }

    /**
     * Reconstruye la trayectoria del vertice origen al de destino y agrega los
     * vértices a la lista proporcionada.
     * 
     * @param trayectoria Lista de vertice donde se van a agregar los vértices de la
     *                    trayectoria.
     * @param origen      Vértice de origen.
     * @param destino     Vértice de destino.
     * @param lambda      Comparador para cuando dos vértices son consecutivos en la
     *                    trayectoria.
     */
    private Lista<VerticeGrafica<T>> reconstruirTrayectoria(Vertice origen, Vertice destino,
            BuscadorCamino lambda) {
        Lista<VerticeGrafica<T>> trayectoria = new Lista<>();
        trayectoria.agregaInicio(destino);
        Vertice v = destino;
        while (v != origen) {
            for (Vecino vecino : v.vecinos) {
                if (lambda.seSiguen(v, vecino)) {
                    trayectoria.agregaInicio(vecino.vecino);
                    v = vecino.vecino;
                    break;
                }
            }
        }
        return trayectoria;
    }
}
