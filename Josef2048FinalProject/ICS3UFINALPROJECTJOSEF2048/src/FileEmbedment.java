import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
/*
 * Class gets files embedded in a JAR file.
 * 
 * Adapted version of Mr. Benum's Jar Embedment Example Program.
 * @author Josef Gavronskiy (2023-12-28 -> 2024-01-21) for ICS3U course
 * 
 * Recourses:
 * Google classroom and Mr. Benum's JarEmbedment written tutorial
 */

public class FileEmbedment {
	/**
	 * Returns an ImageIcon embedded in the JAR (or Recourses folder)
	 * Precondition: image exists and is located in the Recourses folder
	 * Postcondition: returns an ImageIcon if it exits, if not, returns null.
	 * */
	public ImageIcon returnImageIcon(String name) {
		ImageIcon icon = null;
		try {
			BufferedImage image = ImageIO.read(getClass().getResourceAsStream(name));
		    icon = new ImageIcon(image);
			return icon;
		} catch (Exception e) {
			return icon;
		}
	}
	 
}
