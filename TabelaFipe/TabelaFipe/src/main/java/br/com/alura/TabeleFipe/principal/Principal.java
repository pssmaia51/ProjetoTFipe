package br.com.alura.TabeleFipe.principal;

import br.com.alura.TabeleFipe.model.Dados;
import br.com.alura.TabeleFipe.model.Modelos;
import br.com.alura.TabeleFipe.model.Veiculo;
import br.com.alura.TabeleFipe.service.ConsumoApi;
import br.com.alura.TabeleFipe.service.ConveteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();

    private ConveteDados conversor = new ConveteDados();

    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";
    public void exibeMenu(){

        var menu = """
                ==== OPÇÕES ===
                Carro
                Moto
                Caminhao
                Sair
                Digite umas das opções para consultar:                             
                """;
        System.out.println(menu);
        var opcao = leitura.nextLine();
        String endereco;

        switch (opcao.toLowerCase()) {
            case "carro":
                endereco = URL_BASE + "carros/marcas";
                break;
            case "moto":
                endereco = URL_BASE + "motos/marcas";
                break;
            case "caminhao":
                endereco = URL_BASE + "caminhao/marcas";
                break;
            case "sair":
                System.exit(0);
                return;
            default:
                System.out.println("Opção inválida. Tente novamente.");
                exibeMenu();
                return;
        }

        var json = consumo.obterDados(endereco);
        System.out.println(json);
        var marcas = conversor.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("Informe o código da marca para consulta: ");
        var codigoMarca = leitura.nextLine();

        endereco = endereco + "/" + codigoMarca + "/modelos";
        json = consumo.obterDados(endereco);
        var modeloLista = conversor.obterDados(json, Modelos.class);

        System.out.println("\nModelos dessa marca: ");
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\nDigite um trecho do nome do carro a ser buscado");
        var nomeVeiculo = leitura.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\nModelos filtrados");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("Digite código do modelo para buscar os valores de avaliação: ");
        var codigoModelo = leitura.nextLine();

        endereco = endereco + "/" + codigoModelo + "/anos";
        json = consumo.obterDados(endereco);
        List<Dados> anos = conversor.obterLista(json, Dados.class);
        List<Veiculo> veiculos = new ArrayList<>();

        for (int i = 0; i < anos.size(); i++) {
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumo.obterDados(enderecoAnos);
            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("\nVeículos filtrados com avaliações por ano: ");
        veiculos.forEach(System.out::println);
    }
}

