/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Innan
 */
public class TCPServernome {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    //Variavel global para contagem de servidores na Lista
    public static int cont;

    //Inicialização do Servidor
    public static void main(String[] args) throws IOException {
        System.out.println("Iniciando servidor.");
        //Criando socket para receber conexão
        ServerSocket server = new ServerSocket(2525);
        //Lista para armazenar os IP's dos Servidores
        ArrayList<String> lista = new ArrayList<>();

        System.out.println("Aguardando conexão.");

        cont = 0;
        while (true) {
            try {
                //Aguarda conexão do cliente com o servidor
                Socket socket = server.accept();
                System.out.println("Conexão estabelecida ip:[" + socket.getInetAddress().getHostAddress() + "]");
                //Permite conexão de mais de um cliente ao servidor simultaneamente
                multiThread thread = new multiThread(socket, lista, cont);
                thread.start();

                //Encerra o servidor quando ocorre algum erro de conexão
            } catch (Exception except) {
                System.out.println("Encerrando servidor.");
                server.close();
                break;
            }
        }
    }
}

class multiThread extends Thread {

    private ArrayList<String> lista;
    private Socket socket;
    private InputStream input;
    private OutputStream output;
    private BufferedReader in;
    private PrintStream out;

    //Inicilização das trhreads para conexões multi-cliente
    public multiThread(Socket socket, ArrayList lista, int cont) throws IOException {

        this.lista = lista;
        this.socket = socket;
        input = socket.getInputStream();
        output = socket.getOutputStream();
        in = new BufferedReader(new InputStreamReader(input));
        out = new PrintStream(output);

    }

    @Override
    public void run() {

        try {
            while (true) {
                //Recebe a mensagem enviada pelo cliente/servidor e armazena em "mensagem"
                String mensagem = in.readLine().trim();

                //Verifica se é servidor. Armazena o IP na ArrayList
                if (mensagem.contains("1.SERVIDOR")) {
                    /*Verifica se o IP do servidor já está na Lista
                    Caso esteja, não faz nada.*/
                    if (lista.contains(socket.getInetAddress().getHostAddress())) {
                        break;
                    }
                    out.println(socket.getInetAddress().getHostAddress());
                    lista.add(socket.getInetAddress().getHostAddress());
                    System.out.println("Servidor salvo!");
                    break;
                } /*Verifica se é Cliente. Envia o IP de um servidor para conexão,
                de forma sequencial. O primeiro servidor a entrar na lista, será
                o primeiro a receber cliente, e assim por diante.*/ else if (mensagem.contains("2.CLIENTE")) {

                    if (TCPServernome.cont == lista.size()) {
                        TCPServernome.cont = 0;
                    }
                    out.println(lista.get(TCPServernome.cont));
                    System.out.println("Cliente Conectado!");
                    TCPServernome.cont++;
                    break;
                }
            }
            System.out.println("Encerrando conexão");
            System.out.println("Cliente [" + socket.getInetAddress().getHostName() + "] finalizado. ");
            //Encerra conexão com o cliente/servidor
            in.close();
            out.close();
            socket.close();
        } //Caso ocorra algum erro, exibe mensagem abaixo
        catch (Exception except) {
            System.out.println("FATAL ERROR, FAVOR SEARCH NO GOOGLE.");
        }
    }
}
