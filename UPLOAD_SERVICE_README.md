# Image Upload Service

This service allows you to upload images from your frontend application, automatically commit them to a GitHub repository, and return jsDelivr CDN links for fast image delivery.

## Features

- ✅ Single image upload
- ✅ Multiple image upload
- ✅ Automatic GitHub repository integration
- ✅ jsDelivr CDN link generation
- ✅ File validation (type, size)
- ✅ Error handling and logging
- ✅ Cross-origin support

## Setup Instructions

### 1. GitHub Repository Setup

1. Create a new GitHub repository (or use an existing one)
2. Make sure the repository is public (required for jsDelivr CDN)
3. Create a Personal Access Token:
   - Go to GitHub Settings → Developer settings → Personal access tokens
   - Generate a new token with `repo` permissions
   - Copy the token

### 2. Configuration

Update the `application.properties` file with your GitHub credentials:

```properties
# GitHub Configuration
github.token=your_github_personal_access_token_here
github.repository=your_username/your_repository_name
github.branch=main
github.path=images
```

**Important:** Replace the placeholder values:
- `your_github_personal_access_token_here`: Your GitHub personal access token
- `your_username/your_repository_name`: Your GitHub username and repository name (e.g., `john/my-images`)

### 3. Repository Structure

The service will automatically create the following structure in your GitHub repository:

```
your-repository/
├── images/
│   ├── 20241201_143022_abc12345.jpg
│   ├── 20241201_143023_def67890.png
│   └── ...
└── README.md
```

## API Endpoints

### 1. Upload Single Image

**Endpoint:** `POST /api/upload/image`

**Request:**
```javascript
const formData = new FormData();
formData.append('file', imageFile);

fetch('/api/upload/image', {
    method: 'POST',
    body: formData
})
.then(response => response.json())
.then(data => {
    console.log('CDN URL:', data.cdnUrl);
    console.log('GitHub URL:', data.githubUrl);
});
```

**Response:**
```json
{
    "cdnUrl": "https://cdn.jsdelivr.net/gh/username/repo@main/images/20241201_143022_abc12345.jpg",
    "originalFileName": "my-image.jpg",
    "githubUrl": "https://raw.githubusercontent.com/username/repo/main/images/20241201_143022_abc12345.jpg",
    "message": "Image uploaded successfully",
    "success": true
}
```

### 2. Upload Multiple Images

**Endpoint:** `POST /api/upload/images`

**Request:**
```javascript
const formData = new FormData();
files.forEach(file => {
    formData.append('files', file);
});

fetch('/api/upload/images', {
    method: 'POST',
    body: formData
})
.then(response => response.json())
.then(data => {
    data.forEach(item => {
        console.log('CDN URL:', item.cdnUrl);
    });
});
```

**Response:**
```json
[
    {
        "cdnUrl": "https://cdn.jsdelivr.net/gh/username/repo@main/images/20241201_143022_abc12345.jpg",
        "originalFileName": "image1.jpg",
        "githubUrl": "https://raw.githubusercontent.com/username/repo/main/images/20241201_143022_abc12345.jpg",
        "message": "Image uploaded successfully",
        "success": true
    },
    {
        "cdnUrl": "https://cdn.jsdelivr.net/gh/username/repo@main/images/20241201_143023_def67890.png",
        "originalFileName": "image2.png",
        "githubUrl": "https://raw.githubusercontent.com/username/repo/main/images/20241201_143023_def67890.png",
        "message": "Image uploaded successfully",
        "success": true
    }
]
```

### 3. Health Check

**Endpoint:** `GET /api/upload/health`

**Response:**
```
Upload service is running
```

## File Validation

The service validates uploaded files with the following rules:

- **File Type:** Must be an image (MIME type starting with `image/`)
- **File Size:** Maximum 10MB per file
- **File Content:** Must not be empty

## Error Handling

The service returns appropriate error messages for various scenarios:

- Empty file: `"File is empty"`
- Invalid file type: `"File must be an image"`
- File too large: `"File size must be less than 10MB"`
- Upload failure: `"Failed to upload image: [error details]"`

## CDN Benefits

Using jsDelivr CDN provides:

- **Fast Loading:** Global CDN with edge locations worldwide
- **Caching:** Automatic caching for better performance
- **Reliability:** High availability and uptime
- **Free:** No cost for using jsDelivr CDN

## Security Considerations

1. **GitHub Token:** Keep your GitHub personal access token secure
2. **Repository Privacy:** Use a public repository for jsDelivr CDN to work
3. **File Validation:** The service validates file types and sizes
4. **CORS:** Configure CORS settings as needed for your frontend

## Troubleshooting

### Common Issues

1. **401 Unauthorized:** Check your GitHub token and repository permissions
2. **404 Not Found:** Verify the repository name and branch
3. **File Upload Fails:** Check file size and type validation
4. **CDN URL Not Working:** Ensure the repository is public

### Logs

Check the application logs for detailed error information:

```bash
tail -f logs/application.log
```

## Example Frontend Integration

```html
<!DOCTYPE html>
<html>
<head>
    <title>Image Upload Example</title>
</head>
<body>
    <input type="file" id="imageInput" accept="image/*" multiple>
    <button onclick="uploadImages()">Upload Images</button>
    <div id="results"></div>

    <script>
        async function uploadImages() {
            const input = document.getElementById('imageInput');
            const files = input.files;
            
            if (files.length === 0) {
                alert('Please select files');
                return;
            }

            const formData = new FormData();
            for (let file of files) {
                formData.append('files', file);
            }

            try {
                const response = await fetch('/api/upload/images', {
                    method: 'POST',
                    body: formData
                });

                const results = await response.json();
                displayResults(results);
            } catch (error) {
                console.error('Upload failed:', error);
                alert('Upload failed');
            }
        }

        function displayResults(results) {
            const resultsDiv = document.getElementById('results');
            resultsDiv.innerHTML = '';

            results.forEach(result => {
                if (result.success) {
                    const img = document.createElement('img');
                    img.src = result.cdnUrl;
                    img.style.maxWidth = '200px';
                    img.style.margin = '10px';
                    resultsDiv.appendChild(img);
                } else {
                    const error = document.createElement('p');
                    error.textContent = `Error: ${result.message}`;
                    error.style.color = 'red';
                    resultsDiv.appendChild(error);
                }
            });
        }
    </script>
</body>
</html>
```

## Dependencies

The service uses the following key dependencies:

- `org.kohsuke:github-api`: GitHub API integration
- `commons-io:commons-io`: File operations
- `org.apache.commons:commons-lang3`: Utility functions
- `spring-boot-starter-web`: Web framework

## License

This project is part of the Truyenchu demo application. 