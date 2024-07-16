package com.aluracursos.challenges.literAlura;

import com.aluracursos.challenges.literAlura.principal.Principal;
import com.aluracursos.challenges.literAlura.repository.AutorRepository;
import com.aluracursos.challenges.literAlura.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiterAluraApplication implements CommandLineRunner {

	@Autowired
	private AutorRepository repositorioAutores;
	@Autowired
	private LibroRepository repositorioLibros;

	public static void main(String[] args) {
		SpringApplication.run(LiterAluraApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(repositorioAutores, repositorioLibros);
		principal.muestraMenu();
	}
}
