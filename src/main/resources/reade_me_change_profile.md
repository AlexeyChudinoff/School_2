профиль prod запускаем через GitBash
проверить где мы сейчас pwd
собрать проект из корня , в корень cd /c/Users/Alex/IdeaProjects/SCHOOL_2
потом ./gradlew clean build
потом переходим в либс cd /c/Users/Alex/IdeaProjects/SCHOOL_2/build/libs
потом java -jar School_2-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

🎯 Вариант 1: Через Run Configurations (Идеально!)
School2Application Prod конфигурация в IDEA
Environment variables: SPRING_PROFILES_ACTIVE=prod
Сохраняется между запусками
Удобное переключение между конфигурациями
Не требует изменения файлов

🎯 Вариант 2: Через переменные окружения в терминале
bash
$env:SPRING_PROFILES_ACTIVE="prod"
./gradlew bootRun
✅ Преимущества подхода:
Не нужно удалять/менять spring.profiles.active=dev из application.properties
Dev профиль остается для разработки по умолчанию
Prod профиль активируется только когда нужно

Чистое разделение конфигураций



или в PowerShell
для профиля dev
./gradlew bootRun -D"spring.profiles.active=dev"
для профиля prod
./gradlew bootRun -D"spring.profiles.active=prod"

самый надёжный вариант
Используйте переменную окружения (имеет высший приоритет):
bash
$env:SPRING_PROFILES_ACTIVE="prod"
./gradlew bootRun
$env:SPRING_PROFILES_ACTIVE="dev"
./gradlew bootRun
$env:SPRING_PROFILES_ACTIVE="test"
./gradlew bootRun