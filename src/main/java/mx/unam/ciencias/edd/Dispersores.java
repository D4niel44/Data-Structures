package mx.unam.ciencias.edd;

/**
 * Clase para métodos estáticos con dispersores de bytes.
 */
public class Dispersores {

    /* Constructor privado para evitar instanciación. */
    private Dispersores() {
    }

    /**
     * Función de dispersión XOR.
     * 
     * @param llave la llave a dispersar.
     * @return la dispersión de XOR de la llave.
     */
    public static int dispersaXOR(byte[] llave) {
        int r = 0, i = 0;
        int longitud = llave.length;
        do {
            byte a, b, c, d;
            a = (i >= longitud) ? 0 : llave[i++];
            b = (i >= longitud) ? 0 : llave[i++];
            c = (i >= longitud) ? 0 : llave[i++];
            d = (i >= longitud) ? 0 : llave[i++];
            r ^= combina(a, b, c, d);
        } while (i < longitud);
        return r;
    }

    /**
     * Función de dispersión de Bob Jenkins.
     * 
     * @param llave la llave a dispersar.
     * @return la dispersión de Bob Jenkins de la llave.
     */
    public static int dispersaBJ(byte[] llave) {
        int[] arr = new int[3];
        arr[0] = arr[1] = 0x9e3779b9;
        arr[2] = 0xffffffff;
        int i = 0;
        while (llave.length - i >= 12) {
            arr[0] += combina(llave[i + 3], llave[i + 2], llave[i + 1], llave[i]);
            arr[1] += combina(llave[i + 7], llave[i + 6], llave[i + 5], llave[i + 4]);
            arr[2] += combina(llave[i + 11], llave[i + 10], llave[i + 9], llave[i + 8]);
            i += 12;
            mezcla(arr);
        }
        switch (llave.length - i) {
            case 11: arr[2] += aEnteroPos(llave[i+10], 3);
            case 10: arr[2] += aEnteroPos(llave[i+9], 2);
            case 9: arr[2] += aEnteroPos(llave[i+8], 1);
            case 8: arr[1] += aEnteroPos(llave[i+7], 3);
            case 7: arr[1] += aEnteroPos(llave[i+6], 2);
            case 6: arr[1] += aEnteroPos(llave[i+5], 1);
            case 5: arr[1] += aEnteroPos(llave[i+4], 0);
            case 4: arr[0] += aEnteroPos(llave[i+3], 3);
            case 3: arr[0] += aEnteroPos(llave[i+2], 2);
            case 2: arr[0] += aEnteroPos(llave[i+1], 1);
            case 1: arr[0] += aEnteroPos(llave[i], 0);
        }
        arr[2] += llave.length;
        mezcla(arr);
        return arr[2];
    }

    private static void mezcla(int[] arr) {
        int[] aux = { 13, 8, 13, 12, 16, 5, 3, 10, 15 };
        int i = 0;
        do {
            arr[0] -= arr[1];
            arr[0] -= arr[2];
            arr[0] ^= (arr[2] >>> aux[i++]);
            arr[1] -= arr[2];
            arr[1] -= arr[0];
            arr[1] ^= (arr[0] << aux[i++]);
            arr[2] -= arr[0];
            arr[2] -= arr[1];
            arr[2] ^= (arr[1] >>> aux[i++]);
        } while (i < 9);
    }

    /**
     * Función de dispersión Daniel J. Bernstein.
     * 
     * @param llave la llave a dispersar.
     * @return la dispersión de Daniel Bernstein de la llave.
     */
    public static int dispersaDJB(byte[] llave) {
        int hash = 5381;
        for (int i = 0; i < llave.length; i++)
            hash += (hash << 5) + (llave[i] & 0xff);
        return hash;
    }

    /**
     * Combina cuatro bytes en un entero de 32 bits.
     * 
     * @param a Primer byte.
     * @param b Segundo byte.
     * @param c Tercer byte.
     * @param d Cuarto byte
     * @return Entero con los cuatro bytes anteriores en el orden abcd.
     */
    private static int combina(byte a, byte b, byte c, byte d) {
        return (aEnteroPos(a, 3) | aEnteroPos(b, 2) | aEnteroPos(c, 1) | aEnteroPos(d, 0));
    }

    /**
     * Devuelve un entero con el byte en la posición indicada, (3 para regresar el
     * entero con el byte en la posición mas significativa, 0 para regresarlo en la
     * posición menos significativa)
     * 
     * @param a Byte de entrada
     * @param posicion Posición en la cuál introducir el byte (3,2,1,0)
     * @return Entero con el byte en la posición dada.
     */
    private static int aEnteroPos(byte a, int posicion) {
        return ((a & 0xff) << (8 * posicion));
    }
}
