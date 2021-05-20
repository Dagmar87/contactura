package com.contactura.contactura.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.contactura.contactura.model.ContacturaUser;
import com.contactura.contactura.repository.ContacturaUserRepository;

@RestController
@RequestMapping({"/user"})
public class ContacturaUserController {
	
	@Autowired
	private ContacturaUserRepository repository;
	
	@RequestMapping("/login")
	@GetMapping
	public ResponseEntity<?> login(HttpServletRequest request){
		
		//Random random = new Random();
		//int token = random.nextInt(999999999);			
		String token = request.getHeader("Authorization")
				.substring("Basic".length()).trim();
		//return () -> new String(Base64.getDecoder())
		//		.decode(token)).split(":")[0];
		return ResponseEntity.ok().body("Usuario Logado com Sucesso\nToken:" + token);
				
	}
	
	//Lista todos os usuarios
	@GetMapping
	public List findAll(){
		
		return repository.findAll();
		
	}
	
	//Retora Usuario por id
	@GetMapping(value = "{id}")
	public ResponseEntity<?> findById(@PathVariable long id){
		
		return repository.findById(id)
				.map(user -> ResponseEntity.ok().body(user))
				.orElse(ResponseEntity.notFound().build());			
		
	}
	
	//Criar novo Usuario
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ContacturaUser create(@RequestBody ContacturaUser user){
		
		user.setPassword(criptografarSenha(user.getPassword()));
		return repository.save(user);
		
	}
	
	//Atualizar Usuario
	@PutMapping(value = "{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> update(@PathVariable long id, @RequestBody ContacturaUser user){
		
		return repository.findById(id)
				.map(record -> {
					record.setName(user.getName());
					record.setUsername(user.getUsername());
					record.setPassword(criptografarSenha(user.getPassword()));
					record.setAdmin(user.isAdmin());
					ContacturaUser update = repository.save(record);
					return ResponseEntity.ok().body(update);
				}).orElse(ResponseEntity.notFound().build());
		
	}
	
	//Deletar Usuario
	@DeleteMapping(path = "{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> delete(@PathVariable long id){
		
		return repository.findById(id)
				.map(record -> {
					repository.deleteById(id);
					return ResponseEntity.ok().body("Deletado com Sucesso");
				}).orElse(ResponseEntity.notFound().build());
		
	}
	
	public String criptografarSenha(String password){
		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String passwordParaCriptografar = passwordEncoder.encode(password);
		return passwordParaCriptografar;
		
	}

}
