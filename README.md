# Book Atlas

Android-приложение для поиска книг из каталога Project Gutenberg. Данные загружаются через [Gutendex API](https://gutendex.com/). Приложение написано на Kotlin с использованием Jetpack Compose.

## Возможности

- Каталог книг с поиском по названию, автору и теме.
- Экран с информацией о книге и переходом на Project Gutenberg.
- Избранное с сохранением между запусками.
- Просмотр ранее загруженных книг без интернета.
- Индикаторы загрузки, сообщения об ошибках и пустые состояния.

## Структура проекта

Основные пакеты находятся в `app/src/main/java/com/example/bookatlas/`:

| Пакет | Назначение |
| --- | --- |
| `domain` | Модель `Book`, объединение новых данных с кэшем |
| `data/network` | Retrofit API, DTO и преобразование ответа |
| `data/local` | Хранение данных через Preferences DataStore |
| `data` | Интерфейс и реализация репозитория |
| `ui/catalog` | Каталог, `CatalogUiState`, `CatalogViewModel` |
| `ui/favorites` | Избранное, `FavoritesUiState`, `FavoritesViewModel` |
| `ui/detail` | Информация о книге, `DetailUiState`, `DetailViewModel` |
| `ui/navigation` | Переходы между экранами |
| `ui/components`, `ui/theme` | Общие компоненты и оформление |

Экран получает данные из `StateFlow` своей ViewModel. ViewModel обрабатывает действия пользователя через `onEvent()` и работает с интерфейсом `BooksRepository`. Сам репозиторий отвечает за запросы к Gutendex и сохранение данных в DataStore.

При запуске отображается сохранённый каталог, после чего выполняется запрос к API. Если сеть недоступна, данные из кэша остаются на экране. В `BookMerger` сохраняется признак избранного при обновлении информации о книгах.

## Технологии

- Kotlin, Jetpack Compose, Material 3
- MVVM, ViewModel, StateFlow, Coroutines
- Retrofit, OkHttp, Gson
- Preferences DataStore
- Navigation Compose с аргументом `bookId` типа `Int`
- Coil для обложек
- JUnit для модульных тестов

## Сборка

Требуются Android Studio, JDK 17 или 21, Android SDK Platform 35 и подключение к интернету для первой загрузки зависимостей. Минимальная версия Android: API 24.

1. Открыть папку проекта в Android Studio.
2. Указать JDK 21 в **Settings > Build, Execution, Deployment > Build Tools > Gradle**, если среда предлагает неподдерживаемую версию Java.
3. Дождаться синхронизации Gradle.
4. Выполнить из корня проекта:

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug
```

APK после сборки: `app/build/outputs/apk/debug/app-debug.apk`.

Если отсутствует `gradle/wrapper/gradle-wrapper.jar`, выполните `powershell -ExecutionPolicy Bypass -File .\scripts\prepare-windows.ps1`: скрипт ищет этот файл в соседних локальных Android-проектах. Для сборки также можно использовать Gradle, настроенный в Android Studio.

## Проверка на устройстве

1. Загрузить каталог и найти книгу по запросу `Sherlock`.
2. Открыть подробную информацию о книге.
3. Добавить книгу в избранное и проверить второй экран.
4. Закрыть приложение, отключить интернет и открыть его снова. Сохранённые карточки и избранное должны остаться доступны.
5. Проверить кнопку обновления без интернета и после восстановления подключения.
6. Повернуть экран и проверить сохранение поискового запроса.

Модульные тесты: `BookMergerTest` и `CatalogFilterTest`. Запуск: `./gradlew testDebugUnitTest` (для Windows: `.\gradlew.bat :app:testDebugUnitTest`).

В `.github/workflows/android.yml` находится конфигурация сборки и тестов через GitHub Actions.
