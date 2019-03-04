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
import static tcpserver.TCPServer.fibo;

/**
 *
 * @author Innan
 */
public class TCPServer {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    //Inicialização do Servidor
    public static void main(String[] args) throws IOException {
        System.out.println("Iniciando servidor.");
        //Criando socket para receber conexão
        ServerSocket server = new ServerSocket(2525);

        //Cria socket para se conectar com o servidor de nomes
        Socket socket2 = new Socket("192.168.43.202", 2525);
        InputStream input = socket2.getInputStream();
        OutputStream output = socket2.getOutputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        PrintStream out = new PrintStream(output);

        //Informa ao Srv de nomes que é SERVIDOR
        out.println("1.SERVIDOR");
        //Recebe Confirmação
        String mensagem = in.readLine();
        System.out.println(mensagem);
        //Fecha socket com o Srv de nomes
        socket2.close();

        System.out.println("Aguardando conexão.");
        while (true) {
            try {
                //Aguarda conexão do cliente com o servidor
                Socket socket = server.accept();
                System.out.println("Conexão estabelecida ip:[" + socket.getInetAddress().getHostName() + "]");
                //Permite conexão de mais de um cliente ao servidor simultaneamente
                multiThread thread = new multiThread(socket);
                thread.start();

                //Encerra o servidor quando ocorre algum erro de conexão
            } catch (Exception except) {
                System.out.println("Encerrando servidor.");
                server.close();
                break;
            }
        }
    }

    //Função fibonacci
    public static int fibo(int n) {
        if (n < 2) {
            return n;
        } else {
            return fibo(n - 1) + fibo(n - 2);
        }
    }

}

class multiThread extends Thread {

    private Socket socket;
    private InputStream input;
    private OutputStream output;
    private BufferedReader in;
    private PrintStream out;

    //Inicilização das trhreads para conexões multi-cliente
    public multiThread(Socket socket) throws IOException {
        this.socket = socket;
        input = socket.getInputStream();
        output = socket.getOutputStream();

        in = new BufferedReader(new InputStreamReader(input));
        out = new PrintStream(output);

    }

    @Override
    public void run() {

        try {
            //Mantém conexão com o cliente até ser digitado "FIM"
            while (true) {
                //Recebe a mensagem enviada pelo cliente e armazena em "mensagem"
                String mensagem = in.readLine().trim();

                System.out.println("Msg recebida do cliente [" + socket.getInetAddress().getHostName() + "]: " + mensagem);
                //Verifica o que foi digitado, caso seja "FIM" encerra conexão do cliente
                if ("FIM".equals(mensagem.toUpperCase())) {
                    break;
                }
                //Retorna o resultado da função fibonacci
                out.println(String.valueOf(fibo(Integer.valueOf(mensagem))));
                System.out.println("Msg enviada ao cliente [" + socket.getInetAddress().getHostName() + "]: " + fibo(Integer.valueOf(mensagem)));
            }

            System.out.println("Encerrando conexão");
            System.out.println("Cliente [" + socket.getInetAddress().getHostName() + "] finalizado. ");
            //Encerra conexão com o cliente
            in.close();
            out.close();
            socket.close();
        } //Caso a mensagem digitada não seja um numero ou "FIM" exibe uma mensagem de erro
        catch (Exception except) {
            System.out.println("FATAL ERROR, FAVOR SEARCH NO GOOGLE.");
        }
    }
}
