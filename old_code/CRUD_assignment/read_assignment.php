<?php
header('Content-Type: application/json; charset=utf-8');

/* DB creds - update for your environment */
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

/*
Query params supported (GET):
 - assignment_id  (int)
 - course_id      (int)
 - completion_status (0 or 1)
 - due_before     (YYYY-MM-DD)
 - due_after      (YYYY-MM-DD)
 - limit          (int)
 - offset         (int)
*/
$wh = [];
$types = '';
$vals = [];

if (isset($_GET['assignment_id'])) {
    $wh[] = "assignment_id = ?";
    $types .= 'i'; $vals[] = (int)$_GET['assignment_id'];
}
if (isset($_GET['course_id'])) {
    $wh[] = "course_id = ?";
    $types .= 'i'; $vals[] = (int)$_GET['course_id'];
}
if (isset($_GET['completion_status'])) {
    $wh[] = "completion_status = ?";
    $types .= 'i'; $vals[] = (int)$_GET['completion_status'];
}
if (isset($_GET['due_before'])) {
    $wh[] = "due_date <= ?";
    $types .= 's'; $vals[] = $_GET['due_before'];
}
if (isset($_GET['due_after'])) {
    $wh[] = "due_date >= ?";
    $types .= 's'; $vals[] = $_GET['due_after'];
}

$sql = "SELECT a.assignment_id, a.course_id, c.course_name, a.name, a.due_date, a.completion_status, a.type
        FROM Assignment a
        LEFT JOIN Course c ON a.course_id = c.course_id";

if (count($wh)) $sql .= " WHERE " . implode(" AND ", $wh);

$limit = isset($_GET['limit']) ? (int)$_GET['limit'] : 100;
$offset = isset($_GET['offset']) ? (int)$_GET['offset'] : 0;
$sql .= " ORDER BY a.due_date IS NULL, a.due_date ASC, a.assignment_id ASC LIMIT ? OFFSET ?";

$types .= 'ii'; $vals[] = $limit; $vals[] = $offset;

$stmt = $conn->prepare($sql);
if ($types) {
    // bind params dynamically
    $bind_names[] = $types;
    for ($i=0; $i < count($vals); $i++) {
        $bind_name = 'bind' . $i;
        $$bind_name = $vals[$i];
        $bind_names[] = &$$bind_name;
    }
    call_user_func_array([$stmt, 'bind_param'], $bind_names);
}

$stmt->execute();
$res = $stmt->get_result();

$out = [];
while ($row = $res->fetch_assoc()) $out[] = $row;

echo json_encode(['success'=>true,'count'=>count($out),'rows'=>$out]);

$stmt->close();
$conn->close();
