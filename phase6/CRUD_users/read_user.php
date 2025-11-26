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

// Supports ?email=... or returns list (with limit)
if (isset($_GET['email'])) {
    $stmt = $conn->prepare("SELECT email, name FROM `User` WHERE email = ?");
    $stmt->bind_param("s", $_GET['email']);
    $stmt->execute();
    $res = $stmt->get_result();
    $row = $res->fetch_assoc();
    if ($row) echo json_encode(['success'=>true,'user'=>$row]);
    else { http_response_code(404); echo json_encode(['error'=>'User not found']); }
    $stmt->close();
} else {
    $limit = isset($_GET['limit']) ? (int)$_GET['limit'] : 100;
    $offset = isset($_GET['offset']) ? (int)$_GET['offset'] : 0;
    $stmt = $conn->prepare("SELECT email, name FROM `User` ORDER BY name LIMIT ? OFFSET ?");
    $stmt->bind_param("ii", $limit, $offset);
    $stmt->execute();
    $res = $stmt->get_result();
    $out = [];
    while ($r = $res->fetch_assoc()) $out[] = $r;
    echo json_encode(['success'=>true,'count'=>count($out),'rows'=>$out]);
    $stmt->close();
}

$conn->close();
