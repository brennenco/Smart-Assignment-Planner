<?php
$conn = new mysqli("localhost", "root", "", "smart_assignment_planner");

$email = $_POST['email'];
$course_id = $_POST['course_id'];
$grade = $_POST['grade'];

$sql = "UPDATE Takes SET grade = ? WHERE email = ? AND course_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ssi", $grade, $email, $course_id);

if ($stmt->execute()) {
    echo "Grade updated!";
} else {
    echo "Error: " . $stmt->error;
}

$stmt->close();
$conn->close();
?>

