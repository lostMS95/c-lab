<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
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

        .user-info {
            background-color: #f0f8ff;
            padding: 12px 20px;
            border-radius: 12px;
            margin-bottom: 20px;
            font-size: 18px;
            color: #007bff;
            font-weight: 500;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            display: inline-block;
            text-align: left;
            border-left: 4px solid #007bff;
        }

        .user-info:before {
            content: '🔒';
            margin-right: 8px;
        }

        /* 로딩 바 스타일 */
        .loading-bar {
            display: none;
            margin-top: 20px;
        }

        .loading-bar div {
            width: 10px;
            height: 10px;
            margin: 3px;
            background-color: #007bff;
            border-radius: 50%;
            display: inline-block;
            animation: loading-animation 1.2s infinite ease-in-out both;
        }

        .loading-bar div:nth-child(1) { animation-delay: -0.24s; }
        .loading-bar div:nth-child(2) { animation-delay: -0.12s; }
        .loading-bar div:nth-child(3) { animation-delay: 0; }

        @keyframes loading-animation {
            0%, 100% { transform: scale(0); }
            50% { transform: scale(1); }
        }

        /* 하단 네비게이션 바 스타일 */
        .navbar-fixed-bottom {
            position: fixed;
            bottom: 0;
            left: 50%;
            transform: translateX(-50%);
            max-width: 500px;  /* 폼에 맞게 최대 너비 설정 */
            width: 100%;
            background-color: #ffffff;
            box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
            z-index: 1000;
            display: flex;
            justify-content: space-around;
            padding: 8px 0;
            border-radius: 15px 15px 0 0;  /* 위쪽 모서리 둥글게 */
        }

        /* 네비게이션 링크 스타일 */
        .navbar-fixed-bottom .nav-link {
            color: #007bff;
            font-weight: 500;
            text-align: center;
            padding: 8px 0;
            flex-grow: 1;
            font-size: 14px;
            text-decoration: none;
        }

        .navbar-fixed-bottom .nav-link:hover {
            background-color: #f8f9fa;
            color: #0056b3;
        }

        /* 모바일 기기용 스타일 */
        @media (max-width: 768px) {
            .navbar-fixed-bottom .nav-link {
                font-size: 12px;
                padding: 6px 0;
            }
        }

        /* PC용 스타일 */
        @media (min-width: 769px) {
            .navbar-fixed-bottom .nav-link {
                font-size: 14px;
                padding: 8px 0;
            }
        }

    </style>
</head>
<body>
<div class="upload-container">
    <h1>파일 업로드</h1>

    <!-- 로그인된 사용자 이름 표시 -->
    <sec:authorize access="isAuthenticated()">
        <p class="user-info">로그인된 사용자: ${pageContext.request.userPrincipal.name}</p>
    </sec:authorize>

    <form action="/svn/upload" method="post" enctype="multipart/form-data" onsubmit="return handleFormSubmit()">
        <div class="form-group">
            <label for="title">업로드 파일명:</label>
            <input type="text" class="form-control" id="title" name="title" required>
        </div>
        <div class="form-group">
            <label for="description">커밋메세지:</label>
            <textarea class="form-control" id="description" name="description" rows="2"></textarea>
        </div>

        <!-- 셀렉트 박스들 -->
        <div class="form-group">
            <label for="position">소속:</label>
            <select class="form-control" id="position" name="position" required>
                <option value="경영기획">경영기획</option>
                <option value="사업기획">사업기획</option>
                <option value="개발">개발</option>
                <option value="회사공통">회사공통</option>
                <option value="산업안전협회">산업안전협회</option>
                <option value="경운대학교">경운대학교</option>
                <option value="숭실대학교">숭실대학교</option>
                <option value="밸류마크">밸류마크</option>
                <option value="KMA">KMA</option>
                <option value="선행기술">선행기술</option>
                <option value="비즈비">비즈비</option>
            </select>
        </div>

        <div class="form-group">
            <label for="expend_type">지출구분:</label>
            <select class="form-control" id="expend_type" name="expend_type" required>
                <option value="회사공통">회사공통</option>
                <option value="야근식대">야근식대</option>
                <option value="교통비_야근">교통비_야근</option>
                <option value="친목활동">친목활동</option>
                <option value="교통비_출장/파견">교통비_출장/파견</option>
                <option value="교육비">교육비</option>
            </select>
        </div>

        <div class="form-group">
            <label for="card_type">카드 종류:</label>
            <select class="form-control" id="card_type" name="card_type" required>
                <option value="개인카드">개인카드</option>
                <option value="법인카드">법인카드</option>
            </select>
        </div>

        <div class="form-group">
            <label for="excel_date">날짜:</label>
            <input type="date" class="form-control" id="excel_date" name="excel_date" required>
        </div>
        <div class="form-group">
            <label for="excel_detail">내용:</label>
            <textarea class="form-control" id="excel_detail" name="excel_detail" rows="2" required></textarea>
        </div>
        <div class="form-group">
            <label for="excel_amount">금액:</label>
            <input type="number" class="form-control" id="excel_amount" name="excel_amount" required>
        </div>

        <div class="form-group">
            <label for="file">파일 선택:</label>
            <input type="file" class="form-control" id="file" name="file" required>
        </div>
        <button type="submit" class="btn btn-primary">업로드</button>
    </form>

    <div class="loading-bar" id="loadingBar">
        <div></div><div></div><div></div>
    </div>

    <c:if test="${not empty message}">
        <div class="alert alert-info">
                ${message}
        </div>
    </c:if>
</div>

<!-- 하단 네비게이션 바 추가 -->
<nav class="navbar navbar-fixed-bottom">
    <div class="container-fluid d-flex justify-content-around">
        <a class="nav-link" href="/mypage">메세지</a>
        <a class="nav-link" href="/test1">홈</a>
        <a class="nav-link" href="/test2">마이페이지</a>
    </div>
</nav>

<!-- Bootstrap JS (optional for Bootstrap features) -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
    function handleFormSubmit() {
        // 업로드 확인 메시지
        const confirmUpload = confirm("파일을 업로드하시겠습니까?");
        if (!confirmUpload) {
            return false;
        }

        // 로딩 바 표시
        document.getElementById('loadingBar').style.display = 'block';
        return true;
    }
</script>
</body>
</html>
