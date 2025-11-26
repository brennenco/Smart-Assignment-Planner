<?php
$conn = new mysqli("localhost", "root", "", "smart_assignment_planner");

$email = $_GET['email'];

$sql = "
SELECT T.course_id, C.course_name, T.grade
FROM Takes T
JOIN Course C ON T.course_id = C.course_id
WHERE T.email = ?
";

$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

$enrollments = [];

while ($row = $result->fetch_assoc()) {
    $enrollments[] = $row;
}

echo json_encode($enrollments);

$stmt->close();
$conn->close();
?>

