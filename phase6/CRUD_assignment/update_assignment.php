<?php
$conn = new mysqli("localhost", "root", "YOURPASS", "smart_assignment_planner");

$id = $_POST['assignment_id'];
$name = $_POST['name'];
$due_date = $_POST['due_date'];
$status = $_POST['completion_status'];

$stmt = $conn->prepare("UPDATE Assignment SET name=?, due_date=?, completion_status=? WHERE assignment_id=?");
$stmt->bind_param("ssii", $name, $due_date, $status, $id);
$stmt->execute();

echo "success";
