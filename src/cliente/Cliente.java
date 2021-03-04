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
    
    public static void main(String[] args) throws IOException{
        Scanner in=new Scanner(System.in);
        
        //Se solicita la dirección
        InetAddress address = InetAddress.getByName("127.0.0.1");
        
        System.out.print("Nombre del archivo que quieres: ");
        String nombre=in.next();
        
        byte[] archivo = (nombre).getBytes();
        
                
        //Aqui se solicita el puerto
        int port = 7171;
        
        DatagramSocket datagrama = new  DatagramSocket();
        
        datagrama.setSoTimeout(TIMEOUT);
        
        DatagramPacket enviar = new DatagramPacket(archivo, archivo.length, address, port);
        DatagramPacket recibir = new DatagramPacket(new byte[255], 255);
        
        int tries = 0;
        boolean respuestaRecibido = false;
        String mensaje = "";
        do{
            datagrama.send(enviar);
            
            try{
                datagrama.receive(recibir);
                
                byte k = 1;
                if(recibir.getData().equals(k) ) {
                    
                    datagrama.receive(recibir);
                    System.out.println(new String(recibir.getData()));
                    mensaje += new String(recibir.getData());
                    k++;
                }
                
                if(!recibir.getAddress().equals(address)){
                    throw new IIOException("No se supo de quien se recibio");
                }
                respuestaRecibido = true;
            }catch(InterruptedIOException e){
                tries++;
                System.out.println("Intentos " + (INTENTOS - tries));
            }
        }while((!respuestaRecibido)&&(tries < INTENTOS)&& recibir.getData().equals(0));
        
        if(respuestaRecibido){
            System.out.println("Contenido: " + mensaje);
        }else{
            System.out.println("No responde");
        }
        datagrama.close();
    }
    
}
