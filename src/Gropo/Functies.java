package Gropo;

import org.ccil.cowan.tagsoup.Parser;
import org.jetbrains.annotations.NotNull;
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
		return HttpRequest.newBuilder()
						.uri(uri)
						.setHeader("Content-Type", "application/x-www-form-urlencoded")
						.POST(HttpRequest.BodyPublishers.ofString(bericht))
						.build();
	}
//	protected static HttpRequest postMaar(URI uri, String bericht, String header, String headerWaarde) {
//		return HttpRequest.newBuilder()
//						.uri(uri)
//						.setHeader(header, headerWaarde)
//						.POST(HttpRequest.BodyPublishers.ofString(bericht))
//						.build();
//	}
	protected static HttpRequest getMaar(URI uri){
	return HttpRequest.newBuilder()
						.uri(uri)
						.build();
	}
	protected static DOMResult
		document(final InputStream bericht) throws TransformerException, IOException {
		Reader reader = new InputStreamReader(bericht);
		Parser parser = new Parser();
		DOMResult uitkomst = new DOMResult();
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(new SAXSource(parser, new InputSource(reader)), uitkomst);
		reader.close();
		return uitkomst;
	}
	protected static Node vindNode(final Node node, String nodeNaam){
		int lengte;

		if(node.getLocalName() != null &&
				node.getLocalName().equals(nodeNaam)) return node;
		lengte = node.getChildNodes().getLength();
		if(lengte == 0) return null;
		for(int i = 0; i < lengte; i++){
			Node uitkomst = vindNode(node.getChildNodes().item(i), nodeNaam);
			if(uitkomst != null) return uitkomst;
		}
		return null;
	}
	protected static Node vindNode(
			final Node node,
			String nodeNaam,
			String attribuutNaam,
			String attribuutWaarde){
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
			Node uitkomst = vindNode(
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
