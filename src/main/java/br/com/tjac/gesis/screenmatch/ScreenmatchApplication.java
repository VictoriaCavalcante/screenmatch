package br.com.tjac.gesis.screenmatch;

import br.com.tjac.gesis.screenmatch.model.DadosSerie;
import br.com.tjac.gesis.screenmatch.service.ConsumoApi;
import br.com.tjac.gesis.screenmatch.service.ConverteDados;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	public void run(String... args) throws Exception{
		ConsumoApi consumoApi = new ConsumoApi();
		var json = consumoApi.ObterDados("https://www.omdbapi.com/?t=gilmore+girls&apikey=6585022c");
		//System.out.println(json);
		ConverteDados conversor = new ConverteDados();
		DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
		System.out.println(dados);
	}

}
