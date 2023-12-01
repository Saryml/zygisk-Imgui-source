#pragma once
static int                  g_GlHeight, g_GlWidth;
enum TouchPhase {
	Began = 0,
	Moved = 1,
	Stationary = 2,
	Ended = 3,
	Canceled = 4,
};

enum TouchType {
	Direct = 0,
	Indirect = 1,
	Stylus = 2,
};

struct Touch {
	int m_FingerId;
	Vector2 m_Position;
	Vector2 m_RawPosition;
	Vector2 m_PositionDelta;
	float m_TimeDelta;
	int m_TapCount;
	TouchPhase m_Phase;
	TouchType m_Type;
	float m_Pressure;
	float m_maximumPossiblePressure;
	float m_Radius;
	float m_RadiusVariance;
	float m_AltitudeAngle;
	float m_AzimuthAngle;
};

int get_touchCount(void *_this) {
	int (*_get_touchCount)(void *_this) = (int (*)(void *)) (Il2CppGetMethodOffset(OBFUSCATE("UnityEngine.dll"), OBFUSCATE("UnityEngine"), OBFUSCATE("Input"), OBFUSCATE("get_touchCount"), 0));
	return _get_touchCount(_this);
}

bool get_touchSupported(void *_this) {
	bool (*_get_touchSupported)(void *_this) = (bool (*)(void *)) (Il2CppGetMethodOffset(OBFUSCATE("UnityEngine.dll"), OBFUSCATE("UnityEngine"), OBFUSCATE("Input"), OBFUSCATE("get_simulateMouseWithTouches"), 0));
	return _get_touchSupported(_this);
}

Vector3 get_mousePosition(void *_this) {
	Vector3 (*_get_mousePosition)(void *_this) = (Vector3 (*)(void *)) (Il2CppGetMethodOffset(OBFUSCATE("UnityEngine.dll"), OBFUSCATE("UnityEngine"), OBFUSCATE("Input"), OBFUSCATE("get_mousePosition"), 0));
	return _get_mousePosition(_this);
}

Touch GetTouch(void *_this, int index) {
	Touch (*_GetTouch)(void *_this, int index) = (Touch (*)(void *, int)) (Il2CppGetMethodOffset(OBFUSCATE("UnityEngine.dll"), OBFUSCATE("UnityEngine"), OBFUSCATE("Input"), OBFUSCATE("GetTouch"), 1));
	return _GetTouch(_this, index);
}

void OnTouchEvent() {
	void *_this = nullptr;
	
	ImGuiIO* io = &ImGui::GetIO();
	
	if (get_touchCount(_this) > 0) {
			
		switch (GetTouch(_this, 0).m_Phase) {
			case TouchPhase::Began:
			case TouchPhase::Stationary:
				io->MouseDown[0] = true;
				io->MousePos = ImVec2(get_mousePosition(_this).x, g_GlHeight - get_mousePosition(_this).y);
				break;
					
			case TouchPhase::Ended:
			case TouchPhase::Canceled:
				io->MouseDown[0] = false;
				break;
					
			case TouchPhase::Moved:
				io->MousePos = ImVec2(get_mousePosition(_this).x, g_GlHeight - get_mousePosition(_this).y);
				break;
			default:
				break;
		}
	}
}
