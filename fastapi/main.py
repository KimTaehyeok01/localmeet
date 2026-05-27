from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from routers import chat_router, weather_router

app = FastAPI(title="LocalMeet AI API", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:8080",
        "http://13.209.110.13:8080",
        "http://13.209.110.13",
    ],
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(chat_router.router, prefix="/chat", tags=["chat"])
app.include_router(weather_router.router, prefix="/weather", tags=["weather"])


@app.get("/health")
def health():
    return {"status": "ok"}
