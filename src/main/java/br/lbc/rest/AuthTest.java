package br.lbc.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.restassured.http.ContentType;

public class AuthTest {

	@Test
	public void deveAcessarSWAPI() {
		given()
			.log().all()
		.when()
			.get("https://swapi.dev/api/people/1")
		.then()
			.log().all()
			.statusCode(200)
			.body("name", is("Luke Skywalker"))
		;
	}
	
	@Test
	public void deveAcessarAPIComChave() {
		given()
			.log().all()
			.queryParam("q", "London")
			.queryParam("appid", "c72a0f747e595eccf77f0837fa0fc2ba")
			.queryParam("unit", "metric")
		.when()
			.get("https://api.openweathermap.org/data/2.5/weather")
		.then()
			.log().all()
			.statusCode(200)
			.body("name", is("London"))
		;
	}
	
	@Test
	public void deveAcessarAPISemSenha() {
		given()
			.log().all()
		.when()
			.get("http://restapi.wcaquino.me/basicauth")
		.then()
			.log().all()
			.statusCode(401)
		;
	}
	
	@Test
	public void deveAcessarAPIComSenha() {
		given()
			.log().all()
		.when()
			.get("http://admin:senha@restapi.wcaquino.me/basicauth")
		.then()
			.log().all()
			.statusCode(200)
			.body("status", is("logado"))
		;
	}
	
	@Test
	public void deveAcessarAPIComSenha2() {
		given()
			.log().all()
			.auth().basic("admin" ,"senha")
		.when()
			.get("http://restapi.wcaquino.me/basicauth")
		.then()
			.log().all()
			.statusCode(200)
			.body("status", is("logado"))
		;
	}
	
	@Test
	public void deveAcessarAPIComSenhaChallenge() {
		given()
			.log().all()
			.auth().preemptive().basic("admin" ,"senha")
		.when()
			.get("http://restapi.wcaquino.me/basicauth2")
		.then()
			.log().all()
			.statusCode(200)
			.body("status", is("logado"))
		;
	}
	
	@Test
	public void deveFazerAutenticacaoComTokenJWT() {
		Map<String, String> login = new HashMap<String, String>();
		login.put("email", "wagner@aquino");
		login.put("senha", "123456");
		String token = given()
			.log().all()
			.body(login)
			.contentType(ContentType.JSON)
		.when()
			.post("http://barrigarest.wcaquino.me/signin")
		.then()
			.log().all()
			.statusCode(200)
			.extract().path("token")
		;
		
		//obter as contas
		
		given()
			.log().all()
			.header("Authorization", "JWT " + token) //envia token
		.when()
			.get("http://barrigarest.wcaquino.me/contas")
		.then()
			.log().all()
			.statusCode(200)
				.body("nome", hasItem("Conta para saldo"))
			;
	}
	
	@Test
	public void deveAcessarAplicaoWeb() {
		 String cookie = given()
		.log().all()
		.formParam("email", "wagner@aquino")		
		.formParam("senha", "123456")
		.contentType(ContentType.URLENC.withCharset("UTF-8"))
	.when()
		.post("http://seubarriga.wcaquino.me/logar")
	.then()
		.log().all()
		.statusCode(200)
		.extract().header("set-cookie")
		;
		
		cookie = cookie.split("=")[1].split(";")[0];
		System.out.println("AQUI ESTÁ O COOKIE: " + cookie);
		
		//obert conta
		
		given()
			.log().all()
			.cookie("connect.sid", cookie) // é necessário sempre enviar requisições
		.when()
			.get("http://seubarriga.wcaquino.me/contas")
		.then()
			.log().all()
			.statusCode(200)
			.body("html.body.table.tbody.tr[0].td[0]", is("conta 33749358105000"))
			.extract().body().asString();
		;
	}
	
	
//	@Test
//	public void deveAcessarAplicaoWeb() {
//		given()
//			.log().all()
//			.formParam("email", "wagner@aquino")		
//			.formParam("senha", "123456")
//			.contentType(ContentType.URLENC.withCharset("UTF-8"))
//		.when()
//		.post("http://seubarriga.wcaquino.me/logar")
//		.then()
//			.log().all()
//		;
//	}
	
}
