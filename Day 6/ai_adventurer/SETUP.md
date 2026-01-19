# AI Adventurer - Настройка чат-приложения

## Описание
Это Android чат-приложение на Kotlin и Jetpack Compose для общения с LLM через DeepSeek API.

## Требования
- Android Studio
- Минимальная версия SDK: 29
- DeepSeek API ключ

## Настройка

### 1. Получите API ключ DeepSeek
1. Зарегистрируйтесь на [DeepSeek Platform](https://platform.deepseek.com/)
2. Перейдите в раздел API Keys
3. Создайте новый API ключ

### 2. Добавьте API ключ в приложение

⚠️ **Безопасный способ (рекомендуется):**

1. Если у вас ещё нет файла `local.properties`, создайте его на основе примера:
   ```bash
   cp local.properties.example local.properties
   ```

2. Откройте файл `local.properties` в корне проекта

3. Добавьте ваш DeepSeek API ключ:
   ```properties
   DEEPSEEK_API_KEY=sk-ваш-ключ-здесь
   ```

4. Сохраните файл

✅ **Преимущества:**
- API ключ не попадёт в Git (файл уже в `.gitignore`)
- Безопасно для публичных репозиториев
- Легко управлять разными ключами для разных окружений

⚠️ **ВАЖНО:** 
- Файл `local.properties` уже добавлен в `.gitignore`
- **НИКОГДА** не коммитьте этот файл с реальным API ключом!
- Используйте `local.properties.example` как шаблон для других разработчиков

### 3. Соберите и запустите приложение
1. Откройте проект в Android Studio
2. Дождитесь синхронизации Gradle
3. Запустите приложение на эмуляторе или реальном устройстве

## Доступные модели DeepSeek

DeepSeek предоставляет несколько моделей:
- `deepseek-chat` - основная модель для чата (по умолчанию)
- `deepseek-coder` - специализированная модель для программирования

Чтобы изменить модель, измените значение в `data/ApiModels.kt`:
```kotlin
val model: String = "deepseek-coder" // или другая модель
```

## Использование альтернативных LLM API

Если вы хотите использовать другой LLM API (например, OpenAI, Anthropic Claude, Google Gemini):

1. Измените `BASE_URL` в `RetrofitInstance.kt`:
   - OpenAI: `https://api.openai.com/`
   - Anthropic: `https://api.anthropic.com/`
2. Измените модель в `data/ApiModels.kt`:
   - OpenAI: `gpt-3.5-turbo`, `gpt-4`, `gpt-4-turbo`
   - DeepSeek: `deepseek-chat`, `deepseek-coder`
3. Обновите модели данных если API несовместим с OpenAI форматом

## Структура проекта

```
app/src/main/java/com/nihao/ai_adventurer/
├── api/
│   ├── LLMApiService.kt        # Retrofit API интерфейс
│   └── RetrofitInstance.kt     # Настройка Retrofit клиента
├── data/
│   ├── Message.kt              # Модель сообщения для UI
│   └── ApiModels.kt            # Модели для API запросов/ответов
├── viewmodel/
│   └── ChatViewModel.kt        # ViewModel для управления состоянием чата
├── ui/
│   ├── ChatScreen.kt           # Основной UI экрана чата
│   └── theme/                  # Тема приложения
└── MainActivity.kt             # Главная активность
```

## Возможные проблемы

### Ошибка сети
- Убедитесь, что у вас есть интернет-соединение
- Проверьте правильность API ключа
- Убедитесь, что разрешения добавлены в AndroidManifest.xml

### Ошибка сборки
- Выполните `Gradle Sync`
- Очистите проект: Build → Clean Project
- Пересоберите: Build → Rebuild Project

## Возможности для расширения

- Сохранение истории чата в локальную БД (Room)
- Поддержка нескольких чатов
- Настройки моделей и параметров
- Поддержка изображений и файлов
- Темная тема
- Экспорт истории чата
