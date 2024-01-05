package br.com.tjac.gesis.screenmatch.principal;

import br.com.tjac.gesis.screenmatch.model.DadosEpisodio;
import br.com.tjac.gesis.screenmatch.model.DadosSerie;
import br.com.tjac.gesis.screenmatch.model.DadosTemporada;
import br.com.tjac.gesis.screenmatch.model.Episodio;
import br.com.tjac.gesis.screenmatch.service.ConsumoApi;
import br.com.tjac.gesis.screenmatch.service.ConverteDados;
import org.springframework.boot.autoconfigure.web.format.DateTimeFormatters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner in = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

    public void exibeMenu(){
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = in.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();

		for(int i=1 ; i<=dados.totalTemporadas(); i++){
			json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
			DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
			temporadas.add(dadosTemporada);
		}

		temporadas.forEach(System.out::println);

//        for(int i=0 ; i<dados.totalTemporadas() ; i++){
//            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
//            for(int j=0 ; j<episodiosTemporada.size() ; j++){
//                System.out.println(episodiosTemporada.get(j).titulo());
//            }
//        }

        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

//        System.out.println("\nTop 5 melhores episódios:");
//
//        dadosEpisodios.stream()
//                .filter(e -> !e.avaliacao().equals("N/A"))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .limit(5)
//                .forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d))
                ).collect(Collectors.toList());

        episodios.forEach(System.out::println);

//busca por título
//        System.out.println("Digite o trecho do título do episódio:");
//        var trechoTitulo = in.nextLine();
//        List<Episodio> eps = episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
//                .collect(Collectors.toList());
//        eps.forEach(System.out::println);

//busca por ano
//        System.out.println("A partir de qual ano você deseja buscar?");
//        var ano = in.nextInt();
//        in.nextLine();
//        var dataBusca = LocalDate.of(ano, 1, 1);
//
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//        episodios.stream().filter(e -> e.getDataLancamento()!=null && e.getDataLancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println("Temporada:" + e.getTemporada() +
//                                                    " Episódio:" + e.getNumeroEpisodio() +
//                                                    " Data de lançamento:" + e.getDataLancamento().format(formatador)));

        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));

        System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));

        System.out.println("Média: " + est.getAverage() +
                            "\nMelhor episódio:" + est.getMax() +
                            "\nPior episódio: " + est.getMin() +
                            "\nQuantidade: " + est.getCount());
    }
}
