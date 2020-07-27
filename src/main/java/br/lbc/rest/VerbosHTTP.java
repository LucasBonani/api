package br.lbc.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import io.restassured.http.ContentType;

public class VerbosHTTP {
	
	@Test
	public void deveSalvarUsuario() {
		given()
			.log()
			.all()
			.contentType("application/json")
			.body("{\"name\": \"Lucas\",\"age\": 32}")
		.when()
			.post("http://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", is(notNullValue()))
			.body("name", is("Lucas"))
			.body("age", is(32))
		;
	}
	
	@Test
	public void naoSalvarUsuarioSemNome() {
		given()
			.log()
			.all()
//			.contentType("application/json")
			.contentType(ContentType.JSON)
			.body("{\"age\": 32}")
		.when()
			.post("http://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(400)
			.body("id", is(nullValue()))
			.body("error" , is("Name é um atributo obrigatório"))
		;
	}
	
	@Test
	public void salvarUsuarioViaXML() {
		given()
			.log()
			.all()
//			.contentType("application/xml")
//			mais indicado um enum
			.contentType(ContentType.XML)
			.body("<user><name>Lucas Bonanno</name><age>32</age></user>")
		.when()
			.post("http://restapi.wcaquino.me/usersXML")
		.then()
			.log().all()
			.statusCode(201)
			.body("user.@id", is(notNullValue()))
			.body("user.name", is("Lucas Bonanno"))
			.body("user.age", is("32"))
		;
	}
	
	
	@Test
	public void salvarUsuarioViaXMLObj() {
		User user = new User("Usuario XML", 40);
		given()
			.log().all()
			.contentType(ContentType.XML)
			.body(user)
		.when()
			.post("http://restapi.wcaquino.me/usersXML")
		.then()
			.log().all()
			.statusCode(201)
			.body("user.@id", is(notNullValue()))
			.body("user.name", is("Usuario XML"))
			.body("user.age", is("40"))
		;
	}
	
	@Test
	public void salvarDeserializarXMLAoSalarUsuario() {
		User user = new User("Usuario XML", 40);
			
		User usuarioInserido = given()
			.log().all()
			.contentType(ContentType.XML)
			.body(user)
		.when()
			.post("http://restapi.wcaquino.me/usersXML")
		.then()
			.log().all()
			.statusCode(201)
			.extract().body().as(User.class)
		;
		
		Assert.assertThat(usuarioInserido.getId(), notNullValue());
		Assert.assertEquals("Usuario XML", usuarioInserido.getName());
		Assert.assertThat(usuarioInserido.getAge(), is(40));
		Assert.assertThat(usuarioInserido.getId(), notNullValue());
	}
	
	@Test
	public void deveAlterarrUsuario() {
		given()
			.log()
			.all()
			.contentType("application/json")
			.body("{\"name\": \"Lucas\",\"age\": 32}")
		.when()
			.put("http://restapi.wcaquino.me/users/1")
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Lucas"))
			.body("age", is(32))
		;
	}
	
	@Test
	public void deveCustomizarURL() {
		given()
			.log()
			.all()
			.contentType("application/json")
			.body("{\"name\": \"Lucas\",\"age\": 32}")
		.when()
			.put("http://restapi.wcaquino.me/{entidade}/{userId}" , "users" , "1")
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Lucas"))
			.body("age", is(32))
		;
	}
	
	@Test
	public void deveCustomizarURL2() {
		given()
			.log()
			.all()
			.contentType("application/json")
			.body("{\"name\": \"Lucas\",\"age\": 32}")
			.pathParam("entidade", "users")
			.pathParam("userId", 1)
		.when()
			.put("http://restapi.wcaquino.me/{entidade}/{userId}")
		.then()
			.log().all()
			.statusCode(200)
			.body("id", is(1))
			.body("name", is("Lucas"))
			.body("age", is(32))
		;
	}
	
	@Test
	public void deveDeletarUsuario() {
		given()
			.log().all()
		.when()
			.delete("http://restapi.wcaquino.me/users/1")
		.then()
			.log().all()
			.statusCode(204)
		;
	}
	
	@Test
	public void naoDeveDeletarUsuarioInexistente() {
		given()
			.log().all()
		.when()
			.delete("http://restapi.wcaquino.me/users/1000")
		.then()
			.log().all()
			.statusCode(400)
			.body("error", is("Registro inexistente"))
		;
	}
	
	@Test
	public void deveSalvarUsuarioMap() {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("name", "Lucas");
		paramsMap.put("age", 32);
		
		given()
			.log().all()
			.contentType("application/json")
			.body(paramsMap)
		.when()
			.post("http://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", is(notNullValue()))
			.body("name", is("Lucas"))
			.body("age", is(32))
		;
		
	}
	
	@Test
	public void deveSalvarUsuarioObj() {
		User user = new User("Lucas", 32);
		
		given()
			.log().all()
			.contentType("application/json")
			.body(user)
		.when()
			.post("http://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.body("id", is(notNullValue()))
			.body("name", is("Lucas"))
			.body("age", is(32))
		;
		
	}
	
	@Test
	public void deveDeserializarSalvarUsuarioObj() {
		User user = new User("Lucas", 32);
		
		User usuarioInserido = given()
			.log().all()
			.contentType("application/json")
			.body(user)
		.when()
			.post("http://restapi.wcaquino.me/users")
		.then()
			.log().all()
			.statusCode(201)
			.extract().body().as(User.class)
		;
		
		Assert.assertEquals("Lucas", usuarioInserido.getName());
		Assert.assertThat(usuarioInserido.getAge(), is(32));
		Assert.assertThat(usuarioInserido.getId(), notNullValue());
	}
}



















