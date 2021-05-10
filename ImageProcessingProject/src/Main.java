import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JOptionPane;

public class Main {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Path path1 = FileSystems.getDefault().getPath("../images");
		Path path2 = FileSystems.getDefault().getPath("./icons");
		if(Files.notExists(path1) || Files.notExists(path2)) {
			JOptionPane.showMessageDialog(null,"Folder: \"images\" or \"icons\" not found.","Saving",JOptionPane.INFORMATION_MESSAGE);
		}
		MyFrame mainFrame = new MyFrame(); 
		System.out.println("Application successfully compiled!");
	}
	
}
