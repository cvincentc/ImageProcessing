


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TIFFimage {
	BufferedImage image;
	TIFFHeader header;
	static int dataSize[] = {0,1,1,2,4,16,1,1,2,4,16,4,8};
	static String dataType[] = {"","BYTE","ASCII","SHORT","LONG",
			"RATIONAL","SBYTE","UNDEFINEDED","SSHORT","SLONG",
			"SRATIONAL","FLOAT","DOUBLE"};
	DEntry entry[];
	int raw[];
	int imageW;
	int imageL;
	String dir;
	TIFFimage(String path) throws IOException {
		dir = path;
		readFile(path);
		header = new TIFFHeader(raw);
		entry = new DEntry[(int) getEntryCount()];
		readIFD();
		readImgSize();
		loadImage();
	}
	TIFFimage(BufferedImage img) {
		image = img;
		imageW = img.getWidth();
		imageL = img.getHeight();
	}

	public int getWidth() {
		return imageW;
	}
	public int getHeight() {
		return imageL;
	}

	
	private void loadImage() {
		image = new BufferedImage(imageW,imageL,BufferedImage.TYPE_INT_ARGB_PRE);
		int stripIndex=0,byteCountIndex=0;
		for(int i = 0;i<entry.length;i++) {
			int tag = entry[i].getTag();
			if(tag == 258)
				entry[i].getCount();
			else if(entry[i].getTag()==273) stripIndex = i;
			else if(entry[i].getTag()==279) {
				byteCountIndex = i;
				break;
			}
		}
		
		if(entry[stripIndex].isOffset()) {
			for(int i = 0;i<entry[byteCountIndex].getCount();i++) {
				entry[byteCountIndex].getValue(i);
			}
			int k = 0;
			
			for(int y = 0;y<entry[stripIndex].getCount();y++) {
				int offset = entry[stripIndex].getValue(y);
				for(int x = 0;x<entry[byteCountIndex].getValue(y);x+=3) {
					//assuming rgb image
					image.setRGB(k%imageW, k/imageW, new Color(raw[x+offset],raw[x+offset+1],raw[x+offset+2]).getRGB());
					
					k++;
				}
				
			}
			
		}
		else {
			int offset = entry[stripIndex].getValue(0);
			for(int y = 0;y<imageL;y++) {
				for(int x = 0;x<imageW;x++) {
					//assuming rgb image
					image.setRGB(x, y, new Color(raw[offset],raw[offset+1],raw[offset+2]).getRGB());
					offset+=3;
				}
			}
		}
		
	}
	public void saveImg( BufferedImage newImg, File file) throws IOException {
		//FileWriter myWriter = new FileWriter(fileName);
		int addr=0;
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(73);
		fos.write(73);
		fos.write(42);
		fos.write(0);
		fos.close();
		writeInt(file,8,4);
		writeInt(file,8,2);
		addr = 10+8*12+4;
		
		for(int i =0;i<entry.length;i++) {
			int tag = entry[i].getTag();
			if(tag == 256 ||tag == 257) {//width height
				writeInt(file,entry[i].getTag(),2);
				writeInt(file,entry[i].getType(),2);
				writeInt(file,1,4);
				if(tag == 256) {
					writeInt(file,newImg.getWidth(),4);
				}
				else writeInt(file,newImg.getHeight(),4);
			}
			else if(tag == 258) {	//bits per sample
				writeInt(file,entry[i].getTag(),2);
				writeInt(file,3,2);
				writeInt(file,3,4);
				writeInt(file,addr,4);
				addr+=6;
			}
			else if(tag == 259) {	//compression
				writeInt(file,entry[i].getTag(),2);
				writeInt(file,entry[i].getType(),2);
				writeInt(file,entry[i].getCount(),4);
				writeInt(file,entry[i].getValue(0),4);
			}
			else if(tag == 262||tag == 284) { //PhotometricInterpretation   284:PlanarConfiguration
				writeInt(file,entry[i].getTag(),2);
				writeInt(file,entry[i].getType(),2);
				writeInt(file,entry[i].getCount(),4);
				writeInt(file,entry[i].getValue(0),4);
			}
			else if(tag == 273) {	//strip offset
				writeInt(file,entry[i].getTag(),2);
				writeInt(file,entry[i].getType(),2);
				writeInt(file,1,4);
				writeInt(file,addr,4);
				addr = addr + image.getHeight()*image.getWidth()*3;
			}
			else if(tag == 279) {	//StripByteCounts
				writeInt(file,entry[i].getTag(),2);
				writeInt(file,4,2);
				writeInt(file,1,4);
				writeInt(file,image.getHeight()*image.getWidth()*3,4);
				System.out.println();
				
			}
			else if(tag==284) {
				writeInt(file,entry[i].getTag(),2);
				writeInt(file,4,2);
				writeInt(file,1,4);
				writeInt(file,entry[i].getValue(0),4);
			}
//			else if(tag == 278) {
//				writeInt(file,entry[i].getType(),2);
//				writeInt(file,1,4);
//				writeInt(file,1,4);
//			}
//			else {
//				writeInt(file,entry[i].getType(),2);
//				writeInt(file,entry[i].getCount(),4);
//				if(entry[i].isOffset()) {
//					writeInt(file,addr,4);
//					addr = addr + entry[i].getCount() * entry[i].getDataSize();
//				}
//				else if(entry[i].getTag()==282 || entry[i].getTag()==283) {
//					writeInt(file,addr,4);
//					addr += 8;
//				}
//				else {
//					writeInt(file,entry[i].getValue(0),4);
//				}
//			}
		}
		writeInt(file,0,4);
		for(int i = 0;i<entry.length;i++) {
			int tag = entry[i].getTag();
			if(tag == 258) {
				writeInt(file,8,2);
				writeInt(file,8,2);
				writeInt(file,8,2);
			}
			else if(tag == 273) {
				writeRGB(file,newImg);
			}

//			if(entry[i].getTag() == 273 ) {
//				writeRGB(file,newImg);
//			}
//			else if(entry[i].getTag()==282 || entry[i].getTag()==283) {
//				writeInt(file,entry[i].getValue(0),4);
//				writeInt(file,entry[i].getValue(1),4);
//			}
//			else if(entry[i].isOffset() && entry[i].getTag() != 279) {
//				for(int j = 0;j<entry[i].getCount();j++) {
//					writeInt(file,entry[i].getValue(j),entry[i].getDataSize());
//				}
//			}
		}
	}
	
	
	public void writeInt(File file, int x, int bytes) throws IOException {
		FileOutputStream os = null;
		os = new FileOutputStream(file,true);
		int y = x;
		for(int i = 0;i<bytes;i++) {
		
			os.write(y%(bytes*256));
			y /=256;
		}
		os.close();
	}
	
	public void writeRGB(File file, BufferedImage img) throws IOException {
		FileOutputStream os = null;
		os = new FileOutputStream(file,true);
		for(int y = 0; y<img.getHeight();y++) {
			for(int x = 0;x<img.getWidth();x++) {
				Color rgb = new Color(img.getRGB(x,y));
				os.write(rgb.getRed());
				os.write(rgb.getGreen());
				os.write(rgb.getBlue());
			}
		}
		os.close();
	}
	public BufferedImage getImage() {
		return image;
	}
//	private void loadRGB() {
//		int k =0;
//		for(int i = 0;i<stripCount;i++) {
//			int x =  stripOffset[i];
//			for(int j = 0;j < stripByteCount[i];j+=3,x+=3) {
//				if(header.ending =="MM") {
//					rgb[k] = new Color(raw[x], raw[x+1], raw[x+2]);
//				}
//				else if(header.ending =="II")
//					rgb[k] = new Color(raw[x], raw[x+1], raw[x+2]);
//				k++;
//			}
//		}
//		
//	}
	
	private void readImgSize() {
		for(int i = 0; i<entry.length;i++) {
			int tag = entry[i].getTag();
			if(tag == 256) {
				if(entry[i].isOffset()) {
					//do something
				}
				else imageW = entry[i].getValue(0);
			}
			else if(tag == 257) {
				if(entry[i].isOffset()){
					//do something
				}
				else imageL = entry[i].getValue(0);
			}
		}
	}
	private void readIFD() {
		int x = header.firstIFD+2;
		for(int i = 0; i < entry.length; i++,x+=12 ) {
			entry[i]= new DEntry(raw,x,header.ending);
		}
		
	}
	private void readFile(String path) throws IOException {
		InputStream in = null;
		in = new FileInputStream(path);
		raw = new int[in.available()];
		int count = 0;
		int data = in.read();
		while(data != -1) {
			raw[count]=data;
			count++;
			data = in.read();
		}
		in.close();
		
		
//		FileWriter myWriter = new FileWriter("lenaRaw.txt");
//		for(int i = 0;i<count;i++) {
//			String s = Integer.toHexString(raw[i]);
//			if(s.length()<2) s= "0"+s;
//			myWriter.write(i +" " + s + "\n");
//		}
//		myWriter.close();
//		System.out.println("read");
	}
	public int decode2(int x,int y) {
		String s = Integer.toHexString(x);
		String t = Integer.toHexString(y);
		while(t.length()<2) 
			t = "0"+t;
		while(s.length()<2) 
			s = "0"+s;
		return Integer.parseInt(s+t,16);
	}
	
	private int getEntryCount() {
		int x = header.firstIFD;
		if(header.ending=="MM") {
			return decode2(raw[x],raw[x+1]);
		}
		else if(header.ending =="II") {
			return decode2(raw[x+1],raw[x]);
		}
		return 0;
	}

	public int decode4(int w, int x,int y,int z) {
		String s = Integer.toHexString(w);
		String t = Integer.toHexString(x);
		String u = Integer.toHexString(y);
		String v = Integer.toHexString(z);
		for(int i =1;i<=2;i++) {
			if(s.length()<i) s = "0" + s;
			if(t.length()<i) t = "0" + t;
			if(u.length()<i) u = "0" + u;
			if(v.length()<i) v = "0" + v;
		}
		s= s+t+u+v;
		return Integer.parseInt(s,16);
	}
	
	public void showInfo() {
		System.out.println("\nimage: "+ dir);
		System.out.println("\nfirst IFD:"+header.firstIFD);
		System.out.println("\n[\n\"ifdEntries\"\n:");
		for(int i =0;i<entry.length;i++) {
			entry[i].showEntry();	
			if(i != entry.length-1) System.out.println();
		}
		System.out.println("]");
	}
	
	public class TIFFHeader{
		String ending; 
		String format;
		int firstIFD;
		TIFFHeader(final int rawData[]){
			if(rawData[0]==rawData[1] && rawData[1] == 73 && rawData[2] ==42 ) {
				ending = "II";
				format = "TIFF";
			}
			else if(rawData[0]==rawData[1] && rawData[1] == 77 && rawData[3] ==42 )  {
				ending ="MM";
				format = "TIFF";
			}
			else {
				format = "undefined";
				ending = "undefined";
			}
			if(ending == "II") {
				firstIFD = decode4(rawData[7],rawData[6],rawData[5],rawData[4]);
			}
			else if(ending == "MM") {
				firstIFD = decode4(rawData[4],rawData[5],rawData[6],rawData[7]);
			}
		}
		
	}
//	long ImageWidth;
//	long ImageLength;
//	short BitsPerSample;
//	short Compression;
//	short PhtotmetricInterpretation;
//	long StripOffsets;
//	short SamplesPerPixel;
//	long RowsPerStrip;
//	long StripByteCounts;
//	double XResolution;
//	double YResolution;
//	short ResolutionUnit;
}
