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
        
        //Se solicita la dirección
        InetAddress address = InetAddress.getByName("127.0.0.1");
        
        //Aqui es donde vamos a mandar el archivo
        byte[] archivo = ("0001"+"archivo").getBytes();
        
        //Aqui se solicita el puerto
        int port = 7171;
        
        DatagramSocket datagrama = new  DatagramSocket();
        
        datagrama.setSoTimeout(TIMEOUT);
        
        DatagramPacket enviar = new DatagramPacket(archivo, archivo.length, address, port);
        DatagramPacket recibir = new DatagramPacket(new byte[9], 9);
        
        int tries = 0;
        boolean respuestaRecibido = false;
        do{
            datagrama.send(enviar);
            try{
                datagrama.receive(recibir);
                
                if(!recibir.getAddress().equals(address)){
                    throw new IIOException("No se supo de quien se recibio");
                }
                respuestaRecibido = true;
            }catch(InterruptedIOException e){
                tries++;
                System.out.println("Intentos " + (INTENTOS - tries));
            }
        }while((!respuestaRecibido)&&(tries < INTENTOS));
        
        if(respuestaRecibido){
            System.out.println("Recibido " + new String(recibir.getData()));
        }else{
            System.out.println("No responde");
        }
        datagrama.close();
    }
    
}
