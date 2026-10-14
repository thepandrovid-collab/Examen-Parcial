 
import java.util.Scanner;

class Cancion {

    int id;
    String titulo, artista;
    int duracion; // segundos, mayor que cero

    Cancion(int id, String titulo, String artista, int duracion) {
        this.id = id;
        this.titulo = titulo;
        this.artista = artista;
        this.duracion = duracion;
    }

    public String toString() {
        return "[ID " + id + "] " + titulo + " - " + artista + " (" + duracion + " s)";
    }
}

class NodoCancion {

    Cancion dato;
    NodoCancion anterior, siguiente;

    NodoCancion(Cancion dato) {
        this.dato = dato;
    }
}

class ListaReproduccion {

    private NodoCancion cabeza; // primera canción
    private NodoCancion cola;   // última canción (su siguiente es la cabeza)
    private NodoCancion actual; // índice: canción actual
    private int tamano;
    private int siguienteId = 1;

    public int getTamano() {
        return tamano;
    }

    public Cancion getActual() {
        return actual == null ? null : actual.dato;
    }

    // Agrega al inicio o al final. Devuelve null si los datos no son válidos. O(1)
    private Cancion agregar(String titulo, String artista, int duracion, boolean alInicio) {
        if (titulo.isEmpty() || artista.isEmpty() || duracion <= 0) {
            return null;
        }
        NodoCancion nuevo = new NodoCancion(new Cancion(siguienteId++, titulo, artista, duracion));
        if (cabeza == null) {
            cabeza = cola = actual = nuevo;
            nuevo.siguiente = nuevo; // se apunta a sí mismo
            nuevo.anterior = nuevo;
        } else {
            nuevo.anterior = cola;
            nuevo.siguiente = cabeza; // cierra el círculo
            cola.siguiente = nuevo;
            cabeza.anterior = nuevo;
            if (alInicio) {
                cabeza = nuevo;
            } else {
                cola = nuevo;
            }
        }
        tamano++;
        return nuevo.dato;
    }

    public Cancion agregarInicio(String t, String a, int d) {
        return agregar(t, a, d, true);
    }

    public Cancion agregarFinal(String t, String a, int d) {
        return agregar(t, a, d, false);
    }

    // Localiza un nodo por ID. O(n)
    private NodoCancion localizar(int id) {
        NodoCancion n = cabeza;
        for (int i = 0; i < tamano; i++) {
            if (n.dato.id == id) {
                return n;
            }
            n = n.siguiente;
        }
        return null;
    }

    public Cancion buscar(int id) {
        NodoCancion n = localizar(id);
        return n == null ? null : n.dato;
    }

    public boolean seleccionar(int id) {
        NodoCancion n = localizar(id);
        if (n != null) {
            actual = n;
        }
        return n != null;
    }

    // Mueve el índice (canción actual) una posición. O(1)
    public Cancion siguiente() {
        if (actual == null) {
            return null;
        }
        actual = actual.siguiente;
        return actual.dato;
    }

    public Cancion anterior() {
        if (actual == null) {
            return null;
        }
        actual = actual.anterior;
        return actual.dato;
    }

    // O(1): el nodo ya está vacío y disponible
    public Cancion eliminarActual() {
        if (actual == null) {
            return null;
        }
        Cancion c = actual.dato;
        desvincular(actual);
        return c;
    }

    // O(n): busca y luego desvincula
    public boolean eliminarPorId(int id) {
        NodoCancion n = localizar(id);
        if (n != null) {
            desvincular(n);
        }
        return n != null;
    }

    private void desvincular(NodoCancion n) {
        if (tamano == 1) {
            cabeza = cola = actual = null;
        } else {
            n.anterior.siguiente = n.siguiente;
            n.siguiente.anterior = n.anterior;
            if (n == cabeza) {
                cabeza = n.siguiente;
            }
            if (n == cola) {
                cola = n.anterior;
            }
            if (n == actual) {
                actual = n.siguiente; // la que le seguía

            }
        }
        tamano--;
    }

    // Lista bidireccional. Puede avanzar y retroceder conforme lo que ingrese el usuario. O(n)
    public void mostrar() {
        NodoCancion n = cabeza;
        System.out.println("Hacia adelante:");
        for (int i = 0; i < tamano; i++, n = n.siguiente) {
            System.out.println("  " + n.dato);
        }
        n = cola;
        System.out.println("Hacia atrás:");
        for (int i = 0; i < tamano; i++, n = n.anterior) {
            System.out.println("  " + n.dato);
        }
    }

    // Muestra la cancion actual y avanza la posicion actual segun el tamaño de la lista. O(k)
    public void reproducir(int k) {
        for (int i = 1; i <= k; i++) {
            System.out.println("  " + i + ". Reproduciendo: " + actual.dato);
            actual = actual.siguiente;
        }
    }

