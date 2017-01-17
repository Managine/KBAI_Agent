package ravensproject;

public class ChangeVector implements Comparable<ChangeVector>{
	
	private final static int vectorSize=8;
	
	boolean usable=true;
	int[] vector=new int[vectorSize];	//0-delete 1-add 2-shape change 3-fill 4-rotate 5-scale 6-move 7-reflect
	ChangeVector() {
		for (int i=0;i<vector.length;i++)
			vector[i]=0;
	}
	
	ChangeVector(int val) {
		for (int i=0;i<vector.length;i++)
			vector[i]=val;
	}

	public ChangeVector getSimilarity(ChangeVector vec) {
		ChangeVector res=new ChangeVector();
		for (int i=0;i<vectorSize-1;i++) {
			if (vector[i]!=vec.vector[i]) {
				if (vector[i]==0 || vec.vector[i]==0) {
					res.usable=false;
					return res;
				}
				res.vector[i]=vector[i]-vec.vector[i];
			}
		}
		return res;
	}
	
	public void setChangeValue(ChangeType type, int val) {
		vector[type.index]=val;
	}
	
	public void addDiff(ChangeVector r) {
		if (!r.usable){
			usable=false;
			return;
		}
		for (int i=0;i<vectorSize;i++) {
			vector[i]=Math.abs(vector[i]);
			vector[i]+=Math.abs(r.vector[i]);
		}
	}
	
	public void setToVector(ChangeVector r) {
		for (int i=0;i<vectorSize;i++)
			vector[i]=r.vector[i];
	}

	@Override
	public int compareTo(ChangeVector r) {
		if (!usable && !r.usable)
			return 0;
		if (!usable)
			return 1;
		if (!r.usable)
			return -1;
		if (vector[ChangeType.Reflect.index]!=0 || r.vector[ChangeType.Reflect.index]!=0) {
			if (vector[ChangeType.Reflect.index]!=0 && r.vector[ChangeType.Reflect.index]==0)
				return -1;
			if (vector[ChangeType.Reflect.index]==0 && r.vector[ChangeType.Reflect.index]!=0)
				return 1;
		}
		
		boolean less=true;
		boolean more=true;
		for (int i=0;i<vectorSize;i++) {
			if (vector[i]<r.vector[i]) {
				less&=true;
				more=false;
			} else if (vector[i]>r.vector[i]) {
				more&=true;
				less=false;
			}
		}
		if (less && more)
			return 0;
		else if (less)
			return -1;
		else if (more)
			return 1;
		return 0;
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append("[");
		for (int i=0;i<vectorSize;i++)
			sb.append(vector[i]+",");
		sb.delete(sb.length()-1, sb.length());
		sb.append("] "+usable);
		return sb.toString();
	}
}