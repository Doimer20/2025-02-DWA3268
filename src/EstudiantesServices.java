import java.sql.*;

public class EstudiantesServices {

    public static void insertarEstudiante(Connection conn, String nombre, String apellido, String correo, int edad, String estadoCivil) throws SQLException {
        String sql = "INSERT INTO estudiantes (nombre, apellido, correo, edad, estado_civil) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, apellido);
            stmt.setString(3, correo);
            stmt.setInt(4, edad);
            stmt.setString(5, estadoCivil);

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Estudiante Agregado correctamente");
            } else {
                System.out.println("Error al Agregar estudiante");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { 
                System.out.println("Error: El correo electronico ya existe");
            } else {
                System.out.println("Error al agregar: " + e.getMessage());
            }
            throw e;
        }
    }

    public static void actualizarEstudiante(Connection conn, String correo, String nombre, String apellido, int edad, String estadoCivil) throws SQLException {
        String sql = "UPDATE estudiantes SET nombre = ?, apellido = ?, edad = ?, estado_civil = ? WHERE correo = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, apellido);
            stmt.setInt(3, edad);
            stmt.setString(4, estadoCivil);
            stmt.setString(5, correo);

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Estudiante actualizado correctamente");
            } else {
                System.out.println("No se encontró estudiante con dicho correo");
            }
        }
    }

    public static void eliminarEstudiante(Connection conn, String correo) throws SQLException {
        String sql = "DELETE FROM estudiantes WHERE correo = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, correo);

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Estudiante eliminado correctamente");
            } else {
                System.out.println("No se encontró estudiante con ese correo");
            }
        }
    }

    public static void consultarTodosLosEstudiantes(Connection conn) throws SQLException {
        String sql = "SELECT * FROM estudiantes ORDER BY id";

        try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("=== TODOS LOS ESTUDIANTES ===");
            System.out.printf("%-5s %-15s %-15s %-25s %-5s %-15s%n",
                    "ID", "NOMBRE", "APELLIDO", "CORREO", "EDAD", "ESTADO CIVIL");
            System.out.println("-".repeat(90));

            boolean hayRegistros = false;
            while (rs.next()) {
                hayRegistros = true;
                System.out.printf("%-5d %-15s %-15s %-25s %-5d %-15s%n",
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("correo"),
                rs.getInt("edad"),
                rs.getString("estado_civil")
                );
            }
            if (!hayRegistros) {
                System.out.println("No hay estudiantes registrados");
            }
            System.out.println("-".repeat(90));
        }
    }

    public static void consultarEstudiantePorCorreo(Connection conn, String correo) throws SQLException {
        String sql = "SELECT * FROM estudiantes WHERE correo = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, correo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("=== ESTUDIANTE ENCONTRADO ===");
                    System.out.println("ID: " + rs.getInt("id"));
                    System.out.println("Nombre: " + rs.getString("nombre"));
                    System.out.println("Apellido: " + rs.getString("apellido"));
                    System.out.println("Correo: " + rs.getString("correo"));
                    System.out.println("Edad: " + rs.getInt("edad"));
                    System.out.println("Estado Civil: " + rs.getString("estado_civil"));
                    System.out.println("============================");
                } else {
                    System.out.println("No se pudo encontrar al estudiante con ese correo.: " + correo);
                }
            }
        }
    }
}
