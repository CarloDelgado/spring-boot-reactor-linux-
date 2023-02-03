package com.bolsadeideas.springboot.reactor.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.bolsadeideas.springboot.reactor.app.models.Usuario;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner {	
	
	private static final Logger log = LoggerFactory.getLogger(SpringBootReactorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Flux <String> nombres = Flux.just("martha marallano", "carlo delgado", "mila salas", "joffre hermosilla", "allison salas", "bruce lee","bruce willis", "johao delgado");
		
	    Flux <Usuario> usuarios = nombres.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
		.filter(usuario -> usuario.getNombre().toLowerCase().equalsIgnoreCase("bruce"))
				.doOnNext(usuario -> {
					if(usuario == null) {
						throw new RuntimeException("nombre no pueden ser vacios");
			}
		System.out.println(usuario.getNombre().concat("").concat(usuario.getApellido()));
					})
		.map(usuario -> {
			String nombre = usuario.getNombre().toLowerCase();
			usuario.setNombre(nombre);
			return usuario;
		});
				
		usuarios.subscribe(e -> log.info(e.toString()),
			error -> log.error(error.getMessage()),
			new Runnable() {

				@Override
				public void run() {
					log.info("ha finalizado la ejecucion delobservable con éxito");
					
				}
			
		});
	}


	
}
