
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;





public class MyFrame extends JFrame implements ActionListener{
	JMenuBar menu;
	JMenu fileMenu;
	JMenu imageMenu;
	JMenuItem openItem;
	JMenuItem saveItem;
	JMenuItem exitItem;
	Image appIcon;
	ImageIcon fileIcon;
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	ImagePanel imagePanel;
	JPanel leftPanel;
	JPanel rightPanel;
	BufferedImage defaultImg;
	BufferedImage grayImage;
	BufferedImage ditheredImage;
	BufferedImage dynamicImage;
	TIFFimage tifImage;
	File file=null;
	int frameW = 720;
	int frameH = 720;
	int WWIDTH = (int)screenSize.getWidth()/2;
	int WHEIGHT = (int)screenSize.getHeight()/2;
	JButton processButton;
	JPanel bottomPanel;
	MyFrame() throws Exception{
		processButton = new JButton("Process");
		bottomPanel = new JPanel();
		bottomPanel.add(processButton);
		processButton.addActionListener(this);
		processButton.setEnabled(false);
		//application icons
		appIcon = new ImageIcon("icons/imageIconS.png").getImage();
		fileIcon = new ImageIcon("icons/folderSmall.png");
		
		//frame settings
		this.setTitle("Image Processor");
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocation(WWIDTH-frameW/2,WHEIGHT-frameH/2);
		this.setLayout(new BorderLayout());
		this.setSize(frameW,frameH);
		
		//menu options
		fileMenu = new JMenu("File");
		
		//set menu option icons
		fileMenu.setIcon(fileIcon);
		
		//menu items
		openItem = new JMenuItem("Open");
		saveItem = new JMenuItem("Save");
		exitItem = new JMenuItem("Exit");
		
		openItem.addActionListener(this);
		saveItem.addActionListener(this);
		exitItem.addActionListener(this);
		
		//add key bindings
		fileMenu.setMnemonic(KeyEvent.VK_F); //alt + F for file menu
		openItem.setMnemonic(KeyEvent.VK_O);
		saveItem.setMnemonic(KeyEvent.VK_S);
		exitItem.setMnemonic(KeyEvent.VK_E);
		
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(exitItem);
		
		menu = new JMenuBar();
		menu.add(fileMenu);
		//menu.setBackground(Color.LIGHT_GRAY);
		//defaultImg = readImg("images/balloons.tif");
		defaultImg = readImg("images/defaultImg.png");
		
		
		
		imagePanel = new ImagePanel(defaultImg);
		
		
		this.add(imagePanel,BorderLayout.CENTER);
		this.add(menu,BorderLayout.NORTH);
		this.add(bottomPanel,BorderLayout.SOUTH);
		this.setIconImage(appIcon);
		this.setVisible(true);
	}

	public BufferedImage readImg(String path) {
		if(path.endsWith(".tif")) {
			try {
				tifImage = new TIFFimage(path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return tifImage.getImage();
		}
		else {
			BufferedImage image = null;
			try {
				image = ImageIO.read(new File(path));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return image;
		}
		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(e.getActionCommand());
		if(e.getSource() == openItem) {
			
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File("."));
			int response = fileChooser.showOpenDialog(null);
			if(response == JFileChooser.APPROVE_OPTION){
				String file = fileChooser.getSelectedFile().getAbsolutePath();
				BufferedImage newImage = null;
				newImage = readImg(file);
				imagePanel.openImage(newImage);
				processButton.setEnabled(true);
			}
		}
		else if(e.getSource() == saveItem) {
			System.out.println("Saving...");
		}
		else if(e.getSource() == exitItem) {
			System.exit(0);
		}
		else if(e.getSource() == processButton) {
			grayImage = Func.grayScale(tifImage.getRGB(),tifImage.getWidth(),tifImage.getHeight());
			imagePanel.changeImg2(grayImage,"Grayscale");
			//ditheredImage = Func.dithering(grayImage, grayImage.getWidth(), grayImage.getHeight());
			ditheredImage = Func.dithering(tifImage.getRGB(), tifImage.getWidth(), tifImage.getHeight());
			imagePanel.changeImg3(ditheredImage,"Ordered dithering");
			System.out.println("Processing...");
		}
		
	}
}