package Gropo;

import org.ccil.cowan.tagsoup.Parser;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.http.HttpRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class Functies {
	private Functies(){
		throw new IllegalAccessError("Kssssst!");
	}

	protected static HttpRequest postMaar(URI uri, String bericht) {
		HttpRequest uitkomst =
				HttpRequest.newBuilder()
						.uri(uri)
						.setHeader("Content-Type", "application/x-www-form-urlencoded")
						.POST(HttpRequest.BodyPublishers.ofString(bericht))
						.build();
		return uitkomst;
	}
	protected static HttpRequest postMaar(URI uri, String bericht, String header, String headerWaarde) {
		HttpRequest uitkomst =
				HttpRequest.newBuilder()
						.uri(uri)
						.setHeader(header, headerWaarde)
						.POST(HttpRequest.BodyPublishers.ofString(bericht))
						.build();
		return uitkomst;
	}
	protected static HttpRequest getMaar(URI uri){
		HttpRequest uitkomst =
				HttpRequest.newBuilder()
						.uri(uri)
						.build();
		return uitkomst;
	}
	protected static DOMResult document(final InputStream bericht) throws TransformerException, IOException {
		Reader reader = new InputStreamReader(bericht);
		Parser parser = new Parser();
		DOMResult uitkomst = new DOMResult();
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(new SAXSource(parser, new InputSource(reader)), uitkomst);
		reader.close();
		return uitkomst;
	}
	protected static Node htmlBody(final DOMResult document){
		return document
				.getNode()  // document zelf
				.getChildNodes()
				.item(0) // <html>
				.getChildNodes()
				.item(1); // <body>; item 0 is <head>
	}
	protected static Node vindNode(final Node node, String nodeNaam){
		Node uitkomst = null;
		int lengte;

		if(node.getLocalName() != null &&
				node.getLocalName().equals(nodeNaam)) return node;
		lengte = node.getChildNodes().getLength();
		if(lengte == 0) return null;
		for(int i = 0; i < lengte; i++){
			uitkomst = vindNode(node.getChildNodes().item(i), nodeNaam);
			if(uitkomst != null) return uitkomst;
		}
		return null;
	}
	protected static Node vindNode(
			final Node node,
			String nodeNaam,
			String attribuutNaam,
			String attribuutWaarde){
		Node uitkomst = null;
		int lengte;

		if(
				node.getLocalName() != null &&
				node.getLocalName().equals(nodeNaam) &&
				node.getAttributes()
						.getNamedItem(attribuutNaam) != null &&
				node.getAttributes()
						.getNamedItem(attribuutNaam)
						.getNodeValue() != null &&
				node.getAttributes()
						.getNamedItem(attribuutNaam)
						.getNodeValue().equals(attribuutWaarde)
		) return node;

		lengte = node.getChildNodes().getLength();
		if(lengte == 0) return null;
		for(int i = 0; i < lengte; i++){
			uitkomst = vindNode(
					node.getChildNodes().item(i),
					nodeNaam,
					attribuutNaam,
					attribuutWaarde);
			if(uitkomst != null) return uitkomst;
		}
		return null;
	}
	protected static Date parseTijd(String datum, String tijd) throws ParseException {
		SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return parser.parse(datum + " " + tijd);
	}
	protected static boolean tijdTussen(Date starttijd, Date t1, Date t2){
		return
			(t1.equals(starttijd) || t1.before(starttijd)) &&
			(t2.equals(starttijd) || t2.after(starttijd));
	}
}
