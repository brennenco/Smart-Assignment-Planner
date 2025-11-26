<?php
$conn = new mysqli("localhost", "root", "", "smart_assignment_planner");

$result = $conn->query("SELECT * FROM Course");

$courses = [];

while ($row = $result->fetch_assoc()) {
    $courses[] = $row;
}

echo json_encode($courses);

$conn->close();
?>
