package tcparquivo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {

    // função que remove a numeração e o traço
    public static String[] extrairQuestoes(String[] questoesNumeradas) {
        String[] questoes = new String[questoesNumeradas.length];

        for (int i = 0; i < questoesNumeradas.length; i++) {
            String[] partes = questoesNumeradas[i].split("-", 2); // Divide em duas partes no primeiro traço encontrado
            if (partes.length == 2) {
                questoes[i] = partes[1].trim(); // Pega a segunda parte e remove espaços em branco
            }
        }
        return questoes;
    }

    public static void main(String[] args) throws IOException {
        //inicializando a thread do servidor 
        new Thread(new Server()).start();
    }

    static class Server implements Runnable {

        @Override
        public void run() {
            try ( ServerSocket servidor = new ServerSocket(12345)) {
                System.out.println("Porta 12345 aberta!");
                while (true) {
                    Socket cliente = servidor.accept();
                    //enquanto o socket do servidor estiver disponível, uma thread para as requisições do cliente é iniciada.
                    new Thread(new RequestHandler(cliente)).start();
                    System.out.println("Nova conexão com o cliente " + cliente.getInetAddress().getHostAddress());
                }

            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    static class RequestHandler implements Runnable {

        private final Socket clienteSocket;

        public RequestHandler(Socket clientSocket) {
            this.clienteSocket = clientSocket;
        }

        public void run() {
            try {
                // cria um arquivo recebido.txt que é um clone da prova que o cliente enviou
                File arquivoServidor = new File("recebido.txt");

                // cria um vetor contendo tudo que tem no gabarito, ele é criado a partir do caminho do gabarito
                String path1 = ".\\gabarito.txt";
                String[] vetorGabarito = ManipuladorArquivo.leitor(path1);

                try ( OutputStream output = new FileOutputStream(arquivoServidor);  InputStream input = clienteSocket.getInputStream()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;

                    // Continue lendo e gravando até não haver mais dados disponíveis
                    while ((bytesRead = input.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }

                    // cria um vetor contendo tudo que tem no recebido, ele é criado a partir do caminho do recebido
                    String path2 = ".\\recebido.txt";
                    String[] vetorRes = ManipuladorArquivo.leitor(path2);

                    // utiliza a função extrairQuestoes para deixar somente as reposta no vetor, sem a numeração e sem o traço
                    String[] respostaProva = extrairQuestoes(vetorRes);
                    String[] respostaGabarito = extrairQuestoes(vetorGabarito);

                    // printa o gabarito e a prova repectivamente
                    System.out.println(" ------- Comparação Gabarito/Prova -------");
                    for (int i = 1; i < respostaProva.length; i++) {
                        System.out.println(i + ": " + respostaGabarito[i] + " - " + respostaProva[i]);
                    }
                    System.out.println("------------------------------------------");

                    // variáveis de para contabilizar os erros e acertos
                    int certas = 0;
                    int erradas = 0;

                    // realiza a comparação de cada questão e incrementa as variáveis
                    for (int i = 1; i < respostaGabarito.length; i++) {
                        for (int j = 0; j < respostaGabarito[i].length(); j++) {
                            if (respostaGabarito[i].charAt(j) == respostaProva[i].charAt(j)) {
                                certas++;
                            } else {
                                erradas++;
                            }
                        }
                    }

                    System.out.println("\u001B[32;40mAcertos: " + certas + "\n\u001B[31;40mErros: " + erradas);

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    clienteSocket.close();
                }
            } catch (IOException erro) {
                erro.printStackTrace();
            }

        }

    }
}
