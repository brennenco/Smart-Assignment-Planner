<?php
$conn = new mysqli("localhost", "root", "", "smart_assignment_planner");

$course_id = $_POST['course_id'];

$sql = "DELETE FROM Course WHERE course_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $course_id);

if ($stmt->execute()) {
    echo "Course deleted!";
} else {
    echo "Error: " . $stmt->error;
}

$stmt->close();
$conn->close();
?>

