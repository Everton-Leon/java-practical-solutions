package tcparquivo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ManipuladorArquivo {

    // funçao que lê o arquivo a partir de seu caminho
    public static String[] leitor(String path) throws IOException {
        BufferedReader buffRead = new BufferedReader(new FileReader(path));
        String vetor[] = new String[51];
        int cont = 0;
        String linha = "";
        while (true) {
            if (linha != null) {
                vetor[cont] = linha;
                cont += 1;

            } else {
                break;
            }
            linha = buffRead.readLine();
        }
        buffRead.close();
        return vetor;
    }
}
