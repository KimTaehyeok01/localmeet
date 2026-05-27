from fastapi import APIRouter
from pydantic import BaseModel
from services.profanity_service import check_profanity

router = APIRouter()


class ChatFilterRequest(BaseModel):
    content: str


@router.post("/filter")
def filter_chat(req: ChatFilterRequest):
    """채팅 욕설 감지"""
    return check_profanity(req.content)
