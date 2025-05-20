from flask import Flask, request, jsonify
import whisper
import os
import tempfile
import logging
import subprocess

app = Flask(__name__)

# Configure logging
logging.basicConfig(level=logging.ERROR)
logger = logging.getLogger(__name__)

# Load the Whisper model (you can choose different sizes: tiny, base, small, medium, large)
model = whisper.load_model("base")

def check_ffmpeg():
    try:
        subprocess.run(['ffmpeg', '-version'], capture_output=True, check=True)
        logger.info("FFmpeg is installed and working")
        return True
    except (subprocess.SubprocessError, FileNotFoundError) as e:
        logger.error(f"FFmpeg check failed: {str(e)}")
        return False

@app.route('/transcribe', methods=['POST'])
def transcribe_audio():
    temp_file_path = None
    try:
        # Check if FFmpeg is installed
        if not check_ffmpeg():
            return jsonify({"error": "FFmpeg is not installed. Please install FFmpeg to process audio files."}), 500

        # Check if file is in the request
        if 'file' not in request.files:
            return jsonify({"error": "No file provided"}), 400
        
        file = request.files['file']
        if file.filename == '':
            return jsonify({"error": "No file selected"}), 400

        # Get the file extension
        file_ext = os.path.splitext(file.filename)[1]
        if not file_ext:
            file_ext = '.mp3'  # default extension

        # Create a temporary file with the correct extension
        temp_file = tempfile.NamedTemporaryFile(delete=False, suffix=file_ext)
        temp_file_path = temp_file.name
        temp_file.close()

        # Save the uploaded file
        file.save(temp_file_path)
        logger.debug(f"File saved to: {temp_file_path}")

        # Verify the file exists and has content
        if not os.path.exists(temp_file_path):
            return jsonify({"error": "Failed to save temporary file"}), 500
        
        file_size = os.path.getsize(temp_file_path)
        if file_size == 0:
            return jsonify({"error": "Uploaded file is empty"}), 400

        logger.debug(f"File size: {file_size} bytes")

        # Transcribe the audio
        result = model.transcribe(temp_file_path)
        logger.debug("Transcription completed successfully")

        return jsonify({
            "text": result["text"],
            "segments": result["segments"]
        })

    except Exception as e:
        logger.error(f"Error during transcription: {str(e)}", exc_info=True)
        return jsonify({"error": str(e)}), 500

    finally:
        # Clean up the temporary file
        if temp_file_path and os.path.exists(temp_file_path):
            try:
                os.unlink(temp_file_path)
                logger.debug(f"Temporary file deleted: {temp_file_path}")
            except Exception as e:
                logger.error(f"Error deleting temporary file: {str(e)}")

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)