# Image Upload Service

Upload images to GitHub repository and get jsDelivr CDN links.

## Setup

1. Create GitHub Personal Access Token with `repo` permissions
2. Update `application.properties`:
```properties
github.token=your_token_here
github.repository=username/repo_name
github.branch=main
github.path=images
```

## API Endpoints

### Single Image Upload
`POST /api/upload/image`
- Parameter: `file` (MultipartFile)

### Multiple Images Upload  
`POST /api/upload/images`
- Parameter: `files` (MultipartFile[])

### Health Check
`GET /api/upload/health`

## Response Format
```json
{
    "cdnUrl": "https://cdn.jsdelivr.net/gh/username/repo@main/images/filename.jpg",
    "originalFileName": "original.jpg", 
    "githubUrl": "https://raw.githubusercontent.com/username/repo/main/images/filename.jpg",
    "message": "Image uploaded successfully",
    "success": true
}
```

## File Validation
- Image files only
- Max size: 10MB
- Non-empty files

## Example Usage
```javascript
const formData = new FormData();
formData.append('file', imageFile);

fetch('/api/upload/image', {
    method: 'POST',
    body: formData
})
.then(response => response.json())
.then(data => console.log('CDN URL:', data.cdnUrl));
``` 