package br.lbc.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class TesteCurso {

	@Test
	public void devoValidarBody() {
		given()
		.when()
			.get("http://restapi.wcaquino.me/ola")
		.then()
			.statusCode(200)
			.body(is("Ola Mundo!"))
			.body(is(not(nullValue())));
	}

	@Test
	public void verificarPrimeiroNivelJson() {
		Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/users/1");
		System.out.println(response.asString());

		// path
		Assert.assertEquals(Integer.valueOf(1), response.path("id"));
		Assert.assertEquals(Integer.valueOf(1), response.path("%s", "id"));

		// Json
		JsonPath jpath = new JsonPath(response.asString());
		Assert.assertEquals(1, jpath.getInt("id"));

		// from
		int id = JsonPath.from(response.asString()).getInt("id");
		Assert.assertEquals(1, id);

	}

	@Test
	public void verificarSegundoNivelJson() {
		given().when().get("https://restapi.wcaquino.me/users/2").then().statusCode(200)
				.body("name", containsString("Joaquina")).body("endereco.rua", is("Rua dos bobos"));
	}

	@Test
	public void verificarListaJson() {
		given().when().get("https://restapi.wcaquino.me/users/3").then().statusCode(200)
				.body("name", containsString("Ana")).body("filhos", hasSize(2)).body("filhos[0].name", is("Zezinho"))
				.body("filhos[1].name", is("Luizinho")).body("filhos.name", hasItems("Zezinho", "Luizinho"));
	}
	
	@Test
	public void verificarMensagemErro() {
//		Response res = RestAssured.get("https://restapi.wcaquino.me/users/4");
//		res.getStatusCode();
//		if(res.equals("404")) {
//			
//		}
//		String body = res.getBody().asString();
//		if(body.contains("error")) {
//			res.prettyPrint();
//		}
		given()
		.when()
			.get("https://restapi.wcaquino.me/users/4")
		.then()
			.statusCode(404)
			.body("error", is("Usuário inexistente"))
		;
	}
	
	@Test
	public void verificarListaRaiz() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/users")
		.then()
			.statusCode(200)
			.body("$", hasSize(3))//verifica quantidade de itens 
			.body("name", hasItems("João da Silva", "Maria Joaquina","Ana Júlia"))//verifica nome dos itens
			.body("age[1]", is(25))//verifica se segundo item
			//verificando itens do arrayList dentro de outro arrayList
			.body("filhos.name", hasItems(Arrays.asList("Zezinho" , "Luizinho")))
			//contains verifica itens na ordem em que são esperadas
			.body("salary", contains(1234.5678f, 2500, null))
		;
	}
	
	@Test
	public void verificacoesAvancadasLista() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/users")
		.then()
			.statusCode(200)
			//age = parâmetro
			//findAll = metodo 
			//it = objeto será a instancia da idade na busca onde sejam <= 25
			.body("age.findAll{it <= 25}.size()", is(2))
			.body("age.findAll{it <= 25 && it > 20}.size()", is(1))
			.body("findAll{it.age <= 25 && it.age > 20}.name", hasItem("Maria Joaquina"))
		;
	}
	
	@Test
	public void unirJsonPathComJava() {
		ArrayList<String> nomes =
		given()
		.when()
			.get("https://restapi.wcaquino.me/users")
		.then()
			.statusCode(200)
			.extract().path("name.findAll{it.startsWith('Maria')}")
		;
		
		Assert.assertEquals(1, nomes.size());
		Assert.assertTrue(nomes.get(0).equalsIgnoreCase("mARia JoAQuiNa"));
		Assert.assertEquals(nomes.get(0).toUpperCase(), "MARIA JOAQUINA");
	}
}













