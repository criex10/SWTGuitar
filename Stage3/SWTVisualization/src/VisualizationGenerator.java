
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;

import efg.WidgetId;

public class VisualizationGenerator {
	static HashMap<String, Widget> widgets = new HashMap<String, Widget>();
	static HashMap<WidgetId, Widget> widgetIDs = new HashMap<WidgetId, Widget>();
	static HashMap<Widget,WidgetId> widgetList = new HashMap<Widget,WidgetId>();
	static Display display = new Display();
	static ArrayList<Shell> shellList = new ArrayList<Shell>();
	public static Widget addWidget(HashMap<String, String> properties)
	{
		if(properties.get("Rootwindow") != null || properties.get("data")==null)
		{
			return null;
		}


		String parent = "";
		if(properties.get("data").split(" ").length > 1)
			parent = properties.get("data").split(" ")[1];
		String data = properties.get("data").split(" ")[0];
		
		int x=0;
		int y=0;
		int width=0;
		int height=0;
		
		if(properties.get("X")!=null)
			x = Integer.parseInt(properties.get("X"));

		if(properties.get("Y")!=null)
			y = Integer.parseInt(properties.get("Y"));

		if(properties.get("width")!=null)
			width = Integer.parseInt(properties.get("width"));

		if(properties.get("height")!=null)
			height = Integer.parseInt(properties.get("height"));

		if(widgets.get(parent) != null && widgets.get(parent) instanceof Composite)
		{
			Rectangle parentBounds = ((Composite)(widgets.get(parent))).getBounds();
			//x = x - parentBounds.x;
			//y = y - parentBounds.y;
		}

		String ID = properties.get("ID");
		int style = Integer.parseInt(properties.get("style"));

		
		if(properties.get("Class").equals("org.eclipse.swt.widgets.Shell"))
		{
			Shell shell;
			if(parent.equals(""))
			{
				shell = new Shell(display);
				shellList.add(shell);
			}
			else
				shell = new Shell((Shell)(widgets.get(parent)), style);

			shell.setBounds(x, y, width, height);

			if(properties.get("text") != null)
				shell.setText(properties.get("text"));

			shell.setToolTipText(ID);
			//shell.open();

			addWidgetToMap(data, properties.get("ID"), shell);
			return shell;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Composite"))
		{
			Composite composite = new Composite((Composite)(widgets.get(parent)), style);
			if(!(x==0&&y==0&&width==0&&height==0))
				composite.setBounds(x, y, width, height);
			else
			{
				FillLayout fillLayout = new FillLayout();
				fillLayout.type = SWT.VERTICAL;
				composite.setLayout(fillLayout);
				composite.pack();
			}
			composite.setToolTipText(ID);
			addWidgetToMap(data, properties.get("ID"), composite);
			return composite;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Label"))
		{
			Label label = new Label((Composite)(widgets.get(parent)), style);
			if(!(x==0&&y==0&&width==0&&height==0))
				label.setBounds(x, y, width, height);
			else
				label.pack();
			label.setToolTipText(ID);
			if(properties.get("text") != null)
				label.setText(properties.get("text"));
			addWidgetToMap(data, properties.get("ID"), label);
			return label;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Button"))
		{
			Button button = new Button((Composite)(widgets.get(parent)), style);
			if(!(x==0&&y==0&&width==0&&height==0))
				button.setBounds(x, y, width, height);
			else
				button.pack();
			button.setToolTipText(ID);
			if(properties.get("text")==null)
				button.setText("X");
			else
				button.setText(properties.get("text"));
			addWidgetToMap(data, properties.get("ID"), button);
			return button;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Spinner"))
		{
			Spinner spinner = new Spinner((Composite)(widgets.get(parent)), style);
			if(!(x==0&&y==0&&width==0&&height==0))
				spinner.setBounds(x, y, width, height);
			else
				spinner.pack();
			spinner.setToolTipText(ID);
			addWidgetToMap(data, properties.get("ID"), spinner);
			return spinner;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Link"))
		{
			Link control = new Link((Composite)(widgets.get(parent)), style);
			if(!(x==0&&y==0&&width==0&&height==0))
				control.setBounds(x, y, width, height);
			control.setToolTipText(ID);
			if(properties.get("text") != null)
				control.setText(properties.get("text"));
			addWidgetToMap(data, properties.get("ID"), control);
			return control;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Group"))
		{
			Group group = new Group((Composite)(widgets.get(parent)), style);
			if(!(x==0&&y==0&&width==0&&height==0))
				group.setBounds(x, y, width, height);
			else
			{
				FillLayout fillLayout = new FillLayout();
				fillLayout.type = SWT.HORIZONTAL;
				group.pack();
				group.setLayout(fillLayout);
			}
			group.setToolTipText(ID);

			if(properties.get("text") != null)
				group.setText(properties.get("text"));

			addWidgetToMap(data, properties.get("ID"), group);
			return group;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Text"))
		{
			Text text = new Text((Composite)(widgets.get(parent)), style);

			if(!(x==0&&y==0&&width==0&&height==0))
				text.setBounds(x, y, width, height);
			else
				text.pack();
			
			text.setToolTipText(ID);

			if(properties.get("text") != null)
				text.setText(properties.get("text"));

			addWidgetToMap(data, properties.get("ID"), text);
			return text;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.TabFolder"))
		{
			TabFolder folder = new TabFolder((Composite)(widgets.get(parent)), style);

			if(!(x==0&&y==0&&width==0&&height==0))
				folder.setBounds(x, y, width, height);
			else
				folder.pack();
			
			folder.setToolTipText(ID);

			addWidgetToMap(data, properties.get("ID"), folder);
			return folder;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.TreeItem"))
		{
			TreeItem item = new TreeItem((Tree)widgets.get(parent),style);
			if(properties.get("text")!=null)
				item.setText(properties.get("text"));
			
			item.setText(item.getText()+" "+ID);
			
			addWidgetToMap(data, properties.get("ID"),item);
			return item;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.TableItem"))
		{
			TableItem item = new TableItem((Table)widgets.get(parent),style);
			if(properties.get("text")!=null)
				item.setText(properties.get("text"));
			
			item.setText(item.getText()+" "+ID);
			
			addWidgetToMap(data, properties.get("ID"),item);
			return item;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.TableColumn"))
		{
			TableColumn item = new TableColumn((Table)widgets.get(parent),style);
			if(properties.get("text")!=null)
			{
				item.setText(properties.get("text"));
				((Table)widgets.get(parent)).setHeaderVisible(true);
			}
			item.setToolTipText(ID);
			
			//TODO: set the width properly
			item.pack();
			item.setWidth(width);
			addWidgetToMap(data, properties.get("ID"),item);
			return item;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Table"))
		{
		    Table table = new Table((Composite)widgets.get(parent), style);
		    table.setLinesVisible(true);
		    table.setToolTipText(ID);
		    //table.setHeaderVisible(true);

			if(!(x==0&&y==0&&width==0&&height==0))
		    table.setBounds(x, y, width, height);
			addWidgetToMap(data, properties.get("ID"),table);
			return table;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.TabItem"))
		{
			TabItem item = new TabItem((TabFolder)widgets.get(parent),style);
			if(properties.get("text")!=null)
				item.setText(properties.get("text"));
			
			item.setToolTipText(ID);
			
			addWidgetToMap(data, properties.get("ID"),item);
			return item;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.MenuItem"))
		{
			System.out.println(properties.get("ID"));
			System.out.println(widgets.get(parent));
			MenuItem item = new MenuItem((Menu)widgets.get(parent),style);
			if(properties.get("text")!=null)
				item.setText(properties.get("text"));
			
			item.setText(item.getText()+" "+ID);
			
			addWidgetToMap(data, properties.get("ID"),item);
			return item;
			
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.ToolItem"))
		{
			Text item = new Text((ToolBar)widgets.get(parent),SWT.BORDER);
			item.setBounds(x,y,width,height);
			if(properties.get("text")!=null)
				item.setText(properties.get("text"));
			
			addWidgetToMap(data, properties.get("ID"),item);
			return item;
		}
		else if(properties.get("Class").equals("org.eclipse.swt.widgets.Menu"))
		{
			if(widgets.get(parent) instanceof Shell)
			{
				Menu menubar = new Menu((Shell)widgets.get(parent),style-33554432);
	
				System.out.println(style-33554432);
				System.out.println(SWT.BAR);
				
				System.out.println(widgets.get(parent));
				
				if(style-33554432 == SWT.BAR || style == SWT.BAR)
					((Shell)widgets.get(parent)).setMenuBar(menubar);
				else if (widgets.get(parent) instanceof Control && (style-33554432 == SWT.POP_UP || style == SWT.POP_UP))
					((Control)widgets.get(parent)).setMenu(menubar);
				
				addWidgetToMap(data, properties.get("ID"),menubar);
				return menubar;
			}
			else if(widgets.get(parent) instanceof MenuItem)
			{
				Menu menu = new Menu((MenuItem)widgets.get(parent));
				((MenuItem)widgets.get(parent)).setMenu(menu);
				
				addWidgetToMap(data, properties.get("ID"),menu);
				return menu;
			}
			else
			{
				System.out.println("Yay we have others");
			}
		}
		else if(properties.get("Class").equals("org.eclipse.swt.browser.Browser"))
		{
			Text browser = new Text((Composite)widgets.get(parent),SWT.BORDER);
			browser.setBounds(x,y,width,height);
			browser.setText("Browser");
			browser.setToolTipText(ID);
			addWidgetToMap(data, properties.get("ID"),browser);
			return browser;
		}
		else
		{
			Class [] classParm = {Composite.class, int.class};
			
			boolean customControlFlag = false;
			try
			{
				Class cl = Class.forName(properties.get("Class"));
				Constructor co = cl.getConstructor(classParm);
				System.out.println(widgets.get(parent));
				Control control = (Control) co.newInstance(widgets.get(parent), style);
				if(!(x==0&&y==0&&width==0&&height==0))
					control.setBounds(x, y, width, height);
				else
					control.pack();
				control.setToolTipText(ID);
				addWidgetToMap(data, properties.get("ID"), control);
				return control;
			}
			catch (Exception e) {
				e.printStackTrace();
				customControlFlag = true;
			}
			
			if(customControlFlag)
			{
				Group customControl = new Group((Composite)widgets.get(parent),SWT.BORDER);
				customControl.setBounds(x,y,width,height);
				customControl.setText(properties.get("Class"));
				customControl.setToolTipText(ID);
				addWidgetToMap(data, properties.get("ID"),customControl);
				return customControl;
			}
		}
		return null;
	}

	public static void Show()
	{
		for (int i = 0; i < shellList.size(); i++) 
			shellList.get(i).open();
		
		for (int i = 0; i < shellList.size(); i++) 
		{
			Shell currentShell = shellList.get(i);
			while (!currentShell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}

		}
		display.dispose();
	}
	
	private static void addWidgetToMap(String data, String widgetID, Widget widget)
	{
		widgetList.put(widget,new WidgetId(widgetID));
		widgets.put(data, widget);
		widgetIDs.put(new WidgetId(widgetID), widget);
	}
}
