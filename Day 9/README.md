# AI_advent

## Day 9. Сжатие диалога

**Задача**
- Реализуйте механизм «сжатия истории диалога»
  (например, каждые 10 сообщений делать summary и хранить его вместо оригинала)
- Проверьте, как агент продолжает вести разговор с учётом summary вместо всей истории
- Сравните качество ответов и использование токенов

**Результат**
- Агент работает с компрессией и выполняет ту же работу за меньшее количество токенов

**Формат**
- Видео + Код

**Промт для сжатия:**
```
                        You are maintaining a LONG-TERM MEMORY SUMMARY for an LLM agent.

                        Your task is to UPDATE the existing summary using the NEW conversation turns.

                        CRITICAL RULES:
                        - Do NOT rewrite the summary from scratch.
                        - Only ADD, UPDATE, or REMOVE information if the conversation explicitly requires it.
                        - Preserve stable facts, decisions, and user preferences.
                        - Do NOT include conversational filler, politeness, or phrasing.
                        - Do NOT infer intent unless it is clearly stated.
                        - Prefer explicit facts over interpretations.

                        OUTPUT FORMAT:
                        Return a VALID JSON object in the exact schema provided.
                        Do NOT add new top-level fields.
                        Do NOT include explanations or comments.

                        UPDATE STRATEGY:
                        - Add new goals, preferences, constraints ONLY if explicitly stated.
                        - Update "current_focus" if the topic of work has clearly shifted.
                        - Move resolved items from "open_questions" to "decisions_made" when appropriate.
                        - Remove outdated information only if directly contradicted.
```

