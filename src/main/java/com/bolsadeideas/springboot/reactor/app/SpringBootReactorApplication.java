package com.bolsadeideas.springboot.reactor.app;





import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.bolsadeideas.springboot.reactor.app.models.Comentarios;
import com.bolsadeideas.springboot.reactor.app.models.Usuario;
import com.bolsadeideas.springboot.reactor.app.models.UsuarioComentarios;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner {	
	
	private static final Logger log = LoggerFactory.getLogger(SpringBootReactorApplication.class);
	

	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	
		ejemplointervaloinfinito();
	}
	
	public void ejemplointervaloinfinito() throws InterruptedException {
		
		CountDownLatch latch = new CountDownLatch(1);
		
		Flux.interval(Duration.ofSeconds(1))
		//*.doOnTerminate(() -> latch.countDown()) primera forma
		.doOnTerminate(latch::countDown)//*segunda forma la forma estatica
		.flatMap(i ->{ 
			if (i >= 10) {
				return Flux.error(new InterruptedException("solo hasta 10!"));
			}
			return Flux.just(i);
		})	
		    .map(i ->"hola "+i)
		    .retry(2)
		    //* .doOnNext(s -> log.info(s)) para no imprimir dos veces en la consola
		    .subscribe(s -> log.info(s), e -> log.error(e.getMessage()));
		
			latch.await();
	}
	
	public void ejemploDelayElements() { //*throws InterruptedException//
	
		Flux<Integer> rango = Flux.range(1, 12)
		.delayElements(Duration.ofSeconds(1))
		.doOnNext(i -> log.info(i.toString()));
		 rango.blockLast();
		//* rango.subscribe();
		 //*Thread.sleep(12000);	
	} 
	public void ejemploInterval() {
		Flux<Integer> rango = Flux.range(1, 12);
		Flux<Long> retraso = Flux.interval(Duration.ofSeconds(1));
		rango.zipWith(retraso, (ra, re) -> ra)
		.doOnNext(i -> log.info(i.toString()))
		.blockLast();//bloquea los hilos que trabajan en paralelo para que se pueda ejecutar el retraso
	
	} 
	
	// convinamos varios flujos 
	public void ejemploZipwitfhRangos(){
		Flux<Integer> rangos = Flux.range(0, 4);
		Flux.just(1,2 ,3 ,4)
		.map(i -> (i*2))
		.zipWith(rangos,(uno, dos) -> String.format("Primer Flux: %d , Segundo Flux: %d", uno, dos))
		//*.zipWith(Flux.range(0, 4),(uno, dos) -> String.format("Primer Flux: %d , Segundo Flux: %d", uno, dos)) forma distinta para hacerlo 
		.subscribe(texto -> log.info(texto));
	}
	//*FORMA 1 Zipwitfh DESPLEGANDO Y ELIGIENDO LA OPCION UNO
	public void ejemploUsuarioComentariosZipwitfhForma2(){
		Mono<Usuario> usuarioMono = Mono.fromCallable(()-> new Usuario("jhon","doe"));
			//*return new Usuario("jhon","doe");  otra forma es solo colocar*//
		Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable(()->{
			Comentarios comentarios = new Comentarios();
			comentarios.addComentarios("hola carlo que tal!");
			comentarios.addComentarios("mañana voy a la playa!");
			comentarios.addComentarios("estoy tomando el curso de spring boot con reactor!");
			return comentarios;
		});
		  Mono<UsuarioComentarios> ususarioConComentarios = usuarioMono
		.zipWith(comentariosUsuarioMono)
		.map(tuple ->{
			Usuario u= tuple.getT1();
			Comentarios c = tuple.getT2();
			return new UsuarioComentarios(u,c);
		});
		  ususarioConComentarios.subscribe(uc ->log.info(uc.toString()));
}
	
	//*FORMA 1 Zipwitfh DESPLEGANDO Y ELIGIENDO LA OPCION DOS
	public void ejemploUsuarioComentariosZipwitfh(){
		Mono<Usuario> usuarioMono = Mono.fromCallable(()-> new Usuario("jhon","doe"));
			//*return new Usuario("jhon","doe");  otra forma es solo colocar*//
		Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable(()->{
			Comentarios comentarios = new Comentarios();
			comentarios.addComentarios("hola carlo que tal!");
			comentarios.addComentarios("mañana voy a la playa!");
			comentarios.addComentarios("estoy tomando el curso de spring boot con reactor!");
			return comentarios;
		});
		  Mono<UsuarioComentarios> ususarioConComentarios = usuarioMono
				  .zipWith(comentariosUsuarioMono,(usuario, comentariosUsuario) -> new UsuarioComentarios(usuario, comentariosUsuario));
		  ususarioConComentarios.subscribe(uc ->log.info(uc.toString()));
}
	
	
	 // 1 forma 
	//*public Usuario crearUsuario() {
	//*	return new Usuario("jhon","doe"); }
	
	public void ejemploUsuarioComentariosFlatMap(){
		Mono<Usuario> usuarioMono = Mono.fromCallable(()-> new Usuario("jhon","doe"));
			//*return new Usuario("jhon","doe");  otra forma es solo colocar*//
		Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable(()->{
			Comentarios comentarios = new Comentarios();
			comentarios.addComentarios("hola carlo que tal!");
			comentarios.addComentarios("mañana voy a la playa!");
			comentarios.addComentarios("estoy tomando el curso de spring boot con reactor!");
			return comentarios;
		});
		usuarioMono.flatMap(u -> comentariosUsuarioMono.map(c -> new UsuarioComentarios(u,c)))
		.subscribe(uc ->log.info(uc.toString()));
}
	
	
public void ejemploCollectList() throws Exception {
		
		List<Usuario> usuariosList = new ArrayList<>();//*queremos crear un flujo apartir de una lista tipo colecction 
		usuariosList.add(new Usuario("martha", "marallano"));
		usuariosList.add(new Usuario("carlo", "delgado"));
		usuariosList.add(new Usuario("mila", "salas"));
		usuariosList.add(new Usuario("joffre", "hermosilla"));
		usuariosList.add(new Usuario("allison", "salas"));
		usuariosList.add(new Usuario("bruce", "lee"));
		usuariosList.add(new Usuario("bruce", "willis"));
		usuariosList.add(new Usuario("johao", "delgado"));
		//*fromIterable combierte en un String reactivo
		 Flux.fromIterable(usuariosList)
		 .collectList() //* listar los datos un una sola linea 
		 .subscribe( lista -> {
			 lista.forEach(item -> log.info(item.toString()));
					 
		 });
		 //.subscribe( lista -> log.info(lista.toString()));
		 //*.subscribe( usuario -> log.info(usuario.toString())); una forma de listar 
}
	
	
public void ejemploToString() throws Exception {
		
		List<Usuario> usuariosList = new ArrayList<>();//*queremos crear un flujo apartir de una lista tipo colecction 
		usuariosList.add(new Usuario("martha", "marallano"));
		usuariosList.add(new Usuario("carlo", "delgado"));
		usuariosList.add(new Usuario("mila", "salas"));
		usuariosList.add(new Usuario("joffre", "hermosilla"));
		usuariosList.add(new Usuario("allison", "salas"));
		usuariosList.add(new Usuario("bruce", "lee"));
		usuariosList.add(new Usuario("bruce", "willis"));
		usuariosList.add(new Usuario("johao", "delgado"));
		//*fromIterable combierte en un String reactivo
		 Flux.fromIterable(usuariosList)//*flatmap  combierte  a otro flujo mono o flux 
		
	     .map(usuario -> usuario.getNombre().toUpperCase().concat(" ").concat(usuario.getApellido().toUpperCase()))
		 
	     .flatMap(nombre -> {
			if(nombre.contains("bruce".toUpperCase())) {
				return Mono.just(nombre); 
			}
			else {
				return Mono.empty();
			}
		})
		
		.map(nombre -> {
			//*String Usuario = nombre.toLowerCase();
			//*usuario.setNombre(nombre);
			return nombre.toLowerCase();
		})
		 .subscribe(u ->log.info(u.toString()));
	}
	
