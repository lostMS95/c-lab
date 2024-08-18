<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - C-Lab</title>
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap" rel="stylesheet">
    <!-- Font Awesome Icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" />
    <!-- Custom CSS -->
    <style>
        body {
            font-family: 'Roboto', sans-serif;
            background-color: #f4f4f9;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .login-container {
            background-color: #ffffff;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            max-width: 400px;
            width: 100%;
        }

        .login-container h1 {
            text-align: center;
            margin-bottom: 30px;
            color: #333333;
        }

        .login-container .input-group {
            margin-bottom: 20px;
        }

        .login-container .input-group label {
            display: block;
            font-weight: bold;
            margin-bottom: 5px;
            color: #666666;
        }

        .login-container .input-group input {
            width: 100%;
            padding: 10px;
            border-radius: 5px;
            border: 1px solid #cccccc;
            font-size: 16px;
        }

        .login-container .input-group input:focus {
            border-color: #6c63ff;
            outline: none;
        }

        .login-container .btn {
            display: block;
            width: 100%;
            background-color: #6c63ff;
            color: #ffffff;
            padding: 15px;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }

        .login-container .btn:hover {
            background-color: #5752d4;
        }

        .login-container .footer {
            text-align: center;
            margin-top: 20px;
        }

        .login-container .footer a {
            color: #6c63ff;
            text-decoration: none;
        }

        .login-container .footer a:hover {
            text-decoration: underline;
        }

        .login-container .social-login {
            display: flex;
            justify-content: center;
            margin-top: 30px;
        }

        .login-container .social-login a {
            margin: 0 10px;
            font-size: 24px;
            color: #666666;
            transition: color 0.3s ease;
        }

        .login-container .social-login a:hover {
            color: #6c63ff;
        }

        .error-message {
            color: #ff4d4d;
            margin-bottom: 20px;
            text-align: center;
        }
    </style>
</head>
<body>
<div class="login-container">
    <h1>C-LAB</h1>

    <c:if test="${not empty error}">
        <p class="error-message">${error}</p>
    </c:if>

    <form action="/perform_login" method="post">
        <div class="input-group">
            <label for="username">아이디</label>
            <input type="text" id="username" name="username" required>
        </div>
        <div class="input-group">
            <label for="password">비밀번호</label>
            <input type="password" id="password" name="password" required>
        </div>
        <button type="submit" class="btn">로그인</button>
    </form>

    <div class="footer">
        <p>Forgot your password? <a href="#">Reset it here</a></p>
        <p>Don't have an account? <a href="#">Sign up now</a></p>
    </div>

    <div class="social-login">
        <a href="#"><i class="fab fa-facebook-square"></i></a>
        <a href="#"><i class="fab fa-google"></i></a>
        <a href="#"><i class="fab fa-twitter-square"></i></a>
    </div>
</div>
</body>
</html>
