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

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://stackoverflow.com/questions/3393751/what-does-asynchronous-means-in-ajax"))
				.build();
		String uitk = client.send(
				request,
				HttpResponse.BodyHandlers.ofString())
			.body();
		System.out.println(uitk);
		System.out.println(gebruiker);


	}
}
