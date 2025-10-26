# Начало работы с PixelGreenhouse

## Быстрый старт

### 1. Настройка окружения разработки

Выполните следующие команды в терминале из корневой папки проекта:

```bash
# Настройка workspace и декомпиляция Minecraft
gradlew setupDecompWorkspace

# Для Eclipse
gradlew eclipse

# Для IntelliJ IDEA
gradlew idea
```

**Важно:** Первый запуск `setupDecompWorkspace` может занять 10-30 минут, так как Gradle загрузит все зависимости и декомпилирует Minecraft.

### 2. Импорт проекта в IDE

#### IntelliJ IDEA:
1. File → Open
2. Выберите папку проекта
3. Дождитесь индексации проекта

#### Eclipse:
1. File → Import → Existing Projects into Workspace
2. Выберите папку проекта
3. Нажмите Finish

### 3. Первый запуск

После настройки окружения вы можете запустить Minecraft с вашим модом:

```bash
# Запуск клиента
gradlew runClient

# Запуск сервера
gradlew runServer
```

## Что уже реализовано

✅ **Базовая структура мода**
- Главный класс `PixelGreenhouse`
- Система прокси (Client/Server)
- Регистрация блоков и предметов

✅ **Блок теплицы**
- Класс `BlockGreenhouse` с базовой функциональностью
- TileEntity `TileEntityGreenhouse` для хранения данных
- Система тиков для обновления состояния

✅ **Ресурсы**
- Модели блоков и предметов
- Blockstates
- Локализация (английский)

## Что нужно добавить

### Приоритет 1: Текстура блока
Создайте текстуру размером 16x16 пикселей и поместите её в:
```
src/main/resources/assets/pixelgreenhouse/textures/blocks/greenhouse.png
```

### Приоритет 2: Рецепт крафта
Создайте файл рецепта в:
```
src/main/resources/assets/pixelgreenhouse/recipes/greenhouse.json
```

Пример рецепта:
```json
{
  "type": "minecraft:crafting_shaped",
  "pattern": [
    "GGG",
    "G G",
    "III"
  ],
  "key": {
    "G": {
      "item": "minecraft:glass"
    },
    "I": {
      "item": "minecraft:iron_ingot"
    }
  },
  "result": {
    "item": "pixelgreenhouse:greenhouse"
  }
}
```

### Приоритет 3: Интеграция с Pixelmon
В файле `TileEntityGreenhouse.java` нужно добавить:
- Инвентарь для хранения апприконов
- Логику роста апприконов через Pixelmon API
- Проверку условий для роста

Пример кода для работы с апприконами:
```java
import com.pixelmonmod.pixelmon.items.ItemApricorn;
import net.minecraft.item.ItemStack;

// В методе update()
if (inventory.getStackInSlot(0).getItem() instanceof ItemApricorn) {
    // Логика роста априкона
}
```

## Структура файлов для разработки

```
PixelGreenhouse/
├── src/main/java/              # Исходный код
├── src/main/resources/         # Ресурсы (текстуры, модели, локализация)
├── build.gradle                # Конфигурация сборки
├── gradle.properties           # Настройки Gradle
└── README_RU.md               # Документация
```

## Полезные команды

```bash
# Очистка проекта
gradlew clean

# Сборка мода
gradlew build

# Запуск клиента
gradlew runClient

# Запуск сервера
gradlew runServer

# Обновление зависимостей
gradlew --refresh-dependencies
```

## Отладка

### Логирование
Используйте logger из главного класса:
```java
PixelGreenhouse.getLogger().info("Сообщение");
PixelGreenhouse.getLogger().error("Ошибка");
```

### Точки останова
В IntelliJ IDEA и Eclipse вы можете ставить breakpoints и отлаживать код в режиме debug.

## Дополнительные ресурсы

- [Документация Forge](https://mcforge.readthedocs.io/)
- [Pixelmon Wiki](https://pixelmonmod.com/wiki/)
- [Minecraft Forge Forums](https://forums.minecraftforge.net/)

## Проблемы и решения

### Gradle не может загрузить зависимости
- Проверьте подключение к интернету
- Попробуйте: `gradlew --refresh-dependencies`

### IDE не видит классы Minecraft/Forge
- Убедитесь, что выполнили `gradlew setupDecompWorkspace`
- Переимпортируйте проект в IDE

### Мод не загружается в игре
- Проверьте логи в папке `run/logs/`
- Убедитесь, что Pixelmon установлен и совместим

## Следующие шаги

1. Добавьте текстуру для блока теплицы
2. Создайте рецепт крафта
3. Реализуйте GUI для взаимодействия с теплицей
4. Добавьте логику выращивания апприконов
5. Протестируйте мод в игре

Удачи в разработке! 🚀
