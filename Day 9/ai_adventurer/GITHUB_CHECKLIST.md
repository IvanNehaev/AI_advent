# ✅ Чек-лист перед публикацией на GitHub

## 🔒 Безопасность (КРИТИЧНО!)

- [x] ✅ API ключ вынесен из кода в `local.properties`
- [x] ✅ `local.properties` добавлен в `.gitignore`
- [x] ✅ Создан файл `local.properties.example` для других разработчиков
- [ ] ⚠️ Убедитесь, что в истории Git нет коммитов с API ключом

### Как проверить историю Git:

```bash
# Поиск API ключей в истории
git log -p | grep -i "api_key\|deepseek"

# Если нашли ключ в истории, очистите её:
# ВНИМАНИЕ: Это перепишет историю!
git filter-branch --force --index-filter \
  "git rm --cached --ignore-unmatch app/src/main/java/com/nihao/ai_adventurer/api/RetrofitInstance.kt" \
  --prune-empty --tag-name-filter cat -- --all
```

## 📝 Документация

- [x] ✅ README.md создан с описанием проекта
- [x] ✅ SETUP.md с инструкциями по настройке
- [x] ✅ DEEPSEEK_INFO.md с информацией об API
- [x] ✅ local.properties.example как шаблон

## 🗂️ Файлы для коммита

### ✅ Включить в Git:
```
✓ app/
✓ gradle/
✓ build.gradle.kts
✓ settings.gradle.kts
✓ gradle.properties (без секретов)
✓ gradlew, gradlew.bat
✓ .gitignore
✓ local.properties.example  ← ВАЖНО!
✓ README.md
✓ SETUP.md
✓ DEEPSEEK_INFO.md
✓ LICENSE (если есть)
```

### ❌ НЕ включать в Git:
```
✗ local.properties           ← API ключ здесь!
✗ .idea/ (кроме некоторых файлов)
✗ .gradle/
✗ build/
✗ *.iml
✗ .DS_Store
```

## 🚀 Инициализация Git репозитория

Если ещё не инициализировали Git:

```bash
# 1. Инициализация
git init

# 2. Добавление всех файлов (кроме игнорируемых)
git add .

# 3. Первый коммит
git commit -m "Initial commit: AI Adventurer chat app"

# 4. Создайте репозиторий на GitHub
# Перейдите на github.com и создайте новый репозиторий

# 5. Подключите удалённый репозиторий
git remote add origin https://github.com/ваш-username/ai_adventurer.git

# 6. Отправка на GitHub
git branch -M main
git push -u origin main
```

## 🔍 Финальная проверка

Перед пушем в GitHub:

```bash
# 1. Проверьте статус
git status

# 2. Убедитесь, что local.properties НЕ в списке
git ls-files | grep local.properties
# Должен вывести ТОЛЬКО: local.properties.example

# 3. Проверьте содержимое файлов, которые будут закоммичены
git diff --cached

# 4. Поиск возможных секретов
grep -r "sk-" app/src/ || echo "✓ Секреты не найдены"
```

## 📱 После публикации

1. **Добавьте описание репозитория** на GitHub:
   - Topics: `android`, `kotlin`, `jetpack-compose`, `ai`, `chatbot`, `deepseek`
   - Description: "🤖 AI chat app built with Kotlin & Jetpack Compose"

2. **Создайте Issues templates** (опционально)

3. **Добавьте GitHub Actions** для CI (опционально):
   ```yaml
   # .github/workflows/android.yml
   name: Android CI
   on: [push, pull_request]
   jobs:
     build:
       runs-on: ubuntu-latest
       steps:
         - uses: actions/checkout@v3
         - name: Set up JDK 11
           uses: actions/setup-java@v3
           with:
             java-version: '11'
         - name: Build with Gradle
           run: ./gradlew build
   ```

4. **Обновите README badges** с вашим username

## ⚠️ Если случайно закоммитили ключ

Если вы случайно закоммитили API ключ:

1. **НЕМЕДЛЕННО смените ключ** на DeepSeek Platform
2. Очистите историю Git (см. выше)
3. Force push: `git push --force`
4. Используйте новый ключ

## 🎉 Готово!

После выполнения всех пунктов ваш проект готов к публикации на GitHub!

### Полезные команды:

```bash
# Клонирование для других разработчиков
git clone https://github.com/ваш-username/ai_adventurer.git
cd ai_adventurer
cp local.properties.example local.properties
# Затем добавить свой API ключ в local.properties
```

---

**Помните:** Безопасность API ключей - это ваша ответственность! 🔐
