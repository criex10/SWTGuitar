package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import testvalidation.TestValidatorShell;

import efg.EFGParser;
import efg.EventFlowGraph;
import efg.WidgetId;
import efg.EventFlowGraph.EdgeType;


public class DomParserExample {

	//No generics
	List myEmpls;
	static Document dom;
	static Document dom2;
	public static EFGRenderListener EFGHighlighted;
	public static EventFlowGraph parsedGraph;
	public static String efgPath, guiPath, tstPath; //paths to .efg, .gui, and .tst files

	
	public static void main(String[] args)
	{
		if(args.length<2 || args.length>3) {
			System.out.println("Invalid number of arguments");
			System.exit(0);
		}
		efgPath = args[0];
		guiPath = args[1];
		
		//If test case path exists for test validation
		if(args.length==3) {
			tstPath = args[2];
			//run test validator
			runExample(true);
		}
		
		//run efg validator
		runExample(false);
	}
	

	public static void runExample(boolean testValidation) {

		//parse the xml file and get the dom object
		parseXmlFile();

		//get each employee element and create a Employee object
		parseDoc();
		
		//Load EFG data from efgPath
		try {
			parsedGraph = EFGParser.parseFile(efgPath);
		} catch (SAXException e1) {
			System.out.println("DEBUG: XML Parse Exception in EFG");
			System.exit(0);
		}
		
		
		if(testValidation)
			VisualizationGenerator.shellList.add(TestValidatorShell.getShell(tstPath));
		else
			setEFGVerifiers();
	

		VisualizationGenerator.Show();
		//generateXML();

		//Iterate through the list and print the data
		//printData();

	}

