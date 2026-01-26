# Интеграция Mistral AI

## Описание

В приложение добавлена поддержка **Mistral AI** в дополнение к существующей поддержке DeepSeek. Теперь пользователь может выбирать между двумя LLM провайдерами в настройках приложения.

## Что было сделано

### 1. Создан enum `LLMProvider`
- **Файл:** `app/src/main/java/com/nihao/ai_adventurer/data/LLMProvider.kt`
- **Описание:** Перечисление поддерживаемых провайдеров с их параметрами:
  - `DEEPSEEK` - DeepSeek AI
  - `MISTRAL` - Mistral AI
- Каждый провайдер содержит:
  - `displayName` - отображаемое имя
  - `baseUrl` - базовый URL API
  - `description` - описание провайдера
- Добавлены методы для получения моделей по умолчанию для каждого провайдера

### 2. Обновлен `RetrofitInstance`
- **Файл:** `app/src/main/java/com/nihao/ai_adventurer/api/RetrofitInstance.kt`
- **Изменения:**
  - Добавлена поддержка обоих API ключей (DeepSeek и Mistral)
  - Создан метод `getApi(provider: LLMProvider)` для получения API сервиса для выбранного провайдера
  - Реализован кэш API сервисов для оптимизации
  - Старый метод `api` помечен как deprecated для обратной совместимости

### 3. Обновлен `ChatViewModel`
- **Файл:** `app/src/main/java/com/nihao/ai_adventurer/viewmodel/ChatViewModel.kt`
- **Изменения:**
  - Добавлен `StateFlow<LLMProvider>` для текущего провайдера
  - Обновлен метод `sendMessage()` для использования выбранного провайдера
  - Обновлен метод `loadAvailableModels()` для загрузки моделей текущего провайдера
  - Добавлен метод `changeProvider(newProvider: LLMProvider)` для смены провайдера
  - При смене провайдера автоматически выбирается модель по умолчанию и загружается список доступных моделей

### 4. Обновлен UI в `SettingsScreen`
- **Файл:** `app/src/main/java/com/nihao/ai_adventurer/ui/SettingsScreen.kt`
- **Изменения:**
  - Добавлена новая секция "LLM Провайдер" перед секцией "Модель"
  - Создан Composable `ProviderSelectionCard` для отображения карточки выбора провайдера
  - Добавлены описания для всех моделей Mistral AI:
    - `mistral-small-latest` - Компактная и быстрая модель
    - `mistral-medium-latest` - Сбалансированная модель
    - `mistral-large-latest` - Самая мощная модель Mistral
    - `open-mistral-7b` - Open-source модель 7B параметров
    - `open-mixtral-8x7b` - Open-source Mixture of Experts модель
    - `open-mixtral-8x22b` - Open-source продвинутая MoE модель

### 5. Обновлены файлы конфигурации
- **build.gradle.kts:**
  - Добавлен `MISTRAL_API_KEY` в BuildConfig
- **local.properties.example:**
  - Добавлен пример для Mistral API ключа
  - Добавлена ссылка на консоль Mistral AI

## Поддерживаемые модели

### DeepSeek
- `deepseek-chat` - Универсальная модель для диалога
- `deepseek-reasoner` - Модель для сложных задач и рассуждений

### Mistral AI
- `mistral-small-latest` - Компактная и быстрая модель
- `mistral-medium-latest` - Сбалансированная модель
- `mistral-large-latest` - Самая мощная модель
- `open-mistral-7b` - Open-source 7B модель
- `open-mixtral-8x7b` - Open-source MoE 8x7B
- `open-mixtral-8x22b` - Open-source MoE 8x22B

## Настройка

### 1. Получите API ключи

#### DeepSeek
1. Перейдите на https://platform.deepseek.com/
2. Зарегистрируйтесь или войдите
3. Создайте API ключ

#### Mistral AI
1. Перейдите на https://console.mistral.ai/
2. Зарегистрируйтесь или войдите
3. Создайте API ключ

### 2. Настройте local.properties

Скопируйте `local.properties.example` в `local.properties` и добавьте ваши API ключи:

```properties
# DeepSeek API Key
DEEPSEEK_API_KEY=your_deepseek_api_key_here

# Mistral AI API Key
MISTRAL_API_KEY=your_mistral_api_key_here
```

### 3. Соберите проект

После добавления API ключей пересоберите проект для применения изменений.

## Использование

1. Откройте приложение
2. Перейдите в Настройки (⚙️)
3. В секции "LLM Провайдер" выберите желаемого провайдера (DeepSeek или Mistral AI)
4. Выберите модель из списка доступных моделей для выбранного провайдера
5. Вернитесь в чат и начните общение

## Особенности реализации

### Автоматическое переключение моделей
При смене провайдера автоматически выбирается модель по умолчанию для нового провайдера и загружается актуальный список доступных моделей.

### Кэширование API сервисов
Для оптимизации производительности API сервисы для каждого провайдера кэшируются и создаются только один раз.

### Обратная совместимость
Старый API интерфейс сохранен как deprecated для обратной совместимости со старым кодом.

### Обработка ошибок
Если не удается загрузить список моделей с API, используется предустановленный список моделей по умолчанию для выбранного провайдера.

## API совместимость

Оба провайдера используют OpenAI-совместимый API формат:
- Endpoint: `/v1/chat/completions`
- Endpoint: `/v1/models`

Это позволяет использовать одинаковые модели данных (`ChatRequest`, `ChatResponse`) для обоих провайдеров.

## Будущие улучшения

Возможные направления для дальнейшего развития:
- Добавление других провайдеров (OpenAI, Anthropic, и т.д.)
- Сохранение выбранного провайдера между сеансами
- Статистика использования по провайдерам
- Сравнение стоимости между провайдерами
- Поддержка streaming ответов
