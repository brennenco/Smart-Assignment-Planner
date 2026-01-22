<?php
$conn = new mysqli("localhost", "root", "", "smart_assignment_planner");

$course_name = $_POST['course_name'];

$sql = "INSERT INTO Course (course_name) VALUES (?)";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $course_name);

if ($stmt->execute()) {
    echo "Course created successfully!";
} else {
    echo "Error: " . $stmt->error;
}

$stmt->close();
$conn->close();
?>
