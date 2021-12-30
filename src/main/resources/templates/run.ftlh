<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Config</title>
</head>
<body>
<h1>Run</h1>
<table>
    <tr>
        <td>Id</td>
        <td>Hash</td>
        <td>Config name</td>
        <td>Base url</td>
        <td>State</td>
        <td>User id</td>
    </tr>
    <tr>
        <td>${run.id}</td>
        <td>${run.hash}</td>
        <td>${run.config.name}</td>
        <td>${run.config.base_url}</td>
        <td>${run.state}</td>
        <td>${run.user_id}</td>
    </tr>
</table>
</body>
</html>