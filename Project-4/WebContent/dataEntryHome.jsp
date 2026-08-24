<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Data Entry - Project 4</title>
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
            color: #6f42c1;
            text-align: center;
            border-bottom: 2px solid #6f42c1;
            padding-bottom: 10px;
        }

        .subtitle {
            text-align: center;
            color: #666;
            font-style: italic;
            margin-bottom: 20px;
        }

        .user-info {
            background-color: #f0e6ff;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 20px;
            border-left: 4px solid #6f42c1;
        }

        .form-section {
            border: 1px solid #ddd;
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 4px;
        }

        .form-section h3 {
            margin-top: 0;
            color: #6f42c1;
        }

        .form-group {
            margin-bottom: 10px;
        }

        label {
            display: inline-block;
            width: 120px;
            font-weight: bold;
        }

        input {
            padding: 5px;
            border: 1px solid #ddd;
            border-radius: 3px;
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

        .submit-btn {
            background-color: #6f42c1;
            color: white;
        }

        .clear-btn {
            background-color: #dc3545;
            color: white;
        }

        .logout-btn {
            background-color: #6f42c1;
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
        <div class="subtitle">Data Entry Application</div>

        <div class="user-info">
            You are connected to the Project 4 Enterprise System database as a <strong>data-entry-level user</strong>.
            Enter the data values in a form below to add a new record to the corresponding database table.
        </div>

        <!-- Single Logout Button at the top - FIXED -->
        <div class="button-group">
            <div class="left-buttons">
                <!-- Empty left side to balance the logout button on right -->
            </div>
            <a href="authentication.html" class="logout-btn">Logout</a>
        </div>

        <!-- Rest of your forms remain the same -->
        <!-- Suppliers Form -->
        <div class="form-section">
            <h3>Suppliers Record Insert</h3>
            <form action="DataEntryUserServlet" method="post">
                <input type="hidden" name="formType" value="suppliers">
                <div class="form-group">
                    <label for="snum">snum:</label>
                    <input type="text" id="snum" name="snum" required>
                </div>
                <div class="form-group">
                    <label for="sname">sname:</label>
                    <input type="text" id="sname" name="sname" required>
                </div>
                <div class="form-group">
                    <label for="status">status:</label>
                    <input type="number" id="status" name="status" required>
                </div>
                <div class="form-group">
                    <label for="city">city:</label>
                    <input type="text" id="city" name="city" required>
                </div>
                <div class="button-group">
                    <div class="left-buttons">
                        <button type="submit" class="submit-btn">Enter Supplier Record Into Database</button>
                        <button type="button" class="clear-btn" onclick="clearFormAndResults(this)">Clear Data and Results</button>
                    </div>
                </div>
            </form>
        </div>

        <!-- Parts Form -->
        <div class="form-section">
            <h3>Parts Record Insert</h3>
            <form action="DataEntryUserServlet" method="post">
                <input type="hidden" name="formType" value="parts">
                <div class="form-group">
                    <label for="pnum">pnum:</label>
                    <input type="text" id="pnum" name="pnum" required>
                </div>
                <div class="form-group">
                    <label for="pname">pname:</label>
                    <input type="text" id="pname" name="pname" required>
                </div>
                <div class="form-group">
                    <label for="color">color:</label>
                    <input type="text" id="color" name="color" required>
                </div>
                <div class="form-group">
                    <label for="weight">weight:</label>
                    <input type="number" id="weight" name="weight" step="0.01" required>
                </div>
                <div class="form-group">
                    <label for="city">city:</label>
                    <input type="text" id="city" name="city" required>
                </div>
                <div class="button-group">
                    <div class="left-buttons">
                        <button type="submit" class="submit-btn">Enter Part Record Into Database</button>
                        <button type="button" class="clear-btn" onclick="clearFormAndResults(this)">Clear Data and Results</button>
                    </div>
                </div>
            </form>
        </div>

        <!-- Jobs Form -->
        <div class="form-section">
            <h3>Jobs Record Insert</h3>
            <form action="DataEntryUserServlet" method="post">
                <input type="hidden" name="formType" value="jobs">
                <div class="form-group">
                    <label for="jnum">jnum:</label>
                    <input type="text" id="jnum" name="jnum" required>
                </div>
                <div class="form-group">
                    <label for="jname">jname:</label>
                    <input type="text" id="jname" name="jname" required>
                </div>
                <div class="form-group">
                    <label for="numworkers">numworkers:</label>
                    <input type="number" id="numworkers" name="numworkers" required>
                </div>
                <div class="form-group">
                    <label for="city">city:</label>
                    <input type="text" id="city" name="city" required>
                </div>
                <div class="button-group">
                    <div class="left-buttons">
                        <button type="submit" class="submit-btn">Enter Job Record Into Database</button>
                        <button type="button" class="clear-btn" onclick="clearFormAndResults(this)">Clear Data and Results</button>
                    </div>
                </div>
            </form>
        </div>

        <!-- Shipments Form -->
        <div class="form-section">
            <h3>Shipments Record Insert</h3>
            <form action="DataEntryUserServlet" method="post">
                <input type="hidden" name="formType" value="shipments">
                <div class="form-group">
                    <label for="snum_ship">snum:</label>
                    <input type="text" id="snum_ship" name="snum" required>
                </div>
                <div class="form-group">
                    <label for="pnum_ship">pnum:</label>
                    <input type="text" id="pnum_ship" name="pnum" required>
                </div>
                <div class="form-group">
                    <label for="jnum_ship">jnum:</label>
                    <input type="text" id="jnum_ship" name="jnum" required>
                </div>
                <div class="form-group">
                    <label for="quantity">quantity:</label>
                    <input type="number" id="quantity" name="quantity" required>
                </div>
                <div class="button-group">
                    <div class="left-buttons">
                        <button type="submit" class="submit-btn">Enter Shipment Record Into Database</button>
                        <button type="button" class="clear-btn" onclick="clearFormAndResults(this)">Clear Data and Results</button>
                    </div>
                </div>
            </form>
        </div>

        <div class="results-area">
            <strong>Execution Results:</strong>
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
    function clearFormAndResults(button) {
        // Clear the form
        var form = button.closest('form');
        form.reset();
        
        // Clear the results
        clearResults();
    }
    </script>
</body>
</html>