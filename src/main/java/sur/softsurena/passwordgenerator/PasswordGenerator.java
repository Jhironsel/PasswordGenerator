package sur.softsurena.passwordgenerator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;

public class PasswordGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static SecureRandom random = new SecureRandom();

    public static void main(String[] args) {
        int length = 13; // Longitud de cada contraseña
        int count = 10000000; // Cantidad de contraseñas a generar
        String fileName = "diccionario"; // Nombre del archivo para guardar las contraseñas

        generatePasswords(length, count, fileName);
    }

    public static void generatePasswords(int length, int count, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 0; i < count; i++) {
                String password = generatePassword(length);
                writer.write(password);
                writer.newLine();
            }
            System.out.println(count + " contraseñas generadas y guardadas en " + fileName);
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

    public static String generatePassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }
}
