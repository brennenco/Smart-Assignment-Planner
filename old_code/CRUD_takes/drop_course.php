<?php
$conn = new mysqli("localhost", "root", "", "smart_assignment_planner");

$email = $_POST['email'];
$course_id = $_POST['course_id'];

$sql = "DELETE FROM Takes WHERE email = ? AND course_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("si", $email, $course_id);

if ($stmt->execute()) {
    echo "Course dropped!";
} else {
    echo "Error: " . $stmt->error;
}

$stmt->close();
$conn->close();
?>


