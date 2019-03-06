package fractals;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

@SuppressWarnings("serial")
public class JuliaSets2 extends JPanel implements ActionListener {
	
	private int vOffset;
	private int hOffset;	
	private int upperLimit = 760;
	private String real;
	private String complex;
	private Image pic;
	private JButton startBtn;
	private JButton stopBtn;
	private JButton resetBtn;
	private JButton imageBtn;
	private double realPart;
	private double imagPart;
	private String equation;
	private double xMin;
	private double xMax;
	private double yMin;
	private double yMax;
	private double xDim;
	private double yDim;
	private JTextField overallTxt;
	private JLabel picLabel;
	private JLabel mouseLabel; //show cell coordinates of mouse
	private JLabel xMinLabel;
	private JLabel xMaxLabel;
	private JLabel yMinLabel;
	private JLabel yMaxLabel;
	private int[] mouseCoords;
	private int[][] cells;
	private int[][] k;
//	private Point.Double[] clicks;	//you may want a variable like this
//	private int clickIndex;
	private Timer timer;
	private boolean isRunning;
	Point2D.Double origin;
	
	private double SIZE = 1.5; //how much of the coordinate plane from the origin to begin with
	
    public JuliaSets2(int xSize, int ySize) {
        super(new GridBagLayout());                       				// set up graphics window
        setBackground(Color.LIGHT_GRAY);
		addMouseListener(new MAdapter());
		addMouseMotionListener(new MAdapter());
		setFocusable(true);
		setDoubleBuffered(true);
		xMin = -1 * SIZE;
		yMin = -1 * SIZE;
		xMax = SIZE;
		yMax = SIZE;
		xDim = xSize;
		yDim = ySize;
		initBtns();
		initTxt();
		initLabels();
		pic = new BufferedImage(xSize, ySize, BufferedImage.TYPE_INT_RGB);
    	mouseCoords = new int[2];
		cells = new int[xSize][ySize];
		k = new int[xSize][ySize];
		picLabel = new JLabel(new ImageIcon(pic));
		timer = new Timer(1, this);					// initialize the timer
		timer.start();
		drawCells(pic.getGraphics());
		isRunning = false;
		addThingsToPanel();
    }

    //more of that annoying placement code
	public void addThingsToPanel() {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1, 1, 0, 1);
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 6;
		c.gridheight = 11;
		add(picLabel, c);
		c.gridwidth = 1;
		c.gridheight = 1;
		c.insets = new Insets(0, 2, 0, 2);
		c.gridx = 0;
		c.gridy = 0;
		add(startBtn, c);
		c.gridx = 1;
		c.gridy = 0;
		add(stopBtn, c);
		c.gridx = 2;
		add(resetBtn, c);
		c.gridx = 3;
//		add(speedBtn, c);
		c.insets = new Insets(0, 10, 0, 10);
		c.gridx = 4;
		c.gridy = 0;
		c.fill = GridBagConstraints.VERTICAL;
//		add(generations, c);
		c.gridx = 5;
		add(mouseLabel, c);
		c.gridx = 6;
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(imageBtn, c);
		c.gridy = 2;
		c.fill = GridBagConstraints.BOTH;
		add(new JLabel("f(z) = z^2 + c"), c);
		c.gridy = 3;
		c.gridx = 7;
		c.gridy = 2;
		add(new JLabel("where c = a+bi"), c);    	
		c.gridy = 3;
		add(overallTxt, c);
		c.gridy = 4;
		add(xMinLabel, c);
		c.gridy = 8;
		add(xMaxLabel, c);
		c.gridy = 9;
		add(yMinLabel, c);
		c.gridy = 10;
		add(yMaxLabel, c);
    }
    
    public void initTxt() {
    	overallTxt = new JTextField("0", 8);
    	overallTxt.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent arg0) {
    			try {
    				int i = 0;
    				real = "";
    				complex = "";
    				equation = overallTxt.getText();
    				while (Character.toString(equation.charAt(i)).matches("[.1234567890-]")) {
    					real = real + String.valueOf(equation.charAt(i));
    					System.out.println(real);
    					i++;
    				}
    				while (i < equation.length() && !Character.toString(equation.charAt(i)).matches("[.1234567890-]")) i++;
    				while (i < equation.length() && Character.toString(equation.charAt(i)).matches("[.1234567890-]")) {
    					complex = complex + String.valueOf(equation.charAt(i));
    					System.out.println(complex);
    					i++;
    				}
    				realPart = Double.parseDouble(real);
    				imagPart = Double.parseDouble(complex);
    				isRunning = true;
    			}
    			catch (NumberFormatException e) {
    			}
    		}
    	});
    }
