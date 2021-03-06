import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

public class Func {
	static double rgb2yuv[][]= {{.299,.587,.114},
						{-.299,-.587,.886},
						{.701,-.587,-.114}};
	static double yuv2rgb[][]= {{1,0,1},
						{1,-.1942,-.5094},
						{1,1,0}};
	static int rgbMax = 16777216;
	
//	static int ditherMtx[][] = {{0,3},{2,1}};
	static int ditherMtx[][] = {{0,8,2,10},{12,4,14,6},
								{3,11,1,9},{15,7,13,5}};
	
//	static int ditherMtx[][] = {{0,8,2,10},{12,4,14,6},
//								{3,11,1,9},{15,7,13,5}};
//	static int ditherMtx[][] = {
//			{1,33,9,41,3,35,11,43},
//			{49,17,57,25,51,19,59,27},
//			{13,45,5,37,15,47,7,39},
//			{61,29,53,21,63,31,55,23},
//			{4,36,12,44,2,34,10,42},
//			{52,20,60,28,50,18,58,26},
//			{16,48,8,40,14,46,6,38},
//			{64,32,56,24,62,30,54,22}};
	
//	static int ditherMtx[][] = {
//			{0,32,8,40,2,34,10,42},
//			{48,16,56,24,50,18,58,26},
//			{12,14,4,36,14,46,6,38},
//			{60,28,52,20,60,30,54,22},
//			{3,35,11,43,1,33,9,41},
//			{51,19,59,27,49,17,57,25},
//			{15,47,7,39,13,45,5,37},
//			{63,31,55,23,61,29,53,21}};
	static double r = 65536, g=256;
	public static BufferedImage grayScale(final Color rgb[], int width, int height) {

		BufferedImage ret = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB_PRE);
		int k = 0;
		for(int i = 0;i<height;i++) 
			for(int j  = 0; j< width;j++) {
				int y = (int)(rgb[k].getRed()*rgb2yuv[0][0] + rgb[k].getGreen()*rgb2yuv[0][1] + rgb[k].getBlue()*rgb2yuv[0][2]);
				ret.setRGB(j, i, new Color(y,y,y).getRGB());
				k++;
			}
		return ret;
	}
	public static BufferedImage dithering(final Color rgb[], int width, int height) {
		BufferedImage ret = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB_PRE);
		double var = rgbMax / ditherMtx.length;
		int k =0;
		int len = ditherMtx[0].length;
		for(int i = 0;i<height;i++)
			for(int j = 0; j<width;j++) {
				int x = (int) ((rgb[k].getRed()*r+rgb[k].getGreen()*g+rgb[k].getBlue())/var);
				if(x>ditherMtx[j%len][i%len]) ret.setRGB(j, i, new Color(255,255,255).getRGB());
				else ret.setRGB(j, i, new Color(0,0,0).getRGB());
				k++;
			}
		return ret;
	}
	public static BufferedImage dithering(BufferedImage img, int width, int height) {
		BufferedImage ret = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB_PRE);
		double var = rgbMax/ditherMtx.length;
		int len = ditherMtx[0].length;
		for(int i=0;i<height;i++) {
			for(int j = 0;j<width;j++) {
				int x = (int)(img.getRGB(j, i)/var);
				if(x>ditherMtx[j%len][i%len])ret.setRGB(j, i, new Color(255,255,255).getRGB());
				else ret.setRGB(j, i, new Color(0,0,0).getRGB());
			}
		}
		return ret;
	}
	public BufferedImage dynamicRange(BufferedImage img) {
		return null;
	}
}
