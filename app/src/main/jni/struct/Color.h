#pragma once
using namespace std;

struct Color {
	union {
		struct {
			float r, b, g, a;
		};
		float data[4];
	};
	
	Color() {
		SetColor(0, 0, 0, 255);
	}
	
	Color(float r, float g, float b) {
		SetColor(r, g, b, 255);
	}
	
	Color(float r, float g, float b, float a) {
		SetColor(r, g, b, a);
	}
	
	void SetColor(float r1, float g1, float b1, float a1 = 255) {
		r = r1;
		g = g1;
		b = b1;
		a = a1;
	}
	
	static Color Black(float a = 255) {
		return Color(0, 0, 0, a);
	}
	
	static Color Red(float a = 255) {
		return Color(255, 0, 0, a);
	}
	
	static Color Green(float a = 255) {
		return Color(0, 255, 0, a);
	}
	
    static Color Blue(float a = 255) {
		return Color(0, 0, 255, a);
	}
	
    static Color White(float a = 255) {
		return Color(255, 255, 255, a);
	}
	
    static Color Orange(float a = 255) {
		return Color(255, 153, 0, a);
	}
	
    static Color Magenta(float a = 255) {
		return Color(255, 255, 0, a);
	}
	
    static Color Cyan(float a = 255) {
		return Color(0, 255, 255, a);
	}
	
    static Color Yellow(float a = 255) {
		return Color(255, 0, 255, a);
	}
	
	static Color random(float a = 255) {
        float r = static_cast <float> (rand()) / static_cast <float> (255);
        float g = static_cast <float> (rand()) / static_cast <float> (255);
        float b = static_cast <float> (rand()) / static_cast <float> (255);
        return Color(r, g, b, a);
    }
	
	static Color rainbow() {
		static float x = 0, y = 0;
		static float r = 0, g = 0, b = 0;
		if (y >= 0.0f && y < 255.0f ) {
			r = 255.0f;
			g = 0.0f;
			b = x;
		} else if (y >= 255.0f && y < 510.0f ) {
			r = 255.0f - x;
			g = 0.0f;
			b = 255.0f;
		} else if (y >= 510.0f && y < 765.0f ) {
			r = 0.0f;
			g = x;
			b = 255.0f;
		} else if (y >= 765.0f && y < 1020.0f ) {
			r = 0.0f;
			g = 255.0f;
			b = 255.0f - x;
		} else if (y >= 1020.0f && y < 1275.0f ) {
			r = x;
			g = 255.0f;
			b = 0.0f;
		} else if (y >= 1275.0f && y < 1530.0f ) {
			r = 255.0f;
			g = 255.0f - x;
			b = 0.0f;
		}
		x+= 0.25f; 
		if (x >= 255.0f )
			x = 0.0f;
		y+= 0.25f;
		if (y > 1530.0f )
			y = 0.0f;
		return Color((int)r, (int)g, (int)b, 255);
	}
};
