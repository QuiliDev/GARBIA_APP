# Mejoras propuestas para GarbiaApp

> Listado de mejoras identificadas tras revisar el estado actual del proyecto.
> Ordenadas por categoría y prioridad.

---

## Arquitectura (base técnica)

| # | Mejora | Descripción | Prioridad |
|---|--------|-------------|-----------|
| 1 | **ViewModels + StateFlow** | Toda la lógica de estado está en `remember` local dentro de los Composables. Extraerla a ViewModels con Hilt permitiría que los datos sobrevivan rotaciones de pantalla y sea testeable. | Alta |
| 2 | **Room Database** | No hay persistencia local. Los puntos, historial de escaneos y datos del usuario se pierden al cerrar la app. Room resolvería esto con tablas de `Escaneo`, `Usuario` y `Logro`. | Alta |
| 3 | **Integrar API de IA real** | `ProcessingScreen` simula el análisis con un temporizador y `ResultScreen` devuelve resultados aleatorios. Ktor ya está configurado en el proyecto, solo falta conectarlo a un endpoint real (Google Vision, OpenAI, Clarifai…). | Alta |

---

## Funcionalidad

| # | Mejora | Descripción | Prioridad |
|---|--------|-------------|-----------|
| 4 | **Historial real de escaneos** | La sección "Actividad reciente" en HomeScreen muestra datos falsos. Con Room, podría mostrar los últimos escaneos reales con miniatura de la foto, fecha y puntos obtenidos. | Alta |
| 5 | **Sistema de logros / badges** | Basándose en los puntos y escaneos acumulados, desbloquear medallas (ej: _"Primer reciclaje"_, _"50 escaneos"_, _"Experto en vidrio"_). Coherente con la gamificación existente. | Media |
| 6 | **Onboarding** | No existe flujo de primera apertura. Una pantalla de bienvenida de 2-3 pasos explicando cómo funciona la app mejoraría mucho la primera impresión. | Media |
| 7 | **Autenticación de usuario** | El perfil actualmente es ficticio. Integrar Firebase Auth (Google Sign-In) daría identidad real al usuario y permitiría sincronizar datos entre dispositivos. | Media |
| 8 | **Notificaciones locales** | Recordatorios para reciclar con WorkManager (ej: _"¡Llevas 3 días sin escanear nada!"_). | Baja |

---

## UX / UI

| # | Mejora | Descripción | Prioridad |
|---|--------|-------------|-----------|
| 9 | **Animaciones de transición** | Compose Navigation admite `AnimatedNavHost` con transiciones de entrada/salida. Las pantallas actuales aparecen sin animación. | Media |
| 10 | **Pantalla de estadísticas** | Nueva pantalla con gráficas (usando `Canvas` o librería Vico) mostrando la evolución de puntos y CO₂ ahorrado por semana/mes. | Media |
| 11 | **Compartir resultado** | Botón en `ResultScreen` para compartir la tarjeta de resultado como imagen en redes sociales via `Intent.ACTION_SEND`. | Baja |
| 12 | **Modo offline + caché** | Si no hay conexión, mostrar el último resultado similar en base a la categoría detectada localmente, en lugar de un error. | Baja |

---

## Técnico / Calidad

| # | Mejora | Descripción | Prioridad |
|---|--------|-------------|-----------|
| 13 | **Tests unitarios** | No hay ningún test en el proyecto. Con ViewModels sería sencillo añadir tests con JUnit5 + MockK para la lógica de puntuación y clasificación. | Media |
| 14 | **Soporte multi-idioma (i18n)** | Todos los textos están hardcodeados en el código. Extraerlos a `strings.xml` permitiría añadir inglés u otros idiomas. | Baja |
| 15 | **Gestión de errores de cámara** | `CameraScreen` maneja permisos, pero no errores de hardware (cámara ocupada por otra app, dispositivo sin cámara trasera, etc.). | Baja |

---

## Hoja de ruta sugerida

Para un proyecto final DAM, el orden recomendado sería:

```
Fase 1 — Base técnica
  └── ViewModels (1) → Room Database (2) → Historial real (4)

Fase 2 — Funcionalidad core
  └── API de IA real (3) → Autenticación Firebase (7)

Fase 3 — Experiencia de usuario
  └── Onboarding (6) → Logros/Badges (5) → Animaciones (9) → Estadísticas (10)

Fase 4 — Pulido
  └── Compartir (11) → Notificaciones (8) → Tests (13) → i18n (14)
```
