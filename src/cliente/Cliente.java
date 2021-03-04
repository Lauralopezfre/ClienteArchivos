/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

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
        DatagramPacket recibir = new DatagramPacket(new byte[255], 255);

        int tries = 0;
        boolean respuestaRecibido = false;
        String mensaje = "";
        do {
            socketCliente.send(enviar);

            try {
                socketCliente.receive(recibir);

                byte id = 1;
                byte[] ks = recibir.getData();

                if (ks[0] == id) {
                    for (int i = 0; ks[0] == id; i++) {
                        socketCliente.receive(recibir);
                        System.out.println(new String(recibir.getData()));
                        mensaje += new String(recibir.getData());
                        id++;
                    }
                } else {
                    mensaje += new String(recibir.getData());
                }

                if (!recibir.getAddress().equals(address)) {
                    throw new IIOException("No se supo de quien se recibio");
                }
                socketCliente.receive(recibir);

                //if (recibir.getData()[0] == 0) {
                respuestaRecibido = true;
                //}

            } catch (InterruptedIOException e) {
                tries++;
                System.out.println("Intentos " + (INTENTOS - tries));
            }
        } while (((!respuestaRecibido) && (tries < INTENTOS)));
//|| recibir.getData()[0]!=0
        if (respuestaRecibido) {
            System.out.println("Contenido: " + mensaje);
        } else {
            System.out.println("No responde");
        }
        socketCliente.close();
    }

}
