package Gropo;

import org.w3c.dom.Node;
import java.io.*;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.time.Duration;
import java.util.Date;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;

public class GRRRRRopo {
	public static void main(String[] args) throws IOException, InterruptedException, TransformerException, DatumNietGevondenException, OnjuisteParametersException {
		// Invoerparameters interpreteren
		if(args.length != 6)
			throw new OnjuisteParametersException(
					"Gebruik: host gebruiker wachtwoord datum t1 t2"
			);
		String host = args[0];
		String bericht = "email=" + args[1] +
				"&password=" + args[2];
		String datum = args[3];
		Date t1, t2;
		try {
			t1 = Functies.parseTijd(datum, args[4]);
			t2 = Functies.parseTijd(datum, args[5]);
		} catch (ParseException e) {
			throw new OnjuisteParametersException("Tijd graag in HH:mm");
		}

		// Connectie en cookies
		CookieHandler.setDefault(new CookieManager());
		HttpClient client = HttpClient.newBuilder()
				.version(HttpClient.Version.HTTP_1_1)
				.cookieHandler(CookieHandler.getDefault())
				.connectTimeout(Duration.ofSeconds(30))
				.followRedirects(HttpClient.Redirect.NEVER)
				.build();
		HttpRequest request = Functies.postMaar(
				URI.create(host + "/auth/login"),
				bericht);
		client.send(
				request,
				HttpResponse.BodyHandlers.ofString())
			.body();


		while(true){
			HttpResponse<InputStream> missatge = client.send(
					Functies.getMaar(URI.create(host + "/booking")),
					HttpResponse.BodyHandlers.ofInputStream());

			// Resultaat interpreteren
			DOMResult document = Functies.document(missatge.body());
			Node events = Functies.vindNode(
					document.getNode(),
					"div",
					"id",
					"events" +"-" + datum);
			if(events == null)
				throw new DatumNietGevondenException("Datum niet beschikbaar: " + datum);
			events = Functies.vindNode(
					events,
					"div",
					"class",
					"hide-on-med-and-up");
			events = Functies.vindNode(
					events,
					"ul",
					"class",
					"collection list");
			if(events.getChildNodes().getLength() == 0)
				throw new DatumNietGevondenException("Datum niet beschikbaar: " + datum);

			for (int i = 0; i < events.getChildNodes().getLength(); i++) {  // itereren over de <li> elementen, waar events de <ul> is
				Node li = events.getChildNodes().item(i);
				if (li.getLocalName() != null &&
						!li.getLocalName().equals("li")) continue;
				Node span = Functies.vindNode(
						li,
						"span",
						"class",
						"title");

				Date starttijd = null;
				try {
					starttijd = Functies.parseTijd(datum,
							span.getFirstChild()
									.getNodeValue()
									.trim().split(" ")[0]);
				} catch (ParseException | NullPointerException e) {
					e.printStackTrace();
				}
				boolean beschikbaar =
						!span.getLastChild().getNodeValue().trim().equals("Vol");
				if (Functies.tijdTussen(starttijd, t1, t2) && beschikbaar) {
					String href = Functies.vindNode(
							li, "a")
							.getAttributes()
							.getNamedItem("href")
							.getNodeValue();
					href = href + "/course?ref=booking";

					System.out.println("Boekt op: " + starttijd);
					request = Functies.postMaar(
							URI.create(href),
							"user_product_id=31826&accept_terms=1");
					String uitk =
							client.send(
									request,
									HttpResponse.BodyHandlers.ofString())
									.body();
					System.exit(0);
				}
			}
			Thread.sleep(10 * 60 * 60 * 1000); // 10 min
			System.out.println("Nog een poging");
		}
	}
}
