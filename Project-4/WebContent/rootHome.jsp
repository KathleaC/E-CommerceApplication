<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Root User - Project 4</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }

        .container {
            max-width: 900px;
            margin: 0 auto;
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }

        h1 {
            color: #007cba;
            text-align: center;
            border-bottom: 2px solid #007cba;
            padding-bottom: 10px;
        }

        .subtitle {
            text-align: center;
            color: #666;
            font-style: italic;
            margin-bottom: 20px;
        }

        .user-info {
            background-color: #e8f4fc;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 20px;
            border-left: 4px solid #007cba;
        }

        .sql-form {
            margin-bottom: 20px;
        }

        textarea {
            width: 100%;
            height: 100px;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-family: monospace;
            box-sizing: border-box;
        }

        .button-group {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin: 10px 0;
        }

        .left-buttons {
            display: flex;
            gap: 10px;
        }

        button {
            padding: 8px 16px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            transition: all 0.2s ease;
        }
        
        button:hover{
            opacity: 0.9;
            transform: scale(1.02);
        }

        .execute-btn {
            background-color: #28a745;
            color: white;
        }

        .reset-btn {
            background-color: #ffc107;
            color: black;
        }

        .clear-btn {
            background-color: #dc3545;
            color: white;
        }

        .logout-btn {
            background-color: #007cba;
            color: white;
            text-decoration: none;
            padding: 8px 16px;
            border-radius: 4px;
            display: inline-block;
        }

        .logout-btn:hover {
            opacity: 0.9;
            transform: scale(1.02);
        }

        .results-area {
            margin-top: 20px;
            border-top: 2px solid #ccc;
            padding-top: 20px;
        }
    </style>
</head>

<body>
    <div class="container">
        <h1>Welcome to the Fall 2025 Project 4 Enterprise System</h1>
        <div class="subtitle">
            A Servlet/JSP-based Multi-tiered Enterprise Application Using A Tomcat Container
        </div>

        <div class="user-info">
            You are connected to the Project 4 Enterprise System database as a <strong>root-level user</strong>.
            Please enter any SQL query or update command in the box below.
        </div>

        <form action="RootUserServlet" method="post">
            <div class="sql-form">
                <textarea name="sqlCommand" placeholder="Enter SQL command here..."></textarea>
            </div>

            <div class="button-group">
                <div class="left-buttons">
                    <button type="submit" class="execute-btn">Execute Command</button>
                    <button type="reset" class="reset-btn">Reset Form</button>
                    <button type="button" class="clear-btn" onclick="clearResults()">Clear Results</button>
                </div>
                <a href="authentication.html" class="logout-btn">Logout</a>
            </div>
        </form>

        <div class="results-area">
            <strong>All execution results will appear below this line.</strong>
            <hr>
            <div id="executionResults">
                <% 
                    String resultsHtml = (String) request.getAttribute("resultsHtml");
                    if (resultsHtml != null) {
                        out.print(resultsHtml);
                    } else {
                        out.print("<em>Execution Results will appear here...</em>");
                    }
                %>
            </div>
        </div>
    </div>

    <script>
        function clearResults() {
            document.getElementById('executionResults').innerHTML = '<em>Execution Results cleared...</em>';
        }
    </script>
</body>

</html>