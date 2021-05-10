
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;





@SuppressWarnings("serial")
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
	BufferedImage image;
	BufferedImage defaultImg;
	BufferedImage grayImage;
	BufferedImage ditheredImage;
	BufferedImage dynamicImage;
	TIFFimage tifImg=null;
	String imgName;
	int numOfImg = 4;
	BufferedImage album[];
	String imgType[]= {"Original","Grayscale","Ordered dithering", "Dynamic range adjusted"}; 
	int n=0;
	File file=null;
	int frameW = 720;
	int frameH = 720;
	int WWIDTH = (int)screenSize.getWidth()/2;
	int WHEIGHT = (int)screenSize.getHeight()/2;
	JButton nextButton;
	JButton quitButton;
	JPanel bottomPanel;
	JLabel nextImg;
	MyFrame() throws Exception{
		album = new BufferedImage[numOfImg];

		
		nextButton = new JButton("Next");
		quitButton = new JButton("Quit");
		nextImg = new JLabel("");
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout()); 
		bottomPanel.add(nextButton,BorderLayout.WEST);
		bottomPanel.add(quitButton,BorderLayout.EAST);
		bottomPanel.add(nextImg,BorderLayout.CENTER);
		nextButton.addActionListener(this);
		quitButton.addActionListener(this);
		nextButton.setEnabled(false);
		
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
		
		//filemenu options
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(exitItem);
		menu = new JMenuBar();
		
		
		defaultImg = readImg("icons/defaultImg.tif");
		menu.add(fileMenu);
		imagePanel = new ImagePanel(defaultImg,"Welcome");
		this.add(imagePanel,BorderLayout.CENTER);
		this.add(menu,BorderLayout.NORTH);
		this.add(bottomPanel,BorderLayout.SOUTH);
		this.setIconImage(appIcon);
		this.setVisible(true);
	}
	
	
	//read different format of images
	public BufferedImage readImg(String path) {
		
		if(path.endsWith(".tif") || path.endsWith(".tiff")) {
			try {
				tifImg = new TIFFimage(path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tifImg.showInfo();
			return tifImg.getImage();
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
		
		//open file option in filemenu
		if(e.getSource() == openItem) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File("../images"));
			int response = fileChooser.showOpenDialog(null);
			if(response == JFileChooser.APPROVE_OPTION){
				String file = fileChooser.getSelectedFile().getAbsolutePath();
				imgName = fileChooser.getSelectedFile().getName();
				image = readImg(file);
				imagePanel.openImage(image,imgType[0]); 
				processImages();
				n=0;
				nextImg.setText("Next: " +imgType[(n+1)%numOfImg] );
				nextButton.setEnabled(true);
			}
		}
		else if(e.getSource() == saveItem) {
			if(tifImg == null) {
				JOptionPane.showOptionDialog(null, "Sorry, nothing to save.","Save", JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE, null, null, null);
			}
			else {
				String p = "./processed";
				Path path = Paths.get(p);
				if(Files.notExists(path)) {
					try {
						Files.createDirectories(path);
					} catch (IOException f) {
						// TODO Auto-generated catch block
						f.printStackTrace();
					}
				}
				
				try {
					String name = null;
					if(imgName.endsWith(".tif")) {
						name = imgName.substring(0, imgName.length()-4);
					}
					else if(imgName.endsWith(".tiff")) {
						name = imgName.substring(0, imgName.length()-5);
					}
					File file1 = new File(p+"/"+name+"_grayScale.tif");
					tifImg.saveImg( grayImage, file1);
					File file2 = new File(p+"/"+name+"_ordered_dithering.tif");
					tifImg.saveImg( ditheredImage, file2);
					File file3 = new File(p+"/"+name+"_DN_adjustment.tif");
					tifImg.saveImg( dynamicImage, file3);
				} catch (IOException f) {
					// TODO Auto-generated catch block
					f.printStackTrace();
				}
				JOptionPane.showMessageDialog(null,"Images saved.","Save",JOptionPane.INFORMATION_MESSAGE);
			}
			
		}
		else if(e.getSource() == exitItem) {
			System.exit(0);
		}
		else if(e.getSource() == nextButton) {
			n = (n+1)% numOfImg;
			System.out.println("N:"+n +" " +album[n].getHeight() );
			imagePanel.changeImage(album[n],imgType[n]);
			nextImg.setText("Next: " +imgType[(n+1)%numOfImg] );
		}
		else if(e.getSource() == quitButton) {
			int a = JOptionPane.showConfirmDialog(bottomPanel, "Quit?");
			if(a == JOptionPane.YES_OPTION)
				System.exit(0);
		}
		
	}
	private void processImages() {
		//process opened image
		grayImage = Func.grayscale(image);
		ditheredImage = Func.dithering(grayImage);
		dynamicImage = Func.dynamicRange(image,.2);
		album[0]=image;
		album[1]=grayImage;
		album[2]=ditheredImage;
		album[3]=dynamicImage;
	}
}