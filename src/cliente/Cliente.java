/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Scanner;
import javax.imageio.IIOException;

/**
 *
 * @author laura
 */
public class Cliente {

    /**
     * @param args the command line arguments
     */
    private static final int TIMEOUT = 3000;
    private static final int INTENTOS = 5;

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);

        //Se solicita la dirección
        InetAddress address = InetAddress.getByName("127.0.0.1");

        System.out.print("Nombre del archivo que quieres: ");
        String nombre = in.next();

        byte[] archivo = (nombre).getBytes();

        //Aqui se solicita el puerto
        int port = 7171;

        DatagramSocket socketCliente = new DatagramSocket();

        socketCliente.setSoTimeout(TIMEOUT);

        DatagramPacket enviar = new DatagramPacket(archivo, archivo.length, address, port);
        DatagramPacket recibir = new DatagramPacket(new byte[258], 258);

        int tries = 0;
        boolean respuestaRecibido = false;
        String mensaje = "";

        // Se envia el archivo solicitado
        socketCliente.send(enviar);

        // Orden del primer paquete
        byte id = (byte) 1;

        byte orden = (byte) 1;

        // En un ciclo
        do {
            try {
                // recibe el orden
                socketCliente.receive(recibir);

                if (!recibir.getAddress().equals(address)) {
                    throw new IIOException("No se supo de quien se recibio");
                }

                // obtiene el byte del orden
                orden = recibir.getData()[0];

                if (orden == id) {

                    // recibe el mensaje
                    socketCliente.receive(recibir);

                    if (!recibir.getAddress().equals(address)) {
                        throw new IIOException("No se supo de quien se recibio");
                    }

                    // añade el mensaje al total
                    mensaje += new String(recibir.getData(), recibir.getOffset(), recibir.getLength());

                    // incrementa el orden
                    id++;

                    // si recibe como orden 0, significa que ya no hay mas paquetes por recibir
                } else if (recibir.getData()[0] == 0) {

                    respuestaRecibido = true;

                } else {
                    throw new InterruptedIOException();
                }

            } catch (InterruptedIOException e) {
                tries++;
                System.out.println("Intentos " + (INTENTOS - tries));
            }
            // si todavia hay mas paquetes y mas intentos, se continua recibiendo paquetes
        } while (((orden != 0) && (tries < INTENTOS)));

        // si se recibieron los paquetes
        if (respuestaRecibido) {
            
            System.out.println("Contenido: " + mensaje);
            
            //Creación del archivo
            
            String ruta = "archivos\\Document" + (int)Math.floor(Math.random() * 100) + ".txt";
            File archivoRecibido = new File(ruta);

            if (archivoRecibido.exists() == false) {
                archivoRecibido.createNewFile();
            }

            FileWriter escribir = new FileWriter(archivoRecibido);
            BufferedWriter buffer = new BufferedWriter(escribir);
            buffer.write(mensaje);
            buffer.close();
            
        } else {
            System.out.println("No responde");
        }

        socketCliente.close();
    }

}
