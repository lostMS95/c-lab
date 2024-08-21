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
            min-height: 100vh;
        }

        .upload-container {
            background-color: #ffffff;
            padding: 30px;
            border-radius: 15px;
            box-shadow: 0 0 25px rgba(0, 0, 0, 0.1);
            max-width: 500px;
            width: 90%;
            margin: 20px;
            text-align: center;
        }

        .upload-container h1 {
            color: #007bff;
            font-weight: 700;
            margin-bottom: 20px;
            font-size: 24px;
        }

        .upload-container .form-group {
            margin-bottom: 15px;
        }

        .upload-container .form-group label {
            font-weight: bold;
            color: #495057;
            font-size: 14px;
            text-align: left;
            display: block;
            margin-bottom: 8px;
        }

        .upload-container .form-control {
            padding: 12px;
            border-radius: 10px;
            border: 1px solid #ced4da;
            font-size: 16px;
        }

        .upload-container .form-control:focus {
            border-color: #007bff;
            box-shadow: 0 0 5px rgba(0, 123, 255, 0.5);
            outline: none;
        }

        .upload-container .btn-primary {
            background-color: #007bff;
            border: none;
            padding: 12px;
            border-radius: 10px;
            font-size: 18px;
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
            border-radius: 10px;
        }

        .upload-container .btn-secondary {
            margin-top: 10px;
            background-color: #6c757d;
            border: none;
            padding: 10px;
            border-radius: 10px;
            font-size: 16px;
            width: 100%;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }

        .upload-container .btn-secondary:hover {
            background-color: #5a6268;
        }

        .footer-text {
            margin-top: 20px;
            font-size: 14px;
            color: #6c757d;
        }

        .footer-text a {
            color: #007bff;
            text-decoration: none;
        }

        .footer-text a:hover {
            text-decoration: underline;
        }

        @media (max-width: 576px) {
            .upload-container h1 {
                font-size: 22px;
            }

            .upload-container .btn-primary,
            .upload-container .btn-secondary {
                font-size: 16px;
                padding: 10px;
            }
        }
    </style>
</head>
<body>
<div class="upload-container">
    <h1>파일 업로드</h1>
    <form action="/upload" method="post" enctype="multipart/form-data">
        <div class="form-group">
            <label for="file">파일 선택:</label>
            <input type="file" class="form-control" id="file" name="file" required>
        </div>
        <button type="submit" class="btn btn-primary">업로드</button>
    </form>

    <c:if test="${not empty message}">
        <div class="alert alert-info">
                ${message}
        </div>
    </c:if>


</div>

<!-- Bootstrap JS (optional for Bootstrap features) -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>