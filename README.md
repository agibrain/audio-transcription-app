# 🎙️ Automatic Speech Recognition Application

Provide a lightweight, full-stack web solution that lets anyone upload an audio file and instantly receive an accurate text transcription. It serves as:
A demo of integrating OpenAI Whisper into a production-ready microservice stack.
A template you can extend to add speaker diarization, language detection, summarization, etc.
A reference for bridging React → Spring Boot → Python ML workloads in a clean, container-friendly way.

End-to-end, full-stack web application for **automatic speech recognition (ASR)**. Upload an audio file in your browser and get the transcription in seconds — powered by [OpenAI Whisper](https://github.com/openai/whisper).

---

## ✨ Features

• Drag-and-drop audio upload (any format accepted)  
• Real-time progress indicator  
• Accurate transcription using Whisper-base model  
• JSON segments & plain-text output  
• 10 MB upload limit (configurable)  
• CORS and size limits pre-configured for production

---

## 🏗️ Tech Stack

| Layer      | Technology | Notes |
|------------|------------|-------|
| Front-end  | React 19 + Vite | Very small footprint, hot-reload, environment variables via `VITE_*` |
| Back-end   | Spring Boot 3 (Java 17) | Exposes `/audio/api/transcribe`, proxies file to Python service |
| ML Service | Python 3 + Flask + Whisper | Runs the heavy ASR model and returns JSON |
| Build      | Maven Wrapper, npm, pip | All projects are fully self-contained |
| Container  | Multi-stage Dockerfile (backend) | Produces <60 MB JRE image ready for Render/Fly.io |

---

## 📂 Repository Layout

```
📦audio-transcribe
 ├─ backend/               # Spring-Boot service
 ├─ frontend/              # React SPA
 ├─ python-service/        # Whisper micro-service
 ├─ README.md              # You are here 😊
 └─ …
```

---

## 🚀 Quick Start

### 1. Prerequisites

* **Java 17+** & **Maven 3.9+**  
* **Node 18+** & **npm** (or **pnpm**/ **yarn**)  
* **Python 3.9+** & **pip**  
* FFmpeg binary in `PATH` (required by Whisper)

### 2. Clone & configure

```bash
git clone https://github.com/<your-org>/audio-transcribe.git
cd audio-transcribe
```

Create local env-files (optional):

```bash
# backend/src/main/resources/application.properties
whisper.service.url=https://subhrajeetghosh-audio-transcriber.hf.space/transcribe

# frontend/.env.local
VITE_JAVA_API=https://audio-transcription-backend-f5q4.onrender.com
```

### 3. Start the Python Whisper service

```bash
cd python-service
python -m venv .venv && source .venv/bin/activate  # Windows: .venv\Scripts\activate
pip install -r requirements.txt
python app.py  # runs on :5000
```

### 4. Start the Spring Boot backend

```bash
cd ../backend
./mvnw spring-boot:run  # uses port 8080
```

### 5. Start the React front-end

```bash
cd ../frontend
npm install
npm run dev  # http://localhost:5173
```

Open your browser, select an audio file, and hit **“Upload & Transcribe”**.

---

## 🛠️ Running with Docker

The backend already ships with a multi-stage Dockerfile. A minimal three-service setup can be achieved via **Docker Compose** (snippet below). Save as `docker-compose.yml` and run `docker compose up --build`.

```yaml
version: '3.9'
services:
  whisper:
    build: ./python-service
    ports: ["5000:5000"]
  api:
    build: ./backend
    environment:
      - WHISPER_SERVICE_URL=http://whisper:5000/transcribe
    depends_on: [whisper]
    ports: ["8080:8080"]
  web:
    build: ./frontend
    environment:
      - VITE_JAVA_API=http://api:8080
    ports: ["5173:80"]
    depends_on: [api]
```

*(A production-ready compose file is out-of-scope for the demo but trivial to extend.)*

---

## 🌐 API Reference

### POST `/audio/api/transcribe`

| Field | Type | Description |
|-------|------|-------------|
| `file` | multipart/form-data | Audio file (any format supported by FFmpeg) |

**Response** `200 OK`
```json
{
  "text": "Transcribed sentence …",
  "segments": [
    {"id": 0, "start": 0.0, "end": 4.2, "text": "…"}
  ]
}
```

Errors will be returned with suitable `4xx / 5xx` codes and `{"error": "message"}` payload.

The Python microservice itself exposes the same contract at `POST /transcribe`.

---

## ⚙️ Configuration

| Component | Property/Env | Default | Description |
|-----------|--------------|---------|-------------|
| Backend | `whisper.service.url` | `https://subhrajeetghosh-audio-transcriber.hf.space/transcribe` | Target Python endpoint |
| Frontend | `VITE_JAVA_API` | `https://audio-transcription-backend-f5q4.onrender.com` | Base URL for Spring Boot |
| Backend | `spring.servlet.multipart.max-file-size` | `10MB` | Upload size limit |

---

## 🤕 Troubleshooting

1. **`ffmpeg` not found** – Install FFmpeg and ensure it is on your system `PATH`.
2. **CUDA out-of-memory** – Whisper will automatically fall back to CPU if GPU VRAM is insufficient.
3. **CORS errors** – Confirm allowed origins in `WebConfig.java`.

---

## 🙌 Contributing

Pull requests are welcome! Please open an issue first to discuss significant changes.  
1. Fork ➡ 2. Feature branch ➡ 3. PR with description & screenshots.

### Code Style
* Java: Google style-guide via `spotless` (coming soon)  
* JS/TS: ESLint + Prettier  
* Python: `black` & `ruff`

---

## 📝 License

This project is licensed under the **MIT License** – see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgements

* [OpenAI Whisper](https://github.com/openai/whisper)
* [Spring Boot](https://spring.io/projects/spring-boot)
* [React](https://react.dev/)
* [Vite](https://vitejs.dev/)
* [Render](https://render.com/) – effortless deployment
