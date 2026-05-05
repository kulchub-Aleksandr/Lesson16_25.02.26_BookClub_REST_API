# Проект по автоматизации тестовых сценариев для сайта https://book-club.qa.guru
## :card_index_dividers: Содержание:

- [Использованный стек технологий и инструментов](#tech-stack)
- [Запуск автотестов](#arrow_forward-запуск-автотестов)
- [Сборка в Jenkins](#-сборка-в-jenkins)
- [Allure Report](#-allure-report)
- [Интеграция с Allure TestOps](#-интеграция-с-allure-testops)  
- [Интеграция с Jira](#-интеграция-с-jira)
- [Уведомления в Telegram](#-уведомления-в-telegram)
- [Видео примера запуска UI части тестов в Selenoid](#-видео-примера-запуска-тестов-в-selenoid)

## <span id="tech-stack"> :computer: Использованный стек технологий и инструментов

<p align="center">
<img width="6%" title="IntelliJ IDEA" src="images/logo/Intelij_IDEA.svg">
<img width="6%" title="GitHub" src="images/logo/GitHub.svg">
<img width="6%" title="Java" src="images/logo/Java.svg">
<img width="6%" title="Selenide" src="images/logo/Selenide.svg">
<img width="6%" title="Gradle" src="images/logo/Gradle.svg">
<img width="6%" title="JUnit5" src="images/logo/JUnit5.svg">
<img width="5%" title="Rest_assured" src="images/logo/rest_assured.png">
<img width="6%" title="Selenoid" src="images/logo/Selenoid.svg">
<img width="6%" title="Jenkins" src="images/logo/Jenkins.svg">
<img width="6%" title="Allure Report" src="images/logo/Allure_Report.svg">
<img width="6%" title="Telegram" src="images/logo/Telegram.svg">
<img width="5%" title="Allure TestOps" src="images/logo/AllureTestOps.svg">
<img width="5%" title="Jira" src="images/logo/Jira.svg">
</p>

- В данном проекте реализованны смешанные автотесты на API и UI: комбинация API‑запросов и UI‑действий в рамках одного сценария.
- Автотесты на API и UI написаны на языке <code>Java</code> с использованием фреймворка для автоматизации тестирования веб‑приложений [Selenide](https://selenide.org/).
- В качестве сборщика был использован - <code>Gradle</code>.
- В качестве фреймворка модульного тестирования задействован <code>JUnit 5</code> и <code>REST-assured</code>.
- `Page Object` шаблон проектирования.
- Использована технология `Owner` для придания тестам гибкости и легкости конфигурации.
- Использована библиотека `Faker` для генерации данных.
- Для моделирования данных в рамках API‑тестов использована конструкция `Record`: это позволило создать лёгкие неизменяемые контейнеры данных без необходимости ручной реализации геттеров и конструкторов.
- При прогоне UI тестов браузер запускается в [Selenoid](https://aerokube.com/selenoid/).
- Для удаленного запуска реализована джоба в **Jenkins** с формированием Allure-отчета и отправкой результатов в **Telegram** при помощи бота.
- Осуществлена интеграция с **Allure TestOps** и **Jira**
- Реализована возможность запуска тестов непосредственно из **Allure TestOps** — как полного прогона, так и выборочного выполнения отдельных тест‑кейсов или групп тестов (по тегам, компонентам, приоритетам и другим фильтрам).

### Реализована следующая схема взаимодействия технологий и инструментов

``` mermaid
flowchart LR;
    A(IntelliJ IDEA
     Java
     Selenide
    Gradle
    JUnit 5)-->B(GitHub);
    B-->C(Jenkins);
    C-->D(Telegram);
    C<==>E(Selenoid);
    
    C-->G(Allure_Report);
    
    C<==>F(Allure_TestOps);
    F-->J(Jira);

```

## :arrow_forward: Запуск автотестов

### Локальный запуск тестов из терминала

```bash
./gradlew clean test
```

### Удалённый запуск осуществляется через Jenkins

При необходимости также можно переопределить параметры запуска

```bash
clean
test
-DremoteUrl="${SELENOID_URL}"
-DbaseUrl="${BASE_URL}"
-DbrowserSize="${BROWSER_SIZE}"
-Dbrowser="${BROWSER_NAME}"
-Dbrowser_version="${BROWSER_VERSION}"
-DbaseUri="$BASE_URI"
-DbasePath="$BASE_PATH"
```

### Параметры сборки

- <code>BROWSER_NAME</code> – браузер, в котором будут выполняться тесты.
- <code>BROWSER_VERSION</code> – версия браузера, в которой будут выполняться тесты.
- <code>BROWSER_SIZE</code> – размер окна браузера, в котором будут выполняться тесты.
- <code>BASE_URL</code> – Url, по которому будет открываться тестируемое приложение.
- <code>BASE_URI</code> — базовый URI API.
- <code>BASE_PATH</code>— базовый путь к API.
- <code>REMOTE_BROWSER_URL</code> – адрес удаленного сервера, на котором будут запускаться тесты.

## <img src="images/logo/Jenkins.svg" title="Jenkins" width="5%"/> Сборка в [Jenkins](https://jenkins.autotests.cloud/view/java_students/job/C39_AleksKulch_lesson18_Book_Club_Reviews/)
### Главная страница
<p align="center">
<img title="Jenkins Build" 
src="images/screen/Jenkins_1.png">
</p>

### Страница запуска с возможностью изменить параметры
<p align="center">
<img title="Jenkins Build" 
src="images/screen/Jenkins_2.png">
</p>

## <img src="images/logo/Allure_Report.svg" title="Allure Report" width="5%"/> Allure [Report](https://jenkins.autotests.cloud/job/C39_AleksKulch_lesson18_Book_Club_Reviews/14/allure/)

Содержание Allure-отчета:

- Шаги теста;
- Скриншот страницы на последнем шаге;
- Page Source;
- Логи браузерной консоли;
- Видео выполнения автотеста.

### Overview

<p align="center">
<img title="Allure Overview" src="images/screen/Allure_1.png">
</p>

### Результат выполнения теста / Тест-кейсы

<p align="center">
<img title="Test Results in Alure" src="images/screen/Allure_2.png">
</p>

  <p align="center">  
<img title="Allure Graphics" src="images/screen/Allure_4.png">

### Графики

<p align="center">
<img title="Allure Graphics" src="images/screen/Allure_3.png">  
</p>

## <img src="images/logo/AllureTestOps.svg" title="Allure TestOps" width="5%"/> Интеграция с [Allure TestOps](https://allure.autotests.cloud/project/5160/dashboards)

Результаты выполнения автотестов в сборке <code>Jenkins</code> передаются в <code>Allure TestOps</code>

На Dashboard в <code>Allure TestOps</code> отображена статистика пройденных тестов.

### Dashboard
<p align="center">
<img title="Allure TestOps DashBoard" src="images/screen/Allure_Test_Ops_1.png">
</p>

### Результат выполнения автотеста
<p align="center">
<img title="Allure TestOps Tests" src="images/screen/Allure_Test_Ops_2.png">
</p>

## <img src="images/logo/Jira.svg" title="Allure TestOps" width="6%"/> Интеграция с [Jira](https://jira.autotests.cloud/browse/HOMEWORK-1600)

Реализована интеграция <code>Allure TestOps</code> с <code>Jira</code>, в тикете отображается информация, какие тест-кейсы были написаны в рамках задачи и результат их прогона.

<p align="center">
<img title="Jira Task" src="images/screen/jira_1.png">
</p>

<p align="center">
<img title="Jira Task" src="images/screen/jira_2.png">
</p>

## <img src="images/logo/Telegram.svg" title="Allure TestOps" width="7%"/> Уведомления в Telegram

После завершения сборки, бот созданный в <code>Telegram</code>, автоматически обрабатывает и отправляет сообщение с результатом пройденных тестов.

<p align="center">
<img width="70%" title="Telegram Notifications" src="images/screen/Telegram.png">
</p>

## <img src="images/logo/Selenoid.svg" title="Allure TestOps" width="5%"/> Видео примера запуска тестов в Selenoid

К тестам, в которых есть часть, которая запускается на удаленном браузере [Selenoid](https://aerokube.com/selenoid/), прилагается видео прогона.
<p align="center">
  <img title="Selenoid Video" src="images/video/UI.gif">
</p>