//    	overallTxt.getDocument().addDocumentListener(new DocumentListener() {
//    		public void changedUpdate(DocumentEvent arg0) {
//    			try {
//    				int i = 0;
//    				real = "";
//    				complex = "";
//    				equation = overallTxt.getText();
//    				while (Character.toString(equation.charAt(i)).matches("[.1234567890-]")) {
//    					real = real + String.valueOf(equation.charAt(i));
//    					System.out.println(real);
//    					i++;
//    				}
//    				while (i < equation.length() && !Character.toString(equation.charAt(i)).matches("[.1234567890-]")) i++;
//    				while (i < equation.length() && Character.toString(equation.charAt(i)).matches("[.1234567890-]")) {
//    					complex = complex + String.valueOf(equation.charAt(i));
//    					System.out.println(complex);
//    					i++;
//    				}
//    				realPart = Double.parseDouble(real);
//    				imagPart = Double.parseDouble(complex);
//    				isRunning = true;
//    			}
//    			catch (NumberFormatException e) {
//    			}
//    			}
//    		public void insertUpdate(DocumentEvent arg0) {
//    			try {
//    				int i = 0;
//    				real = "";
//    				complex = "";
//    				equation = overallTxt.getText();
//    				while (Character.toString(equation.charAt(i)).matches("[.1234567890-]")) {
//    					real = real + String.valueOf(equation.charAt(i));
//    					System.out.println(real);
//    					i++;
//    				}
//    				while (i < equation.length() && !Character.toString(equation.charAt(i)).matches("[.1234567890-]")) i++;
//    				while (i < equation.length() && Character.toString(equation.charAt(i)).matches("[.1234567890-]")) {
//    					complex = complex + String.valueOf(equation.charAt(i));
//    					System.out.println(complex);
//    					i++;
//    				}
//    				realPart = Double.parseDouble(real);
//    				imagPart = Double.parseDouble(complex);
//    				isRunning = true;
//    			}
//    			catch (NumberFormatException e) {
//    			}
//    			}
//    		public void removeUpdate(DocumentEvent arg0) {
//    			try {
//    				int i = 0;
//    				real = "";
//    				complex = "";
//    				equation = overallTxt.getText();
//    				while (Character.toString(equation.charAt(i)).matches("[.1234567890-]")) {
//    					real = real + String.valueOf(equation.charAt(i));
//    					System.out.println(real);
//    					i++;
//    				}
//    				while (i < equation.length() && !Character.toString(equation.charAt(i)).matches("[.1234567890-]")) i++;
//    				while (i < equation.length() && Character.toString(equation.charAt(i)).matches("[.1234567890-]")) {
//    					complex = complex + String.valueOf(equation.charAt(i));
//    					System.out.println(complex);
//    					i++;
//    				}
//    				realPart = Double.parseDouble(real);
//    				imagPart = Double.parseDouble(complex);
//    				isRunning = true;
//    			}
//    			catch (NumberFormatException e) {
//    			}
//    			}
//    		});
//    	}

    public void initBtns() {
		startBtn = new JButton("Start");
		startBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				isRunning = true;
			}
		});
		stopBtn = new JButton("Stop");
		stopBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				isRunning = false;
			}
		});
		resetBtn = new JButton("Reset");
		resetBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				xMin = -1 * SIZE;
				yMin = -1 * SIZE;
				xMax = SIZE;
				yMax = SIZE;
				isRunning = true;
			}
		});    	
		imageBtn = new JButton("Save Picture");
		imageBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Calendar c = Calendar.getInstance();
					String fileName = ".\\" + realPart + "+" + imagPart + "i" + "@" + c.get(Calendar.HOUR) + "." + c.get(Calendar.MINUTE) + "." + c.get(Calendar.SECOND)+ ".png";
					System.out.println(fileName);
					File outputFile = new File(fileName);
					outputFile.createNewFile();
					ImageIO.write((RenderedImage) pic, "png", outputFile);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
    }

    private void initLabels() {
    	mouseLabel = new JLabel("Mouse off-grid");
    	xMinLabel = new JLabel("xMin: " + xMin);
    	xMaxLabel = new JLabel("xMax: " + xMax);
    	yMinLabel = new JLabel("yMin: " + yMin);
    	yMaxLabel = new JLabel("yMax: " + yMax);    	
    }
    
    //380-750, using http://www.efg2.com/Lab/ScienceAndEngineering/Spectra.htm chart
    //I used to think every color was in the spectrum...
    //currently, the transition from 750 to 380 could be improved...
    public static Color makeColor2(int input) { //figure out what this does, modify it if you want
    	if (input < 0) {
    		return Color.BLACK;
    	}
    	if (input < 380) {
    		input += 380;
    	}
    	int red = 0, green = 0, blue = 0; //depending on the input and where it falls on the spectrum and its different graphs, it sees how much of each color to use
    	//red
    	if ((input >= 520) && (input <= 580)) red = 255 * (input - 520) / 60;
    	else if ((input > 580) && (input <= 700)) red = 255;
    	else if ((input > 700) && (input <= 750)) red = 255 - 155 * (input - 700) / 50; // multiplier was 95, this wraps better
    	else if ((input >= 380) && (input <= 400)) red = 100 + 30 * (input - 380) / 20;
    	else if ((input > 400) && (input <= 420)) red = 130 - 30 * (input - 400) / 20;
    	else if ((input > 420) && (input <= 440)) red = 100 - 100 * (input - 420) / 20;
    	//green
    	if ((input >= 440) && (input <= 480)) green = 255 * (input - 440) / 40;
    	else if ((input > 480) && (input <= 560)) green = 255;
    	else if ((input > 560) && (input <= 645)) green = 255 - 255 * (input - 560) / 85;    	
    	//blue
    	if ((input >= 380) && (input <= 420)) blue = 100 + 155 * (input - 380) / 40;
    	else if ((input > 420) && (input <= 490)) blue = 255; 
    	else if ((input > 490) && (input <= 510)) blue = 255 - 255 * (input - 490) / 20;
    	else if ((input > 730) && (input <= 750)) blue = 100 * (input - 730) / 20; // new modification to wrap color wheel
    	return new Color(red, green, blue);
    }

    public void paintComponent(Graphics g) { 	                 // draw graphics in the panel
        super.paintComponent(g);                              	 // call superclass to make panel display correctly
    }
    
	@Override
	public void actionPerformed(ActionEvent e) { 		//things to change every timer tick
		hOffset = picLabel.getLocationOnScreen().x - getLocationOnScreen().x;
		vOffset = picLabel.getLocationOnScreen().y - getLocationOnScreen().y;
		updateCells();
		updateLabels();
		drawCells(pic.getGraphics());
		repaint();
	}
	
	//use setColor and fillRect (or drawRect) to adjust the corresponding graphics to cells in the pic variable
	private void drawCells(Graphics g) {
    	for (int i = 0; i < cells.length; i++) {
    		for (int j = 0; j < cells[i].length; j++) {
   		    	g.setColor(makeColor2(cells[i][j])); //feel free to create your own color function
   				g.drawRect(i, j, 1, 1);
    		}
    	}
	}

	//where the fractal magic happens
	private void updateCells() {
		if (isRunning) {
			//come up with expressions to fit the cells 2d array into the portion of the coordinate plane between...
			// ...xMax and xMin, and also yMax and yMin
			//then, for each cell in cells...
			int k = 1;
			double C = 0;
			for (int i = 0; i <cells[0].length; i++) {
				for (int l = 0; l <cells.length; l++) {
					Point2D.Double complexPoint = getPoint(i, l);
					while (C < 16 && k < upperLimit) {
						complexPoint = function1(complexPoint.getX(), complexPoint.getY());
						C = magnitude(complexPoint.getX(), complexPoint.getY());
						k++;
					}
					cells[i][l] = k;
					k = 1;
					C = 0;
				}
			}
			//run the algorithm as described in the pdf to determine the k value for that pixel
			//store that k value in the corresponding place in the cells array
			//you will need to teach the computer how to deal with complex numbers
			//my suggestion is to make a variable for the real part and a variable for the imaginary part
			//(you can also use a Point variable and deal with x and y as the real and imaginary parts)
			isRunning = false;
		}
	}
	private double magnitude(double d, double e) {
		return (Math.pow(d, 2) + Math.pow(e, 2));
	}
	private Point2D.Double function1 (double d, double e){
		double real = (Math.pow(d, 2) - (Math.pow(e, 2)) + realPart);
		double complex = 2*d*e + imagPart;
		return new Point2D.Double(real, complex);
	}
	private Point2D.Double getPoint(int x, int y) {
    		return new Point.Double(Math.round(1000000*(xMin + (xMax - xMin) * (double)x / (xDim - 1)))/1000000.0,
    											Math.round(1000000*(yMax - (yMax - yMin) * (double)y / (yDim - 1)))/1000000.0);
}

	private void updateLabels() {
		//large numbers for rounding purposes
		//I suppose you can make them even larger
    	if ((mouseCoords[0] >= 0) && (mouseCoords[0] <= cells.length) && (mouseCoords[1] >= 0) && (mouseCoords[1] <= cells[0].length)) {
    		Point2D.Double p = new Point.Double(Math.round(1000000*(xMin + (xMax - xMin) * (double)mouseCoords[0] / (xDim - 1)))/1000000.0,
    											Math.round(1000000*(yMax - (yMax - yMin) * (double)mouseCoords[1] / (yDim - 1)))/1000000.0);
        	mouseLabel.setText("Mouse at (" + p.x + ", " + p.y + ")");    	    		
    	} else {
    		mouseLabel.setText("Mouse off-grid");
    	}
    	xMinLabel.setText("xMin: " + xMin);
    	xMaxLabel.setText("xMax: " + xMax);
    	yMinLabel.setText("yMin: " + yMin);
    	yMaxLabel.setText("yMax: " + yMax);
	}
	
	//mouse input
	private class MAdapter extends MouseAdapter {
		
		@Override
		public void mousePressed(MouseEvent e) {
			Point p = new Point(e.getX() - hOffset, e.getY() - vOffset);
			mouseCoords[0] = p.x;
			mouseCoords[1] = p.y;
			double x = Math.sqrt((Math.pow(xMax - xMin, 2))) / 3;
			if ((mouseCoords[0] >= 0) && (mouseCoords[0] <= cells.length) && (mouseCoords[1] >= 0) && (mouseCoords[1] <= cells[0].length)) {
	    		Point2D.Double origin = new Point.Double(Math.round(1000000*(xMin + (xMax - xMin) * (double)mouseCoords[0] / (xDim - 1)))/1000000.0,
	    											Math.round(1000000*(yMax - (yMax - yMin) * (double)mouseCoords[1] / (yDim - 1)))/1000000.0);
	    		System.out.println(origin);
	    		xMin = Math.round(((origin.getX() - Math.abs(x)) * 100000)) / 100000.0;
	    		xMax = Math.round(((origin.getX() + Math.abs(x)) * 100000)) / 100000.0;
	    	    yMin = Math.round(((origin.getY() - Math.abs(x)) * 100000)) / 100000.0;
	    	    yMax = Math.round(((origin.getY() + Math.abs(x)) * 100000)) / 100000.0;
	    	    drawCells(pic.getGraphics());
	    	    isRunning = true;
			}
		}
		
		@Override
		public void mouseMoved(MouseEvent e) {
			Point p = new Point(e.getX() - hOffset, e.getY() - vOffset);
			mouseCoords[0] = p.x;
			mouseCoords[1] = p.y;	
		}
		
//		@Override
//		public void mouseDragged(MouseEvent e) {
//			//things for when the mouse is dragged (pressed and held down while moving)
//		}

//		@Override
//		public void mouseClicked(MouseEvent e) {
//			//a click is a press and then a release
//			//xMin = mouseCoords[0] - 
//		}
		
//		@Override
//		public void mouseReleased(MouseEvent e) {
//			//things for when the mouse button is released
//		}
	}
}

