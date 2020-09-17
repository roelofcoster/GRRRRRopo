package Gropo;

import java.net.URI;
import java.net.http.HttpRequest;

public class Bericht {
	private Bericht(){
		throw new IllegalAccessError("Kssssst!");
	}

	public static HttpRequest postBericht(URI uri, String bericht) {
		HttpRequest uitkomst =
				HttpRequest.newBuilder()
						.uri(uri)
						.setHeader("Content-Type", "application/x-www-form-urlencoded")
						.POST(HttpRequest.BodyPublishers.ofString(bericht))
						.build();
		return uitkomst;
	}
}
