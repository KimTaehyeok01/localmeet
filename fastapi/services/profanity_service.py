"""
한국어 욕설 감지 서비스
"""

BAD_WORDS = [
    # 한국어
    "시발", "씨발", "ㅅㅂ", "개새", "개새끼", "병신", "ㅂㅅ", "지랄", "씹",
    "좆", "보지", "자지", "새끼", "개색", "쓰레기", "꺼져", "죽어", "죽여",
    "닥쳐", "빠가", "찐따", "정신병자", "미친놈", "미친년", "개년", "창녀",
    "걸레", "썅", "ㅆㅂ", "ㄱㅅㄲ", "존나", "ㅈㄴ", "fuck",
    # 영어
    "shit", "bitch", "asshole", "bastard", "damn", "crap",
]


def check_profanity(text: str) -> dict:
    """
    텍스트에서 욕설 감지.
    Returns:
        {"clean": True} or {"clean": False, "detected": "감지된 단어"}
    """
    text_lower = text.lower().replace(" ", "")
    for word in BAD_WORDS:
        if word in text_lower:
            return {"clean": False, "detected": word}
    return {"clean": True, "detected": None}
