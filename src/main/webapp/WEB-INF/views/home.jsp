<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>File Upload</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap" rel="stylesheet">
    <!-- Custom CSS -->
    <style>
        body {
            font-family: 'Roboto', sans-serif;
            background-color: #f8f9fa;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .upload-container {
            background-color: #ffffff;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
            max-width: 600px;
            width: 100%;
        }

        .upload-container h1 {
            text-align: center;
            margin-bottom: 30px;
            color: #333333;
            font-weight: 700;
        }

        .upload-container .form-group {
            margin-bottom: 20px;
        }

        .upload-container .form-group label {
            font-weight: bold;
            color: #495057;
        }

        .upload-container .form-control {
            padding: 10px;
            border-radius: 5px;
            border: 1px solid #ced4da;
            font-size: 16px;
        }

        .upload-container .form-control:focus {
            border-color: #80bdff;
            box-shadow: 0 0 5px rgba(0, 123, 255, 0.5);
            outline: none;
        }

        .upload-container .btn-primary {
            background-color: #007bff;
            border: none;
            padding: 15px;
            border-radius: 5px;
            font-size: 16px;
            width: 100%;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }

        .upload-container .btn-primary:hover {
            background-color: #0056b3;
        }

        .upload-container .alert {
            margin-top: 20px;
            padding: 15px;
            font-size: 16px;
        }
    </style>
</head>
<body>
<div class="upload-container">
    <h1>Upload Your File</h1>
    <form action="/upload" method="post" enctype="multipart/form-data">
        <div class="form-group">
            <label for="file">Choose a file:</label>
            <input type="file" class="form-control" id="file" name="file" required>
        </div>
        <button type="submit" class="btn btn-primary">Upload</button>
    </form>

    <!-- 업로드 결과 메시지 표시 -->
    <c:if test="${not empty message}">
        <div class="alert alert-info mt-3">
                ${message}
        </div>
    </c:if>
</div>

<!-- Bootstrap JS (optional for Bootstrap features) -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
