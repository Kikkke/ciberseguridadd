
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class DES extends JFrame {
    private JTextArea textArea;
    private JButton btnCargar, btnCifrar, btnDescifrar;
    private JFileChooser fileChooser;
    private File archivoSeleccionado;
    private SecretKey clave;
    private Cipher cifrador;
    private final long MAX_FILE_SIZE = 1024 * 1024; // 1 MB máximo

    public DES() {
        super("Cifrado DES con Validaciones");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLayout(new BorderLayout());

        // Inicializar componentes
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        btnCargar = new JButton("Cargar Archivo");
        btnCifrar = new JButton("Cifrar");
        btnCifrar.setEnabled(false);
        btnDescifrar = new JButton("Descifrar");
        btnDescifrar.setEnabled(false);

        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos de texto", "txt"));

        // Panel de botones
        JPanel panelBotones = new JPanel();
        panelBotones.add(btnCargar);
        panelBotones.add(btnCifrar);
        panelBotones.add(btnDescifrar);

        // Configurar eventos
        btnCargar.addActionListener(e -> cargarArchivo());
        btnCifrar.addActionListener(e -> cifrarArchivo());
        btnDescifrar.addActionListener(e -> descifrarYMostrar());

        // Configurar generador de claves
        try {
            KeyGenerator generadorDES = KeyGenerator.getInstance("DES");
            generadorDES.init(56);
            clave = generadorDES.generateKey();
            cifrador = Cipher.getInstance("DES/ECB/PKCS5Padding");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al inicializar DES: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Agregar componentes al frame
        add(scrollPane, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarArchivo() {
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            archivoSeleccionado = fileChooser.getSelectedFile();
            
            // Validación 1: Tamaño del archivo
            if (archivoSeleccionado.length() > MAX_FILE_SIZE) {
                JOptionPane.showMessageDialog(this, 
                    "El archivo es demasiado grande. Máximo permitido: 1 MB",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (BufferedReader reader = new BufferedReader(new FileReader(archivoSeleccionado))) {
                textArea.setText("");
                String linea;
                boolean caracteresInvalidos = false;
                StringBuilder contenido = new StringBuilder();
                
                while ((linea = reader.readLine()) != null) {
                    // Validación 2: Caracteres especiales
                    if (!esTextoValido(linea)) {
                        caracteresInvalidos = true;
                    }
                    contenido.append(linea).append("\n");
                }
                
                if (caracteresInvalidos) {
                    JOptionPane.showMessageDialog(this,
                        "El archivo contiene caracteres especiales no permitidos.\n" +
                        "Solo se permite texto ASCII estándar (caracteres imprimibles).",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                    textArea.setText("");
                    btnCifrar.setEnabled(false);
                    btnDescifrar.setEnabled(false);
                    return;
                }
                
                textArea.setText(contenido.toString());
                btnCifrar.setEnabled(true);
                btnDescifrar.setEnabled(false);
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error al leer el archivo: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean esTextoValido(String texto) {
        // Permite caracteres ASCII imprimibles (32-126) y saltos de línea
        return texto.chars().allMatch(c -> (c >= 32 && c <= 126) || c == '\n' || c == '\r');
    }

    private void cifrarArchivo() {
        if (archivoSeleccionado == null) return;
        
        // Validación adicional antes de cifrar
        if (textArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El archivo está vacío o no contiene texto válido para cifrar",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        File archivoCifrado = new File(archivoSeleccionado.getAbsolutePath() + ".cifrado");
        
        try {
            cifrador.init(Cipher.ENCRYPT_MODE, clave);
            
            try (FileInputStream entrada = new FileInputStream(archivoSeleccionado);
                 FileOutputStream salida = new FileOutputStream(archivoCifrado)) {
                
                byte[] buffer = new byte[1000];
                int bytesLeidos;
                while ((bytesLeidos = entrada.read(buffer)) != -1) {
                    byte[] bufferCifrado = cifrador.update(buffer, 0, bytesLeidos);
                    salida.write(bufferCifrado);
                }
                byte[] bufferFinal = cifrador.doFinal();
                salida.write(bufferFinal);
            }
            
            JOptionPane.showMessageDialog(this, 
                "Archivo cifrado exitosamente:\n" + archivoCifrado.getAbsolutePath(),
                "Cifrado completado", JOptionPane.INFORMATION_MESSAGE);
            
            btnDescifrar.setEnabled(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al cifrar: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void descifrarYMostrar() {
        File archivoCifrado = new File(archivoSeleccionado.getAbsolutePath() + ".cifrado");
        if (!archivoCifrado.exists()) {
            JOptionPane.showMessageDialog(this, 
                "No se encontró el archivo cifrado correspondiente",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            cifrador.init(Cipher.DECRYPT_MODE, clave);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            try (FileInputStream entrada = new FileInputStream(archivoCifrado)) {
                byte[] buffer = new byte[1000];
                int bytesLeidos;
                while ((bytesLeidos = entrada.read(buffer)) != -1) {
                    byte[] bufferDescifrado = cifrador.update(buffer, 0, bytesLeidos);
                    baos.write(bufferDescifrado);
                }
                byte[] bufferFinal = cifrador.doFinal();
                baos.write(bufferFinal);
            }
            
            String textoDescifrado = baos.toString("UTF-8");
            textArea.setText(textoDescifrado);
            
            JOptionPane.showMessageDialog(this, 
                "Texto descifrado mostrado en pantalla",
                "Descifrado completado", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al descifrar: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DES app = new DES();
            app.setLocationRelativeTo(null); // Centrar la ventana
            app.setVisible(true);
        });
    }
}