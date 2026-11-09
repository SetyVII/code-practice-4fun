import logging
from typing import Literal

from fastapi import APIRouter, HTTPException
from pydantic import BaseModel, Field
from utils.mistral import mistral_client

logger = logging.getLogger(__name__)

router = APIRouter()

MODEL = "mistral-small-latest"


class ChatRequestMessage(BaseModel):
    role: Literal["system", "user", "assistant"]
    content: str


class ChatRequest(BaseModel):
    messages: list[ChatRequestMessage] = Field(min_length=1)


class ChatResponse(BaseModel):
    reply: str


def _reply_text(content: object) -> str:
    if isinstance(content, str):
        return content
    if isinstance(content, list):
        return "".join(str(getattr(chunk, "text", "")) for chunk in content)
    return ""


@router.post("", operation_id="chat", summary=f"Chat with {MODEL}")
async def chat(request: ChatRequest) -> ChatResponse:
    try:
        completion = mistral_client().chat.complete(
            model=MODEL,
            messages=[message.model_dump() for message in request.messages],
        )
    except Exception as error:
        logger.exception("Mistral API request failed")
        raise HTTPException(status_code=502, detail="The Mistral API request failed. Check the server logs.") from error
    return ChatResponse(reply=_reply_text(completion.choices[0].message.content))
