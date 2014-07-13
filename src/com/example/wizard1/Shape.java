package com.example.wizard1;

public enum Shape { 
	TRIANGLE("triangle"),
	CIRCLE("circle"), 
	CLOCK("clock"),
	Z("z"),
	V("v"),
	PI("pi"),
	SHIELD("shield"), 
	FAIL("fail"),
	NONE("none");
	
	private final String name;
	
	private Shape(String s) {
		name = s;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public static int getPictureId(Shape s) {
    	switch( s ) {
    		case TRIANGLE: 
    			return R.drawable.triangle;
			case CIRCLE: 
				return R.drawable.circle;
			case CLOCK:
				return R.drawable.clock;
			case Z:
				return R.drawable.z;
			case V:
				return R.drawable.v;
			case PI:
				return R.drawable.pi;
			case SHIELD:
				return R.drawable.shield;
			default:
				return R.drawable.fail;
    	}
    }
}