package Gropo;

import java.net.URI;
import java.net.http.HttpRequest;

public class Functies {
	private Functies(){
		throw new IllegalAccessError("Kssssst!");
	}

	public static HttpRequest postMaar(URI uri, String bericht) {
		HttpRequest uitkomst =
				HttpRequest.newBuilder()
						.uri(uri)
						.setHeader("Content-Type", "application/x-www-form-urlencoded")
						.POST(HttpRequest.BodyPublishers.ofString(bericht))
						.build();
		return uitkomst;
	}

	public static HttpRequest getMaar(URI uri){
		HttpRequest uitkomst =
				HttpRequest.newBuilder()
						.uri(uri)
						.build();
		return uitkomst;
	}
}