	private static void parseXmlFile(){
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			dom = db.parse(guiPath);
			dom2 = db.newDocument();

		}catch(ParserConfigurationException pce) {
			System.out.println("DEBUG: XML Parse Exception in GUI");
		}catch(SAXException se) {
			System.out.println("DEBUG: XML Parse Exception in GUI");
		}catch(IOException ioe) {
			System.out.println("DEBUG: XML Parse Exception in GUI");
		}
	}

	public 	ArrayList<HashMap<String, String>> eventList = new ArrayList <HashMap <String, String>>();
	public 	HashMap <HashMap<String, String>, ArrayList<String>> edgeMap = new HashMap <HashMap<String, String>, ArrayList<String>>();

	private static void parseDoc(){

		HashMap <String, String> eventMap = new HashMap<String, String>();

		Element GUI = dom.getDocumentElement();

		//Element events = (Element)(efg.getElementsByTagName("Attributes").item(0));

		NodeList attributeList = GUI.getElementsByTagName("Attributes");

		for(int i = 0; i < attributeList.getLength(); i++)
		{
			extractAttributes(i,attributeList,eventMap);		
			Widget addedWidget = VisualizationGenerator.addWidget(eventMap);
			//System.out.println(addedWidget);
/*			if(addedWidget==null)
				System.out.println(eventMap.get("Class"));*/
			if(addedWidget instanceof TabItem)
			{
				i++;
				extractAttributes(i,attributeList,eventMap);
				Widget nextWidget = VisualizationGenerator.addWidget(eventMap);
				if(nextWidget instanceof Control)
					((TabItem) addedWidget).setControl((Control)nextWidget);
			}
			//if the control has a menu, then add it
			if(eventMap.get("menu")!=null)
			{
				i++;
				extractAttributes(i,attributeList,eventMap);
				Widget nextWidget = VisualizationGenerator.addWidget(eventMap);
				if(nextWidget instanceof Menu && addedWidget instanceof Control)
					((Control) addedWidget).setMenu((Menu)nextWidget);
			}
			
		}
	}
	
	private static void extractAttributes(int i, NodeList attributeList, HashMap <String, String> eventMap)
	{
		Element attributes = (Element)(attributeList.item(i));
		NodeList properties = attributes.getElementsByTagName("Property");
		
		eventMap.clear();
		
		for(int j=0;j<properties.getLength();j++)
		{
			Element property = (Element)(properties.item(j));
			NodeList nlList = property.getElementsByTagName("Name").item(0).getChildNodes();
			String propertyName = property.getElementsByTagName("Name").item(0).getChildNodes().item(0).getNodeValue();
			
			if(propertyName.equals("ID"))
			{
				eventMap.put("ID", property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
			}
			else if(propertyName.equals("Class"))
			{
				eventMap.put("Class", property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
			}
			else if(propertyName.equals("bounds"))
			{
				//Rectangle {6, 48, 250, 250}
				String numbers = property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue();
				numbers = numbers.substring(numbers.indexOf("{") + 1, numbers.length() - 1);
				numbers = numbers.replace(" ", "");
				String[] boundList = numbers.split(",");
				
				String x = boundList[0];
				String y = boundList[1];
				String width = boundList[2];
				String height = boundList[3];
				eventMap.put("X", x);
				eventMap.put("Y", y);
				eventMap.put("width", width);
				eventMap.put("height", height);
			}
			else if(propertyName.equals("width"))
			{
				eventMap.put("width", property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
			}
			else if(propertyName.equals("layout"))
			{
				eventMap.put("layout", property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
			}
			else if(propertyName.equals("data"))
			{	
				if(property.getElementsByTagName("Value").item(0).getFirstChild() == null)
					eventMap.put("data", "");
				else
					eventMap.put("data", property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
			}
			else if(propertyName.equals("text"))
			{
				if(property.getElementsByTagName("Value").item(0).getFirstChild() != null)
					eventMap.put("text", property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
			}
			else if(propertyName.equals("Rootwindow"))
			{
				eventMap.put("Rootwindow", property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
			}
			else if(propertyName.equals("style"))
			{
				eventMap.put("style", property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
			}
			else if(propertyName.equals("layout"))
			{
				eventMap.put("layout", property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
			}
			else if(propertyName.equals("menu"))
			{
				eventMap.put("menu", property.getElementsByTagName("Value").item(0).getFirstChild().getNodeValue());
			}
		}
	}
	
	public static void setEFGVerifiers() 
	{
		Display display = Display.getCurrent();
		Color blue = new Color(display, 142, 205, 240);
		Color red = new Color(display, 255, 204, 204);
		Color green = new Color(display, 77, 166, 25);

		for(Widget widget:VisualizationGenerator.widgetList.keySet())
		{
			Map<EdgeType, Set<WidgetId>> neighbors = parsedGraph.getFollowingWidgets(VisualizationGenerator.widgetList.get(widget));
			
			boolean hasNeighbors = false;
			
			hasNeighbors=hasNeighbors || !neighbors.get(EdgeType.NONE).isEmpty();
			hasNeighbors=hasNeighbors || !neighbors.get(EdgeType.NORMAL).isEmpty();
			hasNeighbors=hasNeighbors || !neighbors.get(EdgeType.REACHING).isEmpty();
			
			//System.out.println(widget+" "+hasNeighbors);
			
			if(widget instanceof Control && hasNeighbors)
			{
				Control control = (Control) widget;
				//System.out.println("Setting "+widget+" red");
				control.setBackground(red);
			}
		}
		

		for(Widget widget:VisualizationGenerator.widgetList.keySet())
		{
			Map<EdgeType, Set<WidgetId>> neighbors = parsedGraph.getFollowingWidgets(VisualizationGenerator.widgetList.get(widget));
			EFGRenderListener colorListener = new EFGRenderListener(widget, neighbors, blue, green);
			widget.addListener(SWT.MouseDoubleClick, colorListener);
			widget.addListener(SWT.MouseEnter, colorListener);
			widget.addListener(SWT.MouseExit, colorListener);
			widget.addListener(SWT.Arm, colorListener);
			widget.addListener(SWT.Selection, colorListener);
			if(widget instanceof Menu)
				((Menu)widget).addMenuListener(colorListener);
			//System.out.println(widget+" "+VisualizationGenerator.widgetList.get(widget)+" "+neighbors);
		}
		
		for(final Widget widget:VisualizationGenerator.widgetList.keySet())
		{
		WidgetId idz = VisualizationGenerator.widgetList.get(widget);;

		widget.addListener(SWT.Selection, new Listener(){
		public void handleEvent(Event e)
		{
		WidgetId widgetId = VisualizationGenerator.widgetList.get(widget);
		//System.out.println(widgetId.getId());
		if(parsedGraph.opensWindow(widgetId))
		{
		//System.out.println("MADE IT HERE");
		for(WidgetId widgetId2 : parsedGraph.getFollowingWidgets(widgetId, EdgeType.NORMAL))
		{
		if(VisualizationGenerator.widgetIDs.get(widgetId2) instanceof org.eclipse.swt.widgets.Control)
		{
		((Control)VisualizationGenerator.widgetIDs.get(widgetId2)).getShell().open();
		}

		}
		for(WidgetId widgetId2 : parsedGraph.getFollowingWidgets(widgetId, EdgeType.REACHING))
		{
		if(VisualizationGenerator.widgetIDs.get(widgetId2) instanceof org.eclipse.swt.widgets.Control)
		{
		((Control)VisualizationGenerator.widgetIDs.get(widgetId2)).getShell().open();
		}
		}

		//ArrayList<Widget> neighbors = new ArrayList <Widget>();
		//neighbors.add(parsedGraph.getFollowingWidgets(widgetId, EdgeType.NORMAL));
		}
		}
		});
		}
	}
	
	public static void addColor(Widget w, Color c)
	{
		if(w instanceof Control)
		{
			Control control = (Control) w;
			control.setBackground(c);
		}
		else if(w instanceof Item)
		{
			Item i = (Item) w;
			while(i.getText().charAt(0)=='*')
				i.setText(i.getText().substring(1));
			i.setText("*"+i.getText());
		}
	}
	public static void removeColor(Widget w, Color c)
	{
		if(w instanceof Control)
		{
			Control control = (Control) w;
			control.setBackground(c);
		}
		else if(w instanceof Item)
		{
			Item i = (Item) w;
			if(i.getText().length()>0)
			{
				while(i.getText().charAt(0)=='*')
					i.setText(i.getText().substring(1));
			}
		}
	}
}
