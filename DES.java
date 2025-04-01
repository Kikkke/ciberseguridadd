/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pkg03des;

/**
 *
 * @author Alumno
 */
// es para definir 
import java.io.*;
// es para el calculo de las subllaves 
import java.security.*;
// es para definir el algoritmo del cifrado
import javax.crypto.*;

// para el algoritmo
import javax.crypto.interfaces.*;
//para definir el tamaño de la clave y subclaves
import javax.crypto.spec.*;




public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        /* vamos a crear un programa que lea el archivo en texto planto, debe de introducir
        una clave y cifrarlo generando un archivo correspondiente con el cifrado /*
        
        */
        if (args.length !=1){
            mensajeAyuda();
            System.exit(1);
        }
        System.out.println("1.- Generar las claves DES");
        KeyGenerator generadorDES = 
                KeyGenerator.getInstance("DES");
        System.out.println("");
        
        generadorDES.init(56);
        // tenemos dos opciones para crear la clave, crearla de forma manual o utilizamos secretKey :)
        // si es de forma manual se ingresa por parte del usuario
        // se valida el tamaño de 8 caracteres
        // transforma la clave en bits 
        SecretKey clave = generadorDES.generateKey();        
        System.out.println("La clave es" + clave.getEncoded());
        /* el tipo de cifrado es DES, es de tipo simetrico
        significa que la clave del cifrado es la misma para 
        descifrar, hay que definir el modo de operación del cifrado:
        Flujo o es por bloque 
        */
        Cipher cifrador = Cipher.getInstance("DES/ECB/PKCS5Padding");
        // vamos a crear el menu para cifrar y descifraar 
        System.out.println("2_ Cifrar un fichero con DES: "
        + args [0] + "Dejamos el resultado en " + args [0]+ ".cifrado");
        
        cifrador.init(Cipher.ENCRYPT_MODE, clave);
        
        // aqui es 
        
        byte[] buffer = new byte[1000];
        byte[] buffercifrado;
        
        // definir el archivo
        FileInputStream entrada = new FileInputStream(args[0]);
        FileOutputStream salida = new FileOutputStream(args[0] +".cifrado");
        
        int bytesleidos = entrada.read(buffer,0,100);

        while (bytesleidos != -1){
            buffercifrado = cifrador.update(buffer, 0, 1000);
            salida.write(buffercifrado);
            bytesleidos = entrada.read(buffer,0,bytesleidos);
        }
        
        //construir cifrado
        buffercifrado = cifrador.doFinal();
        // generar el buffer cifrado 
        salida.write(buffercifrado);
        
        entrada.close();
        salida.close();
        
        //descifrar todooo
        
         System.out.println("3_ Descifrar un fichero con DES: "
        + args [0] + "Dejamos el resultado en " + args [0]+ ".cifrado");
        
        cifrador.init(Cipher.DECRYPT_MODE, clave);
        
        // aqui es 
        
        byte[] bufferdescifrado;
        
        // definir el archivo
        entrada = new FileInputStream(args[0]+".cifrado");
        salida = new FileOutputStream(args[0] +".descifrado");
        
        bytesleidos = entrada.read(buffer,0,bytesleidos);

        while (bytesleidos != -1){
            bufferdescifrado = cifrador.update(
            buffer,0,1000);
            salida.write(bufferdescifrado);
            bytesleidos = entrada.read(buffer,0,bytesleidos);
        }
        
        //construir cifrado
        bufferdescifrado = cifrador.doFinal();
        // generar el buffer cifrado 
        salida.write(bufferdescifrado);
        
        entrada.close();
        salida.close();
        
  
    }
    private static void mensajeAyuda(){
        System.out.println("Ejemplo de un programa que sirve para cifrar y descifrar con DES");
        System.out.println("Favor de ingresar un archivo de texto plano, sino, no funciona o sea .txt");

    }
    
    private static void mostrarBytes (byte [] buffer){
        System.out.write(buffer,0,buffer.length);
    }
}







    