package ravensproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SemanticNetwork {
	
	private class ChangeEdge {
		Node nodeX, nodeY;
		List<ChangeType> change=new LinkedList<ChangeType>();
		int point=0;
		ChangeVector vector=new ChangeVector();
		int rotateAngle=0;
		
		@Override
		public String toString() {
			StringBuilder sb=new StringBuilder();
			for (ChangeType type:change)
				sb.append(type+" ");
			return nodeX.name+"->"+nodeY.name+" "+sb.toString();
		}
		
		public ChangeVector getSimilarity(ChangeEdge r) {
			ChangeVector res=vector.getSimilarity(r.vector);
			if (!res.usable)
				return res;
			if ((rotateAngle+r.rotateAngle) % 360 == 0) {
				res.setChangeValue(ChangeType.Reflect, 1);
			}
			return res;
		}
	}
	
	private class Node {
		String name;
		HashMap<String, String> attributes;
		ChangeEdge changeEdge;
		
		Node(RavensObject ob) {
			name=ob.getName();
			attributes=new HashMap<String, String>(ob.getAttributes());
		}
		
		Node(String name) {
			this.name=name;
			attributes=new HashMap<String, String>();
		}

		@Override
		public String toString() {
			return name;
		}
		
		@Override
		public boolean equals(Object o) {
			Node r=(Node)o;
			return name.equals(r.name);
		}
	}
	
	private class Picture {
		String name;
		List<Node> nodeList=new ArrayList<Node>();
		
		Picture(RavensFigure rf) {
			name=rf.getName();
			Set<String> keys=rf.getObjects().keySet();
			for (String key:keys) {
				Node node=new Node(rf.getObjects().get(key));
				nodeList.add(node);
			}
			sort();
		}
		
		@Override
		public String toString() {
			return name;
		}
		
		public void sort() {
			if (nodeList.size()==1) {
				return ;
			}
			
			String[] positions={"inside", "above","left"};
			List<Node> tmpList=new ArrayList<Node>();
			String p="";
			boolean found=false;
			for (String position:positions) {
				if (found)
					break;
				for (int i=0;i<nodeList.size();i++) {
					String attr=nodeList.get(i).attributes.get(position);
					if (attr!=null) {
						found=true;
						p=position;
						break;
					}
				}
			}
			if (!found)
				return ;
//			int baseL=1;
//			for (Node n:nodeList) {
//				if (n.attributes.get(p)==null) {
//					tmpList.add(n);
//					break;
//				}
//			}
//			while (baseL<=nodeList.size()*2-3) {
//				for (Node n:nodeList) {
//					if (n.attributes.get(p)==null)
//						continue;
//					if (n.attributes.get(p).length()==baseL) {
//						tmpList.add(n);
//						break;
//					}
//				}
//				baseL+=2;
//			}
			Map<String, List<String>> edgeMap=new HashMap<String, List<String>>();
			Map<String, Integer> countMap=new HashMap<String, Integer>();
			for (String position:positions) {
				for (Node n:nodeList) {
					String nodesStr=n.attributes.get(position);
					if (nodesStr==null)
						continue;
					String[] nodes=nodesStr.split(",");
					for (String node:nodes) {
						if (position.equals("inside")) {
							if (edgeMap.get(node)==null) {
								List<String> l=new ArrayList<String>();
								l.add(n.name);
								edgeMap.put(node, l);
							} else {
								edgeMap.get(node).add(n.name);
							}
							if (countMap.get(n.name)==null)
								countMap.put(n.name, 1);
							else
								countMap.put(n.name, countMap.get(n.name)+1);
						} else {
							if (edgeMap.get(n.name)==null) {
								List<String> l=new ArrayList<String>();
								l.add(node);
								edgeMap.put(n.name, l);
							} else {
								edgeMap.get(n.name).add(node);
							}
							if (countMap.get(node)==null)
								countMap.put(node, 1);
							else
								countMap.put(node, countMap.get(node)+1);
						}
					}
				}
			}
			for (Node n:nodeList) {
				if (countMap.get(n.name)==null)
					countMap.put(n.name, 0);
			}
			
			while (countMap.size()!=0) {
				List<String> toRemove=new LinkedList<String>();
				for (String key:countMap.keySet()) {
					int c=countMap.get(key);
					if (c==0) {
						tmpList.add(nodeList.get(nodeList.indexOf(new Node(key))));
						List<String> l=edgeMap.get(key);
						if (l!=null) {
							for (String r:l) {
								countMap.replace(r, countMap.get(r)-1);
							}
							edgeMap.remove(key);
						}
						toRemove.add(key);
					}
				}
				for (String r:toRemove)
					countMap.remove(r);
			}
			
			nodeList=tmpList;
		}
	}
	
	private int[] getCoord(String move) {
		if (move.equals("top-left"))
			return new int[]{0,0};
		if (move.equals("top-right"))
			return new int[]{1,0};
		if (move.equals("bottom-left"))
			return new int[]{0,1};
		if (move.equals("bottom-right"))
			return new int[]{1,1};
		System.out.println(move+" has no position");
		return new int[]{-1,-1};
	}
	
	private int getSize(String s) {
		if (s.equals("huge"))
			return 1;
		if (s.equals("very large"))
			return 2;
		if (s.equals("large"))
			return 3;
		if (s.equals("medium"))
			return 4;
		if (s.equals("small"))
			return 5;
		if (s.equals("very small"))
			return 6;
		return 0;
	}
	
	private int getMove(String start, String end) {
		int[] startCoord=getCoord(start);
		int[] endCoord=getCoord(end);
		return 10*(1+startCoord[0]-endCoord[0])+(1+startCoord[1]-endCoord[1]);
	}
	
	private int getPoint(Node src, Node des, ChangeEdge edge) {
		int point=0;
		if (des==null) {
			edge.change.add(ChangeType.Deleted);
			edge.vector.setChangeValue(ChangeType.Deleted, 1);
			return ChangeType.Deleted.point;
		}
		if (src==null) {
			edge.change.add(ChangeType.Added);
			edge.vector.setChangeValue(ChangeType.Added, 1);
			return ChangeType.Added.point;
		}

		String[] attrs={"shape","fill","angle","size","alignment"};
		for (int i=0;i<attrs.length;i++) {
			String attr=attrs[i];
			String attrSrc=src.attributes.get(attr);
			String attrDes=des.attributes.get(attr);
			if (attrSrc==null || attrDes==null)
				continue;
			if (attrSrc.equals(attrDes))
				continue;
			ChangeType change=ChangeType.Unchanged;
			switch (i){
				case 0:
					edge.vector.setChangeValue(ChangeType.ShapeChange, 1);
					change=ChangeType.ShapeChange;
					break;
				case 1:
					edge.vector.setChangeValue(ChangeType.ChangeFill, 1);
					change=ChangeType.ChangeFill;
					break;
				case 2:
					int attrSrcAngle=Integer.parseInt(attrSrc);
					int attrDesAngle=Integer.parseInt(attrDes);
					edge.rotateAngle=attrSrcAngle-attrDesAngle;
					edge.vector.setChangeValue(ChangeType.Rotated, edge.rotateAngle);
					change=ChangeType.Rotated;
					break;
				case 3:
					edge.vector.setChangeValue(ChangeType.Scaled, this.getSize(attrSrc)-this.getSize(attrDes));
					change=ChangeType.Scaled;
					break;
				case 4:
					edge.vector.setChangeValue(ChangeType.Moved, getMove(attrSrc, attrDes));
					change=ChangeType.Moved;
					break;
				default:
					break;
			}
			point+=change.point;
			edge.change.add(change);
		}
		return point;
	}
	
	private int getPoint(Node src, Node des) {
		return this.getPoint(src, des, new ChangeEdge());
	}
	
	private ChangeEdge getChangeEdge(Node src, Node des) {
		ChangeEdge edge=new ChangeEdge();
		
		if (des==null) {
			edge.nodeY=null;
			edge.nodeX=src;
			edge.change.add(ChangeType.Deleted);
			edge.point+=ChangeType.Deleted.point;
			return edge;
		}
		else if (src==null) {
			edge.nodeX=null;
			edge.nodeY=des;
			edge.change.add(ChangeType.Added);
			edge.point+=ChangeType.Added.point;
			return edge;
		}
		
		edge.nodeX=src;
		edge.nodeY=des;
		
		edge.point=getPoint(src, des, edge);
		if (edge.point==0) {
			edge.change.add(ChangeType.Unchanged);
		}
		
		return edge;
	}
	
	private int getBestMap(List<Node> left, int posL, List<Node> right, int posR, Map<Node, Node> map, int curPoint) {
		if (posL==left.size() && posR==right.size())
			return curPoint;
		if (posL==left.size()) {
			map.put(new Node("null"), right.get(posR));
			return this.getBestMap(left, posL, right, posR+1, map, curPoint+1);
		} else if (posR==right.size()){
			map.put(left.get(posL), null);
			return this.getBestMap(left, posL+1, right, posR, map, curPoint+1);
		}
		if (left.size()-posL > right.size()-posR) {
			HashMap<Node, Node> tmpMap1=new HashMap<Node, Node>(map);
			tmpMap1.put(left.get(posL), null);
			int p1=this.getBestMap(left, posL+1, right, posR, tmpMap1, curPoint+1);
			
			HashMap<Node, Node> tmpMap2=new HashMap<Node, Node>(map);
			tmpMap2.put(left.get(posL), right.get(posR));
			int p2=this.getBestMap(left, posL+1, right, posR+1, tmpMap2, curPoint+getPoint(left.get(posL),right.get(posR)));
			
			if (p1<p2) {
				map.putAll(tmpMap1);
				return p1;
			} else {
				map.putAll(tmpMap2);
				return p2;
			}
		} else if (left.size()-posL < right.size()-posR){
			HashMap<Node, Node> tmpMap1=new HashMap<Node, Node>(map);
			tmpMap1.put(new Node("null"), right.get(posR));
			int p1=this.getBestMap(left, posL, right, posR+1, tmpMap1, curPoint+1);
			
			HashMap<Node, Node> tmpMap2=new HashMap<Node, Node>(map);
			tmpMap2.put(left.get(posL), right.get(posR));
			int p2=this.getBestMap(left, posL+1, right, posR+1, tmpMap2, curPoint+getPoint(left.get(posL),right.get(posR)));
			
			if (p1<p2) {
				map.putAll(tmpMap1);
				return p1;
			} else {
				map.putAll(tmpMap2);
				return p2;
			}
		} else {
			map.put(left.get(posL), right.get(posR));
			return this.getBestMap(left, posL+1, right, posR+1, map, curPoint+getPoint(left.get(posL), right.get(posR)));
		}
	}
	
	int size;
	int totalChangePoint;
	Picture[] pictures;
	
	public ChangeVector calculateSimilarity(SemanticNetwork r) {
		ChangeVector res=new ChangeVector();
		ChangeVector dif=new ChangeVector(),dif2=new ChangeVector();
		try {
			for (int i=0;i<Math.min(pictures[0].nodeList.size(), r.pictures[0].nodeList.size());i++) {
				dif.addDiff(pictures[0].nodeList.get(i).changeEdge.getSimilarity(r.pictures[0].nodeList.get(i).changeEdge));
				if (!dif.usable) {
					break;
				}
			}
			if (pictures[0].nodeList.size()!=r.pictures[0].nodeList.size()) {
				for (int i=0;i<Math.min(pictures[0].nodeList.size(), r.pictures[0].nodeList.size())-1;i++) {
					dif2.addDiff(pictures[0].nodeList.get(pictures[0].nodeList.size()-1-i).changeEdge.getSimilarity(r.pictures[0].nodeList.get(r.pictures[0].nodeList.size()-1-i).changeEdge));
					if (!dif2.usable) {
						break;
					}
				}
				if (dif.compareTo(dif2)==-1)
					res.addDiff(dif);
				else
					res.addDiff(dif2);
			} else {
				res.addDiff(dif);
			}
			return res;			
		} catch (Exception e) {
			res.usable=false;
			e.printStackTrace();
			return res;
		}
	}
	
	SemanticNetwork(String size, RavensFigure... rfs) {
		if (size.equals("2x2")) {
			this.size=2;
			pictures=new Picture[this.size];
			for (int i=0;i<rfs.length;i++) {
				RavensFigure rf=rfs[i];
				pictures[i]=new Picture(rf);
			}
			
			Map<Node, Node> changeMap=new HashMap<Node, Node>();
			totalChangePoint=this.getBestMap(pictures[0].nodeList, 0, pictures[1].nodeList, 0, changeMap, 0);
			for (Node key:changeMap.keySet()) {
				if (key.name.equals("null")) {
					pictures[0].nodeList.add(key);
				}
				key.changeEdge=this.getChangeEdge(key, changeMap.get(key));
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		for (Picture p:pictures)
			sb.append(p.name+" ");
		return sb.toString();
	}
}