public void ejemploFlatMap() throws Exception {
		
		List<String> usuariosList = new ArrayList<>();//*queremos crear un flujo apartir de una lista tipo colecction 
		usuariosList.add("martha marallano");
		usuariosList.add("carlo delgado");
		usuariosList.add("mila salas");
		usuariosList.add("joffre hermosilla");
		usuariosList.add("allison salas");
		usuariosList.add("bruce lee");
		usuariosList.add("bruce willis");
		usuariosList.add("johao delgado");
		//*fromIterable combierte en un String reactivo
		 Flux.fromIterable(usuariosList)//*flatmap  combierte  a otro flujo mono o flux 
		
	     .map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
		 
	     .flatMap(usuario -> {
			if(usuario.getNombre().equalsIgnoreCase("bruce")) {
				return Mono.just(usuario); 
			}
			else {
				return Mono.empty();
			}
		})
		
		.map(usuario -> {
			String nombre = usuario.getNombre().toLowerCase();
			usuario.setNombre(nombre);
			return usuario;
		})
		 .subscribe(u ->log.info(u.toString()));
	}


public void ejemploIterable() throws Exception {
		
		List<String> usuariosList = new ArrayList<>();//*queremos crear un flujo apartir de una lista tipo colecction 
		usuariosList.add("martha marallano");
		usuariosList.add("carlo delgado");
		usuariosList.add("mila salas");
		usuariosList.add("joffre hermosilla");
		usuariosList.add("allison salas");
		usuariosList.add("bruce lee");
		usuariosList.add("bruce willis");
		usuariosList.add("johao delgado");
		//*fromIterable combierte en un String reactivo
		Flux <String> nombres = Flux.fromIterable(usuariosList);//*Flux.just("martha marallano", "carlo delgado", "mila salas", "joffre hermosilla", "allison salas", "bruce lee","bruce willis", "johao delgado");*//
		
	    Flux <Usuario> usuarios = nombres.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
		.filter(usuario -> usuario.getNombre().toLowerCase().equalsIgnoreCase("bruce"))
				.doOnNext(usuario -> {
					if(usuario == null) {
						throw new RuntimeException("nombre no pueden ser vacios");
			}
		System.out.println(usuario.getNombre().concat(" ").concat(usuario.getApellido()));
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
