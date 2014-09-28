package game;

public class Ice extends Position {

	private World world;

	public Ice(int col, int row) {
		super(col, row);
	}

	@Override
	public void setGameObject(GameObject g) {
		super.setGameObject(g);
		//System.out.println("Gets here");
		// object needs moving
		if (g != null) {
			if (g instanceof Player) {
				((Player) g).move(((Player) g).getOrientation());
			} else if (g instanceof MovableObject) {
				((MovableObject) g).move(((MovableObject) g).getOrientation());
			}
		}
	}

}
