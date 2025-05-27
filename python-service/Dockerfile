# Use an official Python runtime as a parent image
FROM python:3.9-slim

# Install system dependencies including FFmpeg
RUN apt-get update && apt-get install -y \
    ffmpeg \
    && rm -rf /var/lib/apt/lists/*

# Set the working directory in the container
WORKDIR /app

# Copy requirements first to leverage Docker cache
COPY requirements.txt .

# Install any needed packages specified in requirements.txt
RUN pip install --no-cache-dir -r requirements.txt

# Copy the application code
COPY src/main/python/whisper_service.py .

# Make port 5000 available to the world outside this container
EXPOSE 5000

# Add after the initial setup
RUN useradd -m appuser
USER appuser

# Run the application
CMD ["python", "whisper_service.py"] 