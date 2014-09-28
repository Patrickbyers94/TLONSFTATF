package gui;

public class Movement {
private boolean up;
private boolean right;
private boolean down;
private boolean left;

public boolean isMoving(){
	return (up||right||down||left);
}

public boolean isUp() {
	return up;
}
public boolean isRight() {
	return right;
}
public boolean isDown() {
	return down;
}
public boolean isLeft() {
	return left;
}
public void setUp(boolean up) {
	this.up = up;
}
public void setRight(boolean right) {
	this.right = right;
}
public void setDown(boolean down) {
	this.down = down;
}
public void setLeft(boolean left) {
	this.left = left;
}


}
