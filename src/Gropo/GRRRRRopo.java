package Gropo;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;


public class GRRRRRopo {
	public static void main(String[] args) throws IOException, InterruptedException {
		URI uri = URI.create(args[0]);
		String bericht = "email=" + args[1] +
				"&password=" + args[2];

		CookieHandler.setDefault(new CookieManager());
		HttpClient client = HttpClient.newBuilder()
				.version(HttpClient.Version.HTTP_1_1)
				.cookieHandler(CookieHandler.getDefault())
				.connectTimeout(Duration.ofSeconds(30))
				.followRedirects(HttpClient.Redirect.NEVER)
				.build();
		HttpRequest request = Functies.postMaar(
				uri,
				bericht);

		client.send(
				request,
				HttpResponse.BodyHandlers.ofString())
			.body();

		String uitk = client.send(
				Functies.getMaar(URI.create("https://lets.gropo.nl/booking")),
				HttpResponse.BodyHandlers.ofString()
		).body();

		System.out.println(uitk);


	}

}
