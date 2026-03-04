<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Todo List</title>
</head>
<body>
<h2>Todo List</h2>

<a href="todo?action=add">Add Todo</a>
<br/><br/>

<table border="1">
    <tr>
        <th>ID</th>
        <th>Title</th>
        <th>Action</th>
    </tr>

    <%
        java.util.List<com.example.todo.Todo> todos =
                (java.util.List<com.example.todo.Todo>) request.getAttribute("todos");

        if (todos != null) {
            for (com.example.todo.Todo t : todos) {
    %>
    <tr>
        <td><%= t.getId() %></td>
        <td><%= t.getTitle() %></td>
        <td><a href="todo?action=delete&id=<%= t.getId() %>">Delete</a></td>
    </tr>
    <%
            }
        }
    %>
</table>

</body>
</html>