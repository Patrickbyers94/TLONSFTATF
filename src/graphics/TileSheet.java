package graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;


public class TileSheet implements Serializable{

	private BufferedImage tiles;
	private int width;


	private int height;
	private int tileWidth;
	private int tileHeight;
	private int tileSize;
	private Image path;
	private List<Image> images;
	private List<Boolean> passableTiles;
	private Image ice;
	Image[][] ims;

	public TileSheet(String filename, int tileWidth, int tileHeight){

			tileSize = tileWidth;
			this.tileHeight = tileHeight;
			this.tileWidth = tileWidth;
			chopSheet(filename);

	}

	public void draw(Graphics2D g){
		g.drawImage(tiles, 0 ,0 , null);
	}

	public Image getImage(){
		return tiles;
	}

	/**
	 * returns a cut up tile
	 * @param x
	 * @param y
	 * @return
	 */
	public Image getSubImage(int x, int y, int w, int h){
		return tiles.getSubimage(x, y, w, h);
	}

	public Image getPath(){
		if(path==null){
			path = tiles.getSubimage(32, 32, 32, 32);
		}
		return path;
	}


	/**
	 * internal workings that reads in a text file containing the names of the images and a boolean flag that
	 * indicates whether or not that tile is passable
	 * @param filename
	 */
	private void chopSheet(String filename){

		try{
			//Scanner sc = new Scanner(new File("assets/sheet1.txt"));
			Scanner sc = new Scanner(new File("assets/sheet2.txt"));
			ice = ImageIO.read(new File("assets/sprites/ice.png"));
			tiles = ImageIO.read(new File(filename));
			images = new ArrayList<Image>();
			passableTiles = new ArrayList<Boolean>();
			width = tiles.getWidth();
			height = tiles.getHeight();
			while(sc.hasNext()){
				String assetName = sc.next();
				String canPass = sc.next();
				if(canPass.equals("T")){
					passableTiles.add(true);
				}else{
					passableTiles.add(false);
				}
			}
			// chops up the images here
			for(int row = 0; row < height/tileSize; row++){
				for(int col =0; col < width/tileSize; col++ ){
					images.add(tiles.getSubimage(col*tileSize, row*tileSize, tileSize, tileSize));
				}
			}

		}catch (IOException e) {
			System.out.println("couldn't find image. Please check the path name");
			e.printStackTrace();
		}

	}

	public List<Boolean> getPassableBools(){
		return passableTiles;
	}

	public void printBooles(){
		for(Boolean b: passableTiles){
			System.out.println(b);
		}
	}

	public Boolean getPassable(int index){
		return passableTiles.get(index);
	}

	public Image get(int index){
		return images.get(index);
	}

	public int size(){
		return images.size();
	}

	public Image getIce(){
		return ice;
	}


	/*
	 * Get'ers
	 */

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getTileWidth() {
		return tileWidth;
	}


	public int getTileHeight() {
		return tileHeight;
	}


}
