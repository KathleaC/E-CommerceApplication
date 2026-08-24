<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Accountant - Project 4</title>
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
            color: #fd7e14;
            text-align: center;
            border-bottom: 2px solid #fd7e14;
            padding-bottom: 10px;
        }

        .subtitle {
            text-align: center;
            color: #666;
            font-style: italic;
            margin-bottom: 20px;
        }

        .user-info {
            background-color: #fff3e0;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 20px;
            border-left: 4px solid #fd7e14;
        }

        .report-list {
            margin-bottom: 20px;
        }

        .report-option {
            margin-bottom: 8px;
            padding: 8px;
            background-color: #f8f9fa;
            border-radius: 4px;
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
        
        button:hover {
            opacity: 0.9;
            transform: scale(1.02);
        }

        .execute-btn {
            background-color: #fd7e14;
            color: white;
        }

        .clear-btn {
            background-color: #dc3545;
            color: white;
        }

        .logout-btn {
            background-color: #fd7e14;
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
            You are connected to the Project 4 Enterprise System database as an <strong>accountant-level user</strong>.
            Please select the operation you would like to perform from the list below.
        </div>

        <form action="AccountantUserServlet" method="post">
            <div class="report-list">
                <div class="report-option">
                    <input type="radio" id="report1" name="reportType" value="max_status" required>
                    <label for="report1">Get The Maximum Status Value Of All Suppliers (Returns a maximum value)</label>
                </div>
                <div class="report-option">
                    <input type="radio" id="report2" name="reportType" value="total_weight">
                    <label for="report2">Get The Total Weight Of All Parts (Returns a sum)</label>
                </div>
                <div class="report-option">
                    <input type="radio" id="report3" name="reportType" value="total_shipments">
                    <label for="report3">Get The Total Number of Shipments (Returns the current number of shipments in total)</label>
                </div>
                <div class="report-option">
                    <input type="radio" id="report4" name="reportType" value="max_workers">
                    <label for="report4">Get The Name And Number Of Workers Of The Job With The Most Workers (Returns two values)</label>
                </div>
                <div class="report-option">
                    <input type="radio" id="report5" name="reportType" value="supplier_status">
                    <label for="report5">List The Name And Status Of Every Supplier (Returns a list of supplier names with their current status)</label>
                </div>
            </div>

            <div class="button-group">
                <div class="left-buttons">
                    <button type="submit" class="execute-btn">Execute Command</button>
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