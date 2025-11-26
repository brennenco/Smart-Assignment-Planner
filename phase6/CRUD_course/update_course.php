<?php
$conn = new mysqli("localhost", "root", "", "smart_assignment_planner");

$course_id = $_POST['course_id'];
$course_name = $_POST['course_name'];

$sql = "UPDATE Course SET course_name = ? WHERE course_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("si", $course_name, $course_id);

if ($stmt->execute()) {
    echo "Course updated!";
} else {
    echo "Error: " . $stmt->error;
}

$stmt->close();
$conn->close();
?>

