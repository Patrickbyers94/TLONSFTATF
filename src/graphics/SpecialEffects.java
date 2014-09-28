package graphics;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;


/**
 *  This class renders special effects to the screen. A Special effect is something that affects the whole screen.
 *
 */

public class SpecialEffects {

	static SpriteFactory sf = new SpriteFactory();

	/**
	 * the screen blurs wildly when the character moves
	 * @param g
	 * @param width
	 * @param height
	 */
	public static void onDrugsMode(Graphics2D g, int width, int height){
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT, .8f));
		g.setColor(Color.black);
		g.fillRect(0, 0 , width, height);
	}
	/**
	 * Night time mode without a light
	 * @param g
	 * @param width
	 * @param height
	 */
	public static void nighttime(Graphics2D g, int width, int height){
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, .8f));
		g.setColor(Color.black);
		g.fillRect(0, 0 , width, height);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1));
	}


	/**
	 * Renders nighttime with a soft luscious light surrounding the player with variable darkness
	 * @param g2d
	 * @param width
	 * @param height
	 * @param lightlevel
	 * @param spritePos
	 * @param sources
	 */
	public static void nightTimeLight(Graphics2D g2d, int width, int height, float lightlevel, Point spritePos, List<Point> sources){

		int radius = 100;
		int x = (spritePos.x+16)-radius;
		int y = (spritePos.y+16)-radius;
		 g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			        RenderingHints.VALUE_ANTIALIAS_ON);
		// get image
		BufferedImage im = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) im.getGraphics();

		// make screen dark
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, lightlevel);
		g.setComposite(ac);

		g.setColor(new Color(50, 30, 40));
		g.fillRect(0, 0 , width, height);

		// set up gradient for the circle
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT, .8f));


		float[] dist = { 0.0f, .4f, .8f };
		Color[] cols = {new Color(0,0,0,250),new Color(0,0,0,120), new Color(0,0,0, 0)};

		g.setPaint(new RadialGradientPaint(x+radius, y+radius, (int)(radius*1.2), dist, cols, MultipleGradientPaint.CycleMethod.REFLECT ));


		g.fillOval(x, y, radius*2, radius*2);

		for(Point p : sources){
			g.setPaint(new RadialGradientPaint(p.x, p.y, (int)(radius*1.2), dist, cols, MultipleGradientPaint.CycleMethod.REFLECT ));
			g.fillOval(p.x-radius, p.y-radius, radius*2, radius*2);

		}


//		// lightsource2
//		for(LightSource s : sources){
//			g.setPaint(new RadialGradientPaint(s.getX()+radius, s.getY()+radius, (int)(radius*1.2), dist, cols, MultipleGradientPaint.CycleMethod.REFLECT ));
//
//			g.fillOval(s.getX(), s.getY(), radius*2, radius*2);
//
//		}


		g.dispose();
		g2d.drawImage(im, 0, 0, null);

	}

	public static RadialGradientPaint getGradientPaint(int x, int y){
		float[] dist = { 0.0f, .2f, 1f };
		Color[] cols = {new Color(89, 89, 178, 150),new Color(0,0,0,0), new Color(0,0,0,100)};

		return new RadialGradientPaint(x, y, 40, dist, cols, MultipleGradientPaint.CycleMethod.NO_CYCLE );


	}



	/**
	 * renders nighttime mode but with a flash light
	 * @param g2d
	 * @param width
	 * @param height
	 * @param spritePos
	 * @param direction
	 */
	public static void nightTimeWithFlashLight(Graphics2D g2d, int width, int height, Point spritePos, int direction){
		int sx = spritePos.x +16;
		int sy = spritePos.y +16;

		BufferedImage im = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) im.getGraphics();
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .9f);
		g.setComposite(ac);
		g.setColor(Color.black);
		g.fillRect(0, 0 , width, height);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, .2f));

		//g.draw
		g.fillPolygon(polygonForLight(sx,sy, direction));
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .2f));

		g.setColor(new Color(255, 147, 41)); // supposedly the color of candle light according to
											// http://planetpixelemporium.com/tutorialpages/light.html
		g.setColor(new Color(255, 209, 178));

		g.fillPolygon(polygonForLight(sx,sy, direction));

		g.dispose();

		g2d.drawImage(im, 0, 0, null);

	}

	/**
	 * gets the flashlight direction
	 * @param x
	 * @param y
	 * @param direction
	 * @return
	 */
	private static Polygon polygonForLight(int x, int y,int direction){
		// supposed to be a torch
		int width = 80;
		int length = 400;


		if (direction == 0) {
			int[] xs = { x, x - width, x + width, x };
			int[] ys = { y, y - length, y - length, y };
			return new Polygon(xs, ys, 4);
		}

		else if (direction == 1) {
			int[] xs = { x, x + length, x + length, x };
			int[] ys = { y, y + width, y - width, y };

			return new Polygon(xs, ys, 4);
		} else if (direction == 2) {
			int[] xs = { x, x - width, x + width, x };
			int[] ys = { y, y + length, y + length, y };

			return new Polygon(xs, ys, 4);
		} else {
			int[] xs = { x, x - length, x - length, x };
			int[] ys = { y, y - width, y + width, y };
			return new Polygon(xs, ys, 4);
		}

	}


	//private static


	private void drawGradientS(){
		Color c1 = new Color(255, 246, 118);
		Color c2 = new Color(251, 197, 133);
		Color c3 = new Color(123, 205, 250);
		GradientPaint dawn = new GradientPaint(0,0, c3, 5, 400, c2);
		//setPaint(dawn);

	}


}
