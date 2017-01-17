package ravensproject;

public enum ChangeType {

	ShapeChange(6,2),
	Deleted(5,0),
	Added(5,1),
	Scaled(4,5),
	ChangeFill(4,3),
	Rotated(3,4),
	Moved(3,6),
	Reflect(2,7),
	Unchanged(0,-1);
	
	public final int point;
	public final int index;
	ChangeType(int p, int i) {
		this.point=p;
		this.index=i;
	}
	
	public int getPoint() {
		return point;
	}
}
