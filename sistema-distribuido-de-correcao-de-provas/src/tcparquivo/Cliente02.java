package tcparquivo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Cliente02 {

    public static void main(String[] args) throws UnknownHostException, IOException {
        Socket cliente = new Socket("127.0.0.1", 12345);

        System.out.println("O cliente se conectou ao servidor!");

        // cria um arquivo prova.txt para enviar para o servidor
        File arquivoCliente = new File("prova.txt");
        arquivoCliente.createNewFile();

        // escolhe de forma aleatória as alternativas da prova e as escreve no arquivo
        String[] alternativas = new String[]{"V", "F"};
        try {
            FileWriter writer = new FileWriter(arquivoCliente);
            for (int i = 1; i <= 50; i++) {
                Random random = new Random();
                writer.write(String.valueOf(i) + "-");
                for (int j = 0; j < 5; j++) {
                    writer.write(alternativas[random.nextInt(2)]);
                }
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try ( PrintStream saida = new PrintStream(cliente.getOutputStream());  BufferedReader reader = new BufferedReader(new FileReader(arquivoCliente))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                saida.println(linha);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cliente.close();
        }
    }
}
