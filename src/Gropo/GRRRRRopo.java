package Gropo;

import org.w3c.dom.Node;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.time.Duration;
import java.util.Date;
import javax.swing.*;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;

public class GRRRRRopo {
	private JRadioButton gropoRadioButton;
	private JRadioButton bjoeksRadioButton;
	private JPanel paneel;
	private JTextField tfGebruiker;
	private JTextField tfDatum;
	private JTextField tfT1;
	private JTextField tfT2;
	private JPasswordField pfWachtwoord;
	private JButton boekMaarButton;
	private JTextArea tekstveld;

	public GRRRRRopo() {
		JFrame frame = new JFrame("GRRRRRopo");
		frame.setContentPane(paneel);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

		gropoRadioButton.setSelected(false);
		bjoeksRadioButton.setSelected(false);
		tekstveld.setText("");
		tfGebruiker.setText("");
		pfWachtwoord.setText("");
		tfDatum.setText("");
		tfT1.setText("");
		tfT2.setText("");


		boekMaarButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				String gebruiker = tfGebruiker.getText();
				String wachtwoord = new String(pfWachtwoord.getPassword());
				String datum = tfDatum.getText();
				String t1String = tfT1.getText();
				String t2String = tfT2.getText();

				boolean gegevensIngevuld =
						(gropoRadioButton.isSelected() ^ bjoeksRadioButton.isSelected()) &&
						!"".equals(gebruiker) &&
						!"".equals(wachtwoord) &&
						!"".equals(datum) &&
						!"".equals(t1String) &&
						!"".equals(t2String);
				if(!gegevensIngevuld){
					tekstveld.setText("Vul gegevens in");
					return;
				}
				tekstveld.setText("");

				String host = gropoRadioButton.isSelected() ? "https://lets.gropo.nl" : "https://www.mybjoeks.nl";
				Date t1, t2;
				try {
					t1 = Functies.parseTijd(datum, t1String);
					t2 = Functies.parseTijd(datum, t2String);
				} catch (ParseException parseException) {
					tekstveld.setText("Datum in YYYY-mm-dd; tijd in HH:mm");
					return;
				}

//				 Connectie en cookies
				CookieHandler.setDefault(new CookieManager());
				HttpClient client = HttpClient.newBuilder()
						.version(HttpClient.Version.HTTP_1_1)
						.cookieHandler(CookieHandler.getDefault())
						.connectTimeout(Duration.ofSeconds(30))
						.followRedirects(HttpClient.Redirect.NEVER)
					.build();
				HttpRequest request = Functies.postMaar(
						URI.create(host + "/auth/login"),
						"email=" + gebruiker + "&password=" + wachtwoord);

				String verbindingAntwoord = null;
				try {
					verbindingAntwoord = client.send(
							request,
							HttpResponse.BodyHandlers.ofString()).body();
				} catch(IOException | InterruptedException e) {
					tekstveld.setText("Verbindingsfout");
					return;
				}
				if(verbindingAntwoord.contains("error")){
					tekstveld.setText("Onjuiste gebruikersnaam / wachtwoord");
					return;
				}

				javax.swing.Timer tijd = new javax.swing.Timer(10 * 60 * 1000, null);   // 10 min
				tijd.setInitialDelay(0);
				tijd.setRepeats(true);
				tijd.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						boolean geboekt = false;
						try {
							geboekt = boek(client, host, datum, t1, t2);
						} catch (Exception e) {
							tekstveld.setText(e.getMessage());
						}
						if(geboekt) return;
					}
				});
				tijd.start();

			}
		});
	}

	private boolean boek(HttpClient client, String host, String datum, Date t1, Date t2) throws Exception{
		HttpResponse<InputStream> missatge = null;
		try {
			missatge = client.send(
					Functies.getMaar(URI.create(host + "/booking")),
					HttpResponse.BodyHandlers.ofInputStream());
		} catch (IOException | InterruptedException e) {
			throw new Exception("Verbindingsfout");
		}

		// Resultaat interpreteren
		DOMResult document = null;
		try {
			document = Functies.document(missatge.body());
		} catch (TransformerException | IOException e) {
			throw new Exception("Kan site niet interpreteren");
		}
		Node events = Functies.vindNode(
				document.getNode(),
				"div",
				"id",
				"events" + "-" + datum);
		if (events == null) {
			throw new Exception("Datum niet beschikbaar");
		}
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
		if (events.getChildNodes().getLength() == 0) {
			tekstveld.setText("Datum niet beschikbaar");
		}

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
				throw new Exception("Fout bij lezen site");
			}
			boolean beschikbaar =
					span.getLastChild().getNodeValue() != null &&
							span.getLastChild().getNodeValue().contains("over");

			if (Functies.tijdTussen(starttijd, t1, t2) && beschikbaar) {
				String href = Functies.vindNode(
						li, "a")
						.getAttributes()
						.getNamedItem("href")
						.getNodeValue();

				// Het boekingsschermpje ophalen om cookies bij te werken:
				HttpRequest request = Functies.getMaar(URI.create(href));
				try {
					missatge = client.send(request,
							HttpResponse.BodyHandlers.ofInputStream());
				} catch (IOException | InterruptedException e) {
					throw new Exception("Verbindingsfout");
				}
				try {
					document = Functies.document(missatge.body());
				} catch (TransformerException | IOException e) {
					throw new Exception("Kan site niet interpreteren");
				}
				String token = Functies.vindNode(
						document.getNode(),
						"input",
						"name",
						"_token")
						.getAttributes().getNamedItem("value").getNodeValue();

				// De daadwerkelijke boeking:
				tekstveld.setText("Boekt op: " + starttijd);

				href = href + "/course?ref=booking";
				request = Functies.postMaar(
						URI.create(href),
						"_token=" + token + "&accept_terms=1" +
								"&_token=" + token);

				try {
					client.send(
							request,
							HttpResponse.BodyHandlers.ofString())
							.body();
				} catch (IOException | InterruptedException e) {
					throw new Exception("Verbindingsfout");
				}
				return(true);
			}
		}
		tekstveld.setText(
				tekstveld.getText() +
						(new Date()) + " geen ruimte gevonden\n");
		return(false);
	}

	public static void main(String[] args){
		new GRRRRRopo();
	}
}
