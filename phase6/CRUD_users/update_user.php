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
$name  = $_POST['name'] ?? null;

if (!$email) {
    http_response_code(400);
    echo json_encode(['error'=>'Missing required field email']);
    exit;
}

if ($name === null) {
    http_response_code(400);
    echo json_encode(['error'=>'Nothing to update (provide name)']);
    exit;
}

$stmt = $conn->prepare("UPDATE `User` SET name = ? WHERE email = ?");
$stmt->bind_param("ss", $name, $email);
if ($stmt->execute()) {
    echo json_encode(['success'=>true,'email'=>$email,'changed_rows'=>$stmt->affected_rows]);
} else {
    http_response_code(500);
    echo json_encode(['error'=>'Update failed','details'=>$stmt->error]);
}

$stmt->close();
$conn->close();
