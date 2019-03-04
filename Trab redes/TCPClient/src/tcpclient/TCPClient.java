/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpclient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author Innan
 */
public class TCPClient {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Iniciando cliente.");
        System.out.println("Digite o ip do servidor:");

        //Inicia o socket para conexão com o servidor de nomes
        Socket socket1 = new Socket(scanner.nextLine(), 2525);
        System.out.println("Conexão estabelecida.");

        //Recebimento e envio de mensagem na rede
        InputStream input1 = socket1.getInputStream();
        OutputStream output1 = socket1.getOutputStream();
        BufferedReader in1 = new BufferedReader(new InputStreamReader(input1));
        PrintStream out1 = new PrintStream(output1);

        //Informa ao servidor de nomes que é CLIENTE, e recebe o IP de um SERVIDOR
        out1.println("2.CLIENTE");
        String mensagem2 = in1.readLine();
        System.out.println(mensagem2);
        //Encerra conexão com Srv de nomes
        socket1.close();
        //Inicia conexão com o SERVIDOR informado pelo Srv de nomes
        Socket socket = new Socket(mensagem2, 2525);

        //Recebimento e envio de mensagem na rede
        InputStream input = socket.getInputStream();
        OutputStream output = socket.getOutputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        PrintStream out = new PrintStream(output);

        while (true) {

            System.out.println("Digite um numero: ");
            String mensagem = scanner.nextLine();

            //Envia msg ao servidor
            out.println(mensagem);
            if ("FIM".equals(mensagem)) {
                break;
            }
            //Recebe mensagem do servidor
            mensagem = in.readLine();
            System.out.println("Mensagem recebida do servidor: " + mensagem);
        }

        System.out.println("Encerrando conexão");
        //Encerra conexão, fecha o socket
        in.close();
        out.close();
        socket.close();
    }

}
