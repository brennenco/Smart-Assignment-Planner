<?php
$conn = new mysqli("localhost", "root", "", "smart_assignment_planner");

$email = $_POST['email'];
$course_id = $_POST['course_id'];
$grade = $_POST['grade'];

$sql = "INSERT INTO Takes (email, course_id, grade) VALUES (?, ?, ?)";
$stmt = $conn->prepare($sql);
$stmt->bind_param("sis", $email, $course_id, $grade);

if ($stmt->execute()) {
    echo "User enrolled into course!";
} else {
    echo "Error: " . $stmt->error;
}

$stmt->close();
$conn->close();
?>
