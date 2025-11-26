<?php
$conn = new mysqli("localhost", "root", "YOURPASS", "smart_assignment_planner");

$id = $_POST['assignment_id'];

$stmt = $conn->prepare("DELETE FROM Assignment WHERE assignment_id=?");
$stmt->bind_param("i", $id);
$stmt->execute();

echo "success";
