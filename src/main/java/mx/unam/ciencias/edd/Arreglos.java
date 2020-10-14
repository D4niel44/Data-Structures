package mx.unam.ciencias.edd;

import java.util.Comparator;

/**
 * Clase para ordenar y buscar arreglos genéricos.
 */
public class Arreglos {

    /* Constructor privado para evitar instanciación. */
    private Arreglos() {
    }

    /**
     * Ordena el arreglo recibido usando QickSort.
     * 
     * @param <T>        tipo del que puede ser el arreglo.
     * @param arreglo    el arreglo a ordenar.
     * @param comparador el comparador para ordenar el arreglo.
     */
    public static <T> void quickSort(T[] arreglo, Comparator<T> comparador) {
        quickSortAux(arreglo, 0, arreglo.length - 1, comparador);
    }

    private static <T> void quickSortAux(T[] arreglo, int a, int b, Comparator<T> comparador) {
        if (b <= a)
            return;
        int i = a + 1;
        int j = b;
        while (i < j) {
            if (comparador.compare(arreglo[i], arreglo[a]) > 0 && comparador.compare(arreglo[j], arreglo[a]) <= 0) {
                intercambia(arreglo, i, j);
                i++;
                j--;
            } else if (comparador.compare(arreglo[i], arreglo[a]) <= 0) {
                i++;
            } else {
                j--;
            }
        }
        if (comparador.compare(arreglo[i], arreglo[a]) > 0)
            i--;
        intercambia(arreglo, a, i);
        quickSortAux(arreglo, a, i - 1, comparador);
        quickSortAux(arreglo, i + 1, b, comparador);
    }

    /**
     * Ordena el arreglo recibido usando QickSort.
     * 
     * @param <T>     tipo del que puede ser el arreglo.
     * @param arreglo un arreglo cuyos elementos son comparables.
     */
    public static <T extends Comparable<T>> void quickSort(T[] arreglo) {
        quickSort(arreglo, (a, b) -> a.compareTo(b));
    }

    /**
     * Ordena el arreglo recibido usando SelectionSort.
     * 
     * @param <T>        tipo del que puede ser el arreglo.
     * @param arreglo    el arreglo a ordenar.
     * @param comparador el comparador para ordernar el arreglo.
     */
    public static <T> void selectionSort(T[] arreglo, Comparator<T> comparador) {
        int m;
        for (int i = 0; i < arreglo.length - 1; i++) {
            m = i;
            for (int j = i + 1; j < arreglo.length; j++)
                if (comparador.compare(arreglo[j], arreglo[m]) < 0)
                    m = j;
            intercambia(arreglo, i, m);
        }
    }

    /**
     * Ordena el arreglo recibido usando SelectionSort.
     * 
     * @param <T>     tipo del que puede ser el arreglo.
     * @param arreglo un arreglo cuyos elementos son comparables.
     */
    public static <T extends Comparable<T>> void selectionSort(T[] arreglo) {
        selectionSort(arreglo, (a, b) -> a.compareTo(b));
    }

    /**
     * Hace una búsqueda binaria del elemento en el arreglo. Regresa el índice del
     * elemento en el arreglo, o -1 si no se encuentra.
     * 
     * @param <T>        tipo del que puede ser el arreglo.
     * @param arreglo    el arreglo dónde buscar.
     * @param elemento   el elemento a buscar.
     * @param comparador el comparador para hacer la búsqueda.
     * @return el índice del elemento en el arreglo, o -1 si no se encuentra.
     */
    public static <T> int busquedaBinaria(T[] arreglo, T elemento, Comparator<T> comparador) {
        Pila<Integer> p = new Pila<>();
        p.mete(0);
        p.mete(arreglo.length - 1);
        do {
            int b = p.saca();
            int a = p.saca();
            int m = a + (b - a) / 2;
            if (comparador.compare(arreglo[m], elemento) == 0) {
                return m;
            } else if (comparador.compare(arreglo[m], elemento) > 0 && a < m) {
                p.mete(a);
                p.mete(m - 1);
            } else if (m < b) {
                p.mete(m + 1);
                p.mete(b);
            }
        } while (!p.esVacia());
        return -1;
    }

    /**
     * Hace una búsqueda binaria del elemento en el arreglo. Regresa el índice del
     * elemento en el arreglo, o -1 si no se encuentra.
     * 
     * @param <T>      tipo del que puede ser el arreglo.
     * @param arreglo  un arreglo cuyos elementos son comparables.
     * @param elemento el elemento a buscar.
     * @return el índice del elemento en el arreglo, o -1 si no se encuentra.
     */
    public static <T extends Comparable<T>> int busquedaBinaria(T[] arreglo, T elemento) {
        return busquedaBinaria(arreglo, elemento, (a, b) -> a.compareTo(b));
    }

    /**
     * Intercambia la posicion de dos elementos de un arreglo
     * 
     * @param arreglo el arreglo donde estan los elementos
     * @param a       indice del primer elemento
     * @param b       indice del segundo elemento
     */
    private static <T> void intercambia(T[] arreglo, int a, int b) {
        T auxiliar = arreglo[a];
        arreglo[a] = arreglo[b];
        arreglo[b] = auxiliar;
    }
}
