package sur.softsurena.passwordgenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PasswordGenerator implements Runnable {
//    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final String CHARACTERS = "ABCDEF0123456789";
    private static SecureRandom random = new SecureRandom();
    private static PasswordGenerator obj;
    
    private static ArrayList<String> diccionarioList = new ArrayList<String>();
    private static ArrayList<String> duplicados = new ArrayList<String>();

    @Override
    public void run() {
        addClave(linea);
    }

    public static void main(String[] args) {
//        int length = 8; // Longitud de cada contraseña
//
//        int count = 20000000; // Cantidad de contraseñas a generar
//
//        Conexion.getInstance(
//                "sysdba",
//                "Seguridad43210",
//                "/home/jhironsel/NetBeansProjects/PasswordGenerator/claveDB.fdb",
//                "localhost",
//                "3050");
//
//        Conexion.verificar();

        String diccionario = "/opt/ArchivosExternos/diccionario.txt";
        String duplicados = "/opt/ArchivosExternos/duplicados.txt";
        
//        escribirArchivo(fileName2, leerArchivo(fileName));
        cargarDicionario(diccionario);
        System.out.println("Diccionario size: "+diccionarioList.size());
    }
    private static void cargarDicionario(String nombreArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            
            StringBuilder contenido = new StringBuilder(br.readLine());
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    private static String leerArchivo(String nombreArchivo) {
        StringBuilder contenido = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                contenido.append(linea);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contenido.toString();
    }
    
    private static String leerArchivo3(String nombreArchivo) {
        HashSet<String> pass = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!pass.contains(linea)) {
                    pass.add(linea);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pass.toString();
    }

    private static void escribirArchivo(String nombreArchivo, String contenido) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo))) {
            bw.write(contenido);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static long registro = 0;
    private static String linea = "";

    private static void leerArchivo2(String nombreArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {

            while ((linea = br.readLine()) != null) {
                if (registro > 207792) {
                    obj = new PasswordGenerator();
                    Thread thread = new Thread(obj);
                    thread.start();
                }
                registro++;
            }
        } catch (Exception e) {
        }
    }

    private synchronized static void addClave(String clave) {
        final String INSERT
                = "INSERT INTO T_PASSWORD (CLAVE) VALUES (?);";

        try (PreparedStatement ps = Conexion.getCnn().prepareStatement(INSERT)) {
            ps.setString(1, clave);

            ps.executeUpdate();
        } catch (SQLException ex) {
        }
    }

    

    

    private static void generatePasswords(int length, int count, String fileName) {
        HashSet<String> pass = new HashSet<String>();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

            for (int i = 0; i < count; i++) {
                String password = "48575443" + generatePassword(length);
                pass.add(password);
            }

            pass.stream().forEach(clave -> {
                try {
                    writer.write(clave);
                    writer.newLine();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            System.out.println(count + " contraseñas generadas y guardadas en " + fileName);
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

    private static String generatePassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }

}

class Conexion {

    //Variables Privadas
    private static final Logger LOG = Logger.getLogger(Conexion.class.getName());
    private static Connection cnn;
    private static String user, clave;
    private static StringBuilder urlDB;
    private static final String PROTOCOLO_FIREBIRD = "jdbc:firebirdsql://";
    private static final String VALIDACIONES_DEL_SISTEMA = "Validaciones del sistema";

    //Variables Publicas
//    public static Alert alerta;
    public static final String NO_ES_POSIBLE_CONECTARSE_AL_SERVIDOR = "No es posible conectarse al servidor: ";
    public static final String USUARIO_NO_IDENTIFICADO = "Usuario no identificado";
    public static final String LIBRERIA_DEL_DRIVER_NO_ENCONTRADA = "Libreria no encontrada";
    public static final String USUARIO_LOGEADO = "Usuario logeado";

    public static Connection getCnn() {
        return cnn;
    }

    public synchronized static void setCnn(Connection cnn) {
        Conexion.cnn = cnn;
    }

    /**
     * Unico Metodo que permite obtener una instancia de la clase Conexión. La
     * cual requeire de los siguientes parametros de entrada.
     *
     * @param user Es el usuario registrado en el sistema.
     * @param clave Clave de acceso del usuario.
     * @param pathBaseDatos Ruta de acceso hacia la Base de Datos.
     * @param dominio Direccion ip o local de la base de datos.
     * @param puerto Puerto utilizado para la conexion de la base de datos.
     *
     * @return Devuelve una instancia de la clase conexion. La cual inicializa
     * las variables para la conexion a la base de datos.
     */
    public static Conexion getInstance(String user, String clave,
            String pathBaseDatos, String dominio, String puerto) {

        Conexion.user = user;
        Conexion.clave = clave;

        StringBuilder p = new StringBuilder("");

        if (!puerto.isBlank()) {
            p.append(":").append(puerto);
        }

        urlDB = new StringBuilder();
        urlDB.append(PROTOCOLO_FIREBIRD)
                .append(dominio)
                .append(p)
                .append("/")
                .append(pathBaseDatos);

        return ConexionHolder.INSTANCE;
    }

    private static class ConexionHolder {

        private static final Conexion INSTANCE = new Conexion();
    }

    private Conexion() {
    }

    /**
     * Metodo que permite a los usuarios del sistema validar si estan
     * debidamente Loggeado,
     *
     * @return Retorna true si esta dentro o false si tuvo problema en la
     * conexion.
     */
    public static boolean verificar() {
        final Properties properties = new Properties();
        //Objecto Properties necesario para la base de datos. 
        properties.setProperty("user", user);
        properties.setProperty("password", clave);
        properties.setProperty("charSet", "UTF8");
        try {
            setCnn(DriverManager.getConnection(urlDB.toString(), properties));
            return true;
        } catch (SQLException ex) {
            if (ex.getMessage().contains("password")) {
                LOG.log(Level.INFO, USUARIO_NO_IDENTIFICADO);
            }
            if (ex.getMessage().contains("Unable to complete network request to host")) {

                StringBuilder mensaje = new StringBuilder();

                mensaje.append(NO_ES_POSIBLE_CONECTARSE_AL_SERVIDOR)
                        .append(urlDB);
                LOG.log(Level.INFO, mensaje.toString(), ex);
            }
            ex.printStackTrace();
            return false;
        }
    }
}
