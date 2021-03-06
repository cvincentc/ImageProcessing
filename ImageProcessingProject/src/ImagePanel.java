import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class ImagePanel extends JPanel implements ActionListener{
	BufferedImage ig;
	JPanel scrollPanel;
	ImageLabel imageLabel1;
	ImageLabel imageLabel2;
	ImageLabel imageLabel3;
	ImageLabel imageLabel4;
	JLabel labelTitle1;
	JLabel labelTitle2;
	JLabel labelTitle3;
	JLabel labelTitle4;
	JScrollPane scrollFrame;
	int processed = 0;
//	ImagePanel(){
//		scrollPanel = new JPanel();
//		scrollFrame = new JScrollPane(scrollPanel);
//		this.add(scrollPanel);
//		this.setBackground(Color.LIGHT_GRAY);
//		this.setLayout(new FlowLayout());
//	}
	
	ImagePanel(BufferedImage img){
		scrollPanel = new JPanel();
		scrollPanel.setBackground(Color.LIGHT_GRAY);
		scrollPanel.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
		scrollFrame = new JScrollPane(scrollPanel);
		scrollPanel.setAutoscrolls(true);
		scrollFrame.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollFrame.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollFrame.setPreferredSize(new Dimension(img.getWidth()+20,img.getHeight()+20));
		this.add(scrollFrame);
		this.setBackground(Color.LIGHT_GRAY);
		this.setLayout(new FlowLayout());
		initUI(img);
		openImage(img);
	}

//	public void paintComponent(Graphics g) {
//		super.paintComponent(g);
//		Graphics2D g2d = (Graphics2D) g;
//			g2d.drawImage(ig,0,20, null);
//		
//	}
	public void openImage(BufferedImage image) {
			
			resetImages(image);
			Dimension imageSize = new Dimension(image.getWidth(),image.getHeight());
			setScrollFrameSize(imageSize,scrollFrame);
			setViewPanelSize(labelTitle1.getSize(),imageSize,scrollPanel);
	}
	public void resetImages(BufferedImage image) {
		imageLabel1.changeImage(image);
		imageLabel2.changeImage(image);
		imageLabel3.changeImage(image);
		imageLabel4.changeImage(image);
		labelTitle1.setText("Original");
		labelTitle2.setText("Original");
		labelTitle3.setText("Original");
		labelTitle4.setText("Original");
	}
	
	public void changeImg2(BufferedImage img, String s) {
		imageLabel2.changeImage(img);
		labelTitle2.setText(s);
	}
	public void changeImg3(BufferedImage img, String s) {
		imageLabel3.changeImage(img);
		labelTitle3.setText(s);
	}
	public void changeImg4(BufferedImage img, String s) {
		imageLabel4.changeImage(img);
		labelTitle4.setText(s);
	}
	
	public void initUI(BufferedImage image) {
		String s = "Original";
		imageLabel1 =  new ImageLabel(image);
		imageLabel2 =  new ImageLabel(image);
		imageLabel3 =  new ImageLabel(image);
		imageLabel4 =  new ImageLabel(image);
		labelTitle1 = new JLabel(s);
		labelTitle2 = new JLabel(s);
		labelTitle3 = new JLabel(s);
		labelTitle4 = new JLabel(s);
		labelTitle1.setForeground(Color.black);
		labelTitle2.setForeground(Color.black);
		labelTitle3.setForeground(Color.black);
		labelTitle4.setForeground(Color.black);
		labelTitle1.setHorizontalTextPosition(JLabel.CENTER);
		scrollPanel.add(imageLabel1);
		scrollPanel.add(labelTitle1);
		scrollPanel.add(imageLabel2);
		scrollPanel.add(labelTitle2);
		scrollPanel.add(imageLabel3);
		scrollPanel.add(labelTitle3);
		scrollPanel.add(imageLabel4);
		scrollPanel.add(labelTitle4);
	}
	
	public void setScrollFrameSize(Dimension imageSize, JScrollPane pane) {
		int paneW, paneH;
		int barW;
		
		barW = 14;
		paneW = (int) (imageSize.getWidth()+pane.getVerticalScrollBar().getWidth());
		paneH = (int) (imageSize.getHeight()) + 20;
		pane.getVerticalScrollBar().setPreferredSize(new Dimension(14,paneH));
		pane.setSize(new Dimension(paneW+ barW+4 ,paneH));
		System.out.println(imageSize);
		System.out.println(pane.getSize());
	}
	public void setViewPanelSize(Dimension labelSize, Dimension imageSize, JPanel panel) {
		int viewW,viewH;
		viewW = (int) (imageSize.getWidth());
		viewH = (int) (imageSize.getHeight()*4 + labelSize.getHeight()*4);
		if(labelSize.getHeight()==0) viewH += 4*20;
		System.out.println("height:"+labelSize.getHeight());
		panel.setPreferredSize(new Dimension(viewW,viewH));
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		System.out.println("performed");
		repaint();
	}

	

}
