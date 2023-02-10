package com.bolsadeideas.springboot.reactor.app.models;


//*creamosla clase para juntar las clases Usuario Y Comentarios*// 
public class UsuarioComentarios {
	private Usuario usuario;
	
	private Comentarios comentarios;

	public UsuarioComentarios(Usuario usuario, Comentarios comentarios) {
		
		this.usuario = usuario;
		this.comentarios = comentarios;
	}

	@Override
	public String toString() {
		return "UsuarioComentarios [usuario=" + usuario + ", comentarios=" + comentarios + "]";
	}
	
	

}
