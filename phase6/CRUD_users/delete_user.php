<?php
header('Content-Type: application/json; charset=utf-8');
define('DB_HOST','localhost');
define('DB_USER','root');
define('DB_PASS','YOURMYSQLPASSWORD');
define('DB_NAME','smart_assignment_planner');

$conn = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);
if ($conn->connect_errno) {
    http_response_code(500);
    echo json_encode(['error'=>'DB connection failed','details'=>$conn->connect_error]);
    exit;
}

$email = $_POST['email'] ?? null;
if (!$email) {
    http_response_code(400);
    echo json_encode(['error'=>'Missing required field email']);
    exit;
}

$stmt = $conn->prepare("DELETE FROM `User` WHERE email = ?");
$stmt->bind_param("s", $email);
if ($stmt->execute()) {
    echo json_encode(['success'=>true,'deleted_rows'=>$stmt->affected_rows]);
} else {
    http_response_code(500);
    echo json_encode(['error'=>'Delete failed','details'=>$stmt->error]);
}

$stmt->close();
$conn->close();
