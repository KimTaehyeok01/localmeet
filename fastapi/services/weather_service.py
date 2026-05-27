"""
OpenWeatherMap 기반 날씨 + 모임 카테고리 추천 서비스
"""
import os
import httpx
from dotenv import load_dotenv

load_dotenv()

OPENWEATHER_API_KEY = os.getenv("OPENWEATHER_API_KEY", "")
OPENWEATHER_URL = "https://api.openweathermap.org/data/2.5/weather"

CATEGORY_LABELS = {
    "SPORTS": "🏃 운동",
    "GAME":   "🎮 게임",
    "STUDY":  "📚 공부",
    "FOOD":   "🍽️ 맛집",
    "HOBBY":  "🎨 취미",
    "TRAVEL": "✈️ 여행",
    "PET":    "🐾 반려동물",
    "IT":     "💻 IT",
    "ETC":    "📌 기타",
}


def _get_recommendation_for_id(weather_id: int) -> list[str]:
    """날씨 코드 → 추천 카테고리 목록"""
    if 200 <= weather_id < 300:   # 천둥번개
        return ["GAME", "IT", "STUDY"]
    elif 300 <= weather_id < 400: # 이슬비
        return ["FOOD", "STUDY", "HOBBY"]
    elif 500 <= weather_id < 600: # 비
        return ["GAME", "STUDY", "IT"]
    elif 600 <= weather_id < 700: # 눈
        return ["HOBBY", "STUDY", "GAME"]
    elif 700 <= weather_id < 800: # 안개/황사
        return ["FOOD", "ETC", "HOBBY"]
    elif weather_id == 800:        # 맑음
        return ["SPORTS", "TRAVEL", "PET"]
    else:                          # 구름 많음
        return ["FOOD", "HOBBY", "ETC"]


def _get_desc(weather_id: int) -> str:
    if 200 <= weather_id < 300:   return "천둥번개"
    elif 300 <= weather_id < 400: return "이슬비"
    elif 500 <= weather_id < 600: return "비"
    elif 600 <= weather_id < 700: return "눈"
    elif 700 <= weather_id < 800: return "안개"
    elif weather_id == 800:        return "맑음"
    elif weather_id == 801:        return "구름 조금"
    elif weather_id == 802:        return "구름 많음"
    else:                          return "흐림"


def _get_emoji(weather_id: int) -> str:
    if 200 <= weather_id < 300:   return "⛈️"
    elif 300 <= weather_id < 400: return "🌦️"
    elif 500 <= weather_id < 600: return "🌧️"
    elif 600 <= weather_id < 700: return "❄️"
    elif 700 <= weather_id < 800: return "🌫️"
    elif weather_id == 800:        return "☀️"
    elif weather_id <= 802:        return "⛅"
    else:                          return "☁️"


async def get_recommendation(lat: float, lng: float) -> dict:
    if not OPENWEATHER_API_KEY:
        return {"error": "API key not configured"}

    try:
        async with httpx.AsyncClient(timeout=5.0) as client:
            resp = await client.get(OPENWEATHER_URL, params={
                "lat": lat,
                "lon": lng,
                "appid": OPENWEATHER_API_KEY,
                "units": "metric",
                "lang": "kr",
            })
            resp.raise_for_status()
            data = resp.json()

        weather_id = data["weather"][0]["id"]
        temp       = round(data["main"]["temp"])
        city       = data.get("name", "")
        categories = _get_recommendation_for_id(weather_id)

        return {
            "weatherId":      weather_id,
            "emoji":          _get_emoji(weather_id),
            "desc":           _get_desc(weather_id),
            "temp":           temp,
            "city":           city,
            "categories":     categories,
            "categoryLabels": [CATEGORY_LABELS[c] for c in categories],
        }
    except Exception as e:
        return {"error": str(e)}
