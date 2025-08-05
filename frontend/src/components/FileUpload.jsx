import React, { useState } from 'react';

const FileUpload = () => {
  const [file, setFile] = useState(null);
  const [transcription, setTranscription] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleFileChange = (event) => {
    setFile(event.target.files[0]);
  };

  const handleUpload = async () => {
    if (!file) {
      alert('Please select a file first!');
      return;
    }

    setIsLoading(true);
    const formData = new FormData();
    formData.append('file', file);

    try {
      const response = await fetch('http://localhost:8080/audio/api/transcribe', {
        method: 'POST',
        body: formData,
      });

      if (response.ok) {
        const data = await response.json();
        setTranscription(data.text);
      } else {
        console.error('Upload failed');
        setTranscription('Failed to get transcription.');
      }
    } catch (error) {
      console.error('Error uploading file:', error);
      setTranscription('Error during transcription.', error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div>
      <input type="file" onChange={handleFileChange} accept="audio/*" />
      <button onClick={handleUpload} disabled={isLoading}>
        {isLoading ? 'Transcribing...' : 'Upload and Transcribe'}
      </button>
      <div>
        <h3>Transcription:</h3>
        <p>{transcription}</p>
      </div>
    </div>
  );
};

export default FileUpload;
