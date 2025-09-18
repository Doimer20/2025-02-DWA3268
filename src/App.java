import java.sql.*;
import java.util.Scanner;

public class App {
    public static final String URL = "jdbc:mysql://localhost:3306/estudiantes_db";
    public static final String USUARIO = "root";
    public static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Sin Conexion a Base De Datos. ", e);
        }
    }

    public static void main(String[] args) {
        System.out.println("MENU DE ESTUDIANTES");

        try (Connection conn = getConnection(); Scanner sc = new Scanner(System.in)) {
            boolean salir = false;

            while (!salir) {
                mostrarMenu();
                System.out.print("Selecciona una opcion: ");
                String opcion = sc.nextLine().trim();

                switch (opcion) {
                    case "1":
                        insertar(sc, conn);
                        break;
                    case "2":
                        actualizar(sc, conn);
                        break;
                    case "3":
                        eliminar(sc, conn);
                        break;
                    case "4":
                        EstudiantesServices.consultarTodosLosEstudiantes(conn);
                        break;
                    case "5":
                        consultarPorEmail(sc, conn);
                        break;
                    case "6":
                        salir = true;
                        System.out.println("Saliendo.");
                        break;
                    default:
                        System.out.println("Opción no valida. Intenta de nuevo.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error de conexion: " + e.getMessage());
        }
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("============= MENU ==================");
        System.out.println("1. Insertar Estudiante");
        System.out.println("2. Actualizar Estudiante");
        System.out.println("3. Eliminar Estudiante");
        System.out.println("4. Consultar todos los estudiantes");
        System.out.println("5. Consultar Estudiante por email");
        System.out.println("6. Salir");
        System.out.println("=====================================");
    }

    private static void insertar(Scanner sc, Connection conn) {
        try {
            System.out.print("Nombre: ");
            String nombre = sc.nextLine().trim();
            System.out.print("Apellido: ");
            String apellido = sc.nextLine().trim();
            System.out.print("Correo: ");
            String correo = sc.nextLine().trim();
            System.out.print("Edad: ");
            int edad = Integer.parseInt(sc.nextLine().trim());
            String estadoCivil = pedirEstadoCivil(sc);

            EstudiantesServices.insertarEstudiante(conn, nombre, apellido, correo, edad, estadoCivil);

        } catch (NumberFormatException e) {
            System.out.println("La edad debe ser un numero entero.");
        } catch (SQLException e) {
            System.out.println("Error al insertar estudiante: " + e.getMessage());
        }
    }

    private static void actualizar(Scanner sc, Connection conn) {
        try {
            String correo;
            while (true) {
                System.out.print("Correo del estudiante que desea actualizar: ");
                correo = sc.nextLine().trim();

                if (existeCorreo(conn, correo)) {
                    break;
                } else {
                    System.out.println("No existe un estudiante con ese correo, Intenta de nuevo.");
                }
            }

            System.out.print("Nuevo nombre: ");
            String nombre = sc.nextLine().trim();
            System.out.print("Nuevo apellido: ");
            String apellido = sc.nextLine().trim();
            System.out.print("Nueva edad: ");
            int edad = Integer.parseInt(sc.nextLine().trim());
            String estadoCivil = pedirEstadoCivil(sc);

            EstudiantesServices.actualizarEstudiante(conn, correo, nombre, apellido, edad, estadoCivil);

        } catch (NumberFormatException e) {
            System.out.println("La edad debe ser un numero entero.");
        } catch (SQLException e) {
            System.out.println("Error al actualizar estudiante: " + e.getMessage());
        }
    }

    private static void eliminar(Scanner sc, Connection conn) {
        try {
            String correo;
            while (true) {
                System.out.print("Correo del estudiante que desea eliminar: ");
                correo = sc.nextLine().trim();

                if (existeCorreo(conn, correo)) {
                    break;
                } else {
                    System.out.println("No existe un estudiante con ese correo, Intenta de nuevo.");
                }
            }

            EstudiantesServices.eliminarEstudiante(conn, correo);

        } catch (SQLException e) {
            System.out.println("Error al eliminar estudiante: " + e.getMessage());
        }
    }

    private static void consultarPorEmail(Scanner sc, Connection conn) {
        try {
            String correo;
            while (true) {
                System.out.print("Correo del estudiante que desea buscar: ");
                correo = sc.nextLine().trim();

                if (existeCorreo(conn, correo)) {
                    EstudiantesServices.consultarEstudiantePorCorreo(conn, correo);
                    break;
                } else {
                    System.out.println("No existe un estudiante con ese correo, Intenta de nuevo.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar: " + e.getMessage());
        }
    }

    private static boolean existeCorreo(Connection conn, String correo) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM estudiantes WHERE correo = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, correo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
        }
        return false;
    }

    private static String pedirEstadoCivil(Scanner sc) {
        while (true) {
            System.out.print("Estado Civil (SOLTERO, CASADO, VIUDO, UNION_LIBRE, DIVORCIADO): ");
            String estado = sc.nextLine().trim().toUpperCase();
            switch (estado) {
                case "SOLTERO":
                case "CASADO":
                case "VIUDO":
                case "UNION_LIBRE":
                case "DIVORCIADO":
                    return estado;
                default:
                    System.out.println("Estado invalido, Seleccione un estado valido.");
            }
        }
    }
}
