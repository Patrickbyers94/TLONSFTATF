package graphics;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class SpriteFactory {

	Map<String, Image> sprites = new HashMap<String, Image>();
	
	
	/**
	 * This is for sprites that don't have animations
	 */
	public SpriteFactory(){
		try {
			sprites.put("key", ImageIO.read(new File("assets/sprites/key.png")));
			sprites.put("triforce", ImageIO.read(new File("assets/sprites/antriforce.png")));
			sprites.put("box", ImageIO.read(new File("assets/sprites/box.png")));
		} catch (IOException e) {
			System.out.println("error reading in image files in the spriteFactory Class");
			e.printStackTrace();
		}
	}
	
	public Image get(String image){
		return sprites.get(image);
	}
	
	
}
