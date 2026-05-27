from fastapi import APIRouter, Query
from services.weather_service import get_recommendation

router = APIRouter()


@router.get("/recommend")
async def recommend(
    lat: float = Query(..., description="위도"),
    lng: float = Query(..., description="경도"),
):
    """날씨 기반 모임 카테고리 추천"""
    return await get_recommendation(lat, lng)
