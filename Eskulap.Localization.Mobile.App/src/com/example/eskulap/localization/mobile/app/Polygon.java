package com.example.eskulap.localization.mobile.app;
public class Polygon {

	public int npoints;
	public float xpoints[];
	public float ypoints[];
	private String polygonType;
	private float x;
	private float y;
	private float r1;
	private float r2; //elipsa



	public Polygon(float xpoints[], float ypoints[], int npoints, String type) {
		
		if (npoints > xpoints.length || npoints > ypoints.length) {
			throw new IndexOutOfBoundsException("npoints > xpoints.length || "
					+ "npoints > ypoints.length");
		}

		if (npoints < 0) {
			throw new NegativeArraySizeException("npoints < 0");
		}

		this.npoints = npoints;
		this.xpoints = new float[npoints];
		this.ypoints = new float[npoints];
		this.xpoints = xpoints.clone();
		this.ypoints = ypoints.clone();
		this.polygonType = type;
	}
	
	public Polygon(float x, float y, float r1, float r2, String type){
		this.x = x;
		this.y = y;
		this.r1 = r1;
		this.r2 = r2;
		this.polygonType = type;
	}
	
	public void reset(){
		npoints = 0;
	}
	
	public boolean containsPoint(double x, double y){
		if(polygonType.equals("circle")){
			if (Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2)) <= this.r1)
				return true;
			else
				return false;
		}
		else{
			return contains(x, y);
		}
	}
	
	private boolean contains(double x, double y) {
		        if (npoints <= 2) {
		            return false;
		        }
		        int hits = 0;
		        float lastx = xpoints[npoints - 1];
		        float lasty = ypoints[npoints - 1];
		        float curx, cury;
		
		        // Walk the edges of the polygon
		        for (int i = 0; i < npoints; lastx = curx, lasty = cury, i++) {
		            curx = xpoints[i];
		            cury = ypoints[i];
		
		            if (cury == lasty) {
		                continue;
		            }
		
		            float leftx;
		            if (curx < lastx) {
		                if (x >= lastx) {
		                    continue;
		                }
		                leftx = curx;
		            } else {
		                if (x >= curx) {
		                    continue;
		                }
		                leftx = lastx;
		            }
		
		            double test1, test2;
		            if (cury < lasty) {
		                if (y < cury || y >= lasty) {
		                    continue;
		                }
		                if (x < leftx) {
		                    hits++;
		                    continue;
		                }
		                test1 = x - curx;
		                test2 = y - cury;
		            } else {
		                if (y < lasty || y >= cury) {
		                    continue;
		                }
		                if (x < leftx) {
		                    hits++;
		                    continue;
		                }
		                test1 = x - lastx;
		                test2 = y - lasty;
		            }
		
		            if (test1 < (test2 / (lasty - cury) * (lastx - curx))) {
		                hits++;
		            }
		        }
		
		        return ((hits & 1) != 0);
		    }

}
