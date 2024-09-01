<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html>
<head>
    <title>Chat Room</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f1f1f1;
            margin: 0;
            padding: 0;
        }
        #chat-container {
            max-width: 600px;
            margin: 50px auto;
            background-color: white;
            border-radius: 10px;
            padding: 20px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        .message {
            display: flex;
            margin-bottom: 15px;
        }
        .message.received {
            justify-content: flex-start;
        }
        .message.sent {
            justify-content: flex-end;
        }
        .message-content {
            max-width: 70%;
            padding: 10px;
            border-radius: 10px;
            background-color: #dcf8c6; /* Sent message background color */
            margin: 0 10px;
            position: relative;
        }
        .message.received .message-content {
            background-color: #ffffff; /* Received message background color */
            border: 1px solid #e5e5e5;
        }
        .message-content p {
            margin: 0;
            word-wrap: break-word;
        }
        .message .sender-info {
            font-size: 12px;
            color: #888;
        }
        #message-area {
            max-height: 400px;
            overflow-y: auto;
            margin-bottom: 20px;
        }
        #input-section {
            display: flex;
        }
        #input-section input {
            flex: 1;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
            margin-right: 10px;
        }
        #input-section button {
            padding: 10px 20px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }
    </style>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.4.0/sockjs.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
    <script>
        var socket = new SockJS('/ws');
        var stompClient = null;
        var username = '<sec:authentication property="name" />';

        function connect() {
            stompClient = Stomp.over(socket);

            stompClient.connect({}, function (frame) {
                console.log('Connected: ' + frame);

                stompClient.subscribe('/queue/private', function (message) {
                    var chatMessage = JSON.parse(message.body);
                    if (chatMessage.sender === username) {
                        showMessage(chatMessage.content, 'sent', chatMessage.sender);
                    } else {
                        showMessage(chatMessage.content, 'received', chatMessage.sender);
                    }
                });

                stompClient.subscribe('/topic/public', function (message) {
                    var chatMessage = JSON.parse(message.body);
                    showMessage(chatMessage.sender + " joined the chat", 'received', chatMessage.sender);
                });

                stompClient.send("/app/chat.addUser", {}, JSON.stringify({sender: username, type: 'JOIN'}));
            });
        }

        function sendMessage() {
            var messageContent = document.getElementById("message").value;

            if (messageContent && stompClient) {
                var chatMessage = {
                    sender: username,
                    content: messageContent,
                    type: 'CHAT'
                };

                stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
                document.getElementById("message").value = '';
            }
        }

        function showMessage(message, type, sender) {
            var messageArea = document.getElementById("message-area");
            var messageElement = document.createElement('div');
            messageElement.classList.add('message', type);

            var messageContent = document.createElement('div');
            messageContent.classList.add('message-content');
            messageContent.innerHTML = '<p>' + message + '</p>';

            var senderInfo = document.createElement('div');
            senderInfo.classList.add('sender-info');
            senderInfo.innerHTML = sender;

            if (type === 'received') {
                messageElement.appendChild(senderInfo);
            }
            messageElement.appendChild(messageContent);

            messageArea.appendChild(messageElement);
            messageArea.scrollTop = messageArea.scrollHeight;
        }

        window.onload = function () {
            connect();
        };
    </script>
</head>
<body>
<div id="chat-container">
    <div id="message-area"></div>
    <div id="input-section">
        <input type="text" id="message" placeholder="Enter your message here..." />
        <button onclick="sendMessage()">Send</button>
    </div>
</div>
</body>
</html>