    // ---- Para las pruebas ----
    public boolean consistente() {
        if (tamano == 0) {
            return cabeza == null && cola == null && actual == null;
        }
        if (cola.siguiente != cabeza || cabeza.anterior != cola) {
            return false;
        }
        NodoCancion n = cabeza;
        boolean hayActual = false;
        for (int i = 0; i < tamano; i++, n = n.siguiente) {
            if (n.siguiente.anterior != n || n.anterior.siguiente != n) {
                return false;
            }
            if (n == actual) {
                hayActual = true;
            }
        }
        return hayActual && n == cabeza && (tamano > 1 || cabeza.siguiente == cabeza);
    }

    public String resumen() {
        String f = "", b = "";
        NodoCancion n = cabeza;
        for (int i = 0; i < tamano; i++, n = n.siguiente) {
            f += n.dato.titulo;
        }
        n = cola;
        for (int i = 0; i < tamano; i++, n = n.anterior) {
            b += n.dato.titulo;
        }
        return "tam=" + tamano + " adelante=" + f + " atras=" + b + " actual=" + (actual == null ? "-" : actual.dato.titulo);
    }
}

public class MainEx {

    static Scanner sc = new Scanner(System.in);

    static int leerEntero(String msg) {
        while (true) {
            System.out.print(msg);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida, escribe un número entero.");
            }
        }
    }

    static String leerTexto(String msg) {
        System.out.print(msg);
        return sc.nextLine().trim();
    }

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--pruebas")) {
            pruebas();
            return;
        }
        ListaReproduccion lista = new ListaReproduccion(); //Menu control Lista de Reproducción
        int op = 0;
        while (op != 13) {
            System.out.println("\n1. Agregar canción al inicio             2. Agregar canción al final     3. Buscar canción por ID");
            System.out.println("4. Seleccionar canción por ID              5. Eliminar canción por ID      6. Eliminar canción actual");
            System.out.println("7. Ver canción actual                      8. Saltar canción               9. Retroceder canción");
            System.out.println("10. Mostrar lista de reproducción          11. Cantidad de canciones en la lista actual.");
            System.out.println("12. Reproducir todas las canciones         13. Salir");
            op = leerEntero("Opción: ");

            if (op < 1 || op > 13) {
                System.out.println("Opción inválida.");
            } else if (op == 1 || op == 2) {
                String t = leerTexto("Título: ");
                String a = leerTexto("Artista: ");
                int d = leerEntero("Duración de la canción (segundos): ");
                Cancion c = op == 1 ? lista.agregarInicio(t, a, d) : lista.agregarFinal(t, a, d);
                System.out.println(c == null ? "Datos inválidos: no se agregó." : "Agregada: " + c);
            } else if (op == 11) {
                System.out.println("Canciones: " + lista.getTamano());
            } else if (op != 13 && lista.getTamano() == 0) {
                System.out.println("La lista está vacía.");
            } else if (op == 3) {
                Cancion c = lista.buscar(leerEntero("ID: "));
                System.out.println(c == null ? "No existe ese ID." : "Encontrada: " + c);
            } else if (op == 4) {
                System.out.println(lista.seleccionar(leerEntero("ID: ")) ? "Actual: " + lista.getActual() : "No existe ese ID.");
            } else if (op == 5) {
                System.out.println(lista.eliminarPorId(leerEntero("ID: ")) ? "Eliminada." : "No existe ese ID.");
            } else if (op == 6) {
                System.out.println("Eliminada: " + lista.eliminarActual());
                System.out.println("Actual ahora: " + lista.getActual());
            } else if (op == 7) {
                System.out.println("Actual: " + lista.getActual());
            } else if (op == 8) {
                System.out.println("Siguiente: " + lista.siguiente());
            } else if (op == 9) {
                System.out.println("Anterior: " + lista.anterior());
            } else if (op == 10) {
                lista.mostrar();
            } else if (op == 12) {
                int k = leerEntero("k: ");
                if (k > 0) {
                    lista.reproducir(k);
                    System.out.println("Sigue: " + lista.getActual());
                } else {
                    System.out.println("El numero de canciones debe ser mayor que cero.");
                }
            }
        }
        sc.close();
    }

    // ---------------- Pruebas: java MainEx --pruebas ----------------
    static int n = 0, bien = 0;

    static ListaReproduccion base() { // A B C D E (ids 1 a 5), actual = A
        ListaReproduccion l = new ListaReproduccion();
        for (String s : new String[]{"A", "B", "C", "D", "E"}) {
            l.agregarFinal(s, "x", 100);
        }
        return l;
    }

    static String t(Cancion c) {
        return c == null ? "null" : c.titulo;
    }

    static void prueba(String nombre, String inicial, String operacion, String esperado, String obtenido, ListaReproduccion l) {
        boolean ok = esperado.equals(obtenido) && l.consistente();
        n++;
        if (ok) {
            bien++;
        }
        System.out.println("Prueba " + n + ": " + nombre + "\n  Inicial : " + inicial + "\n  Operación: " + operacion
                + "\n  Esperado: " + esperado + "\n  Obtenido: " + obtenido + "\n  Enlaces consistentes: "
                + l.consistente() + " -> " + (ok ? "OK" : "FALLÓ") + "\n");
    }

    static void pruebas() {
        String b = "tam=5 adelante=ABCDE atras=EDCBA actual=A";
        ListaReproduccion l = new ListaReproduccion();
        String r = t(l.getActual()) + " " + t(l.siguiente()) + " " + t(l.anterior()) + " " + t(l.eliminarActual())
                + " " + t(l.buscar(1)) + " " + l.eliminarPorId(1) + " " + l.seleccionar(1);
        prueba("Lista vacía", "vacía", "actual, siguiente, anterior, eliminarActual, buscar, eliminarPorId, seleccionar",
                "null null null null null false false", r, l);

        l = new ListaReproduccion();
        l.agregarFinal("A", "x", 100);
        prueba("Primera canción", "vacía", "agregarFinal(A)", "tam=1 adelante=A atras=A actual=A", l.resumen(), l);
        r = t(l.siguiente()) + " " + t(l.anterior());
        prueba("Un solo nodo", "A", "siguiente, anterior", "A A", r, l);

        l = new ListaReproduccion();
        l.agregarFinal("A", "x", 100);
        l.agregarFinal("B", "x", 100);
        l.agregarInicio("C", "x", 100);
        prueba("Inserción al inicio y al final", "A", "agregarFinal(B), agregarInicio(C)",
                "tam=3 adelante=CAB atras=BAC actual=A", l.resumen(), l);

        prueba("Recorrido con 5 canciones", "vacía", "agregarFinal x5", b, base().resumen(), base());

        l = base();
        l.seleccionar(5);
        prueba("Avance desde la última", b + " con actual=E", "siguiente", "A", t(l.siguiente()), l);

        l = base();
        prueba("Retroceso desde la primera", b, "anterior", "E", t(l.anterior()), l);

        l = base();
        l.seleccionar(3);
        l.eliminarPorId(1);
        prueba("Eliminar el primero (actual C se conserva)", b + " con actual=C", "eliminarPorId(1)",
                "tam=4 adelante=BCDE atras=EDCB actual=C", l.resumen(), l);

        l = base();
        l.eliminarPorId(5);
        prueba("Eliminar el último", b, "eliminarPorId(5)", "tam=4 adelante=ABCD atras=DCBA actual=A", l.resumen(), l);

        l = base();
        l.eliminarPorId(3);
        prueba("Eliminar uno intermedio", b, "eliminarPorId(3)", "tam=4 adelante=ABDE atras=EDBA actual=A", l.resumen(), l);

        l = base();
        l.seleccionar(3);
        l.eliminarActual();
        prueba("Eliminar la actual (pasa a la siguiente)", b + " con actual=C", "eliminarActual",
                "tam=4 adelante=ABDE atras=EDBA actual=D", l.resumen(), l);

        l = new ListaReproduccion();
        l.agregarFinal("A", "x", 100);
        l.eliminarActual();
        prueba("Eliminar la única canción", "A", "eliminarActual", "tam=0 adelante= atras= actual=-", l.resumen(), l);

        l = base();
        r = t(l.buscar(99)) + " " + l.eliminarPorId(99) + " " + l.resumen();
        prueba("ID inexistente", b, "buscar(99), eliminarPorId(99)", "null false " + b, r, l);

        l = base();
        l.reproducir(7);
        prueba("Reproducir k=7 con 5 canciones", b, "reproducir(7)", "tam=5 adelante=ABCDE atras=EDCBA actual=C", l.resumen(), l);

        l = new ListaReproduccion();
        l.agregarFinal("A", "x", 100);
        r = l.agregarFinal("B", "x", 0) + " " + l.agregarFinal("B", "x", -5) + " " + l.agregarFinal("", "x", 5)
                + " idDeB=" + l.agregarFinal("B", "x", 10).id + " " + l.resumen();
        prueba("Datos inválidos", "A", "duración 0, -5, título vacío y luego B válida",
                "null null null idDeB=2 tam=2 adelante=AB atras=BA actual=A", r, l);

        System.out.println("RESUMEN: " + bien + "/" + n + " pruebas aprobadas");
    }
}
