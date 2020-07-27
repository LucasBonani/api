package br.lbc.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.internal.path.xml.NodeImpl;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class XMLTest {
	
	public static RequestSpecification request;
	public static ResponseSpecification response;
	
	@BeforeClass
	public static void setup() {
		RestAssured.baseURI = "http://restapi.wcaquino.me";
//		RestAssured.port = 443;
//		RestAssured.basePath = "/v2";
		
		RequestSpecBuilder reqBuilder = new RequestSpecBuilder(); //somente um builder
		reqBuilder.log(LogDetail.ALL);// logar tudo 
		request = reqBuilder.build(); // retorna a requisição especificada, no caso "all"
		
		ResponseSpecBuilder resBuilder = new ResponseSpecBuilder();
		resBuilder.expectStatusCode(200);
		response = resBuilder.build();
		
		RestAssured.requestSpecification = request;
		RestAssured.responseSpecification = response;
	}
	
//	@Test
//	public void testeExemplo() {
//		RestAssured.baseURI = "http://restapi.wcaquino.me";
//		RestAssured.port = 80;
//		RestAssured.basePath = "/v2";
//		
//		given()
//		.log().all()
//		.when()
//			.get("/users")
//		.then()
//			.statusCode(200);
//	}
	
	@Test
	public void testaXML() {
//  ---------  nível de request (criar a requisição)
		given()
		.when()
			.get("/usersXML/3")
//  --------  nível de response (depois que recebe a resposta o quer verificar)
		.then()
			.statusCode(200)
			
			//trabalhando com nó
			.rootPath("user")//tira a necessidade de apontar o caminho
			.body("name", is("Ana Julia"))
			//@ referencia ao atributo como quando se monta um Xpath
			.body("@id", is("3"))

		
			.rootPath("user.filhos")//tira a necessidade de apontar o caminho
			.body("name.size()", is(2))
			
			
			.detachRootPath("filhos")//retorna a necessidade de apontar o caminho
			.body("filhos.name[0]", is("Zezinho"))
			.body("filhos.name[1]", is("Luizinho"))
			
			.appendRootPath("filhos")//tira a necessidade de apontar o caminho após "detachRootPath"
			.body("name", hasItem("Luizinho"))
			.body("name", hasItems("Luizinho","Zezinho"))
		;
	}
	
	@Test
	public void pesquisaAvancadaXML() {
		given()
		.when()
			.get("/usersXML")
		.then()
			.statusCode(200)
			.rootPath("users.user")
			.body("size()", is(3))
			.body("findAll{it.age.toInteger() <= 25}.size()", is(2))
			.body("@id", hasItems("1","2","3"))
			.body("salary.find{it != null}.toDouble()", is(1234.5678d))
			.body("name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", is("MARIA JOAQUINA"))
		;
	}
	
	@Test
	public void pesquisaXMLPathcomJava() {
		ArrayList<NodeImpl> nomes =
		given()
		.when()
			.get("/usersXML")
		.then()
			.statusCode(200)
			.extract().path("users.user.name.findAll{it.toString().contains('n')}")
		;
		Assert.assertEquals(2, nomes.size());
		Assert.assertEquals("Maria Joaquina".toUpperCase(), nomes.get(0).toString().toUpperCase());
		Assert.assertTrue("ANA JULIA".equalsIgnoreCase(nomes.get(1).toString()));
	}
	
	@Test
	public void pesquisaAvancadaXpath() {
		given()
		.when()
			.get("/usersXML")
		.then()
			.statusCode(200)
			.body(hasXPath("count(/users/user)", is("3")))
			.body(hasXPath("/users/user[@id= '1']"))
			.body(hasXPath("//user[age > 20 and age < 30]/name", is("Maria Joaquina")))
			.body(hasXPath("//user[age > 20][age < 30]/name", is("Maria Joaquina")))
		;	
	}
}

















