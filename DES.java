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
    private final long MAX_FILE_SIZE = 1024 * 1024; 

    public DES() {
        super("Cifrado DES");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        
        Color colorFondo = new Color(240, 240, 240);
        Color colorBoton = new Color(70, 130, 180);
        Color colorTextoBoton = Color.WHITE;
        
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelPrincipal.setBackground(colorFondo);
        
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.setBackground(colorFondo);
        
        btnCargar = new JButton("Cargar Archivo");
        btnCifrar = new JButton("Cifrar");
        btnDescifrar = new JButton("Descifrar");
        
        for (JButton boton : new JButton[]{btnCargar, btnCifrar, btnDescifrar}) {
            boton.setBackground(colorBoton);
            boton.setForeground(colorTextoBoton);
            boton.setFocusPainted(false);
            boton.setPreferredSize(new Dimension(120, 30));
            panelBotones.add(boton);
        }
        
        btnCifrar.setEnabled(false);
        btnDescifrar.setEnabled(false);
        
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos de texto", "txt"));
        
        
        btnCargar.addActionListener(e -> cargarArchivo());
        btnCifrar.addActionListener(e -> cifrarArchivo());
        btnDescifrar.addActionListener(e -> descifrarYMostrar());
        
        try {
            KeyGenerator generadorDES = KeyGenerator.getInstance("DES");
            generadorDES.init(56);
            clave = generadorDES.generateKey();
            cifrador = Cipher.getInstance("DES/ECB/PKCS5Padding");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al inicializar DES: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        add(panelPrincipal);
    }

    private void cargarArchivo() {
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            archivoSeleccionado = fileChooser.getSelectedFile();
            
            if (archivoSeleccionado.length() > MAX_FILE_SIZE) {
                JOptionPane.showMessageDialog(this, 
                    "El archivo es demasiado grande. Máximo permitido: 1 MB",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try (BufferedReader reader = new BufferedReader(new FileReader(archivoSeleccionado))) {
                textArea.setText("");
                String linea;
                while ((linea = reader.readLine()) != null) {
                    textArea.append(linea + "\n");
                }
                btnCifrar.setEnabled(true);
                btnDescifrar.setEnabled(false);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error al leer el archivo: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cifrarArchivo() {
        if (archivoSeleccionado == null) return;
        
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
                "Archivo cifrado exitosamente",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            
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
                "No se encontró el archivo cifrado",
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
            
            textArea.setText(baos.toString("UTF-8"));
            JOptionPane.showMessageDialog(this, 
                "Texto descifrado mostrado",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al descifrar: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DES app = new DES();
            app.setVisible(true);
        });
    }
}