<?php
$conn = new mysqli("localhost", "root", "YOURPASS", "smart_assignment_planner");

$course_id = $_POST['course_id'];
$name = $_POST['name'];
$due_date = $_POST['due_date'];
$type = $_POST['type'];
$completion_status = isset($_POST['completion_status']) ? 1 : 0;

$stmt = $conn->prepare("INSERT INTO Assignment (course_id, name, due_date, completion_status, type) VALUES (?, ?, ?, ?, ?)");
$stmt->bind_param("issis", $course_id, $name, $due_date, $completion_status, $type);
$stmt->execute();

echo "success";
