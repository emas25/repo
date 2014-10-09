package com.example.eskulap.localization.mobile.app;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Picture;

import com.larvalabs.svgandroid.ParserHelper;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

public class SVGAdapter {

	private DocumentBuilderFactory dbFactory = DocumentBuilderFactory
			.newInstance();
	private DocumentBuilder dBuilder;
	private static Document svgDocument;
	private MapActivity context;
	private String chosenRoom = null;
	private Polygon polygonRoom;
	private String chosenObject = null;
	private float originalXPosition;
	private float originalYPosition;
	private String originalRoom;
	private Picture mapPicture;
	private String markedObject = null;

	public SVGAdapter(InputStream input, MapActivity context) {
		try {
			this.context = context;
			dbFactory.setNamespaceAware(false); // never forget this!
			dbFactory.setCoalescing(false);
			dbFactory.setValidating(false);
			dbFactory.setFeature("http://xml.org/sax/features/namespaces",
					false);
			dbFactory.setFeature("http://xml.org/sax/features/validation",
					false);

			dBuilder = dbFactory.newDocumentBuilder();
			svgDocument = dBuilder.parse(input);
			svgDocument.getDocumentElement().normalize();

			if (svgDocument == null)
				return;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void selectAtPoint(float x, float y) {
		chosenObject = null;
		String room = getElementName(x, y, "room");
		chosenObject = getElementName(x, y, "object");
		if (chosenObject != null)
			originalRoom = room;
		chooseRoom(room);
		chosenRoom = room;
	}

	public boolean selectRoom(float x, float y) {
		if (!polygonRoom.containsPoint(x, y)) {
			String room = getElementName(x, y, "room");
			if(room != null){
				chooseRoom(room);
				chosenRoom = room;
				return true;
			}
			return false;
		}
		return true;
	}

	public void chooseRoom(String name) {
		if (name == null && chosenRoom == null)
			return;
		if (name != null && chosenRoom != null && chosenRoom.equals(name))
			return;
		if (chosenRoom != null)
			findElement(chosenRoom, "room").getAttributes()
					.getNamedItem("fill").setTextContent("#FFFFFF");
		if (name != null)
			findElement(name, "room").getAttributes().getNamedItem("fill")
					.setTextContent("#9ACD32");
	}

	private Polygon getObjectAsPolygon(Node element) {

		Polygon polygon = null;
		String name = element.getNodeName();
		String[] points;
		float[] xPoints;
		float[] yPoints;
		int pointsCount;

		if (name.equals("rect")) {
			pointsCount = 4;
			float x = Float.parseFloat(element.getAttributes()
					.getNamedItem("x").getTextContent());
			float y = Float.parseFloat(element.getAttributes()
					.getNamedItem("y").getTextContent());
			float width = Float.parseFloat(element.getAttributes()
					.getNamedItem("width").getTextContent());
			float height = Float.parseFloat(element.getAttributes()
					.getNamedItem("height").getTextContent());
			xPoints = new float[4];
			yPoints = new float[4];
			xPoints[0] = x;
			yPoints[0] = y;
			xPoints[1] = x + width;
			yPoints[1] = y;
			xPoints[2] = x + width;
			yPoints[2] = y + height;
			xPoints[3] = x;
			yPoints[3] = y + height;

			polygon = new Polygon(xPoints, yPoints, pointsCount, "rect");
		} else if (name.equals("circle")) {
			float x = Float.parseFloat(element.getAttributes()
					.getNamedItem("cx").getTextContent());
			float y = Float.parseFloat(element.getAttributes()
					.getNamedItem("cy").getTextContent());
			float r = Float.parseFloat(element.getAttributes()
					.getNamedItem("r").getTextContent());
			polygon = new Polygon(x, y, r, 0.0f, "circle");

		} else if (name.equals("path")) {
			String path = element.getAttributes().getNamedItem("d").getTextContent();//.replace("M", "").replace("z", "")
					//.replace("l", "");
			/*
			points = path.split(" ");
			xPoints = new float[points.length / 2];
			yPoints = new float[points.length / 2];
			pointsCount = points.length / 2;
			xPoints[0] = Float.parseFloat(points[0]);
			yPoints[0] = Float.parseFloat(points[1]);
			for (int i = 2; i < points.length; i += 2) {
				xPoints[i / 2] = xPoints[(i - 2) / 2]
						+ Float.parseFloat(points[i]);
				yPoints[i / 2] = yPoints[(i - 2) / 2]
						+ Float.parseFloat(points[i + 1]);
			}
			polygon = new Polygon(xPoints, yPoints, pointsCount, "path");*/
			Path p = doPath(path);
			PathMeasure measure = new PathMeasure(p, false);
			float coordinates[] = new float[2];
			xPoints = new float[100];
			yPoints = new float[100];
			
			for(int i = 0; i < 100; i++){
				measure.getPosTan((measure.getLength()*i)/100.0f, coordinates, null);
				xPoints[i] = coordinates[0];
				yPoints[i] = coordinates[1];
			}
			polygon = new Polygon(xPoints, yPoints, 100, "path");
		} else if (name.equals("polyline")) {

		} else if (name.equals("polygon")) {
			String path = element.getAttributes().getNamedItem("points").getTextContent();
			points = path.split(" ");
			xPoints = new float[points.length];
			yPoints = new float[points.length];
			pointsCount = points.length;
			
			for (int i = 0; i < pointsCount; i ++) {
				xPoints[i] = Float.parseFloat(points[i].substring(0,points[i].indexOf(",")));
				yPoints[i] = Float.parseFloat(points[i].substring(points[i].indexOf(",") + 1));
			}
			polygon = new Polygon(xPoints, yPoints, pointsCount, "polygon");
		} else {
			; // Pozosta³e nieistotne elementy
		}
		return polygon;
	}
	
	public String moveObject(Integer objectId, Float x, Float y){
		Node element = findElement(String.valueOf(objectId), "object");
		element.getAttributes().getNamedItem("cx").setTextContent(Float.toString(x));
		element.getAttributes().getNamedItem("cy").setTextContent(Float.toString(y));
		return getElementName(x, y, "room");
	}

	public void moveSelectedObject(float x, float y) {
		Node element = findElement(chosenObject, "object");
		float xPosition = Float.parseFloat(element.getAttributes()
				.getNamedItem("cx").getTextContent());
		float yPosition = Float.parseFloat(element.getAttributes()
				.getNamedItem("cy").getTextContent());
		xPosition += x;
		yPosition += y;
		element.getAttributes().getNamedItem("cx")
				.setTextContent(Float.toString(xPosition));
		element.getAttributes().getNamedItem("cy")
				.setTextContent(Float.toString(yPosition));
	}

	public Picture getPicture(boolean createNew) {
		if (createNew) {
			InputStream svgStream = getInputStream();
			SVG svg = SVGParser.getSVGFromInputStream(svgStream);
			mapPicture = svg.getPicture();
			return svg.getPicture();
		} else {
			return mapPicture;
		}
		
	}

	public InputStream getInputStream() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Source xmlSource = new DOMSource(svgDocument);

		Result outputTarget = new StreamResult(outputStream);
		try {
			TransformerFactory.newInstance().newTransformer()
					.transform(xmlSource, outputTarget);
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InputStream is = new ByteArrayInputStream(outputStream.toByteArray());

		return is;
	}
	
	public void markObject(String id){
		Node object = findElement(id, "object");
		object.getAttributes().getNamedItem("stroke").setTextContent("#ffffff");
		object.getAttributes().getNamedItem("stroke-width").setTextContent("3");
	}
	
	public void unMarkObject(String id){
		Node object = findElement(id, "object");
		object.getAttributes().getNamedItem("stroke").setTextContent("#000000");
		object.getAttributes().getNamedItem("stroke-width").setTextContent("1");
	}
	
	public float[] getObjectCoordinates(String id){
		float[] coordinates = new float[2];
		Node object = findElement(id, "object");
		coordinates[0] = Float.parseFloat(object.getAttributes().getNamedItem("cx").getTextContent());
		coordinates[1] = Float.parseFloat(object.getAttributes().getNamedItem("cy").getTextContent());
		return coordinates;
	}

	private Node findElement(String name, String type) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = "//*[@id='" + name + "'][@class='" + type + "']";
		Node node = null;
		try {
			node = (Node) xpath.evaluate(expression, svgDocument,
					XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return node;
	}
	
	public void addObject(HashMap<String,Object> objectInfo, Integer objId){
		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = "//*[@id='map']";
		Node node = null;
		try {
			node = (Node) xpath.evaluate(expression, svgDocument,
					XPathConstants.NODE);
			Element result = createElement(objectInfo, objId);
			node.appendChild(result);

		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void refreshObjects(String objects, String places, String user){
		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = "//*[@id='map']";
		Node node = null;
		try {
			node = (Node) xpath.evaluate(expression, svgDocument,
					XPathConstants.NODE);
			int mapId = Integer.parseInt(node.getAttributes().getNamedItem("map-id").getTextContent());
			DbAdapter db = new DbAdapter(context);
			db.open();
			List<Element> result = new ArrayList<Element>();
			result = db.getFilteredObjects(mapId, svgDocument, objects, places,user);
			
			for(int i = 0; i < result.size(); i++){
				node.appendChild(result.get(i));
				
			}
			db.close();

		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public Element createElement(HashMap<String, Object> objectInfo, Integer objId){
		Element elem = svgDocument.createElement("circle");
		elem.setAttribute("class", "object");
		elem.setAttribute("id", String.valueOf(objId));
		elem.setAttribute("cx", Float.toString((Float) objectInfo.get("x")));
		elem.setAttribute("cy", Float.toString((Float) objectInfo.get("y")));
		elem.setAttribute("r", "7");
		elem.setAttribute("stroke", "#000000");
		elem.setAttribute("stroke-width", "1");
		
		DbAdapter db = new DbAdapter(context);
		db.open();
		String color = db.getObjectColor(objId.toString());
		db.close();
		
		elem.setAttribute("fill", color);
		
		return elem;
	}
	
	public void hideObject(String objectId){
		Node object = findElement(objectId, "object");
	}
	
	public Integer getMapId(){
		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = "//*[@id='map']";
		Node node = null;
			try {
				node = (Node) xpath.evaluate(expression, svgDocument,
						XPathConstants.NODE);
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Integer mapId = Integer.parseInt(node.getAttributes().getNamedItem("map-id").getTextContent());
		return mapId;
	}

	private NodeList findElements(String name) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = "//*[@class='" + name + "']";
		NodeList list = null;
		try {
			list = (NodeList) xpath.evaluate(expression, svgDocument,
					XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public String getElementName(float x, float y, String elementType) {
		String elementName = null;
		NodeList roomList = findElements(elementType);
		for (int i = roomList.getLength() - 1; i >= 0; i--) {
			Node room = roomList.item(i);
			Polygon polygonObject = getObjectAsPolygon(room);
			if (polygonObject != null && polygonObject.containsPoint(x, y)) {
				elementName = room.getAttributes().getNamedItem("id")
						.getTextContent();
				if (elementType.equals("object")) {
					float[] coordinates = getElementCoordinates(room);
					originalXPosition = coordinates[0];
					originalYPosition = coordinates[1];
				} else if (elementType.equals("room")) {
					polygonRoom = polygonObject;
				}
				break;
			}
		}
		return elementName;
	}

	public float[] getElementCoordinates(Node element) {
		float[] coordinates = new float[2];
		if (element.getNodeName().equals("circle")) {
			coordinates[0] = Float.parseFloat(element.getAttributes()
					.getNamedItem("cx").getTextContent());
			coordinates[1] = Float.parseFloat(element.getAttributes()
					.getNamedItem("cy").getTextContent());
		}
		return coordinates;
	}

	public void setElementOriginalPosition() {
		Node element = findElement(chosenObject, "object");
		if (element.getNodeName().equals("circle")) {
			element.getAttributes().getNamedItem("cx")
					.setTextContent(Float.toString(originalXPosition));
			element.getAttributes().getNamedItem("cy")
					.setTextContent(Float.toString(originalYPosition));
		}
		chooseRoom(originalRoom);
		chosenRoom = originalRoom;
	}
	
	private static Path doPath(String s) {
        int n = s.length();
        ParserHelper ph = new ParserHelper(s, 0);
        ph.skipWhitespace();
        Path p = new Path();
        float lastX = 0;
        float lastY = 0;
        float lastX1 = 0;
        float lastY1 = 0;
        float subPathStartX = 0;
        float subPathStartY = 0;
        char prevCmd = 0;
        while (ph.pos < n) {
            char cmd = s.charAt(ph.pos);
            switch (cmd) {
                case '-':
                case '+':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    if (prevCmd == 'm' || prevCmd == 'M') {
                        cmd = (char) (((int) prevCmd) - 1);
                        break;
                    } else if (prevCmd == 'c' || prevCmd == 'C') {
                        cmd = prevCmd;
                        break;
                    } else if (prevCmd == 'l' || prevCmd == 'L') {
                        cmd = prevCmd;
                        break;
                    }
                default: {
                    ph.advance();
                    prevCmd = cmd;
                }
            }

            boolean wasCurve = false;
            switch (cmd) {
                case 'M':
                case 'm': {
                    float x = ph.nextFloat();
                    float y = ph.nextFloat();
                    if (cmd == 'm') {
                        subPathStartX += x;
                        subPathStartY += y;
                        p.rMoveTo(x, y);
                        lastX += x;
                        lastY += y;
                    } else {
                        subPathStartX = x;
                        subPathStartY = y;
                        p.moveTo(x, y);
                        lastX = x;
                        lastY = y;
                    }
                    break;
                }
                case 'Z':
                case 'z': {
                    p.close();
                    p.moveTo(subPathStartX, subPathStartY);
                    lastX = subPathStartX;
                    lastY = subPathStartY;
                    lastX1 = subPathStartX;
                    lastY1 = subPathStartY;
                    wasCurve = true;
                    break;
                }
                case 'L':
                case 'l': {
                    float x = ph.nextFloat();
                    float y = ph.nextFloat();
                    if (cmd == 'l') {
                        p.rLineTo(x, y);
                        lastX += x;
                        lastY += y;
                    } else {
                        p.lineTo(x, y);
                        lastX = x;
                        lastY = y;
                    }
                    break;
                }
                case 'H':
                case 'h': {
                    float x = ph.nextFloat();
                    if (cmd == 'h') {
                        p.rLineTo(x, 0);
                        lastX += x;
                    } else {
                        p.lineTo(x, lastY);
                        lastX = x;
                    }
                    break;
                }
                case 'V':
                case 'v': {
                    float y = ph.nextFloat();
                    if (cmd == 'v') {
                        p.rLineTo(0, y);
                        lastY += y;
                    } else {
                        p.lineTo(lastX, y);
                        lastY = y;
                    }
                    break;
                }
                case 'C':
                case 'c': {
                    wasCurve = true;
                    float x1 = ph.nextFloat();
                    float y1 = ph.nextFloat();
                    float x2 = ph.nextFloat();
                    float y2 = ph.nextFloat();
                    float x = ph.nextFloat();
                    float y = ph.nextFloat();
                    if (cmd == 'c') {
                        x1 += lastX;
                        x2 += lastX;
                        x += lastX;
                        y1 += lastY;
                        y2 += lastY;
                        y += lastY;
                    }
                    p.cubicTo(x1, y1, x2, y2, x, y);
                    lastX1 = x2;
                    lastY1 = y2;
                    lastX = x;
                    lastY = y;
                    break;
                }
                case 'S':
                case 's': {
                    wasCurve = true;
                    float x2 = ph.nextFloat();
                    float y2 = ph.nextFloat();
                    float x = ph.nextFloat();
                    float y = ph.nextFloat();
                    if (cmd == 's') {
                        x2 += lastX;
                        x += lastX;
                        y2 += lastY;
                        y += lastY;
                    }
                    float x1 = 2 * lastX - lastX1;
                    float y1 = 2 * lastY - lastY1;
                    p.cubicTo(x1, y1, x2, y2, x, y);
                    lastX1 = x2;
                    lastY1 = y2;
                    lastX = x;
                    lastY = y;
                    break;
                }
                case 'A':
                case 'a': {
                    float rx = ph.nextFloat();
                    float ry = ph.nextFloat();
                    float theta = ph.nextFloat();
                    int largeArc = (int) ph.nextFloat();
                    int sweepArc = (int) ph.nextFloat();
                    float x = ph.nextFloat();
                    float y = ph.nextFloat();
                    drawArc(p, lastX, lastY, x, y, rx, ry, theta, largeArc, sweepArc);
                    lastX = x;
                    lastY = y;
                    break;
                }
            }
            if (!wasCurve) {
                lastX1 = lastX;
                lastY1 = lastY;
            }
            ph.skipWhitespace();
        }
        return p;
    }
	
	private static void drawArc(Path p, float lastX, float lastY, float x, float y, float rx, float ry, float theta, int largeArc, int sweepArc) {
        // todo - not implemented yet, may be very hard to do using Android drawing facilities.
    }

	public String getChosenObject() {
		return chosenObject;
	}

	public String getChosenRoom() {
		return chosenRoom;
	}

	public String getXc() {
		return Float.toString(originalXPosition);
	}

	public String getYc() {
		return Float.toString(originalYPosition);
	}

	public String getOriginalRoom() {
		return originalRoom;
	}
	
	public void setMarkedObject(Integer id){
		markedObject = String.valueOf(id);
	}
	
	public String getMarkedObject(){
		return markedObject;
	}
	
	public void setChoosenObject(String id){
		chosenObject = id;
	}
}
