# Code with Friends 👨💻👩💻  
**Социальная сеть для совместной разработки IT-проектов**  

[![RuStore](https://img.shields.io/badge/Download-RuStore-%230078D7?logo=android)](https://www.rustore.ru/catalog/app/com.ilya.codewithfriends)
[![Firebase](https://img.shields.io/badge/Firebase-Platform-%23FFCA28?logo=firebase)](https://firebase.google.com/)

Платформа для программистов, где можно создавать проекты в команде, обмениваться медиа, ставить задачи и находить единомышленников.

---

## 🚀 Возможности
- **Совместная разработка**  
  Редактирование кода в реальном времени, Git-интеграция, таск-менеджер
- **Медиа-чат**  
  Отправка фото/видео/GIF с кастомным видеоплеером
- **Поиск команд**  
  Рекомендации проектов по интересам, система рейтинга
- **Безопасность**  
  Шифрование сообщений и приватных репозиториев

---

## 📸 Скриншоты

| Лента проектов | Совместный редактор | Медиа-чат |
|----------------|----------------------|-----------|
| <img src="https://github.com/user-attachments/assets/64c696c7-0750-4c4f-b5f6-40d5af6c9e2f" width="300"> | <img src="https://github.com/user-attachments/assets/19174eee-e935-4234-b50c-507a6796186a" width="300"> | <img src="https://github.com/user-attachments/assets/03643b98-b3f4-4975-945c-064c8070f2db" width="300"> |

| Управление задачами | Профиль пользователя | Git-история |
|---------------------|----------------------|-------------|
| <img src="https://github.com/user-attachments/assets/5f158115-d12d-4a19-b145-b891d56e42fa" width="300"> | <img src="https://github.com/user-attachments/assets/0c1e8e09-4039-482c-8a80-806914c9c20b" width="300"> | <img src="https://github.com/user-attachments/assets/d5485a57-4336-4498-becd-6b8d63f87298" width="300"> |

---

## 🕰 История разработки  
**23.07.2023** — Первый коммит  
**1 год активной разработки** с командой дизайнеров  

Эволюция интерфейса:  
<div align="center">
  <img src="https://github.com/user-attachments/assets/eb2e3554-1628-4aa2-b7cd-821a559b6b35" width="45%">
  <em>Первый прототип чата → Текущая версия</em>
</div>

Архитектура Git-веток:  
![Git workflow](https://github.com/user-attachments/assets/529a976f-2a36-4f3f-8ce9-8b5b3179f545)

---

## 🛠 Технологии

### Клиент (Android)
- **Аутентификация**: Firebase Auth (Google/Email)
- **Хранилище**: Firebase Storage (видео/фото/GIF)
- **Медиа**: Кастомный видеоплеер (ExoPlayer модификации)
- **Архитектура**: Clean Architecture + MVVM

### Сервер
- **Фреймворк**: Ktor (REST API + WebSockets)
- **Базы данных**: PostgreSQL (основные данные), Redis (кеш)
- **Инфраструктура**: Docker, Kubernetes

---

## 🔒 Безопасность
- Шифрование AES-256 для приватных сообщений
- OAuth2 для авторизации API
- Регулярные аудиты кода

---

## 📄 Лицензия
