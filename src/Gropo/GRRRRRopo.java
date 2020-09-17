package Gropo;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GRRRRRopo {
	public static void main(String[] args) throws IOException, InterruptedException {
		String gebruiker = Gebruiker.gebruiker;
		String wachtwoord = Gebruiker.wachtwoord;
		String bericht = "email=" + gebruiker +
				"&password=" + wachtwoord;
		URI uri = URI.create("https://lets.gropo.nl/auth/login");

		HttpClient client = HttpClient.newBuilder()
				.version(HttpClient.Version.HTTP_1_1)
				.build();
		HttpRequest request = Bericht.postBericht(
				uri,
				bericht);

		String uitk = client.send(
				request,
				HttpResponse.BodyHandlers.ofString())
			.body();
		System.out.println(uitk);
	}

}
